package com.example.administrator.audiorecorder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016-05-16.
 */
public class CustomMessageView extends LinearLayout {
    ImageView emotion;
    TextView text;
    public CustomMessageView(Context context) {
        super(context);
        initView();
    }
    private void initView() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.customview_item, this, false);
        addView(v);
        emotion = (ImageView) findViewById(R.id.ImageView);
        text = (TextView) findViewById(R.id.MessageView);
    }
    void setEmotion(int symbol_resID) {
        emotion.setImageResource(symbol_resID);
    }
    void setText(String text_string) {
        text.setText(text_string);
    }
}