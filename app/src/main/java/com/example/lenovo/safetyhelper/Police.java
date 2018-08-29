package com.example.lenovo.safetyhelper;

import android.app.Service;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

public class Police extends AppCompatActivity {
     TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police);
        tv=findViewById(R.id.police);
    }
    int count = -1;





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_DOWN:

                tv.setText("-----------------"+count);
                count--;

                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:
                tv.setText("++++++++++++++++"+ count);
                count++;
                return true;
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                tv.setText("MUTE");

                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
