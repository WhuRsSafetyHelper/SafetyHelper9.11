package com.example.lenovo.safetyhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;

public class deal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        final Button bt_start=(Button)findViewById(R.id.start);//获取"进入"按钮
        CheckBox checkbox=(CheckBox)findViewById(R.id.checkBox1);//获取复选框
        //为复选框添加监听器
        checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){//当复选框被选中时
                    bt_start.setVisibility(View.VISIBLE);//设置"进入"按钮显示
                }else{
                    bt_start.setVisibility(View.INVISIBLE);//设置"进入"按钮不显示
                }
                bt_start.invalidate();//重绘bt_start
            }
        });
        bt_start.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(deal.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
