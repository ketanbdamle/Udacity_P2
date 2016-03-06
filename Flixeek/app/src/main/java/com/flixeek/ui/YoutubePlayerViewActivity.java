package com.flixeek.ui;

import android.os.Bundle;

import com.flixeek.R;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Activity that displays a YouTube Video in a {@link YouTubePlayerView}.
 *
 * @version 1.0
 * @author Ketan Damle
 *
 */
public class YoutubePlayerViewActivity extends YouTubeFailureRecoveryActivity {

  private String videoId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_trailer_youtubeview);

    videoId = getIntent().getStringExtra("VIDEO_ID");

    YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
    youTubeView.initialize(DEVELOPER_KEY, this);
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
