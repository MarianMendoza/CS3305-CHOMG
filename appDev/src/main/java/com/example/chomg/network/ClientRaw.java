package com.example.chomg.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class ClientRaw {
    private static Retrofit retrofitRaw = null;

    public static Retrofit getClientRaw(String baseUrl) {
        if (retrofitRaw == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            // build without gson converter
            retrofitRaw = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(httpClient.build())
                    .build();
        }
        return retrofitRaw;
    }
}