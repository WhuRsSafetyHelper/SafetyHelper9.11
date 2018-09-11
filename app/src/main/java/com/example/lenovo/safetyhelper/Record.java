package com.example.lenovo.safetyhelper;

import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import sun.misc.BASE64Encoder;


import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/*import com.kingtone.www.record.util.EnvironmentShare;*/
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.carlos.voiceline.mylibrary.VoiceLineView;

import java.io.File;
import java.io.IOException;




public class Record extends AppCompatActivity implements Runnable /*implements View.OnClickListener*/ {

    private ImageButton buttonStart;
    private ImageButton buttonPlay;
    private TextView time;
    private ProgressBar progressBar;
    // private ImageView volume;
    private Button buttonFinish;
    private Button buttonSaveOK;
    private Button buttonSaveCancel;
    private EditText editTextFileName;
    private Button buttonFinishOK;
    private Button buttonFinishCancel;
    private Button buttonUploadOk;
    private Button buttonUploadCancel;

    private MediaRecorder mediaRecorder=new MediaRecorder();  //用于录音
    private MediaPlayer mediaPlayer=new MediaPlayer();  //用于播放录音
    /*private File filetem=new File( Environment.getExternalStorageDirectory().getPath() + "//RecordTest"+"/new.amr");  //创建一个临时的音频文件*/
    private File filetem=new File( Environment.getExternalStorageDirectory().getPath() + "//RecordTest"+"/new.amr");  //创建一个临时的音频文件
    private long currenttime;  //用于确定当前录音时间
    private boolean isrecording=false;  //用于判断当前是否在录音
    private boolean Isplaying=false;  //用于判断是否正在处于播放录音状态

    //定义Handler
    private Handler volumehandler=new Handler();  //显示音量
    private Handler timehandler=new Handler();  //显示录音时间
    private Handler playhandler=new Handler();  //显示播放时间

    private File file;//最终录音文件

    // 传给服务器端的上传和下载标志


    //波形
    private VoiceLineView voiceLineView;

    private String getTime(int timeMs) {
        int total=timeMs/1000;
        StringBuilder stringBuilder=new StringBuilder();
        Formatter formatter=new Formatter(stringBuilder, Locale.getDefault());
        int seconds=total%60;
        int minutes=(total/60)%60;
        int hours=total/3600;
        stringBuilder.setLength(0);
        if (hours>0){
            return formatter.format("%d:%02d:%02d",hours,minutes,seconds).toString();
        }
        else{
            return formatter.format("%02d:%02d",minutes,seconds).toString();
        }
    }

    private Runnable runnable_1=new Runnable() {  //音量
        @Override
        public void run() {
            int volume=0;
            int ratio;
            ratio = mediaRecorder.getMaxAmplitude() / 600;
            if(ratio>1){
                volume= (int) (20 * Math.log10(ratio));
            } //将getMaxAmplitude()的返回值转换为分贝
            progressBar.setMax(15);//将最大音量设置为60分贝
            progressBar.setProgress(volume/4);//显示录音音量
            //volumehandler.postDelayed(runnable_1, 100);

            //波形
           /* double ratio2 = (double) mediaRecorder.getMaxAmplitude() / 100;
            double db = 0;// 分贝
            //默认的最大音量是100,可以修改，但其实默认的，在测试过程中就有不错的表现
            //你可以传自定义的数字进去，但需要在一定的范围内，比如0-200，就需要在xml文件中配置maxVolume
            //同时，也可以配置灵敏度sensibility
            if (ratio2 > 1)
                db = 20 * Math.log10(ratio);
            //只要有一个线程，不断调用这个方法，就可以使波形变化
            //主要，这个方法必须在ui线程中调用
            voiceLineView.setVolume((int) (db));*/
            volumehandler.postDelayed(runnable_1, 100);
        }
    };

    private Runnable runnable_2=new Runnable() {//录音时间
        @Override
        public void run() {
            time.setText(getTime((int) (System.currentTimeMillis()-currenttime)));
            //调用前先获取当前时间，再通过System.currentTimeMillis()-currenttime获得录音时间并转换成文本
            timehandler.postDelayed(runnable_2,1000);
        }
    };

    private Runnable runnable_3=new Runnable() {//播放时间
        @Override
        public void run() {
            time.setText(getTime(mediaPlayer.getCurrentPosition()));
            //MediaPlayer类的getCurrentPosition()方法可以获得音频的当前播放时间
            playhandler.postDelayed(runnable_3,1000);
        }
    };


//波形

    //private MediaRecorder mMediaRecorder;
    private boolean isAlive = true;
    /*private VoiceLineView voiceLineView;*/
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mediaRecorder==null) return;
            double ratio = (double) mediaRecorder.getMaxAmplitude() / 100;
            double db = 0;// 分贝
            //默认的最大音量是100,可以修改，但其实默认的，在测试过程中就有不错的表现
            //你可以传自定义的数字进去，但需要在一定的范围内，比如0-200，就需要在xml文件中配置maxVolume
            //同时，也可以配置灵敏度sensibility
            if (ratio > 1)
                db = 20 * Math.log10(ratio);
            //只要有一个线程，不断调用这个方法，就可以使波形变化
            //主要，这个方法必须在ui线程中调用
            voiceLineView.setVolume((int) (db));
        }
    };

    /*private Runnable runnable_4=new Runnable() {  //音量
        @Override
        public void run() {
            if(mediaRecorder==null) return;
            double ratio = (double) mediaRecorder.getMaxAmplitude() / 100;
            double db = 0;// 分贝
            //默认的最大音量是100,可以修改，但其实默认的，在测试过程中就有不错的表现
            //你可以传自定义的数字进去，但需要在一定的范围内，比如0-200，就需要在xml文件中配置maxVolume
            //同时，也可以配置灵敏度sensibility
            if (ratio > 1)
                db = 20 * Math.log10(ratio);
            //只要有一个线程，不断调用这个方法，就可以使波形变化
            //主要，这个方法必须在ui线程中调用
            voiceLineView.setVolume((int) (db));
        }
    };*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        buttonStart = (ImageButton) findViewById(R.id.buttonStart);
        buttonPlay=(ImageButton)findViewById(R.id.buttonPlay);
        time=(TextView)findViewById(R.id.textViewTime);
        progressBar=(ProgressBar)findViewById(R.id.progressTimeBar);
        buttonFinish = (Button)findViewById(R.id.buttonFinish);

        //波形

        voiceLineView = (VoiceLineView) findViewById(R.id.voicLine);
        //volume=(ImageView)findViewById(R.id.Volume);
        MediaPlayer.OnCompletionListener playerListener = new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                buttonPlay.setImageResource(R.drawable.ic_play);
                playhandler.removeCallbacks(runnable_3);
                time.setText("00:00");
                Isplaying=false;
                mediaPlayer.reset();
            }
        };
        mediaPlayer.setOnCompletionListener(playerListener);
       /* buttonStart.setOnClickListener(this);*/
        buttonStart.setOnClickListener(new View.OnClickListener() {    //设置Button的监听器
            @Override
            public void onClick(View v) {
                if (isrecording) {
                    finishrecord();
                } else {
                    record();
                }
            }
        });
       /* buttonPlay.setOnClickListener(this);
        buttonFinish.setOnClickListener(this);*/


        //波形
        /*voiceLineView = (VoiceLineView) findViewById(R.id.voicLine);*/
        if (mediaRecorder == null)
            mediaRecorder = new MediaRecorder();

       /* mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "hello.log");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mediaRecorder.setOutputFile(file.getAbsolutePath())*/;
        //mediaRecorder.setMaxDuration(1000 * 60 * 10);
       /* try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();*/

        Thread thread = new Thread(this);
        thread.start();

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.buttonStart:
                if (isrecording) {
                    finishrecord();
                } else {
                    record();
                }
                break;
            case R.id.buttonPlay:
                if(mediaPlayer.isPlaying()){
                    playpause();
                }
                else{
                    play();
                }
                break;
            case R.id.buttonFinish:
                finishwork();
                break;
            default:
                break;

        }
    }

    private void record(){
        if(filetem.exists()) {
            filetem.delete();
        }
        mediaRecorder.reset();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置音频来源为麦克风
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);//设置默认的输出文件的格式
        mediaRecorder.setOutputFile(filetem.getAbsolutePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//默认的编码方式
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();//开始录音
        buttonStart.setImageResource(R.drawable.ic_pause);
        isrecording=true;
        currenttime=System.currentTimeMillis();
        volumehandler.postDelayed(runnable_1,100);
        timehandler.postDelayed(runnable_2,1000);
    }

    private void finishrecord(){
        mediaRecorder.stop();
        buttonStart.setImageResource(R.drawable.ic_mic);
        volumehandler.removeCallbacks(runnable_1);
        timehandler.removeCallbacks(runnable_2);//停止相关线程
        time.setText("00:00");
        isrecording=false;
    }

    private void finishwork(){
        AlertDialog.Builder alertdialogbuilderFinish=new AlertDialog.Builder(this);
        final AlertDialog alertdialogFinish=alertdialogbuilderFinish.create();
        View view=View.inflate(Record.this,R.layout.alertdialogfinish,null);
        alertdialogFinish.setView(view);
        alertdialogFinish.show();
        buttonFinishOK = (Button)view.findViewById(R.id.buttonFinishOK);
        buttonFinishCancel = (Button)view.findViewById(R.id. buttonFinishCancel);
       /* buttonFinishOK.setOnClickListener(this);*/

        buttonFinishOK.setOnClickListener(new View.OnClickListener() {    //设置Button的监听器
            @Override
            public void onClick(View v) {
                save();
                alertdialogFinish.dismiss();
            }
        });
        buttonFinishCancel.setOnClickListener(new View.OnClickListener() {    //设置Button的监听器
            @Override
            public void onClick(View v) {
                alertdialogFinish.dismiss();
            }
        });

    }

    private void save(){
        AlertDialog.Builder alertdialogbuilderSave=new AlertDialog.Builder(this);
        final AlertDialog alertdialogSave=alertdialogbuilderSave.create();
        View view=View.inflate(Record.this,R.layout.alertdialogsave,null);
        alertdialogSave.setView(view);
        alertdialogSave.show();
        buttonSaveOK = (Button)view.findViewById(R.id. buttonSaveOK);
        buttonSaveCancel = (Button)view.findViewById(R.id. buttonSaveCancel);
        editTextFileName = (EditText)view.findViewById(R.id.editTextFileName);
       /* buttonSaveOK.setOnClickListener(this);*/
        buttonSaveOK.setOnClickListener(new View.OnClickListener() {    //设置Button的监听器
            @Override
            public void onClick(View v) {
                filesave();
                alertdialogSave.dismiss();
            }
        });

        buttonSaveCancel.setOnClickListener(new View.OnClickListener() {    //设置Button的监听器
            @Override
            public void onClick(View v) {
                alertdialogSave.dismiss();
            }
        });

    }

    private int filesave() {
        String strFileName;
        strFileName = editTextFileName.getText().toString();
        if (strFileName == null) {
            Toast.makeText(this, "录音文件名不能为空", Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            file = new File(Environment.getExternalStorageDirectory().getPath() + "//RecordTest", strFileName+""+".amr");  //最终录音文件实例化
            if (file.exists()) {
                Toast.makeText(this, "录音文件已存在，请重命名", Toast.LENGTH_SHORT).show();
                return 0;
            }
            //执行重命名
            filetem.renameTo(file);
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
        }
        AlertDialog.Builder alertdialogbuilderUpload=new AlertDialog.Builder(this);
        final AlertDialog alertdialogUpload=alertdialogbuilderUpload.create();
        View view=View.inflate(Record.this,R.layout.alertdialogupload,null);
        alertdialogUpload.setView(view);
        alertdialogUpload.show();
        buttonUploadOk = (Button)view.findViewById(R.id.buttonUploadOk);
        buttonUploadCancel = (Button)view.findViewById(R.id.buttonUploadCancel);
        /*buttonUploadOk.setOnClickListener(this);*/
        buttonUploadOk.setOnClickListener(new View.OnClickListener() {    //设置Button的监听器
            @Override
            public void onClick(View v) {
                Toast.makeText(Record.this, "正在上传", Toast.LENGTH_SHORT).show();
                ConnectTest();
                //audioUpLoad();
                Toast.makeText(Record.this, "上传成功", Toast.LENGTH_SHORT).show();
                alertdialogUpload.dismiss();
            }
        });
        buttonUploadCancel.setOnClickListener(new View.OnClickListener() {    //设置Button的监听器
            @Override
            public void onClick(View v) {
                alertdialogUpload.dismiss();
            }
        });
        return 1;

    }

    /**
     * 上传 录音文件
     */
    private void audioUpLoad() {
        new Thread() {
            public void run() {
                // DataInputStream reader = null;
                // DataOutputStream out = null;
                // Socket socket = null;
                // byte[] buf = null;
                // try {
                // // 连接Socket
                // socket = new Socket("192.168.42.219", 9999);
                // // 1. 读取文件输入流
                // reader = new DataInputStream(new BufferedInputStream(new
                // FileInputStream(audioFile)));
                // // 2. 将文件内容写到Socket的输出流中
                // out = new DataOutputStream(socket.getOutputStream());
                // out.writeInt(UP_LOAD);
                // out.writeUTF(audioFile.getName()); // 附带文件名
                //
                // int bufferSize = 2048; // 2K
                // buf = new byte[bufferSize];
                // int read = 0;
                // // 将文件输入流 循环 读入 Socket的输出流中
                // while ((read = reader.read(buf)) != -1) {
                // out.write(buf, 0, read);
                // }
                // handler.sendEmptyMessage(UPLOAD_SUCCESS);
                // } catch (Exception e) {
                // handler.sendEmptyMessage(UPLOAD_FAIL);
                // } finally {
                // try {
                // // 善后处理
                // buf = null;
                // out.close();
                // reader.close();
                // socket.close();
                // } catch (Exception e) {
                //
                // }
                // }

                // post请求上传
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://2f18k91236.imwork.net:47215/Mmenu.php");
                try {
                    //String text = "测试文字";
                    //byte[] textByte = text.getBytes("UTF-8");
                    //String encoding_string=new BASE64Encoder().encode(textByte);
                    StringBody voice_name = new StringBody("filename");
                    MultipartEntity entity = new MultipartEntity();
                    //entity.addPart("encoding_string", encoding_string);
                    entity.addPart("voice_name", voice_name);
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
        }.start();

    }

    private void ConnectTest(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://2f18k91236.imwork.net:47215/Mmenu.php");
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
    private void play(){
        if(!Isplaying) {
            if (file != null && file.exists()) {
                try {
                    mediaPlayer.setDataSource(file.getAbsolutePath());//设置音频来源
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(filetem != null && filetem.exists()){
                try {
                    mediaPlayer.setDataSource(filetem.getAbsolutePath());//设置音频来源
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "未发现录音文件", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Isplaying = true;
        }
        mediaPlayer.start();//开始播放
        buttonPlay.setImageResource(R.drawable.ic_pause);
        playhandler.postDelayed(runnable_3,1000);

    }

    private void playpause(){
        mediaPlayer.pause();
        playhandler.removeCallbacks(runnable_3);
        buttonPlay.setImageResource(R.drawable.ic_play);

    }


    //波形
    @Override
    protected void onDestroy() {
        isAlive = false;
        mediaRecorder.release();
        mediaRecorder = null;
        super.onDestroy();
    }

   // @Override
    public void run() {
        while (isAlive) {
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}