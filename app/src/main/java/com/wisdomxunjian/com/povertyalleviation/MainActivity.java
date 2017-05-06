package com.wisdomxunjian.com.povertyalleviation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wisdomxunjian.com.povertyalleviation.qiandao.DialogActivity;
import com.wisdomxunjian.com.povertyalleviation.service.GPSService;
import com.wisdomxunjian.com.povertyalleviation.qiandao.QianDaoActivity;

public class MainActivity extends BeseActivity {
    private SharedPreferences sp_demo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setText(View.GONE,View.VISIBLE,View.GONE,"精准扶贫");
        startService(new Intent(MainActivity.this, GPSService.class));
        sp_demo = getSharedPreferences("sp_demo", Context.MODE_PRIVATE);
        sp_demo.edit().putInt("isqiandao", -1).commit();
    }
    protected void onClick(View v){
        LinearLayout ll= (LinearLayout) v;
        TextView tv= (TextView) ll.getChildAt(1);
//        tv.getText();
     switch (tv.getText().toString()){
         case "签到":
             startActivity(new Intent(MainActivity.this, QianDaoActivity.class));
             break;

     }
    }
}
