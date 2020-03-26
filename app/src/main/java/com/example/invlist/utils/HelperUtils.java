package com.example.invlist.utils;

public class HelperUtils {
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    public static int cpuCount() {
        return NUMBER_OF_CORES;
    }

    public static String[] getEquityList() {
        String[] MyEquityList = {
                "SBI BLUE CHIP FUND-REGULAR PLAN GROWTH",
                "Mirae Asset Large Cap Fund - Growth Plan",
                "L&T India Value Fund-Regular Plan-Growth",
                "ICICI Prudential Value Discovery Fund - Growth",
                "Aditya Birla Sun Life MIDCAP Fund-Growth",
                "HDFC Mid-Cap Opportunities Fund - Growth Option",
                "Axis Long Term Equity Fund - Regular Plan - Growth",
                "DSP Tax Saver Fund - Regular Plan - Growth",
        };
        return MyEquityList;
    }

    public static String[] getElssList() {
        String[] MyELSSList = {
                "Axis Long Term Equity Fund - Regular Plan - Growth",
                "DSP Tax Saver Fund - Regular Plan - Growth",
        };
        return MyELSSList;
    }

    public static String[] getDebtList() {
        String[] MyDebtList = {
                "Nippon India Low Duration Fund- Growth Plan - Growth Option",
                "Axis Liquid Fund - Regular Plan - Growth Option",
                "SBI MAGNUM LOW DURATION FUND - REGULAR PLAN - GROWTH",
        };
        return MyDebtList;
    }

    public static String[] getStockList() {
        String[] MyStockList = { "VMW", "DELL" };
        return MyStockList;
    }

    public static String[] getForexList() {
        String[] MyForexList = { "USD", "QAR" };
        return MyForexList;
    }
}
