package com.example.invlist.components;

import java.util.HashMap;
import java.util.Map;

public enum InvType {

    EQUITY(1), FOREX(2), STOCK(3);

    private final int value;
    private static Map map = new HashMap();

    static {
        for (InvType invType : InvType.values()) {
            map.put(invType.value, invType);
        }
    }

    private InvType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

    public static InvType valueOf(int invType) {
        return (InvType) map.get(invType);
    }
}
