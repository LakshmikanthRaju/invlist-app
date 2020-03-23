package com.example.invlist.components;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Forex implements InvComponent {

    private static final String[] MY_FOREX = { "USD", "QAR" };
    private static final String FOREX_URL = "https://www.alphavantage.co/query?function=FX_DAILY&from_symbol=%s&to_symbol=INR&apikey=50TGXNDTSOUF0HH6";

    private String values = null;

    public Forex() {
    }

    @Override
    public String values() {
        if (values != null) {
            return values;
        }
        load();
        return "Fetching Forex rates";
    }

    @Override
    public void load() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPrices();
            }
        }).start();
    }

    private void getPrices() {
        String output = "";
        output = Arrays.stream(MY_FOREX).map(s -> getPrice(s)).collect(Collectors.joining("\n"));
        /*for (String forex: MY_FOREX)
            output = output + getPrice(forex) + "\n";*/

        //System.out.println(output);
        this.values = output;
    }

    private int getDaysCount(String curDate, String oldDate) {
        return DateUtils.getDaysCount(curDate, "dd-MMM-yyyy", oldDate, "dd-MM-yyyy");
    }

    private String parseResponse(String response) {

        try {
            JSONObject jsonResponse = new JSONObject(response);
            String date = jsonResponse.getJSONObject("Meta Data").getString("5. Last Refreshed");
            String forex = jsonResponse.getJSONObject("Meta Data").getString("2. From Symbol");

            JSONObject prices = jsonResponse.getJSONObject("Time Series FX (Daily)");
            Iterator<String> datesKey = prices.keys();

            String curDate = datesKey.next();
            JSONObject curObject = prices.getJSONObject(curDate);
            String currPrice = curObject.getString("4. close");
            String prevPrice = prices.getJSONObject(datesKey.next()).getString("4. close");
            float diff = Float.parseFloat(currPrice) - Float.parseFloat(prevPrice);

            datesKey = prices.keys();
            datesKey.next();
            if (diff > 0) {
                while(datesKey.hasNext()) {
                    String oldDate = datesKey.next();
                    JSONObject oldObj = prices.getJSONObject(oldDate);
                    String oldPrice = oldObj.getString("2. high");
                    if (Float.parseFloat(currPrice) < Float.parseFloat(oldPrice)) {
                        int days = getDaysCount(curDate, oldDate);
                        return String.format("%s, %s, %s, %f: Highest in %d days", forex, date, currPrice, diff, days);
                    }
                }
                return String.format("%s, %s, %s, %f: Highest in all days", forex, date, currPrice, diff);
            } else {
                while(datesKey.hasNext()) {
                    String oldDate = datesKey.next();
                    JSONObject oldObj = prices.getJSONObject(oldDate);
                    String oldPrice = oldObj.getString("3. low");
                    if (Float.parseFloat(currPrice) > Float.parseFloat(oldPrice)) {
                        int days = getDaysCount(curDate, oldDate);
                        return String.format("%s, %s, %s, %f: Lowest in %d days", forex, date, currPrice, diff, days);
                    }
                }
                return String.format("%s, %s, %s, %f: Lowest in all days", forex, date, currPrice, diff);
            }
        } catch (JSONException e) {
            return response;
        }
    }

    private String getPrice(String forex) {

        String response = HTTPClient.getResponse(String.format(FOREX_URL, forex));
        return parseResponse(response);
    }
}
