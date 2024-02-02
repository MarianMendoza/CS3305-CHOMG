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
    }

    public void onLoginButtonClick(View view) {
        EditText emailEditText = findViewById(R.id.editTextEmailAddress);
        EditText passwordEditText = findViewById(R.id.editTextPassword);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        isValidCredentials(email, password);
    }


    private void isValidCredentials(final String email, final String password) {
        Api apiService = Client.getClient("http://10.0.2.2:3000").create(Api.class);

        User user = new User(email, password);
        Call<Void> call = apiService.loginUser(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Login successful, navigate to the next screen or perform the necessary action
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);

                } else {
                    // Login failed, show an error message
                    Toast.makeText(LoginActivity.this, "Invalid email or password. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure, possibly due to no internet connection, or server down
                Toast.makeText(LoginActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSignUpButtonClick(View view) {
        // Start the UserSignUpActivity when the "SIGN UP" button is clicked
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onForgotButtonClick(View view){
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }


}
