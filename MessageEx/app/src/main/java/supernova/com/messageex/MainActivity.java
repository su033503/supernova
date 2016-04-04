package supernova.com.messageex;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    Button btn;
    LinearLayout line;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button)findViewById(R.id.btn);
        line = (LinearLayout)findViewById(R.id.line1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView tv = new TextView(MainActivity.this);
                tv.setText("aaaaaaa");
                tv.setBackgroundColor(Color.YELLOW);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(5,5,5,5);
                tv.setLayoutParams(params);

                line.addView(tv);
            }
        });
    }
}
