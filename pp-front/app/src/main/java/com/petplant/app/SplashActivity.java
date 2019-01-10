package com.petplant.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                overridePendingTransition(0, android.R.anim.fade_in);
                SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                String seesionID = mPref.getString("autologin","");
                Intent intent;
                if(!seesionID.isEmpty()){
                    intent = new Intent(SplashActivity.this,MainActivity.class);
                }else{
                    intent = new Intent(SplashActivity.this,FirstActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME);
    }
}
