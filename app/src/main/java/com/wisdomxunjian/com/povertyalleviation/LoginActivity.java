package com.wisdomxunjian.com.povertyalleviation;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {
   private SharedPreferences sp_demo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         sp_demo = getSharedPreferences("sp_demo", Context.MODE_PRIVATE);

    }
}
