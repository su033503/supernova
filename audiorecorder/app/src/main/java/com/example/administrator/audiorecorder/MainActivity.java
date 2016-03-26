package com.example.administrator.audiorecorder;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

import javaFlacEncoder.FLAC_FileEncoder;

public class    MainActivity extends AppCompatActivity {

    Button conButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        conButton = (Button)findViewById(R.id.conButton);
        conButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String EXPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String convertPath = EXPath + "/testconvert";
                    FLAC_FileEncoder a = new FLAC_FileEncoder();
                    File input = new File(convertPath, "1.wav");
                    File output = new File(convertPath, "1.flac");
                    a.encode(input, output);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
