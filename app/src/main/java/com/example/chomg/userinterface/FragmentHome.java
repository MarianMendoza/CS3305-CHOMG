package com.example.chomg.userinterface;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.chomg.R;
import com.example.chomg.SecureStorage;

import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.ui.PlayerView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class FragmentHome extends Fragment {

    private static final String TAG = "FragmentHome";
    private PlayerView playerView;
    private ExoPlayer player;

    private Uri currentVideoUri;

    Map<String, String> requestProperties = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        playerView = view.findViewById(R.id.playerView);
        initializePlayer(); // This will load the most recent video by default

        Button downloadButton = view.findViewById(R.id.downloadButton);
        LinearLayout chatContainer = view.findViewById(R.id.chatContainer);
        String[] activityNames = {"Activity 1", "Activity 2", "Activity 3", "Activity 4", "Activity 5", "Activity 6", "Activity 7", "Activity 8"};

        for (int i = 0; i < activityNames.length; i++) {
            final int activityIndex = i;
            LinearLayout horizontalLayout = new LinearLayout(requireContext());
            horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

            Button videoButton = new Button(requireContext());
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            videoButton.setLayoutParams(buttonLayoutParams);
            videoButton.setText(activityNames[i]);
            videoButton.setOnClickListener(v -> handleButtonClick(activityIndex));

            horizontalLayout.addView(videoButton);
            chatContainer.addView(horizontalLayout);
        }

        downloadButton.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v){
                if (checkPermission()){
                    downloadCurrentVideo(currentVideoUri);
                } else {
                    Toast.makeText(getContext(), "Grant permission to download in app setting.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void handleButtonClick(int activityIndex) {
        Log.d(TAG, "Activity button clicked: Index " + activityIndex);
        initializePlayer(activityIndex);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer(Integer... videoIndex) {
        if (player != null) {
            player.release();
        }
        player = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(player);

        String authToken = SecureStorage.getAuthToken(requireContext());
        requestProperties.put("Authorization", "Bearer " + authToken);

        HttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent("exoplayer-codelab")
                .setDefaultRequestProperties(requestProperties);

        Uri videoUri;
        if (videoIndex.length > 0 && videoIndex[0] != null) {
            currentVideoUri = Uri.parse("https:/178.62.75.31/get-video?index=" + videoIndex[0]);
        } else {
            currentVideoUri = Uri.parse("https:/178.62.75.31/get-recent-video");
        }

        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(currentVideoUri));

        player.setMediaSource(mediaSource);
        player.prepare();
        player.play();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void downloadCurrentVideo(Uri videoUri) {
        new Thread(() -> {
            ContentResolver resolver = requireContext().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "chomg_" + System.currentTimeMillis());
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/chomg");

            Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            boolean downloadSuccessful = false;

            try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "w", null)) {
                FileOutputStream outputStream = new FileOutputStream(pfd.getFileDescriptor());

                HttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                        .setUserAgent("exoplayer-codelab")
                        .setDefaultRequestProperties(requestProperties);

                HttpDataSource dataSource = dataSourceFactory.createDataSource();
                DataSpec dataSpec = new DataSpec(videoUri);

                long dataLength = dataSource.open(dataSpec);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = dataSource.read(buffer, 0, 4096)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                downloadSuccessful = true; // Download completed successfully
                String downloadLocation = "Downloaded to: " + Environment.DIRECTORY_MOVIES + "/chomg";
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "File Downloaded. " + downloadLocation, Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                Log.e(TAG, "Error downloading video", e);
            } finally {
                if (!downloadSuccessful && uri != null) { // Check if download was unsuccessful
                    try {
                        resolver.delete(uri, null, null); // Only delete if download failed
                    } catch (Exception e) {
                        Log.e(TAG, "Error deleting video", e);
                    }
                }
            }
        }).start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
