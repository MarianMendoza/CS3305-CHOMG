package com.example.chomg.userinterface;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;

import com.example.chomg.R;
import com.example.chomg.SecureStorage;

import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.ui.PlayerView;

import java.util.HashMap;
import java.util.Map;

public class FragmentHome extends Fragment {

    private static final String TAG = "FragmentHome";
    private PlayerView playerView;
    private ExoPlayer player;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        playerView = view.findViewById(R.id.playerView);
        initializePlayer(); // This will load the most recent video by default

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

        return view;
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
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Bearer " + authToken);

        HttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent("exoplayer-codelab")
                .setDefaultRequestProperties(requestProperties);

        Uri videoUri;
        if (videoIndex.length > 0 && videoIndex[0] != null) {
            videoUri = Uri.parse("https:/178.62.75.31/get-video?index=" + videoIndex[0]); // Adjusted for a hypothetical URL
        } else {
            videoUri = Uri.parse("https:/178.62.75.31/get-recent-video"); // Adjusted for a hypothetical URL
        }

        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));

        player.setMediaSource(mediaSource);
        player.prepare();
        player.play();
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
