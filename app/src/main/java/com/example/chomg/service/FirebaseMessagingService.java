package com.example.chomg.service;
import static com.example.chomg.SecureStorage.getAuthToken;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;



import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.chomg.R;
import com.example.chomg.SecureStorage;
import com.example.chomg.data.FcmToken;
import com.example.chomg.network.Api;
import com.example.chomg.network.Client;
import com.example.chomg.userinterface.FragmentHome;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.time.Instant;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "tag";




    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            String userId = remoteMessage.getData().get("user_id");
            boolean motionDetected = Boolean.parseBoolean(remoteMessage.getData().get("is_motion_detected"));
            boolean humanDetected = Boolean.parseBoolean(remoteMessage.getData().get("is_human_detected"));
            String expirationTime = remoteMessage.getData().get("exp");

            System.out.println(motionDetected);
            System.out.println(humanDetected);
            System.out.println(expirationTime);

            int expTime = Optional.ofNullable(expirationTime)
                    .map(Integer::parseInt)
                    .orElse(-1);

            Instant currentTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                currentTime = Instant.now();
            }
            if (motionDetected) {
                createNotification("Motion Detected", "Motion has been detected.");
            } else if (humanDetected) {
                createNotification("Person Detected", "A person has been detected.");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (expTime != -1 && currentTime.getEpochSecond() > expTime) {
                    createNotification("Connection Lost", "Connection has been lost.");
                }


            }
        }
        }


    private void createNotification(String title, String body) {
        Intent intent = new Intent(this, FragmentHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.chomgiconwb)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MotionDetect", "DetectMotion", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String fcmToken) {

        String authToken = SecureStorage.getAuthToken(this);
        if (authToken == null) {
            Log.e(TAG, "Authentication token not found.");
            return;
        }

        Api apiService = Client.getClient("https://178.62.75.31").create(Api.class);
        FcmToken tokenObject = new FcmToken(fcmToken);
        // Assuming you have an API method to update the user's FCM token
        Call<Void> call = apiService.updateFcmToken(authToken, tokenObject);

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
}
