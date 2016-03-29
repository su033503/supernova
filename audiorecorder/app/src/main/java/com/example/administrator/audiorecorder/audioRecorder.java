package com.example.administrator.audiorecorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016-03-26.
 */
public class audioRecorder {
    private static final int RECORDER_SAMPLERATE = 22050;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format

    public audioRecorder() {
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
    }

    public void startRecording() {

        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_DOWNLINK,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
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

    private void writeAudioDataToFile() {
        int infinityCount = 0;
        int recordCount = 0;
        int infinityOccur = 0;
        // Write the output audio in byte
        final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(sdPath+"/myvoice");
        if(!dir.exists())
            dir.mkdir();
        File file = Environment.getExternalStorageDirectory();

        SimpleDateFormat sdfNow = new SimpleDateFormat("MM-dd HH:mm:ss");
        String time = sdfNow.format(new Date(System.currentTimeMillis()));

        String filePath = dir.getAbsolutePath()+"/"+time+".pcm";
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format
            recordCount++;
            recorder.read(sData, 0, BufferElements2Rec);
            int amplitude = (sData[0] & 0xff) << 8 | sData[1];

            // Determine amplitude
            double amplitudeDb = 20 * Math
                    .log10((double)Math.abs(amplitude) / 32768);
            String dbString = String.valueOf(amplitudeDb);
            Log.d("Snore DB", "dB " + dbString);
            System.out.println("Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!dbString.equals("-Infinity")){
                infinityCount = 1;
                infinityOccur = 0;
            }
            if(dbString.equals("-Infinity")&&(infinityCount == 1)&&(infinityOccur == 0)) {
                infinityCount = 2;
                infinityOccur = recordCount;
            }
            if((infinityCount > 1)&&(infinityOccur == recordCount-1)) {
                infinityCount++;
                infinityOccur = recordCount;
            }
            if(infinityCount == 5) {
                break;
            }
        }
        try {
            os.close();
            if(isRecording == true)
                writeAudioDataToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }

}
