package com.wisdomxunjian.com.povertyalleviation;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by admin on 2017/4/15.
 */

public class BeseActivity extends AppCompatActivity {


    /**
     * 设置标题栏
     */
    protected void setText(int l,int x,int r,String title){
             findViewById(R.id.bt_heander_l).setVisibility(l);
        TextView tv= (TextView) findViewById(R.id.tv_head_title);
        tv.setVisibility(x);
        tv.setText(title);
        findViewById(R.id.bt_heander_r).setVisibility(r);
       }
    /**
     * 标题栏左侧按钮点击事件
     */
       protected void activityFinish(View view){
           finish();
       }
    /**
     * 标题栏右侧按钮点击事件
     */
       protected void activityXin(View view){

       }
}
