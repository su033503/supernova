package com.example.administrator.audiorecorder;

/**
 * Created by Administrator on 2016-03-26.
 */
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    public static final String END_COMMAND="";
    public CallLog calllog;
    protected View rootView;
    EnvironmentSet environmentSet;
    String displayName = "";
    audioRecorder arc;
    int fixedSize;

    //<윤동희> 변수 추가
    ImageButton btnSize;
    LinearLayout line1;
    ScrollView sv;
    int msgNum=0;
    ArrayList<String> results = new ArrayList<String>();
    ArrayList<Integer> emotions = new ArrayList<Integer>();
    SocketClient soc = new SocketClient();
//    SocketClient soc = SocketClient.getSocketClient();
    //SpeechManager speechManager = SpeechManager.getSpeechManager();
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {


            if(msg.what==0){
                addMessage(results.get(msgNum),emotions.get(msgNum));
                msgNum++;
            }
            if(msg.what==1) {
                ImageView imageView = (ImageView)rootView.findViewById(R.id.decibell);
                imageView.invalidate();
            }
        }
    };
    //</윤동희>

    //<윤동희> 메소드 추가
    public void addResult(String result,String emotion){
        results.add(result);
        emotions.add(Integer.parseInt(emotion));
    }

    public void addMessage(String msg, int emotion){
//        TextView tv = new TextView(this);
//        tv.setText(msg);
//        tv.setBackgroundResource(R.drawable.talkbox);
//        tv.setTextColor(Color.BLACK);
//        tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.setMargins(5, 5, 5, 5);
//        tv.setTextSize(20);
//        tv.setLayoutParams(params);
//        line1.addView(tv);
        CustomMessageView mv = new CustomMessageView(this);
        mv.setText(msg);
        if(msg.equals("@ERROR"))
        {
            calllog.errorMessage();
        }
        else {
            calllog.addMessages(msg);
            if (!environmentSet.getEmotionPlay())
                emotion = 5;
            calllog.addEmotion(emotion);
            switch (emotion) {
                case 0:
                    mv.setEmotion(R.drawable.angry);
                    break;
                case 1:
                    mv.setEmotion(R.drawable.ohhh);
                    break;
                case 2:
                    mv.setEmotion(R.drawable.happy);
                    break;
                case 3:
                    mv.setEmotion(R.drawable.sad);
                    break;
                case 4:
                    break;         //mv.setEmotion(R.drawable.normal);
                case 5:
                    break;
            }
            line1.addView(mv);
            sv.fullScroll(ScrollView.FOCUS_DOWN);
        }
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

        calllog = new CallLog();

        Log.w("콜 레코드서비스", "onCreate()");

        callEnvironmentLog();

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
        LinearLayout bg = (LinearLayout)rootView.findViewById(R.id.sttlinearlayout);
        switch (environmentSet.getBgNo()) {
            case 1: bg.setBackgroundResource(R.drawable.background8); break;
            case 2: bg.setBackgroundResource(R.drawable.background3); break;
            case 3: bg.setBackgroundResource(R.drawable.background0); break;
            case 4: bg.setBackgroundResource(R.drawable.background11); break;
        }

        arc = new audioRecorder(calllog,rootView,this);    //Audiorecord를 이용한 녹음, 녹음포맷: 16bit single pcm


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
        arc.setSocket(soc);

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
//        arc.execute();
//        if (!TextUtils.isEmpty(call_number)) {
//
//            tv_call_number.setText(call_number);
//        }
        System.out.println("CallNumber Test : " + call_number);
        getPhoneName(call_number);
        return START_REDELIVER_INTENT;
    }

    private void getPhoneName(String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME
        };

        displayName = phoneNumber;
        String photoFilePath ="";
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()){
                displayName = cursor.getString(0);
            }
            cursor.close();
        }

        System.out.println("전화번호부 이름검색" + displayName);
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM-dd HH-mm-ss");       //현재시간을 불러와서 파일명으로 지정하기위한 부분
        String time = sdfNow.format(new Date(System.currentTimeMillis()));
        System.out.println(time);
        calllog.setName(displayName);
        calllog.setTime(time);
        calllog.setPhoneNumber(phoneNumber);
//        TextView nameview = (TextView)rootView.findViewById(R.id.name);
//        nameview.setText(displayName);
    }
    private void setExtra(Intent intent) {

        if (intent == null)
        {
            removePopup();
            return;
        }
        call_number = intent.getStringExtra("EXTRA_CALL_NUMBER");
        System.out.println("넘어온 전화번호 " + call_number);

    }



    public void onDestroy() {
        super.onDestroy();
        Log.w("stop service2", "서비스 종료");

        removePopup();
        soc.onStop();

    }


    public void removePopup() {
        //mvp._stopRec();
        arc.stopRecording();
//        arc.cancel(true);
        System.out.println("remove popup");
        MainActivity.talkLayouts.add(line1);
        if (rootView != null && windowManager != null) {
            line1.removeAllViews();
            windowManager.removeView(rootView);
        }
        if(environmentSet.getSaveLog()) {
            MainActivity.callLogs.add(calllog); //배열에 calllog클래스 추가
            saveLog();
        }
    }
    public void saveLog() {
        final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(sdPath+"/myvoice");
        if(!dir.exists())
            dir.mkdir();
        String filePath = dir.getAbsolutePath()+"/"+"Log.dat";
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(filePath));
            oos.writeObject(MainActivity.callLogs);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
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


