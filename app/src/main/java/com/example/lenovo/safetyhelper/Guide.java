package com.example.lenovo.safetyhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import java.util.ArrayList;
import android.support.v4.view.ViewPager;



public class Guide extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGHT = 3000;  //延迟3秒


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Guide.this, LoginActivity.class);
                Guide.this.startActivity(intent);
                Guide.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);

    }
}
