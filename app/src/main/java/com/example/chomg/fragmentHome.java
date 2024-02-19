package com.example.chomg;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class fragmentHome extends Fragment {

    private static final int REQUEST_PERMISSION_CODE = 123;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the button and set click listener
        Button buttonDownload = rootView.findViewById(R.id.buttonDownload);
        buttonDownload.setOnClickListener(v -> checkPermissionAndDownload());

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
