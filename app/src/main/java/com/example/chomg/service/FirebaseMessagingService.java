package com.example.chomg.service;
import static com.example.chomg.SecureStorage.getAuthToken;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.chomg.userinterface.FragmentSettings;
import com.example.chomg.userinterface.HomeActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.time.Instant;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService implements FragmentSettings.SwitchListener {

    private static final String TAG = "tag";

    private boolean switchChecked = false;



    @Override
    public void onSwitchChanged(boolean isChecked) {
        switchChecked = isChecked;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCM Message", "From: " + remoteMessage.getFrom());

        SharedPreferences sharedPreferences = getSharedPreferences("AppSettingsPrefs", Context.MODE_PRIVATE);
        boolean notificationsEnabled = sharedPreferences.getBoolean("NotificationsEnabled", true); // Default is true
        Log.d("FirebaseMsgService", "Read NotificationsEnabled: " + notificationsEnabled);

        if (!notificationsEnabled) {
            return;
        }

        String title = "";
        String body = "";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();

            Log.d("FCM Notification", "Title: " + title + ", Body: " + body);

            createNotification(title, body);
        }

    }



    private void createNotification(String title, String body) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("openFragment", "FragmentHome");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "MotionDetectChannel";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.chomgiconwb)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Motion Detection Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, notificationBuilder.build());
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