package com.example.chomg.network;

import com.example.chomg.userinterface.ChangePasswordRequest;
import com.example.chomg.userinterface.EmailWrapper;
import com.example.chomg.data.TokenResponse;
import com.example.chomg.data.User;
import com.example.chomg.userinterface.MotionDetectionResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Api {
    @POST("/register")
    Call<Void> registerUser(@Body User user);

    @POST("/login")
    Call<TokenResponse> loginUser(@Body User user);

    @POST("/logout")
    Call<Void> userLogout(@Header("Authorization") String authToken);

    @POST("/forgot-password")
    Call<ResponseBody> forgotPassword(@Body EmailWrapper emailWrapper);

    @POST("/change-email")
    Call<ResponseBody> changeEmail(@Header("Authorization") String authToken, @Body EmailWrapper emailChangeRequest);

    @POST("/set-new-password")
    Call<ResponseBody> setNewPassword(@Header("Authorization") String authToken, @Body ChangePasswordRequest request);

    @DELETE("/delete-account")
    Call<Void> deleteAccount(@Header("Authorization") String authToken);

    @GET("/user-details")
    Call<User> getUserDetails(@Header("Authorization") String authToken);

    @GET("/get-recent-video")
    Call<ResponseBody> getRecentVideo(@Header("Authorization") String authToken);

    @POST("/motion-detected")
    Call<MotionDetectionResponse> getMotionDetectionData();

}
