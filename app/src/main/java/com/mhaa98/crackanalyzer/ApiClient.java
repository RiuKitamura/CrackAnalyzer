package com.mhaa98.crackanalyzer;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{   public static final String B_URL = "https://thrisnaspartan.000webhostapp.com/";
    public static Retrofit retrofit = null;


    public static Retrofit getApiClient()
    {   OkHttpClient.Builder okhttpBuilder = new OkHttpClient().newBuilder();
        okhttpBuilder.connectTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.writeTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.readTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.retryOnConnectionFailure(true);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okhttpBuilder.addInterceptor(interceptor);
        }

        if(retrofit==null)
        {   retrofit = new Retrofit.Builder().baseUrl(B_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}