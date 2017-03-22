package com.example.checkmeet.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by victo on 3/19/2017.
 */

public class Utils {

    public static String dateIntegerToString(int date_integer) {
        int hours = date_integer / 100;
        int minutes = date_integer % 100;

        String strdate24 = hours + ":" + minutes;

        SimpleDateFormat originalFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");

        try {
            Date date = originalFormat.parse(strdate24);

            return parseFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int dateStringToInteger(String date_string) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");
        int time24 = 0;

        try {
            Date date = originalFormat.parse(date_string);

            String parsedDate = parseFormat.format(date);

            String[] tokens = parsedDate.split(":");
            int hours = Integer.parseInt(tokens[0]);
            int minutes = Integer.parseInt(tokens[1]);

            hours = hours * 100;
            time24 = hours + minutes;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time24;
    }

    public static int getHourFromTimeInteger(int time) {
        int hours = time / 100;

        if(hours == 0) {
            return 12;
        } else if (hours <= 12) {
            return hours;
        } else return hours - 12;
    }

    public static int getMinuteFromTimeInteger(int time) {
        return time % 100;
    }

    private static String monthIntToString(int month) {
        switch (month) {
            case 0: return "January";
            case 1: return "February";
            case 2: return "March";
            case 3: return "April";
            case 4: return "May";
            case 5: return "June";
            case 6: return "July";
            case 7: return "August";
            case 8: return "September";
            case 9: return "October";
            case 10: return "November";
            case 11: return "December";
            default: return "";
        }
    }

    public static String dateToString(com.example.checkmeet.model.Date date) {
        String month = monthIntToString(date.getMonth());

        return month + " " + date.getDayOfMonth() + ", " + date.getYear();
    }
}
