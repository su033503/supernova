package com.example.administrator.audiorecorder;

/**
 * Created by Administrator on 2016-03-26.
 */
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import javaFlacEncoder.FLACOutputStream;
import javaFlacEncoder.FLAC_FileEncoder;

/**
 * Created by HP on 2016-03-22.
 */
public class RecordService extends Service {    //통화 녹음 서비스
    public static final String EXTRA_CALL_NUMBER = "call_number";
    protected View rootView;
    MyVoicePlayer mvp = new MyVoicePlayer();    //Mediarecorder를 이용한 녹음, 녹음포맷: mp4
    audioRecorder arc = new audioRecorder();    //Audiorecord를 이용한 녹음, 녹음포맷: 16bit single pcm
    int fixedSize;

    @InjectView(R.id.tv_call_number)
    TextView tv_call_number;
    String call_number="";

    WindowManager.LayoutParams params;
    private WindowManager windowManager;


    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize( size );
        fixedSize = (int) (size.x * 0.125);   //최초 크기조절.


        params = new WindowManager.LayoutParams(
                0,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT);
        params.width = fixedSize;
        params.height = fixedSize;
        params.gravity = Gravity.TOP;   //위치를 위쪽
        params.x = size.x;               //오른쪽구석에 고정

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        rootView = layoutInflater.inflate(R.layout.stt_dialog, null);
        ButterKnife.inject(this, rootView);
        setDraggable();
    }



    private void setDraggable() {

        rootView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        if (rootView != null)
                            windowManager.updateViewLayout(rootView, params);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        windowManager.addView(rootView, params);
        setExtra(intent);

       // mvp.record();
        arc.startRecording();
        if (!TextUtils.isEmpty(call_number)) {

            tv_call_number.setText(call_number);
        }
        System.out.println("CallNumber Test : "+call_number);

        return START_REDELIVER_INTENT;
    }


    private void setExtra(Intent intent) {

        if (intent == null)
        {
            removePopup();
            return;
        }
        call_number = intent.getStringExtra(EXTRA_CALL_NUMBER);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        removePopup();
    }


    public void removePopup() {
        //mvp._stopRec();
        arc.stopRecording();

        if (rootView != null && windowManager != null) windowManager.removeView(rootView);
    }
    @OnClick(R.id.btn_changeSize)
    public void sizechange() {
        if(params.width==fixedSize && params.height==fixedSize) {
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            params.height = 1000;
            params.width = size.x;
            params.y = 150;
            windowManager.updateViewLayout(rootView, params);
        }
        else
        {
            params.y = 100;
            params.width = fixedSize;
            params.height = fixedSize;
            windowManager.updateViewLayout(rootView, params);
        }
    }
}


