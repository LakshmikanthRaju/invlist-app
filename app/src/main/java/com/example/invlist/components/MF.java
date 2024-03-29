package com.example.invlist.components;

import com.example.invlist.utils.HTTPClient;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class MF implements Callable<String> {
    public String name;
    public String code;
    public String price;
    public String date;
    public String type;
    public String message;
    public InvType invType;
    public JSONArray pricesData;

    public MF(String name, String code, String price, String date, InvType invType) {
        this.name = WordUtils.capitalizeFully(name).substring(0, Math.min(name.length(), 50));
        this.code = code;
        this.price = price;
        this.date = date;
        this.invType = invType;
        this.message = null;
    }

    private String formatMessage(String status) {
        return String.format("%s\n%s, %s\n%s", name, date, price, status);
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

            Float curPrice = Float.parseFloat(price);
            JSONObject matchObj = null;
            if (diff > 0) {
                for (int i = 0; i < pricesData.length(); i++) {
                    matchObj = pricesData.getJSONObject(i);
                    if (curPrice < Float.parseFloat(matchObj.getString("nav"))) {
                        int days = MutualFund.getDaysCount(date, matchObj.getString("date"));
                        //return formatMessage(String.format("+%f: Highest in %d days", diff, days));
                        return String.format("+%f (%.3f%%): Highest in %d days",diff, (diff/curPrice)*100.0, days);
                    }
                }
                //return formatMessage(String.format("+%f: Highest in all days", diff));
                return String.format("+%f (%.3f%%): Highest in all days", diff, (diff/curPrice)*100.0);
            } else {
                for (int i = 0; i < pricesData.length(); i++) {
                    matchObj = pricesData.getJSONObject(i);
                    if (curPrice > Float.parseFloat(matchObj.getString("nav"))) {
                        int days = MutualFund.getDaysCount(date, matchObj.getString("date"));
                        //return formatMessage(String.format("%f: Lowest in %d days", diff, days));
                        return String.format("%f (%.3f%%): Lowest in %d days", diff, (diff/curPrice)*100.0, days);
                    }
                }
                //return formatMessage(String.format("+%f: Lowest in all days", diff));
                return String.format("+%f (%.3f%%): Lowest in all days", diff, (diff/curPrice)*100.0);
            }
        } catch (JSONException e) {
            return String.format("Failed: %s, %s, %s", name, date, price);
        }
    }

    private String processResponseDetail(String response) {
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

            String message = processResponse(response);
            String tempDate = null;
            float findPrice;

            Float curPrice = Float.parseFloat(price);
            JSONObject matchObj = null;
            JSONObject prevObj = null;
            if (diff > 0) {
                for (int i = 0; i < pricesData.length()-1; i++) {
                    matchObj = pricesData.getJSONObject(i);
                    prevObj = pricesData.getJSONObject(i+1);
                    if (Float.parseFloat(matchObj.getString("nav")) < Float.parseFloat(prevObj.getString("nav"))) {
                        curPrice = Float.parseFloat(matchObj.getString("nav"));
                        tempDate = matchObj.getString("date");
                        for (int j = i; j < pricesData.length(); j++) {
                            matchObj = pricesData.getJSONObject(j);
                            if (curPrice > Float.parseFloat(matchObj.getString("nav"))) {
                                int days = MutualFund.getDaysCount2(tempDate, matchObj.getString("date"));
                                findPrice = Float.parseFloat(matchObj.getString("nav"));
                                message = message + String.format("\n (%f): Lowest in %d days", curPrice, days);
                                return message;
                            }
                        }
                    }
                }
                return message;
            } else {
                for (int i = 0; i < pricesData.length()-1; i++) {
                    matchObj = pricesData.getJSONObject(i);
                    prevObj = pricesData.getJSONObject(i+1);
                    if (Float.parseFloat(matchObj.getString("nav")) > Float.parseFloat(prevObj.getString("nav"))) {
                        curPrice = Float.parseFloat(matchObj.getString("nav"));
                        tempDate = matchObj.getString("date");
                        for (int j = i; j < pricesData.length(); j++) {
                            matchObj = pricesData.getJSONObject(j);
                            if (curPrice < Float.parseFloat(matchObj.getString("nav"))) {
                                int days = MutualFund.getDaysCount2(tempDate, matchObj.getString("date"));
                                findPrice = Float.parseFloat(matchObj.getString("nav"));
                                message = message + String.format("\n (%f): Highest in %d days", curPrice, days);
                                return message;
                            }
                        }
                    }
                }
                return message;
            }
        } catch (JSONException e) {
            return message;
        }
    }

    public String call() throws Exception {
        String response = HTTPClient.getResponse(String.format(MutualFund.MF_URL, code));
        message = processResponse(response);
        //message = processResponseDetail(response);
        MutualFund.displayStatus(this);
        //updateValue(value);
        return message;
    }
}
