package com.example.administrator.audiorecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
/**
 * Created by Administrator on 2016-03-26.
 */
public class IncomingCallBroadcastReceiver extends BroadcastReceiver{
    public static final String TAG = "PHONE STATE";
    private static String mLastState;
    String phone_number2;

    private final Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG,"onReceive()");


        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (state.equals(mLastState)) {
            return;

        } else {
            mLastState = state;

        }


        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            final String phone_number = PhoneNumberUtils.formatNumber(incomingNumber);
            phone_number2 = phone_number;
            Intent serviceIntent = new Intent(context, CallingService.class);
            serviceIntent.putExtra(CallingService.EXTRA_CALL_NUMBER, phone_number);
            context.startService(serviceIntent);

        }
        if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Intent serviceIntent = new Intent(context, RecordService.class);
            serviceIntent.putExtra(CallingService.EXTRA_CALL_NUMBER, phone_number2);
            context.startService(serviceIntent);

        }
    }
}
