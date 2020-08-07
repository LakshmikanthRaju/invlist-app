package com.example.invlist.utils;

import android.content.Context;

import java.util.Arrays;
import java.util.stream.Stream;

public class HelperUtils {
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static Context context;

    public static void setContext(Context context1) {
        context = context1;
    }

    public static int cpuCount() {
        return NUMBER_OF_CORES;
    }

    public static String[] defaultEquityList() {
        String[] MyEquityList = {
                "SBI BLUE CHIP FUND-REGULAR PLAN GROWTH",
                "Mirae Asset Large Cap Fund - Growth Plan",
                "L&T India Value Fund-Regular Plan-Growth",
                "ICICI Prudential Value Discovery Fund - Growth",
                "Aditya Birla Sun Life MIDCAP Fund-Growth",
                "HDFC Mid-Cap Opportunities Fund - Growth Option",
                "Axis Bluechip Fund - Regular Plan - Growth",
                "Kotak Standard Multicap Fund - Growth",
                "L&T Mid Cap Fund-Regular Plan-Growth",
        };
        return MyEquityList;
    }

    public static String[] concatenate(String[] a, String[] b)
    {
        // Function to merge two arrays of same type
        return Stream.of(a, b).flatMap(Stream::of).toArray(String[]::new);
        // Arrays::stream can also be used in place of Stream::of in the flatMap() above.
    }

    public static String[] getEquityList() {
        InvListDbHelper invDb = new InvListDbHelper(context);
        String[] values = null;

        //if (invDb.isEquityEmpty()) {
            //System.out.println("Adding to database");
            //invDb.addEquity(defaultEquityList());
            //values = concatenate(defaultEquityList(), getElssList());
        values = defaultEquityList();
        /*} else {
            System.out.println("Fetching from database");
            values = concatenate(invDb.getEquity(), getElssList());
        }*/
        invDb.close();
        return values;
    }

    public static String[] defaultElssList() {
        String[] MyELSSList = {
                "Axis Long Term Equity Fund - Regular Plan - Growth",
                "DSP Tax Saver Fund - Regular Plan - Growth",
        };
        return MyELSSList;
    }

    public static String[] getElssList() {
        InvListDbHelper invDb = new InvListDbHelper(context);
        String[] values = null;

        //if (invDb.isElssEmpty()) {
            //invDb.addElss(defaultElssList());
            values = defaultElssList();
        /*} else {
            values = invDb.getElss();
        }*/
        invDb.close();
        return values;
    }

    public static String[] defaultDebtList() {
        String[] MyDebtList = {
                "Nippon India Low Duration Fund- Growth Plan - Growth Option",
                "Axis Liquid Fund - Regular Plan - Growth Option",
                "SBI MAGNUM LOW DURATION FUND - REGULAR PLAN - GROWTH",
                "Kotak Savings Fund -Growth"
        };
        return MyDebtList;
    }

    public static String[] getDebtList() {
        InvListDbHelper invDb = new InvListDbHelper(context);
        String[] values = null;

        //if (invDb.isDebtEmpty()) {
            //invDb.addDebt(defaultDebtList());
            //values = defaultDebtList();
            values = concatenate(defaultDebtList(), getElssList());
        /*} else {
            values = invDb.getDebt();
        }*/
        invDb.close();
        return values;
    }

    public static String[] defaultStockList() {
        String[] MyStockList = { "VMW", "DELL" };
        return MyStockList;
    }

    public static String[] getStockList() {
        InvListDbHelper invDb = new InvListDbHelper(context);
        String[] values = null;

        if (invDb.isStockEmpty()) {
            invDb.addStock(defaultStockList());
            values = defaultStockList();
        } else {
            values = invDb.getStock();
        }
        invDb.close();
        return values;
    }

    public static String[] defaultForexList() {
        String[] MyForexList = { "USD", "QAR" };
        return MyForexList;
    }

    public static String[] getForexList() {
        InvListDbHelper invDb = new InvListDbHelper(context);
        String[] values = null;

        if (invDb.isForexEmpty()) {
            invDb.addForex(defaultForexList());
            values = defaultForexList();
        } else {
            values = invDb.getForex();
        }
        invDb.close();
        return values;
    }
}
