package com.example.chomg.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class ClientRaw {
    private static Retrofit retrofitRaw = null;

    public static Retrofit getClientRaw(String baseUrl) {
        if (retrofitRaw == null) {
            // Set up logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configure OkHttpClient
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            // Initialize Retrofit instance for raw responses
            retrofitRaw = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    // Notice: No GsonConverterFactory is added here
                    .client(httpClient.build())
                    .build();
        }
        return retrofitRaw;
    }
}