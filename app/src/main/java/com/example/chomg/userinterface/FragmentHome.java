package com.example.chomg.userinterface;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.example.chomg.R;

public class FragmentHome extends Fragment {

    private static final int REQUEST_PERMISSION_CODE = 123;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the button and set click listener
//        Button buttonDownload = rootView.findViewById(R.id.buttonDownload);
//        buttonDownload.setOnClickListener(v -> checkPermissionAndDownload());

        LinearLayout chatContainer = rootView.findViewById(R.id.chatContainer);

//          Change here for textview
        String[] activityNames = {"Activity 1", "Activity 2", "Activity 3", "Activity 4", "Activity 5","Activity 6","Activity 7"};

        for (String activityName : activityNames) {
            RelativeLayout relativeLayout = new RelativeLayout(requireContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 8, 0, 0);
            relativeLayout.setLayoutParams(layoutParams);

            TextView textView = new TextView(requireContext());
            RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            textViewParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            textView.setWidth(3500);
            textView.setHeight(170);
            textView.setLayoutParams(textViewParams);
            textView.setBackgroundResource(R.drawable.shape1);
            textView.setPadding(8, 8, 8, 8);

            textView.setTextSize(16);

            textView.setText(activityName);

            Button button = new Button(requireContext());
            RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            buttonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            button.setLayoutParams(buttonParams);
            button.setBackgroundResource(R.drawable.borderless_button_background);

            Drawable icon = getResources().getDrawable(R.drawable.download_24px);

            if (icon != null) {
                icon = DrawableCompat.wrap(icon);
                DrawableCompat.setTint(icon, Color.BLACK);
            }
            button.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);

            relativeLayout.addView(textView);
            relativeLayout.addView(button);

            chatContainer.addView(relativeLayout);
        }

        return rootView;
    }

    private void checkPermissionAndDownload() {
        // Check if permission is not granted
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        } else {
            // Permission has already been granted
            // Proceed with the download operation
            downloadFile();
        }
    }

    private void downloadFile() {
        // Implement your file download logic here
        // For demonstration, show a toast message
        Toast.makeText(requireContext(), "Downloading file...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the download
                downloadFile();
            } else {
                Toast.makeText(requireContext(), "Permission denied. Cannot download file.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

