package com.petplant.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        //findViewById(R.id.firstLayout).setBackgroundResource(R.drawable.gradation_background);
    }

    protected void onLoginButtonPressed(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void onRegisterButtonPressed(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onBackPressed() {
        ActivityCompat.finishAffinity(this);
        System.runFinalization();
        System.exit(0);
    }
}
