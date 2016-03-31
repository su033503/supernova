package supernova.com.callhelper;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    Button btn1, btn2;
    TextView tv;
    Integer resCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/good");
        f.mkdir();

        btn1 = (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpThreadClass().execute();
                Toast.makeText(getApplicationContext(), resCode.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        tv = (TextView)findViewById(R.id.textView);
        tv.setText("how");
    }

    private class HttpThreadClass extends AsyncTask<String,String,String>{

        String speechApiURL = "https://www.google.com/speech-api/v2/recognize";
        String apiKey = "AIzaSyAjMHB85JfptvetxeHbHdwcrY8HnWdtnKI";
        String conURL = speechApiURL+"?output=json&lang=ko-KR&key="+apiKey;

        @Override
        protected String doInBackground(String... params) {
            doitHttp();
            return null;
        }

        private void doitHttp(){
            byte[] data=null;
            OutputStream out = null;

            Scanner inSt = null;
            String res="";
            try {
                URL url = new URL(conURL);
                URLConnection con = url.openConnection();
                HttpsURLConnection httpconn = (HttpsURLConnection)con;
                httpconn.setAllowUserInteraction(false);
                httpconn.setInstanceFollowRedirects(true);
                httpconn.setRequestMethod("POST");
                httpconn.setDoOutput(true);
                httpconn.setChunkedStreamingMode(0);
                httpconn.setRequestProperty("Content-Type", "audio/l16; rate=16000");

                httpconn.connect();

                try{
                    String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/rec1.pcm";
                    File file = new File(fileDir);
                    FileInputStream fis = new FileInputStream(file);
                    FileChannel fc = fis.getChannel();

                    int size = (int)fc.size();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY,0,size);
                    data = new byte[bb.remaining()];
                    bb.get(data);
                }catch(Exception e){
                    e.printStackTrace();
                }

                try{
                    out = httpconn.getOutputStream();
                    out.write(data);
                    resCode = httpconn.getResponseCode();



                }catch (Exception e){
                    e.printStackTrace();
                }
                inSt = new Scanner(httpconn.getInputStream());
                while(inSt.hasNext()){
                    res+=(inSt.nextLine());
                }
                tv.setText(res);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
