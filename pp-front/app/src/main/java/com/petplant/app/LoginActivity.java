package com.petplant.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences mPref;
    SharedPreferences.Editor editor;

    CheckBox rememberChecker;
    CheckBox autoLoginChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        editor = mPref.edit();

        String isRemember = mPref.getString("remember","");
        if(!isRemember.isEmpty()){
            String id = mPref.getString("session","");
            ((EditText)findViewById(R.id.login_email_form)).setText(id);
            rememberChecker = (CheckBox)findViewById(R.id.remember_checker);
            rememberChecker.setChecked(true);
        }
    }

    public void onSubmitClicked(View view){

        rememberChecker = (CheckBox)findViewById(R.id.remember_checker);
        autoLoginChecker = (CheckBox)findViewById(R.id.auto_checker);

        final String email = ((EditText)findViewById(R.id.login_email_form)).getText().toString();
        String password = ((EditText)findViewById(R.id.login_pw_form)).getText().toString();
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("email",email);
        params.put("password",password);
        RestAPI rest = RestAPI.retrofit.create(RestAPI.class);
        Call<ResponseBody> call = rest.login(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String,String>>(){}.getType();
                    Map<String,String> myMap = gson.fromJson(response.body().string(),type);
                    if(myMap.get("success").equals("true")){
                        Toast.makeText(LoginActivity.this,"성공",Toast.LENGTH_SHORT).show();

                        editor.putString("session",email);
                        editor.commit();

                        if(rememberChecker.isChecked()){
                            editor.putString("remember","yes");
                            editor.commit();
                        }else{
                            editor.remove("remember");
                            editor.commit();
                        }

                        if(autoLoginChecker.isChecked()){
                            editor.putString("autologin","yes");
                            editor.commit();
                        }else{
                            editor.remove("autologin");
                            editor.commit();
                        }

                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this,"로그인 실패! 로그인 정보를 확인해주세요",Toast.LENGTH_SHORT).show();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    //------------------------ Life Cycle -----------------------//
    @Override
    public void onDestroy() {
        Log.d("log", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("log", "onPause");
        super.onPause();
    }
    @Override
    protected void onRestart() {
        Log.d("log", "onRestart");
        super.onRestart();
    }
    @Override
    protected void onResume() {
        Log.d("log", "onResume");
        super.onResume();
    }
    @Override
    protected void onStart() {
        Log.d("log", "onStart");
        super.onStart();
    }
    @Override
    protected void onStop() {
        Log.d("log", "onStop");
        super.onStop();
    }
    //------------------------ Life Cycle -----------------------//

}
