package com.example.checkmeet.model;

import java.util.Calendar;

/**
 * Created by victo on 3/18/2017.
 */

public class Date {

    private int month;
    private int dayOfMonth;
    private int year;

    public Date() {
        this.month = Calendar.getInstance().get(Calendar.MONTH);        // 0-11 months
        this.dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        this.year = Calendar.getInstance().get(Calendar.YEAR);
    }

    public Date(int month, int dayOfMonth, int year) {
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.year = year;
    }

    // for db
    public Date(long milliseconds) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliseconds);

        this.month = c.get(Calendar.MONTH);
        this.dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        this.year = c.get(Calendar.YEAR);
    }

    public String toString() {
        return (this.month+1) + "/" + this.dayOfMonth + "/" + this.year;
    }

    // for db
    public long toMilliseconds() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, this.month);          // month 0-11 only
        c.set(Calendar.DAY_OF_MONTH, this.dayOfMonth);
        c.set(Calendar.YEAR, this.year);

        return c.getTimeInMillis();
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
