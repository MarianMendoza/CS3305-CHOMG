package com.example.chomg.service;
import static com.example.chomg.SecureStorage.getAuthToken;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chomg.SecureStorage;
import com.example.chomg.data.FcmToken;
import com.example.chomg.network.Api;
import com.example.chomg.network.Client;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "tag";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Handle FCM messages here
    }
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // Send the token to your app server to keep the user's token up-to-date.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String fcmToken) {

        String authToken = SecureStorage.getAuthToken(this);
        if (authToken == null) {
            Log.e(TAG, "Authentication token not found.");
            return;
        }

        Api apiService = Client.getClient("https://178.62.75.31").create(Api.class);
        // Assuming you have an API method to update the user's FCM token
        Call<Void> call = apiService.updateFcmToken(authToken, fcmToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Token updated successfully on the server.");
                } else {
                    Log.d(TAG, "Failed to update token on the server.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error updating token on the server: " + t.getMessage());
            }
        });
    }
    // Implement other overrides as needed
}
