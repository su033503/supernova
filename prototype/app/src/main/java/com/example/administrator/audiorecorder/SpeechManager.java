package com.example.administrator.audiorecorder;


import android.util.Log;

/**
 * Created by inter on 2016-04-01.
 */
public class SpeechManager extends Thread{

    Queue fileNames = new Queue();
    SpeechServerConnection[] ssc = new SpeechServerConnection[3];   //3개까지 한번에 쓰레드로 돌림
    int runningThread = 0;
    String totalReselt="";
    boolean isContinue = true;
    RecordService recordService;

    static SpeechManager speechManager=null;
    static SpeechManager getSpeechManager(){
        if(speechManager==null)
            speechManager = new SpeechManager();
        return speechManager;
    }

    public SpeechManager(){}

    public void setRecordService(RecordService recordService){
        Log.w("콜 레코드 서비스 세팅","");
        this.recordService = recordService;
    }

    public void addFile(String fileName){
        Log.w("콜 파일받음","파일 잘 받았음");
        fileNames.push(fileName);
    }

    public void run(){
        while(true){
            if(!fileNames.empty()){//fileName queue가 비어있지 않을때
                if(runningThread<3) {
                    Log.w("콜 스레드 동작중","스피치 커넥션 연결"+fileNames.peek());
                    SpeechServerConnection temp = new SpeechServerConnection(fileNames.pop());
                    ssc[runningThread] = temp;
                    ssc[runningThread].start();
                    runningThread++;
                }
            }
            if(runningThread>0 && ssc[0]!=null){
                while(isContinue) {
                    isContinue = false;
                    if (ssc[0].isDone()) {
                        if (ssc[0].isError()) {
                            if (ssc[0].getErrcount() < 3) {
                                ssc[0].init();
                                ssc[0].start();
                            } else {
                                totalReselt += "번역을 할 수 없습니다.\n";
                               // Activity.addResult("번역을 할 수 없습니다.\n");/////////////////////////////////////////////////////
                                recordService.addResult("번역을 할 수 없습니다.");
                                ssc[0] = ssc[1];
                                ssc[1] = ssc[2];
                                ssc[2] = null;
                                runningThread--;
                                isContinue = true;
                               // Activity.handler.sendEmptyMessage(0);////////////////////////////////////////////////////////
                                recordService.handler.sendEmptyMessage(0);
                            }
                        } else {
                            totalReselt += ssc[0].getResult();
                            //Activity.addResult(ssc[0].getResult());///////////////////////////////////////////////////////////
                            recordService.addResult(ssc[0].getResult());
                            Log.w("콜 리절트 나옴",ssc[0].getResult());
                            ssc[0] = ssc[1];
                            ssc[1] = ssc[2];
                            ssc[2] = null;
                            runningThread--;
                            isContinue = true;
                            //mainActivity.setTotalText(totalReselt+"\n");
                          //  Activity.handler.sendEmptyMessage(0);///////////////////////////////////////////////////////////////////////
                            recordService.handler.sendEmptyMessage(0);
                        }
                    }
                    if(ssc[0]==null) isContinue = false;
                }
                isContinue = true;
            }
            try {
                sleep(500);//한바퀴에 0.5초씩 쉼
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
