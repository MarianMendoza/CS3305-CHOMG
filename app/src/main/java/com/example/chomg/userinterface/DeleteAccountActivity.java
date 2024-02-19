package com.example.chomg.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chomg.R;
import com.example.chomg.SecureStorage;
import com.example.chomg.network.Api;
import com.example.chomg.network.Client;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deletaaccount);

        Button buttonBack = findViewById(R.id.buttonBack);
        Button buttonDelete = findViewById(R.id.buttonDeleteAccountConfirm);

        // Set click listener for the buttonBack
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to return to the previous one (fragmentSettings)
                finish();
            }
        });
         buttonDelete.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v){
                 deleteAccount();
             }
         });

    }

    private void deleteAccount(){
        String token = SecureStorage.getAuthToken(this);
        if (token == null) {
            Toast.makeText(this, "You're not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        Api apiService = Client.getClient("https://178.62.75.31").create(Api.class);
        Call<Void> call = apiService.deleteAccount("Bearer " + token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DeleteAccountActivity.this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                    SecureStorage.clearAuthToken(DeleteAccountActivity.this);
                    // Navigate back to login or any other appropriate activity
                    Intent intent = new Intent(DeleteAccountActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(DeleteAccountActivity.this, "Failed to delete account.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DeleteAccountActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}