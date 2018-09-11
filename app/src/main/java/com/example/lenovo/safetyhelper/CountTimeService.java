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

public class CountTimeService extends Service {
    public CountTimeService() {
    }
    public static String IN_RUNNING="1001";
    public static String END_RUNNING="1002";
    public CountDownTimer mCountTimer;
    public String mcounttime ;
    public AlertDialog alertDialog;
    public AlertDialog alertDialog1;
    public int t;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent ,int flags, int startId){
        mcounttime=intent.getStringExtra("testtime");
        t =Integer.parseInt(mcounttime);
        mCountTimer = new CountDownTimer(t*60*1000,1000) {
            @Override
            public void onTick(long l) {
                broadcastUpdate(IN_RUNNING,l/1000+"");
            }

            @Override
            public void onFinish() {
                broadcastUpdate(END_RUNNING);
                creatdialog();
                alertDialog.show();
                stopSelf();
            }
        }.start();
        return super.onStartCommand(intent,flags,startId);
    }
    private void broadcastUpdate(final String action){
        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    private void broadcastUpdate(final String action, String time){
        final Intent intent = new Intent(action);
        intent.putExtra("time",time);
        sendBroadcast(intent);
    }
    public void creatdialog(){
        AlertDialog.Builder bdr = new AlertDialog.Builder(this);
        bdr.setMessage("是否安全抵达？");
        bdr.setNegativeButton("一键报警", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface,int i){

            }
        });
        bdr.setPositiveButton("是", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface,int i){
                alertDialog1.show();
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
}