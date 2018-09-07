package com.example.lenovo.safetyhelper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener;
import com.baidu.mapapi.walknavi.adapter.IWRouteGuidanceListener;
import com.baidu.mapapi.walknavi.adapter.IWTTSPlayer;
import com.baidu.mapapi.walknavi.model.RouteGuideKind;
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener;
import com.baidu.platform.comapi.walknavi.widget.ArCameraView;

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

public class Walk extends Activity {

    private final static String TAG = Walk.class.getSimpleName();

    private WalkNavigateHelper mNaviHelper;

    private boolean firstlocation=true;//判断是否刚进入规划

    private int time=0;

    private double latitude=30.60;
    private double longitude=114.30;
    private LocationClient locationClient = null;
    private String currenttime="2018-08-27";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNaviHelper.quit();
        locationClient.stop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mNaviHelper.resume();
        locationClient.restart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNaviHelper.pause();
        locationClient.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNaviHelper = WalkNavigateHelper.getInstance();

        try {
            View view = mNaviHelper.onCreate(Walk.this);
            if (view != null) {
                setContentView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new Walk.MyLocationListener());

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(60000);//传送间隔
        option.setServiceName("com.baidu.location.service_v2.9");
        // option.setPoiExtraInfo(true);
        option.setAddrType("all");
        option.setPriority(LocationClientOption.NetWorkFirst);
        option.setPriority(LocationClientOption.GpsFirst); // gps
        // option.setPoiNumber(10);
        option.disableCache(true);
        locationClient.setLocOption(option);
        locationClient.start();


        mNaviHelper.setWalkNaviStatusListener(new IWNaviStatusListener() {
            @Override
            public void onWalkNaviModeChange(int mode, WalkNaviModeSwitchListener listener) {
                Log.d(TAG, "onWalkNaviModeChange : " + mode);
                mNaviHelper.switchWalkNaviMode(Walk.this, mode, listener);

            }

            @Override
            public void onNaviExit() {
                Log.d(TAG, "onNaviExit");
            }
        });

        boolean startResult = mNaviHelper.startWalkNavi(Walk.this);
        Log.e(TAG, "startWalkNavi result : " + startResult);

        mNaviHelper.setTTsPlayer(new IWTTSPlayer() {
            @Override
            public int playTTSText(final String s, boolean b) {
                Log.d("tts", s);
                return 0;
            }
        });

        mNaviHelper.setRouteGuidanceListener(this, new IWRouteGuidanceListener() {
            @Override
            public void onRouteGuideIconUpdate(Drawable icon) {

            }

            @Override
            public void onRouteGuideKind(RouteGuideKind routeGuideKind) {

            }

            @Override
            public void onRoadGuideTextUpdate(CharSequence charSequence, CharSequence charSequence1) {

            }

            @Override
            public void onRemainDistanceUpdate(CharSequence charSequence) {

            }

            @Override
            public void onRemainTimeUpdate(CharSequence charSequence) {
                if(firstlocation==true){
                    firstlocation=false;
                    Intent it = new Intent(Walk.this, Protection.class);
                    it.putExtra("remain_time_car", charSequence);
                    startActivity(it);
                }
            }

            @Override
            public void onGpsStatusChange(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onRouteFarAway(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onRoutePlanYawing(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onReRouteComplete() {

            }

            @Override
            public void onArriveDest() {

            }

            @Override
            public void onVibrate() {

            }


        });//导航监听




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ArCameraView.WALK_AR_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(Walk.this, "没有相机权限,请打开后重试", Toast.LENGTH_SHORT).show();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNaviHelper.startCameraAndSetMapView(Walk.this);
            }
        }
    }//全景

    private void ConnectTest(final double latitude, final double longitude, final String currenttime){//传输

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://2f18k91236.imwork.net:47215/teststring.php");
                try {
                    MultipartEntity entity = new MultipartEntity();
                    StringBody string1 = new StringBody(String.valueOf(latitude));
                    entity.addPart("string1", string1);
                    StringBody string2 = new StringBody(String.valueOf(longitude));
                    entity.addPart("string2", string2);
                    StringBody string3 = new StringBody(currenttime);
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

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            /*显示当前位置地图*/
            ConnectTest(bdLocation.getLatitude(),bdLocation.getLongitude(),bdLocation.getTime());

        }
    }//定位监听
}
