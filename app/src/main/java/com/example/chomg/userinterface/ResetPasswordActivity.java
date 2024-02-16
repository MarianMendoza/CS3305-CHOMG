package com.example.chomg.userinterface;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chomg.R;
import com.example.chomg.SecureStorage;
import com.example.chomg.network.Api;
import com.example.chomg.network.Client;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        Button buttonBack = findViewById(R.id.buttonBack);
        Button buttonChangePassword = findViewById(R.id.buttonResetPassword);

        // Set click listener for the buttonBack
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to return to the previous one (fragmentSettings)
                finish();
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assume you have EditTexts for currentPassword and newPassword
                EditText newPasswordEditText = findViewById(R.id.newPassword);
                EditText confirmPasswordEditText = findViewById(R.id.confirmPassword);

                String currentPassword = newPasswordEditText.getText().toString();
                String newPassword = confirmPasswordEditText.getText().toString();

                if (!currentPassword.equals(newPassword)) {
                    Toast.makeText(ResetPasswordActivity.this, "Passwords must match!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkPasswordFormat(newPassword)) {
                    Toast.makeText(ResetPasswordActivity.this, "Password must be at least 6 letters long, and contain a capital letter, a lower letter, a number and a special character!", Toast.LENGTH_SHORT).show();
                    return;
                }

                changePassword(currentPassword, newPassword);
            }
        });
    }

    private void changePassword(String currentPassword, String newPassword){
        String token = SecureStorage.getAuthToken(this);
        Api apiService = Client.getClient("https://178.62.75.31").create(Api.class);
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

        Call<ResponseBody> call = apiService.setNewPassword("Bearer " + token, request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                    finish(); // Optionally close the activity or navigate as needed
                } else {
                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                        Log.e("ResetPasswordError", errorBody); // Correctly use Log.e without declaring a Log object
                        // Optionally, parse the error body to display a more specific error message
                    } catch (IOException e) {
                        Log.e("ResetPasswordError", "Error parsing error body", e); // Correctly use Log.e
                    }
                    Toast.makeText(ResetPasswordActivity.this, "Failed to change password. Error: " + errorBody, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean checkPasswordFormat(String password) {
        // need one upper letter, one number, special character and min 6 in length
        String regexPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{6,}$";
        return Pattern.matches(regexPattern, password);
    }
}
