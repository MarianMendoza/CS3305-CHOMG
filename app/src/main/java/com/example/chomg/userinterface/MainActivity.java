package com.example.chomg.userinterface;

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
import android.widget.EditText;

import com.example.chomg.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();
        initializeNotifications();

    }
    protected void initializeUI(){
        setContentView(R.layout.activity_main);

        final EditText emailText = findViewById(R.id.email);
        final EditText passwordText = findViewById(R.id.password);
        Button submitButton = findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
            }
        });

    }
    protected void initializeNotifications(){
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Button notificationButton = findViewById(R.id.notificationButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel with the specified ID and importance
            NotificationChannel channel = new NotificationChannel("channel2", "Motion Detected", NotificationManager.IMPORTANCE_DEFAULT);

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
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build the notification when the button is clicked
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "channel2") // Use the same channel ID
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
