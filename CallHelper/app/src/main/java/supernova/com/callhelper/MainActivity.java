package supernova.com.callhelper;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpThreadClass().execute();
            }
        });
    }

    private class HttpThreadClass extends AsyncTask<String,String,String>{

        String speechApiURL = "https://www.google.com/speech-api/v2/recognize";
        String apiKey = "AIzaSyAjMHB85JfptvetxeHbHdwcrY8HnWdtnKI";
        String conURL = speechApiURL+"?output=json&lang=en-us&key="+apiKey;

        @Override
        protected String doInBackground(String... params) {
            doitHttp();
            return null;
        }

        private void doitHttp(){
            try {
                URL url = new URL(conURL);
                URLConnection con = url.openConnection();
                HttpsURLConnection httpconn = (HttpsURLConnection)con;
                httpconn.setAllowUserInteraction(false);
                httpconn.setInstanceFollowRedirects(true);
                httpconn.setRequestMethod("POST");
                httpconn.setDoOutput(true);
                httpconn.setChunkedStreamingMode(0);
                httpconn.setRequestProperty("Content-Type", "audio/x-flac; rate=44100");

                httpconn.connect();

                InputStream is = httpconn.getInputStream();
                byte[] bb = new byte[512];
                is.read(bb);

                System.out.println("adsfasdfwerdfsdjlatiohlksadnflasdf");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
