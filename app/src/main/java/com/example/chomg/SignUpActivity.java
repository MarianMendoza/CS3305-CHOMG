package com.example.chomg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chomg.data.User;
import com.example.chomg.network.Api;
import com.example.chomg.network.Client;
import com.google.android.material.snackbar.Snackbar;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button loginButton = findViewById(R.id.buttonLogin);
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

        if (!checkEmailFormat(emailString)) {
            Toast.makeText(SignUpActivity.this, "Invalid email format!", Toast.LENGTH_SHORT).show();
        } else if (!checkPasswordFormat(passwordString)) {
            Toast.makeText(SignUpActivity.this, "Password must contain numbers, letters, a capital letter, and a special character", Toast.LENGTH_SHORT).show();
        } else if (!passwordString.equals(passwordConfirmationString)){
            Toast.makeText(SignUpActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        } else {
            registerUser(emailString, passwordString);
        }

    }

    private void registerUser(final String email, final String password){
        Api apiService = Client.getClient("https://178.62.75.31").create(Api.class);

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
                View rootView = findViewById(android.R.id.content);
                Snackbar.make(rootView, "Registration failure " + t.getMessage(), Snackbar.LENGTH_LONG).show();
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

    private boolean checkEmailFormat(String email){
        String regexPattern = "^(.+)@(\\S+)$";
        return Pattern.matches(regexPattern, email);
    }

    private ImageView imageViewMinLen;
    private ImageView imageViewUppercase;
    private ImageView imageViewNumeric;
    private ImageView imageSpecial;



    private void updatePasswordStrength(String password) {
        boolean isFormatValid = checkPasswordFormat(password);

        // Update the images based on the result
        updateImageView(imageViewMinLen, isFormatValid);
        updateImageView(imageViewUppercase, isFormatValid);
        updateImageView(imageViewNumeric, isFormatValid);
        updateImageView(imageSpecial, isFormatValid);
    }

    private boolean checkPasswordFormat(String password) {
        // need one upper letter, one number, special character and min 6 in length
        String regexPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{6,}$";
        return Pattern.matches(regexPattern, password);
    }

    private void updateImageView(ImageView imageView, boolean isConditionMet) {
        if (isConditionMet) {
            imageView.setImageResource(R.drawable.check_circle_24px); // Image when condition is met
        } else {
            imageView.setImageResource(R.drawable.check_circle_24q); // Image when condition is not met
        }
    }

}
