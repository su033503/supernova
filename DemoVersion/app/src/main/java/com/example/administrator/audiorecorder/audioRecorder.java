package com.example.administrator.audiorecorder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

/**
 * Created by Administrator on 2016-03-26.
 */
public class audioRecorder {        //오디오 레코더
    private static final int RECORDER_SAMPLERATE = 16000;       //16000 HZ 주파수 설정
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;       //모노
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;          //PCM 16 BIt
    private AudioRecord recorder = null;        //사용할 오디오레코더 객체 recorder
//    private AudioRecord recorder2 = null;
    private Thread recordingThread = null;      //쓰레드사용
    private boolean isRecording = false;        //녹음중을 나타내는 boolean
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    RecordService Recorder;
    CallLog calllog;
//        FileOutputStream downLink;
//        FileOutputStream upLink;
//        FileOutputStream mixedFile;
//        String filePathUp;
//        String filePathDown;
//        String filePathMixed;
    //<윤동희>
    SocketClient soc;
    private RealDoubleFFT transformer;

    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    //SpeechManager speechManager = SpeechManager.getSpeechManager();
    //</윤동희>

    public audioRecorder(CallLog cl, View rootView,RecordService recorder) {
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        calllog = cl;
        transformer = new RealDoubleFFT(160);
        Recorder = recorder;
        imageView = (ImageView) rootView.findViewById(R.id.decibell);
        bitmap = Bitmap.createBitmap((int) 160, (int) 49,
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        imageView.setImageBitmap(bitmap);
    }
    public AudioRecord getRecorder() {
        System.out.println(recorder.getAudioSessionId());
        return recorder;
    }
    public void setSocket(SocketClient socket) {
        soc = socket;
    }
    public void startRecording() {      //녹음 시작하는 메소드

        //<윤동희>
        Log.w("콜 오디오레코더", "speechManager start()");
        soc.start();
        //</윤동희>

        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_DOWNLINK,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);     //레코더 생성
//        recorder2 = new AudioRecord(MediaRecorder.AudioSource.VOICE_UPLINK,
//                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
//                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
//        final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();      //파일경로지정하는 부분
//        File dir = new File(sdPath+"/myvoice");
//        if(!dir.exists())
//            dir.mkdir();

//        filePathUp = dir.getAbsolutePath()+"/"+"Up.pcm";        //파일이름 생성.
//        filePathDown = dir.getAbsolutePath()+"/"+"Down.pcm";
//        filePathMixed = dir.getAbsolutePath()+"/"+"mixed.pcm";
//        upLink = null;
//        downLink = null;
//        mixedFile = null;
//        try {
//            upLink = new FileOutputStream(filePathUp);
//            downLink = new FileOutputStream(filePathDown);
//            mixedFile =  new FileOutputStream(filePathMixed);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        recorder.startRecording();      //레코딩시작
//        recorder2.startRecording();
        isRecording = true;         //녹음중을 체크하기 위해서 true로 변경
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");     //쓰레드생성 계속해서 writeAudioDataToFile() 메소드를 실행. 녹음데이터를 파일에 쓰는 메소드.
        recordingThread.start();        //쓰레드 시작
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void writeAudioDataToFile() {       //녹음데이터를 파일에 쓰는 메소드
        int infinityCount = 0;          //녹음 데시벨 측정시 입력이 없으면 -infinity가 나옴. infinity가 몇번나오는지 체크
        int recordCount = 0;            //while문을 몇번 루프돌았는지 체크. 이 변수와 infinityOccur를 통해서 infinity의 연속성을 체크
        int infinityOccur = 0;          //infinity가 발생한 순간의 recordCount값을 기록. 다음번 루프시 infinity가 연속인지 아닌지 체크하기위함.
        int recordTime = 0;
        // Write the output audio in byte
        final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();      //파일경로지정하는 부분
        File dir = new File(sdPath+"/myvoice");
        if(!dir.exists())
            dir.mkdir();
        File file = Environment.getExternalStorageDirectory();

        SimpleDateFormat sdfNow = new SimpleDateFormat("MM-dd HH-mm-ss");       //현재시간을 불러와서 파일명으로 지정하기위한 부분
        String time = sdfNow.format(new Date(System.currentTimeMillis()));

        String filePath = dir.getAbsolutePath()+"/"+time+".pcm";        //파일이름 생성.
        short sData[] = new short[BufferElements2Rec];          //오디오데이터가 들어올 버퍼생성
//        short sData2[] = new short[BufferElements2Rec];

        FileOutputStream os = null;         //fileoutputstream. 바로위에서 생성한 파일을 연결
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        outerLoop:      //바깥쪽 while문 라벨지정.
        while (isRecording) {           //while문루프. 녹음중이면 계속해서 바이트단위로 데이터를 읽어와서 os에 쓴다.
            // gets the voice output from microphone to byte format
            recordCount++;                  //while문 몇번째 돌고있는지
            int bufferRead = recorder.read(sData, 0, BufferElements2Rec);        //recorder로 녹음한 데이터를 읽어와 sData버퍼에 저장한다.
//            recorder2.read(sData2, 0 , BufferElements2Rec);
//            byte bDataDown[] = short2byte(sData);
//            byte bDataUp[] = short2byte(sData2);
//            try {
//                downLink.write(bDataDown, 0, BufferElements2Rec * BytesPerElement);
//                upLink.write(bDataUp, 0, BufferElements2Rec * BytesPerElement);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            int amplitude = (sData[0] & 0xff) << 8 | sData[1];
            double[] toTransform = new double[160];
            for (int i = 0; i < 160 && i < bufferRead; i++) {
                toTransform[i] = (double) sData[i] / 32768.0; // signed
                // 16
            }                                       // bit
            transformer.ft(toTransform);
            onProgressUpdate(toTransform);
            // Determine amplitude
            double amplitudeDb = 20 * Math
                    .log10((double)Math.abs(amplitude) / 32768);        //데시벨 측정하기 위한 수식. amplitudeDb 가 데시벨
            String dbString = String.valueOf(amplitudeDb);              //데시벨을 로그에 기록하여 테스트를 진행하기 위해서 String데이터로 변경
            Log.d("Snore DB", "dB " + dbString);                      //로그에 기록.

            if(!dbString.equals("-Infinity")){      //-infinity가 아니라면 즉 의미있는 데시벨 입력이 있다면 변수들 초기화.
                Log.w("의미있는 데시벨발생",dbString);
                infinityCount = 1;      //count는 1로.
                infinityOccur = 0;      //발생시점은 0으로 초기화.
            }
            if(infinityCount == 0) {        //녹음시작후 의미있는 데시벨이 한번도 들어오지않았다면 infinityCount는 0이므로 while문 처음으로 continue
                continue;
            }
            try {           //os에 데이터를 쓴다.
                // // writes the data to file from buffer
                // // stores the voice buffer
                //1번 쓸때마다 2kb의 파일크기 = 0.064초 짜리 데이터
                //156번 쓰면 약 10초짜리 파일생성.
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
 //               soundMix s = new soundMix(bData,bData,bData,100);
                recordTime ++;
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(dbString.equals("-Infinity") &&(infinityCount == 1)&&(infinityOccur == 0)) {  //-infinity가 발생했는데 이전루틴에서 의미있는 db입력이 있었다면. infinity의 연속성을 체크하여 끊을 지점을 알아낸다.
                infinityCount = 2;                                                            //사전에 의미있는 db입력이 없었다면 infinityCount가 0이므로 이 if문에 들어오지 않는다.
                infinityOccur = recordCount;        //count를 2로, 현재시점을 occur변수에 기록한다.
            }
            if((infinityCount > 1)&&(infinityOccur == recordCount-1)) {         //infinityCount가 2이상 = 의미있는 db입력이 있었고 그 뒤로 -infinity가 발생한 상태. 이면서 동시에 occur가 바로 이전 루틴의 recordcount인 경우 연속해서 발생한것이됨.
                infinityCount++;        //count증가
                infinityOccur = recordCount;        //현재시점을 기록
            }
            if(infinityCount == 3) {    //count가 5인경우. 즉 4번 -infinity가 발생하면
                if(recordTime < 15)        //현재 레코드타임이 156번 미만. 즉 현재 녹음데이터의 길이가 10초가 안될경우 처음으로 돌아가서 계속 녹음하도록 함
                {
                    System.out.println("45미만"+recordTime);
                    infinityCount = 0;      //infinityCount만 0으로 초기화해주면 문장과 문장사이의 긴 침묵은 사라진채로 녹음될것임.
                    continue outerLoop;     //바깥쪽루틴으로 한번에 돌아가도록 지정된 while 라벨이름을 언급.
                }
                break;          //데이터를 쓰는 while루틴에서 벗어남.
            }

        }
        try {
            os.close();         //벗어남과 동시에 os를 닫아줌.
            File file1 = new File(filePath);
            if(recordTime == 0)
                file1.delete();
            else {
                Log.w("콜 오디오레코더","파일 전달"+time+".pcm");
                soc.addFile(time + ".pcm");
                Log.w("팝업 파일네임", soc.fileNames.peek());
                calllog.addPcmFile(filePath);
                soc.sendFile(soc.fileNames.pop());

            }
            if(isRecording == true)     //isRecording이 true이면서 여기에 도달했다는 것은 연속성체크에 걸려서 나온것일뿐 녹음은 현재진행중이므로 다시 writeAudioDataToFile()메소드를 실행. 즉 이 메소드의 처음으로 돌아감.
                writeAudioDataToFile();     //처음으로 돌아가면 새로운 파일명을 가지고 os객체를 만들어 데이터를 기록하게됨.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void onProgressUpdate(double[]... toTransform) {

        canvas.drawARGB(20, 255, 255, 255);
//        canvas.drawColor(getgetColor(R.color.decibelBg));

        for (int i = 0; i < toTransform[0].length; i++) {
            int x = i;
            int downy =0;
            if(i<=20) {
                downy = (int) (24 - (toTransform[0][159-i] * 6));
            }
            else {
                downy = (int) (24 - (toTransform[0][i-21] * 6));
            }
            int upy = 24;
            canvas.drawLine(x, downy, x, upy, paint);
        }

        Recorder.handler.sendEmptyMessage(1);

        // TODO Auto-generated method stub
        // super.onProgressUpdate(values);
    }
//    private void mixFiles(){
//        System.out.println("파일합성시작");
//        try {
//            File up = new File(filePathUp);
//            File down = new File(filePathDown);
//            FileInputStream upFis = null;
//            FileInputStream downFis = null;
//            upFis = new FileInputStream(up);
//            downFis = new FileInputStream(down);
//            int upsize = upFis.available();
//            int downsize = downFis.available();
//
//            byte[] upData = null;
//            byte[] downData = null;
//            upData = new byte[upsize];
//            downData = new byte[downsize];
//
//            upFis.read(upData,0,upsize);
//            downFis.read(downData,0,downsize);
//
//            short[] upShorts = new short[upData.length/2];
//            short[] downShorts = new short[downData.length/2];
//            ByteBuffer.wrap(upData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(upShorts);
//            ByteBuffer.wrap(downData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(downShorts);
//
//            short[] output = new short[upShorts.length];
//
//            for(int i=0; i < output.length; i++){
//
//                float samplef1 = upShorts[i] / 32768.0f;
//                float samplef2 = downShorts[i] / 32768.0f;
//                float mixed = samplef1 + samplef2;
//                // reduce the volume a bit:
//                mixed *= 0.8;
//                // hard clipping
//                if (mixed > 1.0f) mixed = 1.0f;
//                if (mixed < -1.0f) mixed = -1.0f;
//                short outputSample = (short)(mixed * 32768.0f);
//                output[i] = outputSample;
//            }
//            byte[] mixed = new byte[output.length * 2];;
//            ByteBuffer.wrap(mixed).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(output);
//            mixedFile.write(mixed,0,mixed.length);
//            mixedFile.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("파일합성완료");
//    }
    public void stopRecording() {       //녹음중지 메소드
        // stops the recording activity
//        try {
//            upLink.close();
//            downLink.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mixFiles();
        System.out.println("stopRecord");
        if (null != recorder) {
            isRecording = false;        //녹음중상태를 false로 변경 writeAudioDatatofile()메소드 실행을 그만하게됨.
            recorder.stop();            //recorder 정지
            recorder.release();         //recorder 객체 해제.
            recorder = null;            //recorder 객체 초기화
//            recorder2.stop();            //recorder 정지
//            recorder2.release();         //recorder 객체 해제.
//            recorder2 = null;            //recorder 객체 초기화
            recordingThread = null;     //레코딩쓰레드 초기화.
        }
    }

}
