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

    public static final String STOCK_URL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=50TGXNDTSOUF0HH6";
    private static String[] MY_STOCK;
    private ArrayList<Share> stockList = null;

    public Stock() {
        super("Fetching Stock prices");
        MY_STOCK = HelperUtils.getStockList();
    }

    @Override
    public void getPrices() {
        String output = "";
        List<Callable<String>> callables = new ArrayList<Callable<String>>();
        this.stockList = new ArrayList<>();
        Arrays.stream(MY_STOCK).forEach((s) -> this.stockList.add(new Share(s)));
        stockList.forEach((s) -> callables.add(s));

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
        Arrays.stream(MY_STOCK).forEach((s) -> this.stockList.add(new Share(s)));
        //output = Arrays.stream(MY_STOCK).map(s -> getPrice(s)).collect(Collectors.joining("\n"));

        try {
            for (Share share: stockList) {
                output = output + share.call() + "\n\n";
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        setValue(output);
    }

    public static int getDaysCount(String curDate, String oldDate) {
        return DateUtils.getDaysCount(curDate, "yyyy-MM-dd", oldDate, "yyyy-MM-dd");
    }
}
