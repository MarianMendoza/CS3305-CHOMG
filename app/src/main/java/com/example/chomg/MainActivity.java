package com.example.chomg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
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

        // Set up the notification manager
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel with the specified ID and importance
            NotificationChannel channel = new NotificationChannel("channel1", "Motion Detected", NotificationManager.IMPORTANCE_DEFAULT);

            // Set up the custom sound for the channel
            Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.motiondetectedsound);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            channel.setSound(soundUri, audioAttributes);
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
        }

        // Set an OnClickListener on the button
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build the notification when the button is clicked
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "channel1") // Use the same channel ID
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
