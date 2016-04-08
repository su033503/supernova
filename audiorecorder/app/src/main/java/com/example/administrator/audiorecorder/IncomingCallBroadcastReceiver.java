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

public class IncomingCallBroadcastReceiver extends BroadcastReceiver{   //전화상태 변화에 따른 서비스를 실행하는 브로드캐스트 리시버
    public static final String TAG = "PHONE STATE";
    private static String mLastState;
    String phone_number2 ="";
    private final Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceive()");
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Intent ringIntent = new Intent(context, CallingService.class);
        Intent callIntent = new Intent(context, RecordService.class);


        if (state.equals(mLastState)) {
            return;
        }
        else {
            mLastState = state;
        }
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state))      //전화벨이 울릴 때
        {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            final String phone_number = PhoneNumberUtils.formatNumber(incomingNumber);
            phone_number2 = phone_number;
//            ringIntent.putExtra(CallingService.EXTRA_CALL_NUMBER, phone_number);
//            context.startService(ringIntent);
        }
        if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state))     //통화 중 상태
        {
            callIntent.putExtra(RecordService.EXTRA_CALL_NUMBER, phone_number2);
            context.startService(callIntent);
        }
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(state))    //통화를 종료했을 때
        {
            context.stopService(callIntent);
        }
        else if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))     //전화를 걸 때
        {

        }
    }
}
