package com.example.lenovo.safetyhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/*
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.BitmapTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;



import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
*/


public class Person extends AppCompatActivity implements View.OnClickListener {

    private ImageView mHBack;
    private ImageView mHHead;
    private TextView mSex;
    private TextView mID;
    private TextView mProList;
    private TextView mVoiFile;
    private TextView mModify;
    private TextView mSet;
    private Button mExit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        mHBack = (ImageView) findViewById(R.id.h_back);
        mHHead = (ImageView) findViewById(R.id.h_head);
        mSex = (TextView) findViewById(R.id.user_sex);
        mID = (TextView) findViewById(R.id.use_ID);
        mProList = (TextView) findViewById(R.id.protection_list);
        mVoiFile = (TextView) findViewById(R.id.voice_file);
        mModify = (TextView) findViewById(R.id.modify);
        mSet = (TextView) findViewById(R.id.setting);
        mExit = (Button)findViewById(R.id.exit);

        mModify.setOnClickListener(this);
        mSet.setOnClickListener(this);
        mExit.setOnClickListener(this);


      /*  MultiTransformation multi = new MultiTransformation(
                new BlurTransformation(25),
                new RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.BOTTOM))))
        Glide.with(this).load(R.drawable.test)
                .apply(bitmapTransform(multi))
                .into((ImageView) findViewById(R.id.h_head));
        //设置背景磨砂效果
        Glide.with(this).load(R.drawable.test)
                .bitmapTransform(new BlurTransformation(this, 25), new CenterCrop(this))
                .into(mHBack);
        //设置圆形图像
        Glide.with(this).load(R.drawable.test)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(mHHead);
*/

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.modify:

                break;
            case R.id.setting:

                break;
            case R.id.exit:
                Intent intent = new Intent(Person.this, LoginActivity.class);
                startActivity(intent);
            default:
                break;

        }

    }
}
