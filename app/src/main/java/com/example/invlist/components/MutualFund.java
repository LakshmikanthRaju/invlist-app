package com.example.invlist.components;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class MutualFund implements InvComponent {

    private class MF implements Callable<String> {
        public String name;
        public String code;
        public String price;
        public String date;

        public MF(String name, String code, String price, String date) {
            this.name = name;
            this.code = code;
            this.price = price;
            this.date = date;
        }

        private String processResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray data = jsonResponse.getJSONArray("data");

                JSONObject prevObject = data.getJSONObject(0);
                if (isSameDate(date, prevObject.getString("date"))) {
                    prevObject = data.getJSONObject(1);
                }

                String prevPrice = prevObject.getString("nav");
                float diff = Float.parseFloat(price) - Float.parseFloat(prevPrice);

                Float curPrice = Float.parseFloat(price);
                JSONObject matchObj = null;
                if (diff > 0) {
                    for (int i = 0; i < data.length(); i++) {
                        matchObj = data.getJSONObject(i);
                        if (curPrice < Float.parseFloat(matchObj.getString("nav"))) {
                            int days = getDaysCount(date, matchObj.getString("date"));
                            return String.format("%s, %s, %s, %f: Highest in %d days", name, date, price, diff, days);
                        }
                    }
                    return String.format("%s, %s, %s, %f: Highest in all days", name, date, price, diff);
                } else {
                    for (int i = 0; i < data.length(); i++) {
                        matchObj = data.getJSONObject(i);
                        if (curPrice > Float.parseFloat(matchObj.getString("nav"))) {
                            int days = getDaysCount(date, matchObj.getString("date"));
                            return String.format("%s, %s, %s, %f: Lowest in %d days", name, date, price, diff, days);
                        }
                    }
                    return String.format("%s, %s, %s, %f: Lowest in all days", name, date, price, diff);
                }

            } catch (JSONException e) {
                return String.format("Failed: %s, %s, %s", name, date, price);
            }
        }

        public String call() throws Exception {
            String response = HTTPClient.getResponse(String.format(MF_URL, code));
            return processResponse(response);
        }
    }

    private static final String[] MY_EQUITY_LIST = {
            "SBI BLUE CHIP FUND-REGULAR PLAN GROWTH",
            "Mirae Asset Large Cap Fund - Growth Plan",
            "L&T India Value Fund-Regular Plan-Growth",
            "ICICI Prudential Value Discovery Fund - Growth",
            "Aditya Birla Sun Life MIDCAP Fund-Growth",
            "HDFC Mid-Cap Opportunities Fund - Growth Option",
    };
    private static final String MF_URL = "https://api.mfapi.in/mf/%s";
    private static final String MF_LIST_URL = "https://www.amfiindia.com/spages/NAVAll.txt";
    private static final List<String> MY_EQUITY = Arrays.asList(MY_EQUITY_LIST);

    private String values = null;
    private boolean loading = false;

    public MutualFund() {
    }

    @Override
    public String values() {
        if (values != null) {
            return values;
        }
        load();
        return "Fetching Equity Mutual NAVs";
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
        long start = System.nanoTime();
        String output = "";
        List<MF> mfList = getMFList();

        try {
            for (MF mf: mfList) {
                output = output + mf.call() + "\n\n";
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        //System.out.println(values);
        this.values = output;
        long end = System.nanoTime();
        System.out.println("Time taken in milliseconds: " + (end-start)/1000000 );
    }

    private void getPrices() {
        String output = "";
        List<Callable<String>> callables = new ArrayList<Callable<String>>();

        List<MF> mfList = getMFList();
        mfList.forEach((mf) -> callables.add(mf));

        //ExecutorService executor = Executors.newCachedThreadPool();
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

    private boolean isSameDate(String curDate, String oldDate) {
        return DateUtils.isSameDate(curDate, "dd-MMM-yyyy", oldDate, "dd-MM-yyyy");
    }

    private int getDaysCount(String curDate, String oldDate) {
        return DateUtils.getDaysCount(curDate, "dd-MMM-yyyy", oldDate, "dd-MM-yyyy");
    }

    private List<MF> getMFList() {
        List<MF> mfList = new ArrayList<>();
        String response = HTTPClient.getResponse(MF_LIST_URL);
        String[] mfListArr = response.split("\\n");

        Arrays.asList(mfListArr).stream()
                .filter(mf->mf.split(";").length == 6)
                .filter(mf->MY_EQUITY.contains(mf.split(";")[3]))
                .forEach(mf->{
                    String[] mfSections = mf.split(";");
                    mfList.add(new MF(mfSections[3], mfSections[0], mfSections[4], mfSections[5]));
        });

        /*for (String mfStr: mfListArr) {
            String[] mfSections = mfStr.split(";");
            if (mfSections.length == 6) {
                String mfName = mfSections[3];
                if (MY_EQUITY.contains(mfName))
                    mfList.add(new MF(mfName, mfSections[0], mfSections[4], mfSections[5]));
            }
        }*/
        //mfList.forEach(e->System.out.print(e.name));

        return mfList;
    }
}
