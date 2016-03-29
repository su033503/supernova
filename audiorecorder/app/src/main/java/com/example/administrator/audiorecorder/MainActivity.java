package com.example.administrator.audiorecorder;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;

import javaFlacEncoder.FLAC_FileEncoder;
import android.util.Log;

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

                    View viewRoot = getWindow().getDecorView().getRootView();
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) viewRoot.getLayoutParams();
                    if(params==null)
                    {
                        System.out.print("error");
                        return;
                    }

                    params.height = 1000;
                    params.gravity = Gravity.TOP;

                    ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).updateViewLayout(viewRoot,params);

//                    String EXPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//                    String convertPath = EXPath + "/testconvert";
//                    FLAC_FileEncoder a = new FLAC_FileEncoder();
//                    File input = new File(convertPath, "1.wav");
//                    File output = new File(convertPath, "1.flac");
//                    a.encode(input, output);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
