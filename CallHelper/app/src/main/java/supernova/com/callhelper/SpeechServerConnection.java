package supernova.com.callhelper;

import android.os.Environment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by inter on 2016-04-01.
 */
public class SpeechServerConnection extends Thread{
    private String fileName;    //불러올 파일이름

    private boolean isDone = false; //번역 작업이 끝났는지 저장
    private int responseCode = -1;  //http통신 후 결과코드 (200 정상 그외 비정상)
    private boolean isError = false;    //통신 결과가 정상인지 오류인지 저장
    private String result = ""; //번역 결과를 저장
    private int errcount = 0;
    private boolean isTakeResult = false;   //결과를 가져갔는지 저장

    private String speechApiURL = "https://www.google.com/speech-api/v2/recognize"; //구글 스피치 api서버 URL
    private String apiKey = "AIzaSyD5BeKYdaDtAkC2it8FstBP2ifNA6fFrv8";      //스피치 api 키
    private String lang = "ko-kr";  //사용할 언어(한글 : ko-kr, 영문 : en-us, etc.)
    private String conURL = speechApiURL+"?output=json&lang="+lang+"&key="+apiKey;  //사용 언어와 api키를 합친 커넥션용 URL


    public SpeechServerConnection(String fileName){
        this.fileName = fileName;
    }

    public void init(){  //객체 초기화 초기화 성공시 true, 실패시 false 리턴
        isDone = isError = isTakeResult = false;
        result="";
        errcount++;
    }

    public void run(){

        byte[] data = null;
        OutputStream out = null;

        Scanner inStream = null;

        try{    //http통신용 try문
            URL url = new URL(conURL);  //커넥션용 URL이 저장된 String으로 URL 객체 생성
            URLConnection con = url.openConnection();   //URL을 이용해 커넥션 생성
            HttpsURLConnection httpconn = (HttpsURLConnection)con;  //URL을 이용해 커넥션 생성
            httpconn.setRequestMethod("POST");  //post메서드를 이용해 http통신
            httpconn.setDoOutput(true); //data를 보내기위해 설정
            httpconn.setRequestProperty("Content-Type", "audio/l16; rate=16000");   //pcm파일용 헤더
            //httpconn.setRequestProperty("Content-Type", "audio/x-flac; rate=44100");   //flac파일용 헤더

            httpconn.connect(); //연결
            try {    //파일 입력용 try문
                String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;  //불러올 파일 (폴더생성시 중간에 입력)
                File file = new File(fileDir);  //파일 open
                FileInputStream fis = new FileInputStream(file);    //파일을 input stream 에 연결

                data = new byte[fis.available()];   //파일 크기만큼의 byte배열 생성
                fis.read(data); //파일 내용을 읽어옴

                fis.close();    //스트림 close
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }

            out = httpconn.getOutputStream();   //http통신의 output stream을 가져옴

            out.write(data);    //데이터 전송

            responseCode = httpconn.getResponseCode();  //응답 코드를 받아옴

            if(responseCode == HttpsURLConnection.HTTP_OK){//정상응답이 온 경우
                inStream = new Scanner(httpconn.getInputStream());
                String tmpStr = "";
                while(inStream.hasNext()){
                    tmpStr += (inStream.nextLine());
                }
                tmpStr = tmpStr.replace("{\"result\":[]}","");  //처음에 나오는 쓰레기 데이터 제거

                try {
                    //Json 방식의 결과를 파싱
                    JSONParser parser = new JSONParser();
                    JSONObject obj = (JSONObject) parser.parse(tmpStr);
                    JSONArray res = (JSONArray) obj.get("result");
                    JSONObject result0 = (JSONObject) res.get(0);
                    JSONArray alter = (JSONArray) result0.get("alternative");
                    JSONObject trans0 = (JSONObject) alter.get(0);

                    result = trans0.get("transcript").toString();   //결과 중 제일 처음 값(=제일 가능성이 높은 값)을 result에 저장
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                isDone = true;
                isError = false;
            }else{//정상이 아닌 응답이 온 경우
                isDone = true;
                isError = true;
                result = null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isDone(){
        return isDone;
    }

    public String getResult(){
        isTakeResult = true;
        return result;
    }

    public boolean isError(){
        return isError;
    }

    public String getFileName(){
        return fileName;
    }

    public int getErrcount(){
        return errcount;
    }
}
