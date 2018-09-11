package com.example.lenovo.safetyhelper;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.WindowManager;

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

public class CountTimeService extends Service {
    public CountTimeService() {
    }
    public   AlertDialog alertDialog;
    public AlertDialog alertDialog1;
    public static String IN_RUNNING="1001";
    public static String END_RUNNING="1002";
    public CountDownTimer mCountTimer;
    public String mcounttime ;
    public int t;
    public static int level =0;
    public  Vibrator vibrator;
    public static boolean issafety =false;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent ,int flags, int startId){
        mcounttime=intent.getStringExtra("test_time");
        t =Integer.parseInt(mcounttime);
        mCountTimer = new CountDownTimer(t*60*1000,1000) {
            @Override
            public void onTick(long l) {
                broadcastUpdate(IN_RUNNING,l/1000+"");
            }

            @Override
            public void onFinish() {
                if(issafety == true){
                    stopSelf();
                }
                else{
                    if(level ==2){
                        level++;
                        send_update_level();
                        stopSelf();
                  }
                  if(level ==1){
                      vir_start();
                      level++;
                      send_update_level();
                      stopSelf();
                   }
                  if(level ==0) {
                       level++;
                       send_update_level();
                       broadcastUpdate(END_RUNNING);
                       stopSelf();
                  }
                }
            }
        }.start();
        return super.onStartCommand(intent,flags,startId);
    }
    private void broadcastUpdate(final String action){
        Vibrator vibrator1 = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator1.vibrate(500);
        creatdialog();
        alertDialog.show();
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    private void broadcastUpdate(final String action, String time){
        final Intent intent = new Intent(action);
        intent.putExtra("time",time);
        sendBroadcast(intent);
    }
    public void vir_start(){
        vibrator = (Vibrator)this.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{310,150,10,150},0);
    }
    public void creatdialog(){
        AlertDialog.Builder bdr = new AlertDialog.Builder(this);
        bdr.setMessage("是否安全抵达？");
        bdr.setNegativeButton("一键报警", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface,int i){
                Intent intent = new Intent(CountTimeService.this,Police.class);
                startActivity(intent);
            }
        });
        bdr.setPositiveButton("是", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface,int i){
                issafety = true;
                if(level >=2){
                vibrator.cancel();
                }
                level =0;
                Intent it =new Intent(getApplicationContext(),CountTimeService.class);
                stopService(it);
                send_end();
                if(Menu.flag_outside_mode ==2){
                    alertDialog1.show();
                }
            }

        });
        bdr.setCancelable(false);
        alertDialog = bdr.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        AlertDialog.Builder bdr1 = new AlertDialog.Builder(this);
        bdr1.setMessage("是否继续预警？");
        bdr1.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        bdr1.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent1 = new Intent(CountTimeService.this,Protection.class);
                startActivity(intent1);
            }

        });
        bdr1.setCancelable(false);
        alertDialog1 = bdr1.create();
    }
    public void send_end(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://2f18k91236.imwork.net:47215/end.php");
                try {
                    MultipartEntity entity = new MultipartEntity();
                    StringBody string1 = new StringBody(CountTimeService.level+"");
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
    public void send_update_level(){
        new Thread(new Runnable() {
            @Override
            public void run() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://2f18k91236.imwork.net:47215/updateLevel.php");
        try {
            MultipartEntity entity = new MultipartEntity();
            StringBody string1 = new StringBody(level+"");
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