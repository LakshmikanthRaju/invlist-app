package com.example.invlist.components;

import com.example.invlist.ui.fragments.ForexFragment;
import com.example.invlist.utils.DateUtils;
import com.example.invlist.utils.HTTPClient;
import com.example.invlist.utils.HelperUtils;

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

public class Forex extends InvComponent {

    public static final String FOREX_URL = "https://www.alphavantage.co/query?function=FX_DAILY&from_symbol=%s&to_symbol=INR&apikey=50TGXNDTSOUF0HH6";
    private static String[] MY_FOREX;
    private ArrayList<Currency> forexList = null;

    public Forex() {
        super("Fetching Forex rates");
        MY_FOREX = HelperUtils.getForexList();
    }

    @Override
    public void getPrices() {
        String output = "";
        List<Callable<String>> callables = new ArrayList<Callable<String>>();

        /*Arrays.stream(MY_FOREX).forEach((f) -> callables.add(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getPrice(f);
            }
        }));*/
        forexList = new ArrayList<>();
        Arrays.stream(MY_FOREX).forEach((f) -> forexList.add(new Currency(f)));
        forexList.forEach((f) -> callables.add(f));

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
        //output = Arrays.stream(MY_FOREX).map(s -> getPrice(s)).collect(Collectors.joining("\n"));
        try {
            for (Currency currency: forexList) {
                output = output + currency.call() + "\n\n";
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        /*for (String forex: MY_FOREX)
            output = output + getPrice(forex) + "\n";*/
        //System.out.println(output);
        setValue(output);
    }

    public static int getDaysCount(String curDate, String oldDate) {
        return DateUtils.getDaysCount(curDate, "dd-MMM-yyyy", oldDate, "dd-MM-yyyy");
    }
}
