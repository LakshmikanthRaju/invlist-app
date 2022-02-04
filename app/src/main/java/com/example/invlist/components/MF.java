package com.example.invlist.components;

import com.example.invlist.utils.HTTPClient;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MF implements Callable<String> {
    public String name;
    public String code;
    public String price;
    public String date;
    public String type;
    public String message;
    public String highest;
    public InvType invType;
    public JSONArray pricesData;

    public MF(String name, String code, String price, String date, InvType invType) {
        this.name = WordUtils.capitalizeFully(name);//.substring(0, Math.min(name.length(), 50));
        this.code = code;
        this.price = price;
        this.date = date;
        this.invType = invType;
        this.message = null;
    }

    private String processResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            type = jsonResponse.getJSONObject("meta").getString("scheme_category");
            pricesData = jsonResponse.getJSONArray("data");

            JSONObject prevObject = pricesData.getJSONObject(0);
            if (MutualFund.isSameDate(date, prevObject.getString("date"))) {
                prevObject = pricesData.getJSONObject(1);
            }

            String prevPrice = prevObject.getString("nav");
            float diff = Float.parseFloat(price) - Float.parseFloat(prevPrice);

            float curPrice = Float.parseFloat(price);
            JSONObject matchObj;
            if (diff > 0) {
                for (int i = 0; i < pricesData.length(); i++) {
                    matchObj = pricesData.getJSONObject(i);
                    if (curPrice < Float.parseFloat(matchObj.getString("nav"))) {
                        int days = MutualFund.getDaysCount(date, matchObj.getString("date"));
                        return String.format("+%f (%.3f%%): Highest in %d days",diff, (diff/curPrice)*100.0, days);
                    }
                }
                return String.format("+%f (%.3f%%): Highest in all days", diff, (diff/curPrice)*100.0);
            } else {
                for (int i = 0; i < pricesData.length(); i++) {
                    matchObj = pricesData.getJSONObject(i);
                    if (curPrice > Float.parseFloat(matchObj.getString("nav"))) {
                        int days = MutualFund.getDaysCount(date, matchObj.getString("date"));
                        return String.format("%f (%.3f%%): Lowest in %d days", diff, (diff/curPrice)*100.0, days);
                    }
                }
                return String.format("+%f (%.3f%%): Lowest in all days", diff, (diff/curPrice)*100.0);
            }
        } catch (JSONException e) {
            return String.format("Failed: %s, %s, %s", name, date, price);
        }
    }

    private String processHighest(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            type = jsonResponse.getJSONObject("meta").getString("scheme_category");
            pricesData = jsonResponse.getJSONArray("data");

            // Create an int array to accomodate the numbers.
            List<Float> priceList = new ArrayList<>();
            Map<Float, JSONObject> pricesMap = new HashMap<>();
            for (int i = 0; i < pricesData.length(); ++i) {
                priceList.add(Float.parseFloat(pricesData.getJSONObject(i).getString("nav")));
                pricesMap.put(Float.parseFloat(pricesData.getJSONObject(i).getString("nav")), pricesData.getJSONObject(i));
            }
            float maxPrice = priceList.stream().max(Float::compare).get();
            JSONObject maxObj = pricesMap.get(maxPrice);
            int diff = MutualFund.getDaysCount(date, maxObj.getString("date"));
            return String.format("Highest ever (%.3f) before %d days on %s", maxPrice, diff, maxObj.getString("date"));

        } catch (JSONException e) {
            return "";
        }
    }

    public String call() throws Exception {
        String response = HTTPClient.getResponse(String.format(MutualFund.MF_URL, code));
        message = processResponse(response);
        highest = processHighest(response);
        MutualFund.displayStatus(this);
        return message;
    }
}
