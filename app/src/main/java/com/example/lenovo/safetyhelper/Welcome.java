package com.example.lenovo.safetyhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Handler;
import android.content.SharedPreferences;
import android.os.Message;

public class Welcome extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGHT=3000;
    private static final int TIME=3000;
    private static final int GO_LOGIN=100;
    private static final int GO_GUIDE=101;

   /* private void goLogin() {
        Intent intent=new Intent(Welcome.this,LoginActivity.class);
        startActivity(intent);
        finish();

    }
    private void goGuide() {
        Intent intent=new Intent(
                Welcome.this,Guide.class);
        startActivity(intent);
        finish();
    }


    Handler mhandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GO_LOGIN:
                    goLogin();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
            }
        }
    };
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Welcome.this, Guide.class);
                Welcome.this.startActivity(intent);
                Welcome.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);

    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();

    }

    private void init() {
        SharedPreferences sf=getSharedPreferences("data", MODE_PRIVATE);//判断是否是第一次进入
        boolean isFirstIn=sf.getBoolean("isFirstIn", true);
        SharedPreferences.Editor editor=sf.edit();
        if(isFirstIn) {     //若为true，则是第一次进入
            editor.putBoolean("isFirstIn", false);
            mhandler.sendEmptyMessageDelayed(GO_GUIDE,TIME);}//将欢迎页停留5秒，并且将message设置为跳转到                                                             引导页SplashActivity，跳转在goGuide中实现
        else{
                mhandler.sendEmptyMessageDelayed(GO_LOGIN,TIME);//将欢迎页停留5秒，并且将message设置文跳转到                                                                   MainActivity，跳转功能在goMain中实现
            }
            editor.commit();

        }



*/

    }
