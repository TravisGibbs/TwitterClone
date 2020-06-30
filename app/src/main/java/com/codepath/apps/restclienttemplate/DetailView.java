package com.codepath.apps.restclienttemplate;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailView extends AppCompatActivity {
    int radius = 80;
    int margin = 5;
    ImageView ivView;
    TextView tvBody;
    TextView tvUser;
    TextView tvScreen;
    TextView tvCreated;
    TextView tvReply;
    ImageButton btRetweet;
    ImageButton btLike;
    ImageButton btReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        ivView = findViewById(R.id.userPicDetail);
        tvBody = findViewById(R.id.twtBodyDetail);
        tvUser = findViewById(R.id.twtUserDetail);
        tvScreen = findViewById(R.id.twtAtDetail);
        tvCreated =findViewById(R.id.twtCreatedDetail);
        btRetweet = findViewById(R.id.retweetButtonDetail);
        btLike = findViewById(R.id.likeButtonDetail);
        btReply = findViewById(R.id.replyButtonDetail);
        tvReply = findViewById(R.id.Tweet);
        String Tag = "DetailView";


        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        Log.i(Tag, tweet.body);
        tvBody.setText(tweet.body);
        tvScreen.setText("@" + tweet.user.screenName);
        tvCreated.setText(tweet.createdAt);
        tvUser.setText(tweet.user.name);
        Glide.with(this).load(tweet.user.privateImgURL).transform(new RoundedCornersTransformation(radius, margin)).into(ivView);
        tvReply.setText("@"+tweet.user.screenName);
    }
}