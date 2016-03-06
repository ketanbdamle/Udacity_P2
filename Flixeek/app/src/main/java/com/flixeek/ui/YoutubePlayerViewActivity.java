package com.flixeek.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.flixeek.R;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Activity that displays a YouTube Video in a {@link YouTubePlayerView}.
 *
 * @author Ketan Damle
 * @version 1.0
 */
public class YoutubePlayerViewActivity extends YouTubeFailureRecoveryActivity {

    private String videoId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_youtubeview);

        videoId = getIntent().getStringExtra("VIDEO_ID");

        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        try {
            youTubeView.initialize(DEVELOPER_KEY, this);
        } catch (IllegalArgumentException iae) {
            Toast.makeText(this, "Unauthorized request - missing credentials", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MovieListActivity.class));
        } catch (Exception e) {
            Toast.makeText(this, "Unauthorized request - missing credentials", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MovieListActivity.class));
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {

        if (!wasRestored) {
            player.cueVideo(videoId);
        }

    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

}
