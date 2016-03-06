package com.flixeek.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.flixeek.R;
import com.flixeek.common.FlixeekConstants;
import com.flixeek.contentapi.model.TrailerDetails;

import butterknife.Bind;

/**
 * Displays the Youtube video trailer in a Webview using an iframe.
 *
 * @author Ketan Damle
 */
public class TrailerWebViewActivity extends AppCompatActivity {

    @Bind(R.id.movieTrailer)
    WebView movieTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_webview);
        movieTrailer = (WebView) findViewById(R.id.movieTrailer);

        Intent trailerWebview = getIntent();
        TrailerDetails trailerDetails = trailerWebview.getParcelableExtra(FlixeekConstants.BUNDLE_KEY_TRAILER_DETAILS);
        String videoUrl = "http://youtube.com/watch?v=" + trailerDetails.getKey();

        //Convert the original trailer link to an embeddable format
        String embedVideoUrl = videoUrl.replace("watch?v=", "embed\\");

        String trailerHtmlText = "<html>" +
                "  <head>" +
                "  </head>" +
                "  <body style=\"margin: 0\">" +
                "      <iframe src=\"" + embedVideoUrl + "\" frameborder=\"0\" allowfullscreen=\"\"" +
                "      style=\"position: absolute;top: 0;left:0;width: 100%;height: 100%;\"></iframe>" +
                "  </body>" +
                "</html>";
        movieTrailer.getSettings().setJavaScriptEnabled(true);
        movieTrailer.loadData(trailerHtmlText, "text/html", "utf-8");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trailer_webview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent movieListIntent = new Intent(this, MovieListActivity.class);
                startActivity(movieListIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
