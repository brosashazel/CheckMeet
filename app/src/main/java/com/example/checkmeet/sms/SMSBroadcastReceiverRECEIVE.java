package com.example.checkmeet.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by victo on 4/5/2017.
 */

public class SMSBroadcastReceiverRECEIVE extends BroadcastReceiver {

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
                strMessage += "SMS From: " + messages[i].getOriginatingAddress();
                strMessage += " : ";
                strMessage += messages[i].getMessageBody();
                strMessage += "\n";
            }

            Toast.makeText(context, strMessage, Toast.LENGTH_SHORT).show();

            Log.e("SMSReceiverRECEIVE", strMessage);

            // check if message contains CKMT-CRT or CKMT-EDT or CKMT-CNCL

            // create methods for create, edit, and cancel
            // these methods may be inside this receiver or in Utils class

            // in Utils class, create one parser method for create and edit, and
        }
    }
}

