package com.example.invlist.components;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Stock implements InvComponent {

    private static final String[] MY_STOCK = { "VMW", "DELL" };
    private static final String STOCK_URL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=50TGXNDTSOUF0HH6";

    private String values = null;

    public Stock() {
    }

    @Override
    public String values() {
        if (values != null) {
            System.out.println("Stored: " + values);
            return values;
        }
        load();
        return "Fetching Stock prices";
    }

    @Override
    public void load() {
        System.out.println("------Starting Stock-------------------------------------");
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPrices();
            }
        }).start();
    }

    private void getPrices() {
        String output = "";
        output = Arrays.stream(MY_STOCK).map(s -> getPrice(s)).collect(Collectors.joining("\n"));
        //for (String stock: MY_STOCK)
        //    output = output + getPrice(stock) + "\n";

        System.out.println(output);
        this.values = output;
    }

    private int getDaysCount(String curDate, String oldDate) {
        return DateUtils.getDaysCount(curDate, "yyyy-MM-dd", oldDate, "yyyy-MM-dd");
    }

    private String parseResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);

            JSONObject metadata = jsonResponse.getJSONObject("Meta Data");
            String date = metadata.getString("3. Last Refreshed");
            String stock = metadata.getString("2. Symbol");

            JSONObject prices = jsonResponse.getJSONObject("Time Series (Daily)");
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
                        return String.format("%s, %s, %s, %f: Highest in %d days", stock, date, currPrice, diff, days);
                    }
                }
                return String.format("%s, %s, %s, %f: Highest in all days", stock, date, currPrice, diff);
            } else {
                while(datesKey.hasNext()) {
                    String oldDate = datesKey.next();
                    JSONObject oldObj = prices.getJSONObject(oldDate);
                    String oldPrice = oldObj.getString("3. low");
                    if (Float.parseFloat(currPrice) > Float.parseFloat(oldPrice)) {
                        int days = getDaysCount(curDate, oldDate);
                        return String.format("%s, %s, %s, %f: Lowest in %d days", stock, date, currPrice, diff, days);
                    }
                }
                return String.format("%s, %s, %s, %f: Lowest in all days", stock, date, currPrice, diff);
            }
        } catch (JSONException e) {
            return response;
        }
    }

    private String getPrice(String stock) {

        String response = HTTPClient.getResponse(String.format(STOCK_URL, stock));
        return parseResponse(response);
    }
}
