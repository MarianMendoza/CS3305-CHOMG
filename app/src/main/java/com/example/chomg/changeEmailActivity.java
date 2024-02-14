package com.example.chomg;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class changeEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeemail);

        Button buttonBack = findViewById(R.id.buttonBack);

        // Set click listener for the buttonBack
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to return to the previous one (fragmentSettings)
                finish();
            }
        });
    }
}