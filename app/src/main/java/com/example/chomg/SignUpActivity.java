package com.example.chomg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chomg.data.User;
import com.example.chomg.network.Api;
import com.example.chomg.network.Client;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button loginButton = findViewById(R.id.button5);
        Button signUpButton = findViewById(R.id.signUpButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LoginActivity
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Optional: finish the SignUpActivity to remove it from the stack
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onSignUpButtonClick();
            }
        });
    }

    public void onSignUpButtonClick(){
        EditText emailEditText = findViewById(R.id.emailAddress);
        EditText password = findViewById(R.id.password);
        EditText passwordConfirmation = findViewById(R.id.passwordConfirmation);

        String emailString = emailEditText.getText().toString();
        String passwordString = password.getText().toString();
        String passwordConfirmationString = passwordConfirmation.getText().toString();

        if (!passwordString.equals(passwordConfirmationString)){
            Toast.makeText(SignUpActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        } else {
            registerUser(emailString, passwordString);
        }

    }

    private void registerUser(final String email, final String password){
        Api apiService = Client.getClient("http://10.0.2.2:3000").create(Api.class);

        User user = new User(email, password);
        Call<Void> call = apiService.registerUser(user);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showLoginRedirectMessage(findViewById(R.id.root_layout));
                } else if (response.code() == 400) {
                    Toast.makeText(SignUpActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Registration Failure" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoginRedirectMessage(View view) {
        Snackbar snackbar = Snackbar.make(view, "User has been successfully registered. Click " +
                        "login button below to be redirected to login page.", Snackbar.LENGTH_LONG)
                .setAction("Login", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Perform your action here
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
        snackbar.show();
    }
}
