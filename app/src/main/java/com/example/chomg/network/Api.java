package com.example.chomg.network;

import com.example.chomg.data.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {
    @POST("/register")
    Call<Void> registerUser(@Body User user);

    @POST("/login")
    Call<Void> loginUser(@Body User user);
}
