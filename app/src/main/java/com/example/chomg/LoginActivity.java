package com.example.chomg;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    // Other variables...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Other initialization...

        Button loginButton = findViewById(R.id.button2);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClick(v);
            }
        });
    }

    public void onLoginButtonClick(View view) {
        EditText emailEditText = findViewById(R.id.editTextTextEmailAddress5);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword2);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Check if the email and password are correct (replace with your authentication logic)
        if (isValidCredentials(email, password)) {
            // Login successful, navigate to the next screen or perform the necessary action
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        } else {
            // Login failed, show an error message
            Toast.makeText(this, "Invalid email or password. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidCredentials(String email, String password) {
        // Implement your authentication logic here
        // For demonstration purposes, let's assume the correct email and password are "demo" and "password"
        return email.equals("demo") && password.equals("password");
    }

    // Other methods...
}
