package com.example.checkmeet.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.checkmeet.model.Meeting;
import com.example.checkmeet.service.MeetingService;
import com.example.checkmeet.utils.Utils;

/**
 * Created by victo on 4/5/2017.
 */

public class SMSBroadcastReceiverRECEIVE extends BroadcastReceiver {

    private String final_message = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle myBundle = intent.getExtras();
        SmsMessage[] messages;
        String strMessage = "";

        if (myBundle != null) {
            Object[] pdus = (Object[]) myBundle.get("pdus");
            assert pdus != null;
            messages = new SmsMessage[pdus.length];

            // one parser for cancel (since only the IMEI-meeting_id will be the only parameter
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                if(messages[i].getMessageBody().contains("CKMT [CRT]") ||
                        messages[i].getMessageBody().contains("CKMT [EDT]") ||
                        messages[i].getMessageBody().contains("CKMT [CNL]")) {
                    final_message = "";
                }

                strMessage += "SMS From: " + messages[i].getOriginatingAddress();
                strMessage += " : ";
                strMessage += messages[i].getMessageBody();
                strMessage += "\n";
                final_message = final_message + messages[i].getMessageBody();
            }

            if(final_message.contains("&&&")) {
                Log.e("SMSReceiverRECEIVE", final_message);


                Meeting meeting;
                Log.e("SMSReceiverRECEIVE", strMessage);
                switch(Utils.getCKMTcode(final_message)){
                    case "CKMT [CRT]":
                        meeting = Utils.parseCreateEditText(final_message);
                        MeetingService.createMeeting(context, meeting, false);
                        break;
                    case "CKMT [EDT]":
                        Log.e("RECEIVE", "EDITING MEETING");
                        meeting = Utils.parseCreateEditText(final_message);
                        int i = MeetingService.updateMeeting(context, meeting, false);
                        Log.e("AFFECTED MEETINGS", " ====== " + i + "");
                        break;
                    case "CKMT [CNL]":
                        String deviceId = Utils.parseCancelText(final_message);

                        Log.e("RECEIVE", "CANCEL MEETING " + deviceId);

                        MeetingService.cancelMeeting(context, deviceId, false);
                }



                final_message = "";
            }


            // check if message contains CKMT-CRT or CKMT-EDT or CKMT-CNCL

            // create methods for create, edit, and cancel
            // these methods may be inside this receiver or in Utils class

            // in Utils class, create one parser method for create and edit, and
        }
    }
}

