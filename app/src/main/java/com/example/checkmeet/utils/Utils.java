package com.example.checkmeet.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.checkmeet.model.Contact;
import com.example.checkmeet.model.Meeting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by victo on 3/19/2017.
 */

public class Utils {

    @SuppressLint("SimpleDateFormat")
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

    @SuppressLint("SimpleDateFormat")
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

    public static String monthIntToString(int month) {
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

    public static int getDarkColor(int fade_color) {
        switch (fade_color) {
            case -3238952: return Color.parseColor("#BA68C8");
            case -7288071: return Color.parseColor("#2196F3");
            case -13184: return Color.parseColor("#FDD835");
            case -5908825: return Color.parseColor("#8BC34A");
            case -10929: return Color.parseColor("#FFA000");
            default: return Color.parseColor("#000000");
        }
    }

    public static Meeting parseText(String text)
    {
        Meeting meeting = new Meeting();
        //TODO: PARSING HERE
        String ckmtCode;
        String [] textParts = text.split("__________");
        String [] meetingDetails = textParts[1].split("$&");
        String [] dateParts = meetingDetails[2].split("/");
        int meetingDetailsCount = meetingDetails.length;

        //deviceID
        meeting.setDevice_id(meetingDetails[0]);
        //Meeting Name
        meeting.setTitle(meetingDetails[1]);
        //Meeting Date
        meeting.setDate(new com.example.checkmeet.model.Date(Integer.parseInt(dateParts[0]),
                Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[2])));
        //Meeting  Time From
        meeting.setStartTime(dateStringToInteger(meetingDetails[3]));
        //Meeting Time To
        meeting.setEndTime(dateStringToInteger(meetingDetails[4]));
        //Meeting Address
        meeting.setAddress(meetingDetails[5]);
        //Meeting Latitude
        meeting.setLatitude(Double.parseDouble(meetingDetails[6]));
        //Meeting Longitude
        meeting.setLongitude(Double.parseDouble(meetingDetails[7]));
        //Meeting Participants (Names)
        meeting.setStringParticipants(meetingDetails[8]);
        //Meeting Hostname
        meeting.setHostName(meetingDetails[9]);
        //Meeting Color
        meeting.setColor(Integer.parseInt(meetingDetails[10]));
        //Meeting Description
        if(meetingDetailsCount == 12)
            meeting.setDescription(meetingDetails[11]);

        return meeting;
    }

    public static void sendSMS(Context context, String message, List<String> participantIDList) {

        for (String participantID: participantIDList) {
            String phoneNumber = findPhoneNumber(context, participantID);
            sendSMSParticipant(context, phoneNumber, message);
        }
    }

    private static void sendSMSParticipant(Context context, String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> messages = smsManager.divideMessage(message);

        ArrayList<PendingIntent> sentIntents = new ArrayList<>();

        Intent intent = new Intent("com.example.checkmeet.SMS_SENT_ACTION");

        long parsedPhoneNumber = Long.parseLong(phoneNumber);

        for (int i = 0; i < messages.size(); i++) {
            sentIntents.add(PendingIntent.getBroadcast(
                    context, (int)(parsedPhoneNumber), intent, PendingIntent.FLAG_CANCEL_CURRENT));
        }

        smsManager.sendMultipartTextMessage(phoneNumber, null, messages, sentIntents, null);
    }

    private static String findPhoneNumber(Context context, String participant_id) {
        ContentResolver cr = context.getContentResolver();
        String phoneNumber = "";

        Cursor cur = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts._ID + " = ?",
                new String[]{participant_id},
                null);

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {

                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    if (pCur != null && pCur.moveToFirst()) {
                        phoneNumber = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        pCur.close();
                    }
                }
            }
            cur.close();
        }
        return phoneNumber;
    }

}
