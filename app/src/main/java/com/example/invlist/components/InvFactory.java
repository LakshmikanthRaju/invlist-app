package com.example.invlist.components;

public class InvFactory {

    private static InvComponent stock = null;
    private static InvComponent mf = null;
    private static InvComponent forex = null;

    public static void reset() {
        stock = null;
        mf = null;
        forex = null;
    }

    public static InvComponent getInvComponent(InvType invType) {
        if (invType == InvType.EQUITY) {
            if (mf == null) {
                mf = new MutualFund();
            }
            return mf;
        } else if (invType == InvType.STOCK) {
            if (stock == null) {
                stock = new Stock();
            }
            return stock;
        } else if (invType == InvType.FOREX) {
            if (forex == null) {
                forex = new Forex();
            }
            return forex;
        } else {
            return null;
        }
    }
}
