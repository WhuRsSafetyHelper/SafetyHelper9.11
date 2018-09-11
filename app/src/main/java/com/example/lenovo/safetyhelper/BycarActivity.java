package com.example.lenovo.safetyhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

public class BycarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bycar);
    }
    public void carinfo(View view){
        EditText carnumber = findViewById(R.id.car_number);
        EditText carinfo_t = findViewById(R.id.carinfo);
        int t=Menu.flag_outside_mode -1;
        final String mode = t+"";
        final String car_number = carnumber.getText().toString();
        final String car_info = carinfo_t.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://2f18k91236.imwork.net:47215/begin_car.php");
                try {
                    MultipartEntity entity = new MultipartEntity();
                    StringBody string1 = new StringBody(mode);
                    StringBody string2 = new StringBody(car_number);
                    StringBody string3 = new StringBody(car_info);
                    StringBody string4 = new StringBody(Menu.Datetime);
                    entity.addPart("string1", string1);
                    entity.addPart("string2", string2);
                    entity.addPart("string3", string3);
                    entity.addPart("string4", string4);
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
        Intent intent = new Intent(this,ByfootActivity.class);
        startActivity(intent);
    }
}