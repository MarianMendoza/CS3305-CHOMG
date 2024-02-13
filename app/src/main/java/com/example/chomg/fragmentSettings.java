package com.example.chomg;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

public class fragmentSettings extends Fragment {

    private Switch switchAppNot;
    private Switch switchEmailNot;
    private Button buttonLogout;

    private Button buttonViewAccount;

    private Button buttonChangePassword;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        switchAppNot = view.findViewById(R.id.switchAppNot);
        switchEmailNot = view.findViewById(R.id.switchEmailNot);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        buttonViewAccount = view.findViewById(R.id.buttonViewAccount);
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword);


        // Set switch listeners to change color
        switchAppNot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
//                Notification settings
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
                switchAppNot.setThumbTintList(getResources().getColorStateList(R.color.your_new_thumb_color_false));
                switchAppNot.setTrackTintList(getResources().getColorStateList(R.color.your_new_track_color_false));
            }
        });

        // Set button click listener to logout
        buttonLogout.setOnClickListener(v -> {
            // Navigate to LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish(); // Close the current activity after navigating to LoginActivity
        });

        // Set button click listener to go to AccountActivity
        buttonViewAccount.setOnClickListener(v -> {
            // Navigate to AccountActivity
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            startActivity(intent);
        });

        buttonChangePassword.setOnClickListener(v -> {
            // Navigate to AccountActivity
            Intent intent = new Intent(getActivity(),resetPasswordActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
