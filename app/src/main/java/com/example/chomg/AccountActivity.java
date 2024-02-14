package com.example.chomg;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chomg.data.User;
import com.example.chomg.network.Api;
import com.example.chomg.network.Client;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountdetails);

        Button buttonBack = findViewById(R.id.buttonBack);
        TextView email = findViewById(R.id.textViewEmailRect);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to return to the previous one (fragmentSettings)
                finish();
            }
        });
//         getUserDetails();
    }
//    private void getUserDetails(){
//        String authToken = "Bearer " + YOUR_AUTH_TOKEN; // Retrieve your stored auth token
//        Api service = Client.getClient("YOUR_BASE_URL").create(Api.class);
//        Call<User> call = service.getUserDetails(authToken);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    email.setText(response.body().getEmail());
//                } else {
//                    // Handle errors
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserDetails> call, Throwable t) {
//                // Handle failure
//            }
//        };
//    }
}
