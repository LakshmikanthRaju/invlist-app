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
        return new String[] {
                "SBI BLUE CHIP FUND-REGULAR PLAN GROWTH",
                "Mirae Asset Large Cap Fund - Growth Plan",
                "Axis Bluechip Fund - Regular Plan - Growth",

                "HDFC Mid-Cap Opportunities Fund - Growth Plan",
                "L&T Mid Cap Fund-Regular Plan-Growth",
                "Axis Midcap Fund - Regular Plan - Growth",

                "Kotak Flexicap Fund - Growth",
                "L&T India Value Fund-Regular Plan-Growth",

                "Nippon India Small Cap Fund - Growth Plan - Growth Option",
                "Kotak-Small Cap Fund - Growth",

                "ICICI Prudential Value Discovery Fund - Growth",
                "Aditya Birla Sun Life MIDCAP Fund-Growth",
        };
    }

    public static String[] concatenate(String[] a, String[] b)
    {
        // Function to merge two arrays of same type
        return Stream.of(a, b).flatMap(Stream::of).toArray(String[]::new);
        // Arrays::stream can also be used in place of Stream::of in the flatMap() above.
    }

    public static String[] getEquityList() {
        InvListDbHelper invDb = new InvListDbHelper(context);
        String[] values = defaultEquityList();

        if (invDb.isEquityEmpty()) {
            System.out.println("Adding to database");
            invDb.addEquity(values);
        } else {
            System.out.println("Fetching from database");
            values = concatenate(invDb.getEquity(), getElssList());
        }
        invDb.close();
        return defaultEquityList();//values;
    }

    public static String[] defaultElssList() {
        return new String[] {
                "Axis Long Term Equity Fund - Regular Plan - Growth",
                "DSP Tax Saver Fund - Regular Plan - Growth",
        };
    }

    public static String[] getElssList() {
        InvListDbHelper invDb = new InvListDbHelper(context);
        String[] values = null;

        if (invDb.isElssEmpty()) {
            values = defaultElssList();
            invDb.addElss(defaultElssList());
        } else {
            values = invDb.getElss();
        }
        invDb.close();
        return defaultElssList();//values;
    }

    public static String[] defaultDebtList() {
        return new String[]{
                "Nippon India Low Duration Fund- Growth Plan - Growth Option",
                "Axis Liquid Fund - Regular Plan - Growth Option",
                "SBI MAGNUM LOW DURATION FUND - REGULAR PLAN - GROWTH",
                "Kotak Savings Fund -Growth"
        };
    }

    public static String[] getDebtList() {
        InvListDbHelper invDb = new InvListDbHelper(context);
        String[] values = defaultDebtList();

        if (invDb.isDebtEmpty()) {
            invDb.addDebt(values);
        } else {
            values = invDb.getDebt();
        }
        invDb.close();
        return concatenate(defaultDebtList(), getElssList());//values;
    }

    public static String[] defaultStockList() {
        return new String[] { "VMW",
                "FB",
                "AAPL",
                "NFLX",
                "MSFT"/*,
                "GOOGL",
                "AMZN",
                "DELL"*/ };
    }

    public static String[] getStockList() {
        InvListDbHelper invDb = new InvListDbHelper(context);
        String[] values = defaultStockList();

        if (invDb.isStockEmpty()) {
            invDb.addStock(values);
        } else {
            values = invDb.getStock();
        }
        invDb.close();
        return defaultStockList();//values;
    }

    public static String[] defaultForexList() {
        return new String[] { "USD", "QAR" };
    }

    public static String[] getForexList() {
        InvListDbHelper invDb = new InvListDbHelper(context);
        String[] values = defaultForexList();

        if (invDb.isForexEmpty()) {
            invDb.addForex(values);
        } else {
            values = invDb.getForex();
        }
        invDb.close();
        return defaultForexList();//values;
    }
}
