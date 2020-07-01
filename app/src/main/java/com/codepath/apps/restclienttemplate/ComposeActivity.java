package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    RelativeLayout relativeLayout;
    EditText evCompose;
    Button bttn;
    String Tag = "ComposeActivity";
    int maxTweetLength = 140;
    TwitterClient twitterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        relativeLayout = findViewById(R.id.ComposeLayout);

        evCompose = findViewById(R.id.TweetDetail);
        bttn = findViewById(R.id.Post);

        twitterClient = TwitterApp.getRestClient(this);

        bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = evCompose.getText().toString();
                if(text.length() == 0){
                    //Toast.makeText(ComposeActivity.this, "Sorry this tweet has no content", Toast.LENGTH_LONG).show();
                    Snackbar.make(relativeLayout, "Sorry this tweet has no content", Snackbar.LENGTH_SHORT).show();
                    Log.i(Tag, "too short");
                }
                else if(text.length()>maxTweetLength){
                    Snackbar.make(relativeLayout, "Sorry this tweet has more than " + maxTweetLength + " characters", Snackbar.LENGTH_SHORT).show();

                }
                else{
                    twitterClient.publishTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(Tag, "succ posted");
                            try {
                                Tweet tweet = Tweet.fromJson(json.jsonObject);
                                Log.i(Tag, "published tweet says: " + tweet.body + " posted by " + tweet.user.screenName);
                                Intent intent = new Intent();
                                intent.putExtra("tweet", Parcels.wrap(tweet));
                                setResult(RESULT_OK, intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(Tag, "fail posted", throwable);
                        }
                    }, text);
                }
            }
        });

    }
}