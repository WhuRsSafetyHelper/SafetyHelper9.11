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
        implements View.OnClickListener,DialogInterface.OnClickListener,
        TimePickerDialog.OnTimeSetListener {
    AlertDialog alertDialog;
    Calendar c = Calendar.getInstance();
    TextView txTime;
    TextView txv1;
    String remain_time_car=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protection);
        //生成结束时的对话框


        AlertDialog.Builder bdr = new AlertDialog.Builder(this);
        bdr.setMessage("是否安全抵达？");
        bdr.setNegativeButton("一键报警", this);
        bdr.setPositiveButton("是", this);
        bdr.setCancelable(true);
        alertDialog = bdr.create();
        txv1 = (TextView) findViewById(R.id.shiyan);
        txTime =  findViewById(R.id.inputtime);
        txTime.setOnClickListener(this);
        if(getIntent().getStringExtra("remain_time_car")!=null) {
            remain_time_car = getIntent().getStringExtra("remain_time_car").toString();
        }
    }
    @Override
    public void onClick(View view) {
        TimePickerDialog t= new  TimePickerDialog(this,this,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE), true);

        t.show();
    }
    public void onTimeSet(TimePicker view, int h, int m) {
        if (m < 10) {
            txTime.setText(h + ":0" + m);
        } else {
            txTime.setText(h + ":" + m);
        }
    }
    //需插入路线返回的路程
    public void walk(View v) {
        TextView txv = (TextView) findViewById(R.id.testtime);
        if(remain_time_car!=null){
        txv.setText(remain_time_car);
     }
     else{
            txv.setText("未知");
        }
    }
    public void bycar(View v) {

        TextView txv = (TextView) findViewById(R.id.testtime);
        if(remain_time_car!=null){
            txv.setText(remain_time_car);
        }
        else{
            txv.setText("未知");
        }
    }
    public void protect_start(View v) {
        //计算路程总用时
        Calendar c = Calendar.getInstance();
        TextView txv = (TextView) findViewById(R.id.inputtime);
        String str = txv.getText().toString().trim();
        String[] b = str.split(":");
        int h = Integer.parseInt(b[0]);
        int m = Integer.parseInt(b[1]);
        int minutes = (h - c.get(Calendar.HOUR_OF_DAY)) * 60 + m - c.get(Calendar.MINUTE);
        if (minutes < 0) {
            minutes = minutes + 24 * 60;
        }
        CountDownTimer timer = new CountDownTimer(minutes * 60 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                txv1.setText("预计" + l / 1000 + "秒后抵达目的地");
            }
            @Override
            public void onFinish() {
                alertDialog.show();
                txv1.setText("结束！");
            }
        }.start();
    }
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == dialogInterface.BUTTON_NEGATIVE) {
            Intent it = new Intent(this, Police.class);
            startActivity(it);
        }
        if (i == dialogInterface.BUTTON_POSITIVE) {
            Intent it = new Intent(this, Menu.class);
            startActivity(it);
        }
    }
}
