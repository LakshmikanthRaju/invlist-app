package com.example.invlist.components;

import com.example.invlist.ui.fragments.StockFragment;
import com.example.invlist.utils.DateUtils;
import com.example.invlist.utils.HTTPClient;
import com.example.invlist.utils.HelperUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Stock extends InvComponent {

    private class Share implements Callable<String> {
        public String name;
        public String price;
        public String date;
        public JSONObject pricesData;

        public Share(String name) {
            this.name = name;
        }

        private String formatMessage(String curDate, String status) {
            if (curDate.split("\\s+").length > 1) {
                curDate = DateUtils.convertTimestamp(curDate, "yyyy-MM-dd HH:mm:ss");
            } else {
                curDate = DateUtils.convertFormat(curDate, "yyyy-MM-dd");
            }
            return String.format("%s :  %s, %s EDT\n%s", name, price, curDate, status);
        }

        private String parseResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                pricesData = jsonResponse.getJSONObject("Time Series (Daily)");

                JSONObject metadata = jsonResponse.getJSONObject("Meta Data");
                String date = metadata.getString("3. Last Refreshed");
                name = metadata.getString("2. Symbol");

                JSONObject prices = jsonResponse.getJSONObject("Time Series (Daily)");
                Iterator<String> datesKey = prices.keys();

                String curDate = datesKey.next();
                JSONObject curObject = prices.getJSONObject(curDate);
                String currPrice = curObject.getString("4. close");
                String prevPrice = prices.getJSONObject(datesKey.next()).getString("4. close");
                float diff = Float.parseFloat(currPrice) - Float.parseFloat(prevPrice);

                this.price = currPrice;
                this.date = DateUtils.convertFormat(curDate, "yyyy-MM-dd");;

                datesKey = prices.keys();
                datesKey.next();
                if (diff > 0) {
                    while(datesKey.hasNext()) {
                        String oldDate = datesKey.next();
                        JSONObject oldObj = prices.getJSONObject(oldDate);
                        String oldPrice = oldObj.getString("2. high");
                        if (Float.parseFloat(currPrice) < Float.parseFloat(oldPrice)) {
                            int days = getDaysCount(curDate, oldDate);
                            return formatMessage(date, String.format("+%f: Highest in %d days", diff, days));
                        }
                    }
                    return formatMessage(date, String.format("+%f: Highest in all days", diff));
                } else {
                    while(datesKey.hasNext()) {
                        String oldDate = datesKey.next();
                        JSONObject oldObj = prices.getJSONObject(oldDate);
                        String oldPrice = oldObj.getString("3. low");
                        if (Float.parseFloat(currPrice) > Float.parseFloat(oldPrice)) {
                            int days = getDaysCount(curDate, oldDate);
                            return formatMessage(date, String.format("%f: Lowest in %d days", diff, days));
                        }
                    }
                    return formatMessage(date, String.format("%f: Lowest in %d days", diff));
                }
            } catch (JSONException e) {
                return response;
            }
        }

        public String call() throws Exception {
            String response = HTTPClient.getResponse(String.format(STOCK_URL, name));
            String value = "";
            if (response.contains("Error Message")) {
                value = "ERROR: Invalid API call for " + name;
            } else if (response.contains("Note")) {
                value = "WARNING: Overload of API calls. Try after a minute";
            } else {
                value = parseResponse(response);
            }
            //updateValue(value);
            StockFragment.updateListView(value);
            return value;
        }
    }

    private static final String STOCK_URL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=50TGXNDTSOUF0HH6";
    private static String[] MY_STOCK;

    public Stock() {
        super("Fetching Stock prices");
        MY_STOCK = HelperUtils.getStockList();
    }

    @Override
    public void getPrices() {
        String output = "";
        List<Callable<String>> callables = new ArrayList<Callable<String>>();
        Arrays.stream(MY_STOCK).forEach((s) -> callables.add(new Share(s)));

        ExecutorService executor = Executors.newFixedThreadPool(HelperUtils.cpuCount() * 2);
        try {
            List<Future<String>> futures = executor.invokeAll(callables);
            output = futures.stream().map(f -> {
                try {
                    return f.get();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }).collect(Collectors.joining("\n\n"));
        } catch (InterruptedException e) {// thread was interrupted
            e.printStackTrace();
        } finally {
            executor.shutdown(); // shut down the executor manually
        }
        //setValue(output);
    }

    private void getPricesSlow() {
        String output = "";
        output = Arrays.stream(MY_STOCK)
                .map(s -> getPrice(s))
                .collect(Collectors.joining("\n"));
        setValue(output);
    }

    private int getDaysCount(String curDate, String oldDate) {
        return DateUtils.getDaysCount(curDate, "yyyy-MM-dd", oldDate, "yyyy-MM-dd");
    }

    private String formatMessage(String stock, String curDate, String currPrice, String status) {
        String date = DateUtils.convertFormat(curDate, "yyyy-MM-dd HH:mm:ss");
        return String.format("%s :  %s, %s EDT\n%s", stock, currPrice, date, status);
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
                        return formatMessage(stock, date, currPrice, String.format("+%f: Highest in %d days", diff, days));
                    }
                }
                return formatMessage(stock, date, currPrice, String.format("+%f: Highest in all days", diff));
            } else {
                while(datesKey.hasNext()) {
                    String oldDate = datesKey.next();
                    JSONObject oldObj = prices.getJSONObject(oldDate);
                    String oldPrice = oldObj.getString("3. low");
                    if (Float.parseFloat(currPrice) > Float.parseFloat(oldPrice)) {
                        int days = getDaysCount(curDate, oldDate);
                        return formatMessage(stock, date, currPrice, String.format("%f: Lowest in %d days", diff, days));
                    }
                }
                return formatMessage(stock, date, currPrice, String.format("%f: Lowest in %d days", diff));
            }
        } catch (JSONException e) {
            return response;
        }
    }

    private String getPrice(String stock) {

        String response = HTTPClient.getResponse(String.format(STOCK_URL, stock));
        String value = "";
        if (response.contains("Error Message")) {
            value = "ERROR: Invalid API call for " + stock;
        } else if (response.contains("Note")) {
            value = "WARNING: Overload of API calls. Try after a minute";
        } else {
            value = parseResponse(response);
        }
        updateValue(value);
        return value;
    }
}
