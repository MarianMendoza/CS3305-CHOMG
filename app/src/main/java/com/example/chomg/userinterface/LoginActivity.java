package com.example.chomg.userinterface;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chomg.R;
import com.example.chomg.SecureStorage;
import com.example.chomg.data.TokenResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chomg.data.User;
import com.example.chomg.network.Api;
import com.example.chomg.network.Client;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.buttonLogIn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClick(v);
            }
        });

        Button signUpButton = findViewById(R.id.buttonSignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpButtonClick(v);
            }
        });

        Button forgotPasswordButton = findViewById(R.id.buttonForgotPassword);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){
                onForgotButtonClick(v);
            }
        });

        clearAuthToken();

    }

    private void clearAuthToken() {
        SecureStorage.clearAuthToken(this);
    }

    public void onLoginButtonClick(View view) {
        EditText emailEditText = findViewById(R.id.editTextEmailAddress);
        EditText passwordEditText = findViewById(R.id.editTextPassword);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String fcmToken = task.getResult();

                        // Proceed with validating credentials and logging in
                        isValidCredentials(email, password, fcmToken);
                    }
                });
    }


    private void isValidCredentials(final String email, final String password, final String fcmToken) {
        Api apiService = Client.getClient("https://178.62.75.31").create(Api.class); // Make sure to use your actual API URL

        User user = new User(email, password, fcmToken);
        Call<TokenResponse> call = apiService.loginUser(user);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken(); // Assuming getToken() retrieves the token
                    SecureStorage.saveAuthToken(LoginActivity.this, token);

                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    MediaPlayer mediaPlayer = MediaPlayer.create(LoginActivity.this, R.raw.loginsound2);
                    mediaPlayer.start();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);

                } else {
                    // Login failed, show an error message
                    Toast.makeText(LoginActivity.this, "Invalid email or password. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                // Handle failure, possibly due to no internet connection, or server down
                View rootView = findViewById(android.R.id.content);
                Snackbar.make(rootView, "Login failed: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }


    public void onSignUpButtonClick(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onForgotButtonClick(View view){
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }


}
