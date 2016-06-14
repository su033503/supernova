package com.example.administrator.audiorecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016-05-12.
 */
public class CallLogDialog extends Activity {
    LinearLayout sttDialog;
    EnvironmentSet environmentSet;
    private static final int RECORDER_SAMPLERATE = 16000;       //16000 HZ 주파수 설정
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_CONFIGURATION_MONO;       //모노
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;          //PCM 16 BIt
    @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            callEnvironmentLog();
            setContentView(R.layout.stt_dialog);
            LinearLayout bg = (LinearLayout)findViewById(R.id.sttlinearlayout);
            ImageView imageView = (ImageView)findViewById(R.id.decibell);
            imageView.setVisibility(View.GONE);
            TextView name = (TextView)findViewById(R.id.name);
            switch (environmentSet.getBgNo()) {
                case 1: bg.setBackgroundResource(R.drawable.background8); name.setTextColor(Color.BLACK); break;
                case 2: bg.setBackgroundResource(R.drawable.background3); name.setTextColor(Color.BLACK); break;
                case 3: bg.setBackgroundResource(R.drawable.background0); name.setTextColor(Color.BLACK);break;
                case 4: bg.setBackgroundResource(R.drawable.background11); name.setTextColor(Color.WHITE); break;
            }
            Intent inIntent = getIntent();
            int position = inIntent.getIntExtra("position", 0);
            System.out.println(position);
            final CallLog clickedCallLog = MainActivity.callLogs.get(position);
            sttDialog = (LinearLayout)findViewById(R.id.line1);

            name.setText(clickedCallLog.getName());
            ImageButton photo = (ImageButton)findViewById(R.id.btn_changeSize);
            photo.setImageBitmap(getPhoneBookPhoto(clickedCallLog.getPhoneNumber()));
            for (int i = 0; i < clickedCallLog.getMessages().size(); i++) {
                System.out.println(i);
                CustomMessageView cv = addMessage(clickedCallLog.getMessages().get(i), clickedCallLog.getEmotions().get(i), i);
                cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            playShortAudioFileViaAudioTrack(clickedCallLog.getPcmFileList().get(v.getId()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                sttDialog.addView(cv);
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
    public CustomMessageView addMessage(String msg, int emotion, int position) {
        CustomMessageView mv = new CustomMessageView(this);
        mv.setText(msg);
        switch(emotion) {
            case 0: mv.setEmotion(R.drawable.angry); break;
            case 1: mv.setEmotion(R.drawable.ohhh); break;
            case 2: mv.setEmotion(R.drawable.happy); break;
            case 3: mv.setEmotion(R.drawable.sad); break;
            case 4: break; //mv.setEmotion(R.drawable.normal);
            case 5: break;
        }
        mv.setId(position);

        return mv;
    }
    private void playShortAudioFileViaAudioTrack(String filePath) throws IOException
    {
        // We keep temporarily filePath globally as we have only two sample sounds now..
        if (filePath==null)
            return;

        //Reading the file..
        byte[] byteData = null;
        File file = null;
        file = new File(filePath); // for ex. path= "/sdcard/samplesound.pcm" or "/sdcard/samplesound.wav"
        byteData = new byte[(int) file.length()];
        FileInputStream in = null;
        try {
            in = new FileInputStream( file );
            in.read(byteData );
            in.close();

        } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Set and push to audio track..
        int intSize = android.media.AudioTrack.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, intSize, AudioTrack.MODE_STREAM);
        if (at!=null) {
            at.play();
        // Write the byte array to the track
            at.write(byteData, 0, byteData.length);
            at.stop();
            at.release();
        }
        else
            Log.d("TCAudio", "audio track is not initialised ");

    }
    public Bitmap getPhoneBookPhoto(String phoneNumber){
        Uri phoneUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Uri photoUri = null;
        ContentResolver cr = this.getContentResolver();
        Cursor contact = cr.query(phoneUri,
                new String[]{ContactsContract.Contacts._ID}, null, null, null);
        if (contact.moveToFirst()) {
            long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
            photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);
        }
        else {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.unknown);
            return defaultPhoto;
        }
        if (photoUri != null) {
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
                    cr, photoUri);
            if (input != null) {
                return BitmapFactory.decodeStream(input);
            }
        } else {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.unknown);
            return defaultPhoto;
        }
        Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.unknown);
        return defaultPhoto;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode) {
             case KeyEvent.KEYCODE_BACK:
                  new AlertDialog.Builder(this).setTitle("통화기록창으로 돌아가기").setMessage("돌아가시겠습니까?").setPositiveButton("예", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int whichButton) {
                          sttDialog.removeAllViews();

                          finish();
                      }
                  })
                  .setNegativeButton("아니오", null).show();
                      return false;
                  default:
                      return false;
        }
    }

}
