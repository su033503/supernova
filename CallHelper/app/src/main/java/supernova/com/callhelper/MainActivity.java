package supernova.com.callhelper;

import android.Manifest;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button goBtn, updateBtn;
    EditText filename, editText;
    private String[] per =  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};    //sd카드에 저장하고 읽어오는 퍼미션
    String fileName;
    SpeechManager manager = new SpeechManager(this);
    ArrayList<String> results = new ArrayList<String>();
    String totalText="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        goBtn = (Button)findViewById(R.id.goBtn);
        filename = (EditText)findViewById(R.id.filename);
        editText = (EditText)findViewById(R.id.editText);
        updateBtn = (Button)findViewById(R.id.updateBtn);

        ActivityCompat.requestPermissions(this, per, 2);    //권한 설정 나중엔 권한 확인하고 없으면 권한설정으로 바꿔야함, 어플 실행시로 이동하면 좋을듯

        manager.start();

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.addFile("rec1.pcm");
                manager.addFile("rec2.pcm");
                manager.addFile("rec3.pcm");
                manager.addFile("rec4.pcm");

            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(totalText);
                Toast.makeText(MainActivity.this, totalText, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setTotalText(String totalText){
        this.totalText = totalText;
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            Toast.makeText(MainActivity.this, totalText, Toast.LENGTH_LONG).show();
            if(msg.what==0){
                editText.setText(totalText);
            }
        }
    };
}
