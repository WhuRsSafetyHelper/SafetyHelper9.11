package com.example.lenovo.safetyhelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class Route extends AppCompatActivity implements OnGetSuggestionResultListener {
    private Button btnok=null;
    private PoiSearch poiSearch;
    private PoiCitySearchOption poiCitySearchOption;
    private TextView tv=null;


    static final String ROUTE_PLAN_NODE = "routePlanNode";

    private boolean hasInitSuccess = false;
    private BNRoutePlanNode mStartNode = null;
    private LocationClient mLocationClient = null;

    double latitude=0;
    double longtitude=0;

    double latitude2=0;
    double longtitude2=0;

    private String mSDCardPath = null;


    private ArrayAdapter<String> sugAdapter = null;
    private AutoCompleteTextView autoTv;

    private SuggestionSearch mSuggestionSearch = null;

    //布局
    private List<Fruit> fruitList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        mSDCardPath=getSdcardDir();


        BaiduNaviManagerFactory.getBaiduNaviManager().init(Route.this,
                mSDCardPath, "1111", new IBaiduNaviManager.INaviInitListener() {

                    @Override
                    public void onAuthResult(int status, String msg) {
                        String result;
                        if (0 == status) {
                            result = "key校验成功!";
                        } else {
                            result = "key校验失败, " + msg;
                        }
                        Toast.makeText(Route.this, result, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void initStart() {
                        Toast.makeText(Route.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void initSuccess() {
                        Toast.makeText(Route.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                        hasInitSuccess = true;
                        // 初始化tts
                    }

                    @Override
                    public void initFailed() {
                        Toast.makeText(Route.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
                    }
                });
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setServiceName("com.baidu.location.service_v2.9");
        // option.setPoiExtraInfo(true);
        option.setAddrType("all");
        option.setPriority(LocationClientOption.NetWorkFirst);
        option.setPriority(LocationClientOption.GpsFirst); // gps
        // option.setPoiNumber(10);
        option.disableCache(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();


        btnok=findViewById(R.id.btn_ok);
        poiSearch=PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(onGetPoiSearchResultListener);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        autoTv= (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);

        autoTv.setAdapter(sugAdapter);
        autoTv.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                String city = "武汉";
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(city));
            }
        });

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poiCitySearchOption=new PoiCitySearchOption().city("武汉")
                        .keyword(autoTv.getText().toString().trim());
                poiSearch.searchInCity(poiCitySearchOption);

            }
        });

        //布局
        initFruits();                 //初始化水果数据
        FruitAdapter adapter=new FruitAdapter(Route.this,R.layout.fruit_item,fruitList);
        ListView listview = (ListView) findViewById(R.id.list_view);
        listview.setAdapter(adapter);

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        sugAdapter.clear();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null)
                sugAdapter.add(info.key);
        }
        sugAdapter.notifyDataSetChanged();
    }//下拉菜单得到检索位置信息
    private OnGetPoiSearchResultListener onGetPoiSearchResultListener=new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {

            String poiname = poiResult.getAllPoi().get(0).name;
            String poiadd = poiResult.getAllPoi().get(0).address;
            String idString = poiResult.getAllPoi().get(0).uid;
            Intent it = new Intent(Route.this, Menu.class);
            latitude = poiResult.getAllPoi().get(0).location.latitude;
            longtitude = poiResult.getAllPoi().get(0).location.longitude;
            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL);
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    private void routeplanToNavi(final int coType) {
        if (!hasInitSuccess) {
            Toast.makeText(Route.this, "还未初始化!", Toast.LENGTH_SHORT).show();
        }

        BNRoutePlanNode sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", "百度大厦", coType);
        BNRoutePlanNode eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", "北京天安门", coType);


        eNode=new BNRoutePlanNode(longtitude, latitude, "终点", "终点", coType);
        sNode=new BNRoutePlanNode(longtitude2, latitude2, "起点", "起点", coType);


        mStartNode = sNode;



        final List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
        list.add(sNode);
        list.add(eNode);

        BaiduNaviManagerFactory.getRoutePlanManager().routeplanToNavi(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                Toast.makeText(Route.this, "算路开始", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                Toast.makeText(Route.this, "算路成功", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                Toast.makeText(Route.this, "算路失败", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                Toast.makeText(Route.this, "算路成功准备进入导航", Toast.LENGTH_SHORT)
                                        .show();
                                Intent intent = new Intent(Route.this,
                                        BNaviMainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(ROUTE_PLAN_NODE, mStartNode);
                                intent.putExtra("slatitude",list.get(0).getLatitude());
                                intent.putExtra("elatitude",list.get(1).getLatitude());
                                intent.putExtra("slongitude",list.get(0).getLongitude());
                                intent.putExtra("elongitude",list.get(1).getLongitude());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
    }//导航
    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }//sd卡
    public class MyLocationListener implements BDLocationListener {

            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                /*显示当前位置地图*/
                latitude2=bdLocation.getLatitude();
                longtitude2=bdLocation.getLongitude();

            }
    }//定位监听



    //布局

    private void initFruits() {
        for (int i = 0; i < 2; i++) {
            Fruit apple = new Fruit("我的位置——光谷广场", R.drawable.navi_guide_turn);
            fruitList.add(apple);
            Fruit orange = new Fruit("我的位置——光谷广场", R.drawable.navi_guide_turn);
            fruitList.add(orange);
            Fruit banana = new Fruit("我的位置——光谷广场", R.drawable.navi_guide_turn);
            fruitList.add(banana);
            Fruit waterlenmo = new Fruit("我的位置——光谷广场", R.drawable.navi_guide_turn);
            fruitList.add(waterlenmo);
            Fruit pear = new Fruit("我的位置——光谷广场", R.drawable.navi_guide_turn);
            fruitList.add(pear);
            Fruit grape = new Fruit("我的位置——光谷广场", R.drawable.navi_guide_turn);
            fruitList.add(grape);
           /* Fruit pineapple=new Fruit("Pineapple",R.drawable.navi_guide_turn);
            fruitList.add(pineapple);
            Fruit strawberry=new Fruit("Strawberry",R.drawable.navi_guide_turn);
            fruitList.add(strawberry);
            Fruit cherry=new Fruit("Cherry",R.drawable.navi_guide_turn);
            fruitList.add(cherry);
            Fruit mango=new Fruit("mango",R.drawable.navi_guide_turn);
            fruitList.add(mango);*/
        }
    }

}


