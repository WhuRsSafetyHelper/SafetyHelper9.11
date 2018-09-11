package com.example.lenovo.safetyhelper;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class ByfootActivity extends AppCompatActivity
        implements View.OnClickListener,
        TimePickerDialog.OnTimeSetListener{
        public Calendar c ;
        public TextView txTime ;
        public TextView txv ;
        public TextView txv2 ;
        public String remain_time_car;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_byfoot);
             c = Calendar.getInstance();
             txTime = findViewById(R.id.inputtime);
             txv = findViewById(R.id.outputtime);
             txv2 =findViewById(R.id.textView2);
             Intent it=getIntent();
             remain_time_car=it.getStringExtra("remain_time_car").toString();
            /*if(getIntent().getStringExtra("remain_time_car")!=null) {
                remain_time_car = getIntent().getStringExtra("remain_time_car").toString();
            };*/

            if(Menu.flag_outside_mode == 1){
                txv.setVisibility(View.VISIBLE);
                txv2.setVisibility(View.VISIBLE);
                if(remain_time_car!=null){
                    txv.setText(remain_time_car);
                }
                else{
                    txv.setText("未知");
                }
            }
            else{
                txv.setVisibility(View.INVISIBLE);
                txv2.setVisibility(View.INVISIBLE);
            }
            txTime.setOnClickListener(this);
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
        public void protect_start(View view) {
            //计算路程总用时
            Calendar c = Calendar.getInstance();
            TextView txv = findViewById(R.id.inputtime);
            String str = txv.getText().toString().trim();
            String[] b = str.split(":");
            int h = Integer.parseInt(b[0]);
            int m = Integer.parseInt(b[1]);
            int minutes = (h - c.get(Calendar.HOUR_OF_DAY)) * 60 + m - c.get(Calendar.MINUTE);
            if (minutes < 0) {
                minutes = minutes + 24 * 60;
            }
            Intent intent = new Intent(getApplicationContext(),CountTimeService.class);
            String test_time = minutes + "";
            if(Menu.flag_outside_mode ==1){
                send_begin_walk(test_time);
            }
            else{
                send_perior(test_time);
            }
            //传入预计所需时间
            intent.putExtra("test_time",test_time);
            startService(intent);
            //一级预警时间
            Intent intent1 = new Intent(getApplicationContext(),CountTimeService.class);
            test_time = minutes +5 + "";
            intent1.putExtra("test_time",test_time);
            startService(intent1);
            //二级预警时间
            Intent intent2 = new Intent(getApplicationContext(),CountTimeService.class);
            test_time = minutes +8 + "";
            intent2.putExtra("test_time",test_time);
            startService(intent2);
            Intent it = new Intent(this,Mmenu.class);
            ByfootActivity.this.finish();
            startActivity(it);
        }
        public void send_begin_walk(final String s){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://2f18k91236.imwork.net:47215/begin_walk.php");
                    try {
                        MultipartEntity entity = new MultipartEntity();
                        int t =Menu.flag_outside_mode-1;
                        StringBody string1 = new StringBody(t+"");
                        StringBody string2 = new StringBody(Menu.Datetime);
                        StringBody string3 = new StringBody(s+"minutes");
                        entity.addPart("string1", string1);
                        entity.addPart("string2", string2);
                        entity.addPart("string3", string3);
                        post.setEntity(entity);
                        HttpResponse response = httpclient.execute(post);
                        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                            HttpEntity entitys = response.getEntity();
                            if (entity != null) {
                                System.out.println(entity.getContentLength());
                                System.out.println(EntityUtils.toString(entitys));
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    httpclient.getConnectionManager().shutdown();
                }
            }).start();
        }
        public void send_perior(final String s){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://2f18k91236.imwork.net:47215/upPerior.php");
                    try {
                        MultipartEntity entity = new MultipartEntity();
                        StringBody string1 = new StringBody(s+"minutes");
                        StringBody string2 = new StringBody(Menu.Datetime);
                        entity.addPart("string1", string1);
                        entity.addPart("string2", string2);
                        post.setEntity(entity);
                        HttpResponse response = httpclient.execute(post);
                        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                            HttpEntity entitys = response.getEntity();
                            if (entity != null) {
                                System.out.println(entity.getContentLength());
                                System.out.println(EntityUtils.toString(entitys));
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    httpclient.getConnectionManager().shutdown();
                }
            }).start();
        }
    }

