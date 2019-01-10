package com.petplant.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onSubmitClicked(View view){

        final String email = ((EditText)findViewById(R.id.register_email_form)).getText().toString();
        String password = ((EditText)findViewById(R.id.register_pw_form)).getText().toString();
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("email",email);
        params.put("password",password);
        RestAPI registerService = RestAPI.retrofit.create(RestAPI.class);
        Call<ResponseBody> call = registerService.createUser(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String,String>>(){}.getType();
                    Map<String,String> myMap = gson.fromJson(response.body().string(),type);
                    if(myMap.get("success").equals("true")){
                        Toast.makeText(RegisterActivity.this,"성공",Toast.LENGTH_SHORT).show();

                        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putString("session",email);
                        editor.commit();

                        Intent intent = new Intent(RegisterActivity.this,ProfileActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(RegisterActivity.this,myMap.get("details"),Toast.LENGTH_SHORT).show();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}