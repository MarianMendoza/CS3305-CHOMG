package com.example.chomg.userinterface;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        return view;
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