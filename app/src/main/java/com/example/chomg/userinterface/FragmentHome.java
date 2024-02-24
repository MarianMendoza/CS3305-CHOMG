package com.example.chomg.userinterface;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;

import com.example.chomg.R;
import com.example.chomg.SecureStorage;
import com.example.chomg.network.Api;
import com.example.chomg.network.Client;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.ui.PlayerView;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FragmentHome extends Fragment {

    private PlayerView playerView;
    private ExoPlayer player;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        playerView = view.findViewById(R.id.playerView);
        initializePlayer();

        LinearLayout chatContainer = view.findViewById(R.id.chatContainer);

        String[] activityNames = {"Activity 1", "Activity 2", "Activity 3", "Activity 4", "Activity 5","Activity 6","Activity 7","Activity 8"};

        for (String activityName : activityNames) {
            LinearLayout horizontalLayout = new LinearLayout(requireContext());
            horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(requireContext());
            LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );
            textLayoutParams.setMargins(0, 8, 0, 0);
            textView.setLayoutParams(textLayoutParams);
            textView.setBackgroundResource(R.drawable.shape1);
            textView.setPadding(8, 8, 8, 8);
            textView.setWidth(2000);
            textView.setHeight(150);
            textView.setText(activityName);
            textView.setGravity(Gravity.START);
            textView.setTextSize(16);

            // Create Button
            Button button = new Button(requireContext());
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            buttonLayoutParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
            button.setLayoutParams(buttonLayoutParams);
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.download_24px, 0, 0, 0); // Set icon drawable

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button click here
                    handleButtonClick(activityName);
                }
            });
            horizontalLayout.addView(textView);
            horizontalLayout.addView(button);

            chatContainer.addView(horizontalLayout);
        }

        return view;
    }

    private void handleButtonClick(String activityName) {
        System.out.println("Button Click");
    }

    @OptIn(markerClass = UnstableApi.class) private void initializePlayer() {
        player = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(player);
        String authToken = SecureStorage.getAuthToken(requireContext()); // Ensure you have this method in your SecureStorage class
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Bearer " + authToken);

        HttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent("exoplayer-codelab")
                .setDefaultRequestProperties(requestProperties);

        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri("https://178.62.75.31/get-recent-video"));

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