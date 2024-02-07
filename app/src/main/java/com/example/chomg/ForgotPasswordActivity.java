package com.example.chomg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chomg.network.Api;
import com.example.chomg.network.Client;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        Button signUpButton = findViewById(R.id.buttonSignUp);
        Button continueButton = findViewById(R.id.continueButton);
        EditText emailText = findViewById(R.id.editTextEmail);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                sendEmail(email);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the SignUpActivity when the sign-up button is pressed
                Intent intent = new Intent(ForgotPasswordActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendEmail(String email){
        EmailWrapper emailWrapper = new EmailWrapper(email);
        Api apiService = Client.getClient("https://178.62.75.31").create(Api.class);
        Call<ResponseBody> call = apiService.forgotPassword(emailWrapper);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Password reset email successfully sent
                    // Handle the successful response here
                    Toast.makeText(ForgotPasswordActivity.this, "Forgot email password successfully sent!", Toast.LENGTH_SHORT).show();
                } else {
                    switch (response.code()) {
                        case 404:
                            Toast.makeText(ForgotPasswordActivity.this, "Email address not found!", Toast.LENGTH_SHORT).show();
                        case 500:
                            Toast.makeText(ForgotPasswordActivity.this, "Internal server error. Please wait some time and try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // An error occurred during the network request
                System.out.println("Error sending password reset email: " + t.getMessage());
            }
        });

    }
}
