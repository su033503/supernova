package com.example.administrator.audiorecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Administrator on 2016-03-26.
 */

public class IncomingCallBroadcastReceiver extends BroadcastReceiver{   //전화상태 변화에 따른 서비스를 실행하는 브로드캐스트 리시버
    public static final String TAG = "PHONE STATE";
    private static String mLastState = null;
    static String phone_number2 ="";
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    public static boolean autoRun = true;
    EnvironmentSet environmentSet = new EnvironmentSet(true,true,true,1);
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceive()");
        callEnvironmentLog();
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        //Intent ringIntent = new Intent(context, CallingService.class);
        Intent callIntent = new Intent(context, RecordService.class);
        if(!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            System.out.println(state);
            if (state.equals(mLastState)) {
                return;
            } else {
                mLastState = state;
            }
        }
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state))      //전화벨이 울릴 때
        {
            System.out.println("autorun값 "+environmentSet.getAutoPlay());
            if(environmentSet.getAutoPlay()) {
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                final String phone_number = PhoneNumberUtils.formatNumber(incomingNumber);
                phone_number2 = phone_number;
            }
//            ringIntent.putExtra(CallingService.EXTRA_CALL_NUMBER, phone_number);
//            context.startService(ringIntent);
        }
        if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state))     //통화 중 상태
        {
            if(environmentSet.getAutoPlay()) {
                System.out.println("전화번호"+phone_number2);
                callIntent.putExtra("EXTRA_CALL_NUMBER", phone_number2);
                context.startService(callIntent);
            }
        }
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(state))    //통화를 종료했을 때
        {
            if(environmentSet.getAutoPlay()) {
                Log.w("stop service", "서비스 종료");
                context.stopService(callIntent);
            }
        }
        else if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))     //전화를 걸 때
        {
            System.out.println("autorun값 " + environmentSet.getAutoPlay());
            if(environmentSet.getAutoPlay()) {
                String outgoingNumber = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
                final String phone_number = PhoneNumberUtils.formatNumber(outgoingNumber);
                phone_number2 = phone_number;
            }
        }
    }
    public void callEnvironmentLog() {
        final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(sdPath+"/myvoice");
        if(!dir.exists())
            dir.mkdir();
        String filePath = dir.getAbsolutePath()+"/"+"Environment.dat";
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream( new FileInputStream(filePath));
            environmentSet = (EnvironmentSet)ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("환경설정 파일이 없습니다.");
        }
    }
}
