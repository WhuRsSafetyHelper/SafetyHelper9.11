package com.example.lenovo.safetyhelper;

import android.app.Service;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

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

public class Police extends AppCompatActivity {
     TextView tv;
    int mBackKeyAction, mVolumnDownKeyAction;
    long mActionTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police);
        tv=findViewById(R.id.police);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int keyCode = event.getKeyCode();
        int action = event.getAction();

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
            mVolumnDownKeyAction = KeyEvent.ACTION_DOWN;   //记录按下状态
            if (mActionTime == 0) {
                mActionTime = System.currentTimeMillis();
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_UP) {
            mVolumnDownKeyAction = KeyEvent.ACTION_UP;    //记录松下状态
            mActionTime = 0;
        }else if (keyCode == KeyEvent.KEYCODE_BACK && action == KeyEvent.ACTION_DOWN) {
            mBackKeyAction = KeyEvent.ACTION_DOWN;  //记录按下状态
            if (mActionTime == 0) {
                mActionTime = System.currentTimeMillis();
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK && action == KeyEvent.ACTION_UP) {
            mBackKeyAction = KeyEvent.ACTION_UP;  //记录松下状态
            mActionTime = 0;
        }

        //长按，且Back键和音量变大键没松

        if (isLongPress()&& mVolumnDownKeyAction == KeyEvent.ACTION_DOWN && mBackKeyAction == KeyEvent.ACTION_DOWN ) {
            //do something
            tv.setText("报警成功");
            Toast.makeText(Police.this, "已发送报警信息", Toast.LENGTH_SHORT).show();
            ConnectTest();
            //事件不下发啦
            return true;
        }
        else{
            tv.setText("222");
        }

        return super.dispatchKeyEvent(event);
    }

    private boolean isLongPress() {
        if (System.currentTimeMillis() - mActionTime > 5000) {
            return true;
        } else {
            return false;
        }
    }

    private void ConnectTest(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://2f18k91236.imwork.net:47215/policerequest.php");
                try {
                    MultipartEntity entity = new MultipartEntity();
                    StringBody string = new StringBody("Tuesday");
                    entity.addPart("string", string);
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

        /*HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://10.135.107.70/testtt.php");
        try {
            MultipartEntity entity = new MultipartEntity();
            StringBody string = new StringBody("Tuesday");
            entity.addPart("string", string);
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
*/
    }
}
