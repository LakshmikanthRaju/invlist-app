package com.example.invlist.components;

import com.example.invlist.ui.fragments.DebtFragment;
import com.example.invlist.ui.fragments.EquityFragment;
import com.example.invlist.utils.DateUtils;
import com.example.invlist.utils.HTTPClient;
import com.example.invlist.utils.HelperUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MutualFund extends InvComponent {

    public static final String MF_URL = "https://api.mfapi.in/mf/%s";
    private static final String MF_LIST_URL = "https://www.amfiindia.com/spages/NAVAll.txt";

    private static List<String> MY_FUND;
    private String[] mfListArr = null;
    private ArrayList<MF> mfList = null;
    private InvType invType;

    public MutualFund(String[] fundList, InvType invType) {
        super("Fetching Mutual Funds NAVs");
        MY_FUND = Arrays.asList(fundList);
        this.invType = invType;
    }

    @Override
    public void getPrices() {
        String output = "";
        List<Callable<String>> callables = new ArrayList<Callable<String>>();

        this.mfList = getMFList();
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

    private ArrayList<MF> getMFList() {

        ArrayList<MF> mfsList = new ArrayList<>();
        String response = HTTPClient.getResponse(MF_LIST_URL);
        mfListArr = response.split("\\n");

        Arrays.asList(mfListArr).stream()
                .filter(mf->mf.split(";").length == 6)
                .filter(mf->MY_FUND.contains(mf.split(";")[3]))
                .forEach(mf->{
                    String[] mfSections = mf.split(";");
                    mfsList.add(new MF(mfSections[3], mfSections[0], mfSections[4], mfSections[5], invType));
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

        return mfsList;
    }

    public static boolean isSameDate(String curDate, String oldDate) {
        return DateUtils.isSameDate(curDate, "dd-MMM-yyyy", oldDate, "dd-MM-yyyy");
    }

    public static int getDaysCount(String curDate, String oldDate) {
        return DateUtils.getDaysCount(curDate, "dd-MMM-yyyy", oldDate, "dd-MM-yyyy");
    }

    public static void displayStatus(MF mf) {
        if (mf.invType == InvType.EQUITY) {
            EquityFragment.updateListView(mf);
        } else if (mf.invType == InvType.DEBT) {
            DebtFragment.updateListView(mf);
        }
    }
}
