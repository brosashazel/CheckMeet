package com.example.checkmeet.sms;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class SMSServiceRECEIVE extends Service {

    private SMSBroadcastReceiverRECEIVE smsReceiver;

    public SMSServiceRECEIVE() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //SMS event receiver
        smsReceiver = new SMSBroadcastReceiverRECEIVE();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, intentFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Unregister the SMS receiver
        unregisterReceiver(smsReceiver);
    }
}
