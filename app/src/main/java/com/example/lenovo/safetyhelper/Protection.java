package com.example.lenovo.safetyhelper;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviCommonParams;

import java.util.Calendar;
public  class Protection extends AppCompatActivity
{
    Calendar c = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protection);
        Menu.Datetime = c.get(Calendar.YEAR)+"."+ c.get(Calendar.MONTH) + "."+c.get(Calendar.DATE)+"."+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);
    }
    public  void walk(View view){
        Menu.flag_outside_mode = 1;
        Intent intent = new Intent(Protection.this,ByfootActivity.class);
        Protection.this.finish();
        startActivity(intent);
    }
    public  void bycar(View view){
        Menu.flag_outside_mode = 2;
        Intent intent = new Intent(Protection.this,BycarActivity.class);
        Protection.this.finish();
        startActivity(intent);
    }
    public void back_menu(View view){
        finish();
    }
}
