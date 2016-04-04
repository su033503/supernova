package supernova.com.callhelper;


import android.widget.EditText;

/**
 * Created by inter on 2016-04-01.
 */
public class SpeechManager extends Thread{

    Queue fileNames = new Queue();
    SpeechServerConnection[] ssc = new SpeechServerConnection[3];   //3개까지 한번에 쓰레드로 돌림
    int runningThread = 0;
    String totalReselt="";
    boolean isContinue = true;
    MainActivity mainActivity;

    public SpeechManager(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public void addFile(String fileName){
        fileNames.push(fileName);
    }

    public void run(){
        while(true){
            if(!fileNames.empty()){//fileName queue가 비어있지 않을때
                if(runningThread<3) {
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
                                mainActivity.addResult("번역을 할 수 없습니다.\n");
                                ssc[0] = ssc[1];
                                ssc[1] = ssc[2];
                                ssc[2] = null;
                                runningThread--;
                                isContinue = true;
                                mainActivity.handler.sendEmptyMessage(0);
                            }
                        } else {
                            totalReselt += ssc[0].getResult();
                            mainActivity.addResult(ssc[0].getResult());
                            ssc[0] = ssc[1];
                            ssc[1] = ssc[2];
                            ssc[2] = null;
                            runningThread--;
                            isContinue = true;
                            //mainActivity.setTotalText(totalReselt+"\n");
                            mainActivity.handler.sendEmptyMessage(0);
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
