package supernova.com.callhelper;

import android.Manifest;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button goBtn, updateBtn;
    EditText filename;
    LinearLayout line;
    private String[] per =  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};    //sd카드에 저장하고 읽어오는 퍼미션
    String fileName;
    SpeechManager manager = new SpeechManager(this);
    ArrayList<String> results = new ArrayList<String>();
    int msgNum=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        goBtn = (Button)findViewById(R.id.goBtn);
        filename = (EditText)findViewById(R.id.filename);
        line = (LinearLayout)findViewById(R.id.line1);

        ActivityCompat.requestPermissions(this, per, 2);    //권한 설정 나중엔 권한 확인하고 없으면 권한설정으로 바꿔야함, 어플 실행시로 이동하면 좋을듯

        manager.start();

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.addFile(filename.getText().toString());
            }
        });
    }

    public void addResult(String result){
        results.add(result);
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            Toast.makeText(MainActivity.this, results.get(0), Toast.LENGTH_SHORT).show();
            if(msg.what==0){
                addMessage(results.get(msgNum));
                msgNum++;
            }
        }
    };

    public void addMessage(String msg){
        TextView tv = new TextView(MainActivity.this);
        tv.setText(msg);
        tv.setBackgroundColor(Color.YELLOW);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        tv.setTextSize(20);
        tv.setLayoutParams(params);

        line.addView(tv);
    }
}
