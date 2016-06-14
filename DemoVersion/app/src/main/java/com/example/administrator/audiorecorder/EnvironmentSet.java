package com.example.administrator.audiorecorder;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-05-19.
 */
public class EnvironmentSet implements Serializable {
    boolean autoPlay;
    boolean emotionPlay;
    boolean saveLog;
    int settedBgNo;
    public EnvironmentSet(Boolean autoplay, Boolean emotionplay, Boolean savelog, int settedbgno) {
        autoPlay = autoplay;
        emotionPlay = emotionplay;
        saveLog = savelog;
        settedBgNo = settedbgno;
    }
    public void setAutoPlay(Boolean b) {
        autoPlay = b;
    }
    public void setEmotionPlay(Boolean b) {
        emotionPlay = b;
    }
    public void setSaveLog(Boolean b) {
        saveLog = b;
    }
    public void setBgNo(int n) {
        settedBgNo = n;
    }
    public Boolean getAutoPlay() {
        return autoPlay;
    }
    public Boolean getEmotionPlay() {
        return emotionPlay;
    }
    public Boolean getSaveLog() {
        return  saveLog;
    }
    public int getBgNo() {
        return settedBgNo;
    }
}
