package com.example.chomg.userinterface;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chomg.R;
import com.example.chomg.SecureStorage;
import com.example.chomg.network.Api;
import com.example.chomg.network.Client;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeemail);

        Button buttonBack = findViewById(R.id.buttonBack);
        EditText newEmailAddress = findViewById(R.id.newEmailAddress);
        Button buttonChangeEmail = findViewById(R.id.buttonResetButton);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to return to the previous one (fragmentSettings)
                finish();
            }
        });

        buttonChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = newEmailAddress.getText().toString();
                updateUserEmail(newEmail);
            }
        });
    }
    private void updateUserEmail(String newEmail){
        String token = SecureStorage.getAuthToken(this);
        Api apiService = Client.getClient("https://178.62.75.31").create(Api.class);
        EmailWrapper emailWrapper = new EmailWrapper(newEmail);

        Call<ResponseBody> call = apiService.changeEmail("Bearer " + token, emailWrapper);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangeEmailActivity.this, "Email updated successfully.", Toast.LENGTH_SHORT).show();
                    // Handle successful email update
                } else {
                    // Handle non-successful response, e.g., email already exists
                    try {
                        // Attempt to parse the error message from the response body
                        String errorBody = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorBody);
                        String errorMessage = jsonObject.optString("error", "Failed to update email."); // "error" is the key in your JSON error response
                        Toast.makeText(ChangeEmailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ChangeEmailActivity.this, "Failed to update email.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ChangeEmailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}