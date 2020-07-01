package com.codepath.apps.restclienttemplate;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class DetailView extends AppCompatActivity {
    int radius = 80;
    int margin = 5;
    String Tag;
    ImageView ivView;
    ImageView tweetImage;
    TextView tvBody;
    TextView tvUser;
    TextView tvScreen;
    TextView tvCreated;
    EditText tvReply;
    TextView tvLikes;
    TextView tvRtNum;
    ImageButton btRetweet;
    ImageButton btLike;
    Button postButton;
    TwitterClient twitterClient;
    Tweet tweet;
    RelativeLayout relativeLayout;
    int maxTweetLength = 140;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        ActionBar bar = getSupportActionBar();
        ivView = findViewById(R.id.userPicDetail);
        tweetImage = findViewById(R.id.tweetImage);
        tvBody = findViewById(R.id.twtBodyDetail);
        tvUser = findViewById(R.id.twtUserDetail);
        tvScreen = findViewById(R.id.twtAtDetail);
        tvCreated =findViewById(R.id.twtCreatedDetail);
        btRetweet = findViewById(R.id.retweetButtonDetail);
        btLike = findViewById(R.id.likeButtonDetail);
        tvReply = findViewById(R.id.Detail);
        tvLikes = findViewById(R.id.likeNumDetail);
        tvRtNum = findViewById(R.id.rtNumDetail);
        postButton = findViewById(R.id.postButtonDetail);
        relativeLayout = findViewById(R.id.DetailLayout);
        Tag = "DetailView";
        twitterClient = TwitterApp.getRestClient(this);


        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        tvBody.setText(tweet.body);
        //tvScreen.setText("@" + tweet.user.screenName);
        tvCreated.setText(tweet.createdAt);
        tvUser.setText(tweet.user.name);
        tvLikes.setText(String.valueOf(tweet.likedByNum));
        tvRtNum.setText(String.valueOf(tweet.retweetedByNum));
        Glide.with(this).load(tweet.user.privateImgURL).transform(new RoundedCornersTransformation(radius, margin)).into(ivView);
        tvReply.setText(String.format("@ %s", tweet.user.screenName));

        if(!tweet.imagePath.isEmpty()){
            Glide.with(this).load(tweet.imagePath).into(tweetImage);
        }
        else{
            tweetImage.setVisibility(View.GONE);
        }

        if(tweet.liked){
            btLike.setImageResource(R.drawable.ic_vector_heart);
        }

        if(tweet.rted){
            btRetweet.setImageResource(R.drawable.ic_vector_retweet);
        }

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = tvReply.getText().toString();
                Log.i(Tag,text);
                if(text.length() == 0){
                    //Toast.makeText(ComposeActivity.this, "Sorry this tweet has no content", Toast.LENGTH_LONG).show();
                    Snackbar.make(relativeLayout, "Sorry this tweet has no content", Snackbar.LENGTH_SHORT).show();
                    Log.i(Tag, "too short");
                }
                else if(text.length()>maxTweetLength){
                    Snackbar.make(relativeLayout, "Sorry this tweet has more than " + maxTweetLength + " characters", Snackbar.LENGTH_SHORT).show();

                }
                twitterClient.publishReply(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(Tag,"reply posted detail view");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.i(Tag,"reply NOT posted detail view", throwable);
                    }
                },text,tweet);
            }
        });

        btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tweet.liked) {
                    twitterClient.likeTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            btLike.setImageResource(R.drawable.ic_vector_heart);
                            Log.i(Tag, "tweet liked");
                            tweet.liked = true;
                            tvLikes.setText(String.valueOf(tweet.likedByNum+1));
                            tweet.likedByNum = tweet.likedByNum+1;
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(Tag, "like failed", throwable);

                        }
                    }, tweet);
                }
                else{
                    twitterClient.removeLikeTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            btLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                            Log.i(Tag, "tweet unliked");
                            tweet.liked=false;
                            tvLikes.setText(String.valueOf(tweet.likedByNum-1));
                            tweet.likedByNum = tweet.likedByNum-1;
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(Tag, "unlike failed", throwable);
                        }
                    }, tweet);
                }
            }
        });

        btRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!tweet.rted){
                    twitterClient.rtTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            btRetweet.setImageResource(R.drawable.ic_vector_retweet);
                            Log.i(Tag, "tweet rted");
                            tweet.rted = true;
                            tvRtNum.setText(String.valueOf(tweet.retweetedByNum+1));
                            tweet.retweetedByNum = tweet.retweetedByNum+1;
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(Tag, "rt failed", throwable);
                        }
                    }, tweet);
                }
                else{
                    twitterClient.removeRtTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            btRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                            Log.i(Tag, "tweet un_rted");
                            tweet.rted = false;
                            tvRtNum.setText(String.valueOf(tweet.retweetedByNum-1));
                            tweet.retweetedByNum = tweet.retweetedByNum-1;
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(Tag,"fail unrted ", throwable);
                        }
                    },tweet);

                }
            }
        });
    }
}