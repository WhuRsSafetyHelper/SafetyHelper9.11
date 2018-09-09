package com.example.lenovo.safetyhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;



public class deal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        final Button bt_start=(Button)findViewById(R.id.start);//获取"进入"按钮
        CheckBox checkbox=(CheckBox)findViewById(R.id.checkBox1);//获取复选框
        //为复选框添加监听器
        checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){//当复选框被选中时
                    bt_start.setVisibility(View.VISIBLE);//设置"进入"按钮显示
                }else{
                    bt_start.setVisibility(View.INVISIBLE);//设置"进入"按钮不显示
                }
                bt_start.invalidate();//重绘bt_start
            }
        });
        bt_start.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(deal.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView tv=(TextView)findViewById(R.id.textview1);
       /* String html="<html><head><title>TextView使用HTML</title></head><body><p><strong>强调</strong></p><p><em>斜体</em></p>"
                +"<p><a href=\"http://www.dreamdu.com/xhtml/\">超链接HTML入门</a>学习HTML!</p><p><font color=\"#aabb00\">颜色1"
                +"</p><p><font color=\"#00bbaa\">颜色2</p><h1>标题1</h1><h3>标题2</h3><h6>标题3</h6><p>大于>小于<</p><p>" +
                "下面是网络图片</p><img src=\"http://avatar.csdn.net/0/3/8/2_zhang957411207.jpg\"/></body></html>";*/
        String html="<html><head><title></title></head><body><p><strong></strong></p><p><em></em></p>"
                +"<p><a href=\"http://www.dreamdu.com/xhtml/\"></a></p><p><font color=\"#aabb00\">"
                +"</p><p><font color=\"#00bbaa\"></p><h1>用户须知</h1><h3>在使用本app的所有功能前，请您务必仔细阅读并透彻理解本声明。您可以选择不使用本app,但如果您使用本app，您的使用行为将被视为对本声明全部内容的认可。</h3>" +
                "<h3>1、使用本APP的一键报警功能时，报警所需承担的法律责任，由用户自行承担。</h3>" +
                "<h3>2、使用本APP的保护功能时，若因用户个人原因未及时取消保护状态，所引起的后果由用户自行承担。</h3>" +
                "<h3>3、使用本APP的安全路径规划功能时，app所规划的路径并非绝对安全，是否采用由用户自己斟酌，所带来的安全隐患，APP开发者概不负责。</h3>" +
                "<h3>4、APP转载的内容并不代表APP开发者之意见及观点，也不意味着本网赞同其观点或证实其内容的真实性。</h3>" +
                "<h3>5、用户明确并同意其使用APP网络服务所存在的风险将完全由其本人承担；因其使用APP网络服务而产生的一切后果也由其本人承担，APP开发者对此不承担任何责任。</h3>" +
                "<h3>6、对于因不可抗力或因黑客攻击、通讯线路中断等APP不能控制的原因造成的网络服务中断或其他缺陷，导致用户不能正常使用APP，APP不承担任何责任，但将尽力减少因此给用户造成的损失或影响。</h3>" +
                "<h3>7、本声明未涉及的问题请参见国家有关法律法规，当本声明与国家有关法律法规冲突时，以国家法律法规为准。</h3>" +
                "<h3>8、本app的最终解释权归开发者所有。</h3>" +
                "</body></html>";
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());//滚动
        tv.setText(Html.fromHtml(html));//Html.FROM_HTML_MODE_COMPACT


    }
}
