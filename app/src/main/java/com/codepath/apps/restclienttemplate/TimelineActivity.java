package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;


public class TimelineActivity extends AppCompatActivity {
    public static final String  Tag = "timeline";
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    EndlessRecyclerViewScrollListener scrollListener;
    FloatingActionButton floatingActionButton;

    private final int reqCode =  27;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.drawable.ic_launcher_twitter);
        bar.setTitle(Html.fromHtml("<font color='#ffffff'>Twitter </font>"));
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DA1F2")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);


        rvTweets = findViewById(R.id.rView);
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(this, tweets);
        client = TwitterApp.getRestClient(this);
        floatingActionButton = findViewById(R.id.ComposeButton);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                popTimeline();

            }
        });
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(Tag,"scrolled"+page);
                LoadMoreData();
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
        popTimeline();


    }
    private void LoadMoreData(){
        client.getNextPag(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(Tag,"succ for load more data");
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArr(jsonArray);
                    adapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(Tag,"fail for load more data");
            }
        }, tweets.get(tweets.size()-1).id);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.Compose==item.getItemId()){
            Intent intent = new Intent(this, ComposeActivity.class);
            intent.putExtra("replyTo", "");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == reqCode && resultCode==RESULT_OK){
            assert data != null;
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));

            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
    }


    /*public void toDetail(Tweet tweet){
        Intent intent = new Intent(this, ComposeActivity.class);
        intent.putExtra("tweet", Parcels.wrap(tweet));
        startActivity(intent);
    }*/



    private void popTimeline(){
        final ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        client.getHomeTimeLine(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(Tag,"succ");
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.clear();
                    tweets.addAll(Tweet.fromJsonArr(jsonArray));
                    adapter.addAll(tweets);
//                    pb.setVisibility(ProgressBar.INVISIBLE);

                    swipeRefreshLayout.setRefreshing(false);

                } catch (JSONException e) {
                    Log.e(Tag,"JSON error", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(Tag,"fail",throwable);
            }
        });
    }
}