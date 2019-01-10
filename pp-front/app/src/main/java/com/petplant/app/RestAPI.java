package com.petplant.app;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RestAPI {

    final static String BASE_URL = "http://117.16.136.73:8080/";
    final static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    @FormUrlEncoded
    @POST("users")
    Call<ResponseBody> createUser(@FieldMap HashMap<String,String> info);

    @FormUrlEncoded
    @POST("users/validation")
    Call<ResponseBody> login(@FieldMap HashMap<String,String> info);
}
