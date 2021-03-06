package com.example.lenovo.safetyhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.view.ViewPager;
import java.util.ArrayList;
import android.widget.Button;
import java.util.List;
import android.content.Intent;
import android.view.LayoutInflater;




public class Guide extends AppCompatActivity {

    private ViewPager viewPager;//需要ViewPager
    private PagerAdapter mAdapter;//需要PagerAdapter适配器
    private List<View> mViews=new ArrayList<>();//准备数据源
    private Button bt_Menu;//在ViewPager的最后一个页面设置一个按钮，用于点击跳转到Menu
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();//初始化view
    }

    private void initView() {
        viewPager= (ViewPager) findViewById(R.id.view_pager);
        LayoutInflater inflater=LayoutInflater.from(this);//将每个xml文件转化为View
        View guideOne=inflater.inflate(R.layout.item01, null);//每个xml中就放置一个imageView
        View guideTwo=inflater.inflate(R.layout.item02,null);
        View guideThree=inflater.inflate(R.layout.item03,null);
        View guideFour=inflater.inflate(R.layout.item04,null);

        mViews.add(guideOne);//将view加入到list中
        mViews.add(guideTwo);
        mViews.add(guideThree);
        mViews.add(guideFour);

        mAdapter=new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view=mViews.get(position);//初始化适配器，将view加到container中
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                View view=mViews.get(position);
                container.removeView(view);//将view从container中移除
            }

            @Override
            public int getCount() {
                return mViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;//判断当前的view是我们需要的对象
            }
        };

        viewPager.setAdapter(mAdapter);

        bt_Menu= (Button) guideFour.findViewById(R.id.to_Menu);
        bt_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Guide.this,deal.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
