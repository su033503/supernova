package com.example.administrator.audiorecorder;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016-05-11.
 */
public class CallLog implements Serializable {
    String time;
    String name;
    String phonenumber;
    ArrayList<String> pcmFileList = new ArrayList<String>();
    ArrayList<String> messages = new ArrayList<String>();
    ArrayList<Integer> emotions = new ArrayList<Integer>();

    public ArrayList<Integer> getEmotions() { return emotions; }
    public ArrayList<String> getMessages() {
        return messages;
    }
    public ArrayList<String> getPcmFileList() {
        return pcmFileList;
    }
    public String getTime() {
        return time;
    }
    public String getName() {
        return name;
    }
    public String getPhoneNumber() { return phonenumber; }
    public void addMessages(String m) {
        messages.add(m);
    }
    public void addPcmFile(String p) {
        pcmFileList.add(p);
    }
    public void addEmotion(int e) {
        emotions.add(e);
    }
    public void setTime(String t) {
        time = t;
    }
    public void setName(String n) { name = n;  }
    public void setPhoneNumber(String p) { phonenumber = p;  }
    public void errorMessage() {
        int size = pcmFileList.size();
        pcmFileList.remove(size-1);
    }
}
