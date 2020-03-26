package com.example.invlist.components;

import com.example.invlist.utils.HelperUtils;

public class InvFactory {

    private static InvComponent stock = null;
    private static InvComponent equity = null;
    private static InvComponent debt = null;
    private static InvComponent forex = null;

    public static void reset() {
        stock = null;
        equity = null;
        debt = null;
        forex = null;
    }

    public static void reset(InvType invType) {
        if (invType == InvType.EQUITY) {
            equity = null;
        } else if (invType == InvType.DEBT) {
            debt = null;
        } else if (invType == InvType.STOCK) {
            stock = null;
        } else if (invType == InvType.FOREX) {
            forex = null;
        }
    }

    public static InvComponent getInvComponent(InvType invType) {
        if (invType == InvType.EQUITY) {
            if (equity == null) {
                equity = new MutualFund(HelperUtils.getEquityList());
            }
            return equity;
        } else if (invType == InvType.DEBT) {
            if (debt == null) {
                debt = new MutualFund(HelperUtils.getDebtList());
            }
            return debt;
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
