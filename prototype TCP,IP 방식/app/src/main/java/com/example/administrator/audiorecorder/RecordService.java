package com.example.administrator.audiorecorder;

/**
 * Created by Administrator on 2016-03-26.
 */
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

    //<윤동희> 변수 추가
    ImageButton btnSize;
    LinearLayout line1;
    ScrollView sv;
    int msgNum=0;
    ArrayList<String> results = new ArrayList<String>();
    SocketClient soc = SocketClient.getSocketClient();
    //SpeechManager speechManager = SpeechManager.getSpeechManager();
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            Toast.makeText(rootView.getContext(), "111"+results.get(0), Toast.LENGTH_SHORT).show();
            Log.w("콜 토스트", "토스트 갔음ㅋ");
            if(msg.what==0){
                addMessage(results.get(msgNum));
                msgNum++;
            }
        }
    };
    //</윤동희>

    //<윤동희> 메소드 추가
    public void addResult(String result){
        results.add(result);
    }

    public void addMessage(String msg){
        TextView tv = new TextView(this);
        tv.setText(msg);
        tv.setBackgroundColor(Color.YELLOW);
        tv.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);
        tv.setTextSize(20);
        tv.setLayoutParams(params);
        line1.addView(tv);
        sv.fullScroll(ScrollView.FOCUS_DOWN);
    }
    //</윤동희>

    //@InjectView(R.id.tv_call_number)
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

        Log.w("콜 레코드서비스","onCreate()");

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
        //ButterKnife.inject(this, rootView);
        setDraggable();
        sv = (ScrollView)rootView.findViewById(R.id.scrollView);
        //<윤동희>
        btnSize = (ImageButton)rootView.findViewById(R.id.btn_changeSize);
        btnSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
        tv_call_number = (TextView)rootView.findViewById(R.id.tv_call_number);
        Log.w("콜 레코드서비스","onCreate() 윤동희 추가 코드");
        line1 = (LinearLayout)rootView.findViewById(R.id.line1);
        //speechManager.setRecordService(this);
        soc.setRecordService(this);
        //</윤동희>
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

        Log.w("콜 레코드서비스", "onStartCommand()");

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
        soc.onStop();
    }


    public void removePopup() {
        //mvp._stopRec();
        arc.stopRecording();
        System.out.println("remove popup");
        if (rootView != null && windowManager != null) windowManager.removeView(rootView);
    }
    /*
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
    }*/
}


