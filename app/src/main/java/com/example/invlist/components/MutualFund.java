package com.example.invlist.components;

import com.example.invlist.utils.DateUtils;
import com.example.invlist.utils.HTTPClient;
import com.example.invlist.utils.HelperUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MutualFund extends InvComponent {

    private class MF implements Callable<String> {
        public String name;
        public String code;
        public String price;
        public String date;
        public String type;
        public JSONArray pricesData;

        public MF(String name, String code, String price, String date) {
            this.name = name;
            this.code = code;
            this.price = price;
            this.date = date;
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
                if (isSameDate(date, prevObject.getString("date"))) {
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
                            int days = getDaysCount(date, matchObj.getString("date"));
                            return formatMessage(String.format("+%f: Highest in %d days", diff, days));
                        }
                    }
                    return formatMessage(String.format("+%f: Highest in all days", diff));
                } else {
                    for (int i = 0; i < pricesData.length(); i++) {
                        matchObj = pricesData.getJSONObject(i);
                        if (curPrice > Float.parseFloat(matchObj.getString("nav"))) {
                            int days = getDaysCount(date, matchObj.getString("date"));
                            return formatMessage(String.format("%f: Lowest in %d days", diff, days));
                        }
                    }
                    return formatMessage(String.format("+%f: Lowest in all days", diff));
                }
            } catch (JSONException e) {
                return String.format("Failed: %s, %s, %s", name, date, price);
            }
        }

        public String call() throws Exception {
            String response = HTTPClient.getResponse(String.format(MF_URL, code));
            String value = processResponse(response);
            updateValue(value);
            return value;
        }
    }

    private static final String MF_URL = "https://api.mfapi.in/mf/%s";
    private static final String MF_LIST_URL = "https://www.amfiindia.com/spages/NAVAll.txt";
    private static List<String> MY_FUND;
    private String[] mfListArr = null;

    public MutualFund(String[] fundList) {
        super("Fetching Mutual Funds NAVs");
        MY_FUND = Arrays.asList(fundList);
    }

    @Override
    public void getPrices() {
        String output = "";
        List<Callable<String>> callables = new ArrayList<Callable<String>>();

        List<MF> mfList = getMFList();
        mfList.forEach((mf) -> callables.add(mf));

        //ExecutorService executor = Executors.newCachedThreadPool();
        ExecutorService executor = Executors.newFixedThreadPool(HelperUtils.cpuCount() * 2);
        try {
            List<Future<String>> futures = executor.invokeAll(callables);
            //output =
            futures.stream().map(f -> {
                try {
                    return f.get();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            });//.collect(Collectors.joining("\n\n"));
        } catch (InterruptedException e) {// thread was interrupted
            e.printStackTrace();
        } finally {
            executor.shutdown(); // shut down the executor manually
        }
        //setValue(output);
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

        setValue(output);
        long end = System.nanoTime();
        System.out.println("Time taken in milliseconds: " + (end-start)/1000000 );
    }

    private List<MF> getMFList() {
        List<MF> mfList = new ArrayList<>();
        String response = HTTPClient.getResponse(MF_LIST_URL);
        mfListArr = response.split("\\n");

        Arrays.asList(mfListArr).stream()
                .filter(mf->mf.split(";").length == 6)
                .filter(mf->MY_FUND.contains(mf.split(";")[3]))
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

    private boolean isSameDate(String curDate, String oldDate) {
        return DateUtils.isSameDate(curDate, "dd-MMM-yyyy", oldDate, "dd-MM-yyyy");
    }

    private int getDaysCount(String curDate, String oldDate) {
        return DateUtils.getDaysCount(curDate, "dd-MMM-yyyy", oldDate, "dd-MM-yyyy");
    }
}
