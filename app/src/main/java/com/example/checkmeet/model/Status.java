package com.example.checkmeet.model;

/**
 * Created by victo on 4/5/2017.
 */

public enum Status {

    PENDING, CANCELLED;

    public static Status getValue(String status) {
        switch(status) {
            case "PENDING" : return PENDING;
            default: return CANCELLED;
        }
    }
}
