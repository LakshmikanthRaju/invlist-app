package com.example.invlist.components;

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

public class Forex implements InvComponent {

    private static final String[] MY_FOREX = { "USD", "QAR" };
    private static final String FOREX_URL = "https://www.alphavantage.co/query?function=FX_DAILY&from_symbol=%s&to_symbol=INR&apikey=50TGXNDTSOUF0HH6";

    private String values = null;
    private boolean loading = false;

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
        if (loading) return;
        loading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPrices();
            }
        }).start();
    }

    private void getPricesSlow() {
        String output = "";
        output = Arrays.stream(MY_FOREX).map(s -> getPrice(s)).collect(Collectors.joining("\n"));
        /*for (String forex: MY_FOREX)
            output = output + getPrice(forex) + "\n";*/
        //System.out.println(output);
        this.values = output;
    }

    private void getPrices() {
        String output = "";
        List<Callable<String>> callables = new ArrayList<Callable<String>>();

        Arrays.stream(MY_FOREX).forEach((s) -> callables.add(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getPrice(s);
            }
        }));

        ExecutorService executor = Executors.newFixedThreadPool(DateUtils.cpuCount() * 2);
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
        this.values = output;
        loading = false;
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
