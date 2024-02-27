package com.example.chomg.userinterface;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chomg.R;
import com.example.chomg.SecureStorage;
import com.example.chomg.data.User;
import com.example.chomg.network.Api;
import com.example.chomg.network.Client;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {

    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountdetails);

        Button buttonBack = findViewById(R.id.buttonBack);
        emailTextView = findViewById(R.id.textViewEmailRect);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUserDetails();
    }

    private void getUserDetails() {
        // retrieve the stored authentication token securely
        String authToken = "Bearer " + SecureStorage.getAuthToken(this);
        if (authToken == null) {
            View rootView = findViewById(android.R.id.content);
            Snackbar.make(rootView, "Authentication token missing. Please log in again.", Snackbar.LENGTH_LONG).show();
            return;
        }
        Api service = Client.getClient("https://178.62.75.31").create(Api.class);

        // Adjust the call object to match the expected response type, assuming getUserDetails returns a User object
        Call<User> call = service.getUserDetails(authToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("AccountActivity", "API Response: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AccountActivity", "Raw JSON: " + response.raw().body());
                    // Assuming the server response includes the user's email
                    String email = response.body().getEmail();
                    Log.d("AccountActivity", "User Email: " + email);// Or however you get the email from the response
                    emailTextView.setText(email);
                } else {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Failed to load email. Error code: " + response.code(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Handle failure, for example, by showing an error message or logging the failure
                emailTextView.setText("Error fetching details.");
            }
        });
    }
}
