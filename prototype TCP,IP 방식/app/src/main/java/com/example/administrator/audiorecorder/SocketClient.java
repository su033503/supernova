package com.example.administrator.audiorecorder;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Environment;
import android.util.Log;

/**
 * Created by Administrator on 2016-04-12.
 */
public class SocketClient extends Thread{
    private Socket socket; // 연결소켓
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    public Queue fileNames = new Queue();
    RecordService recordService;
    private static final String ip = "211.244.134.6";     //"192.168.123.104"
    private static final int port = 7777;

    private int len;
    byte[] data = null;
    private String fileName;
    static SocketClient socketClient=null;
    static SocketClient getSocketClient(){
        if(socketClient==null)
            socketClient = new SocketClient();
        return socketClient;
    }
    public SocketClient() {

    }
    public void setRecordService(RecordService recordService){
        Log.w("콜 레코드 서비스 세팅","");
        this.recordService = recordService;
    }
    public void run() {
        Connection();
        while(!socketClient.currentThread().isInterrupted()) {
            try {
                String result = dis.readUTF();
                Log.w("결과값으로 받아온것", result);
                if (result != null) {
                    String filename, Message, Imotion;
                    filename = result.split("/")[0];
                    Message = result.split("/")[1];
                    Imotion = result.split("/")[2];
                    Log.w("결과값 분리", Message);
                    recordService.addResult(Message);
                    recordService.handler.sendEmptyMessage(0);
                }

                } catch (IOException e) {
                    e.printStackTrace();

                }
        }
    }

    public void Connection() {
        try {
            socket = new Socket(ip, port);
            Log.w("소켓통신", "소켓생성");
            if (socket != null) // socket이 null값이 아닐때 즉! 연결되었을때
             {
                 try { // 스트림 설정
                     is = socket.getInputStream();
                     dis = new DataInputStream(is);
                     os = socket.getOutputStream();
                     dos = new DataOutputStream(os);

                 } catch (IOException e) {
                     Log.w("connection", "연결 에러!!");
                 }
             }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void addFile(String fileName){
        Log.w("파일큐","파일 추가 "+fileName);
        fileNames.push(fileName);
    }
    public void sendFile(String fileName) {
        Socket tmpSocket = null;       //파일전송을 위한 임시소켓
        InputStream tmpIs = null;
        OutputStream tmpOs = null;
        DataInputStream tmpDis = null;
        DataOutputStream tmpDos = null;
        try {
            tmpSocket = new Socket(ip,8888);
            if (tmpSocket != null) // socket이 null값이 아닐때 즉! 연결되었을때
            {
                try { // 스트림 설정
                    tmpIs = tmpSocket.getInputStream();
                    tmpDis = new DataInputStream(tmpIs);

                    tmpOs = tmpSocket.getOutputStream();
                    tmpDos = new DataOutputStream(tmpOs);

                } catch (IOException e) {
                    Log.w("임시 소켓 connection", "임시 소켓 연결 에러!!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvoice/" + fileName;  //불러올 파일 (폴더생성시 중간에 입력)

        File file = new File(fileDir);      //파일 open
        FileInputStream fis = null;         //FileinputStream 생성
        try {
            tmpDos.writeUTF(fileName);              //파일이름 전송
            fis = new FileInputStream(file);        //inputStream에 파일을 연결
            int size = fis.available();             //변수에 파일크기 저장
            data = new byte[size];              //파일 크기만큼의 byte배열 생성

            int readsize =  fis.read(data, 0, size);    //파일크기만큼 읽어서 버퍼에 저장
            tmpDos.write(data, 0, size);                   //버퍼를 크기만큼 DataOutputStream에 씀

            Log.w("파일크기", Integer.toString(readsize) + " " + Integer.toString(size) + " " + Integer.toString(dos.size()));
            fis.close();
            tmpDos.close();
            tmpDis.close();
            tmpIs.close();
            tmpOs.close();
            tmpSocket.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void onEnd() {
        Socket tmpSocket = null;       //파일전송을 위한 임시소켓
        InputStream tmpIs = null;
        OutputStream tmpOs = null;
        DataInputStream tmpDis = null;
        DataOutputStream tmpDos = null;
        try {
            tmpSocket = new Socket(ip,9999);
            if (tmpSocket != null) // socket이 null값이 아닐때 즉! 연결되었을때
            {
                try { // 스트림 설정
                    tmpIs = tmpSocket.getInputStream();
                    tmpDis = new DataInputStream(tmpIs);

                    tmpOs = tmpSocket.getOutputStream();
                    tmpDos = new DataOutputStream(tmpOs);

                } catch (IOException e) {
                    Log.w("임시 소켓 connection", "임시 소켓 연결 에러!!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            tmpDos.writeUTF("ENDCONN");
            Log.w("종료메시지 전송","완료");
            tmpDos.close();
            tmpDis.close();
            tmpIs.close();
            tmpOs.close();
            tmpSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    protected void onStop() {
        // TODO Auto-generated method stub
        onEnd();
        try {
            socketClient.interrupt();
            is.close();
            os.close();
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
