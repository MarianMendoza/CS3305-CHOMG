package com.example.chomg.userinterface;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.chomg.R;
import com.example.chomg.SecureStorage;
import com.example.chomg.network.Api;
import com.example.chomg.network.Client;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentSettings extends Fragment {

    private Switch switchAppNot;
    private Switch switchEmailNot;

    private Button buttonLogout;
    private Button buttonViewAccount;
    private Button buttonChangePassword;
    private Button buttonDeleteAccount;
    private Button buttonChangeEmail;
    private Button buttonSetUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);


        switchAppNot = view.findViewById(R.id.switchAppNot);
        switchEmailNot = view.findViewById(R.id.switchEmailNot);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        buttonViewAccount = view.findViewById(R.id.buttonViewAccount);
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword);
        buttonDeleteAccount = view.findViewById(R.id.buttonDeleteAccount);
        buttonChangeEmail = view.findViewById(R.id.buttonChangeEmail);
        buttonSetUp = view.findViewById(R.id.buttonSetUp);

        fetchMotionDetectionData();

        switchAppNot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchAppNot.setThumbTintList(getResources().getColorStateList(R.color.your_new_thumb_color_true));
                switchAppNot.setTrackTintList(getResources().getColorStateList(R.color.your_new_track_color_true));

            } else {
                switchAppNot.setThumbTintList(getResources().getColorStateList(R.color.your_new_thumb_color_false));
                switchAppNot.setTrackTintList(getResources().getColorStateList(R.color.your_new_track_color_false));
            }
        });

        switchEmailNot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchEmailNot.setThumbTintList(getResources().getColorStateList(R.color.your_new_thumb_color_true));
                switchEmailNot.setTrackTintList(getResources().getColorStateList(R.color.your_new_track_color_true));
            } else {
                switchEmailNot.setThumbTintList(getResources().getColorStateList(R.color.your_new_thumb_color_false));
                switchEmailNot.setTrackTintList(getResources().getColorStateList(R.color.your_new_track_color_false));
            }
        });

        buttonLogout.setOnClickListener(v -> {
            logout();
        });

        buttonViewAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            startActivity(intent);
        });

        buttonChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ResetPasswordActivity.class);
            startActivity(intent);
        });

        buttonChangeEmail.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangeEmailActivity.class);
            startActivity(intent);
        });

        buttonDeleteAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DeleteAccountActivity.class);
            startActivity(intent);
        });



        buttonSetUp.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SetUpActivity.class);
            startActivity(intent);
        });


        return view;
    }

    private void fetchMotionDetectionData() {
        Api apiService = Client.getClient("https://178.62.75.31").create(Api.class);
        Call<MotionDetectionResponse> call = apiService.getMotionDetectionData();
        call.enqueue(new Callback<MotionDetectionResponse>() {
            @Override
            public void onResponse(Call<MotionDetectionResponse> call, Response<MotionDetectionResponse> response) {
                if (response.isSuccessful()) {
                    MotionDetectionResponse motionDetectionResponse = response.body();
                    handleNotification(motionDetectionResponse);
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch notification data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<MotionDetectionResponse> call, Throwable t) {
                Log.e("FetchNotificationData", "Error fetching notification data: " + t.getMessage());
                Toast.makeText(getActivity(), "Failed to fetch notification data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleNotification(MotionDetectionResponse motionDetectionResponse) {
        Log.d("MotionDetection", "User ID: " + motionDetectionResponse.getUserId());
        Log.d("MotionDetection", "Motion Detected: " + motionDetectionResponse.isMotionDetected());
        Log.d("MotionDetection", "Person Detected: " + motionDetectionResponse.isPersonDetected());
        Log.d("MotionDetection", "Expiration Time: " + motionDetectionResponse.getExpirationTime());
    }


    public void logout(){
        String token = SecureStorage.getAuthToken(requireContext());
        if (token != null) {
            Api apiService = Client.getClient("https://178.62.75.31").create(Api.class);
            Call<Void> call = apiService.userLogout("Bearer " + token);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Logout failed", Toast.LENGTH_SHORT).show();
                    }
                    SecureStorage.clearAuthToken(requireContext());
                    goToLogin();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getActivity(), "Logout error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    SecureStorage.clearAuthToken(requireContext());
                    goToLogin();
                }
            });
        } else {
            goToLogin();
        }
    }


    private void goToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }




}
