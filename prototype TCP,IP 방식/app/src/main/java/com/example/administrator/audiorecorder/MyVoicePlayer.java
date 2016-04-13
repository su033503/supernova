package com.example.administrator.audiorecorder;

/**
 * Created by Administrator on 2016-03-26.
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

// Example
public class MyVoicePlayer {

    private static final String TAG = "mp4 Recorder";

    private static MyVoicePlayer Instance = null;

    private static String filePath = "";

    private static MediaRecorder recorder = null;
    private static MediaPlayer player = null;


    public static MyVoicePlayer GetInstance()
    {
        if ( Instance == null )
        {
            Instance = new MyVoicePlayer();
        }
        return Instance;
    }

    public MyVoicePlayer()
    {
        final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(sdPath+"/callRecord");
        if(!dir.exists())
            dir.mkdir();
        File file = Environment.getExternalStorageDirectory();
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM-dd HH-mm-ss");       //현재시간을 불러와서 파일명으로 지정하기위한 부분
        String time = sdfNow.format(new Date(System.currentTimeMillis()));
        filePath = dir.getAbsolutePath()+"/"+time+".mp4";

    }

    // 재생
    public static void _playRec()
    {
        if ( player != null )
        {
            player.stop();
            player.release();
            player = null;
        }


        try {
            player = new MediaPlayer();
            player.setDataSource(filePath);
            player.prepare();
            player.start();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 녹음 중지
    public static void _stopRec() {
        if ( recorder == null ) {
            return;
        }

        recorder.stop();
        recorder.release();
        recorder = null;
    }

    // 재생 중지
    public static void _stop()
    {
        if ( player == null ) {
            return;
        }

        player.stop();
        player.release();
        player = null;
    }

    // 녹음
    public void record()
    {
        if ( recorder != null ) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(filePath);

        try {
            recorder.prepare();
            recorder.start();
            Log.i(TAG, "recording start");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

