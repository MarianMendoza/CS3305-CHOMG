package com.example.chomg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the button by its ID
        Button myButton = findViewById(R.id.myButton);

        // Set up the notification manager and channel
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("my_channel_id", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Set an OnClickListener on the button
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build the notification when the button is clicked
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "my_channel_id")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Security Alert")
                        .setContentText("Movement detected!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(123, builder.build());
            }
        });
    }
}
