package com.codepath.apps.restclienttemplate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

import static androidx.core.content.ContextCompat.startActivity;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{
    int radius = 80;
    int margin = 5;
    Context context;
    TwitterClient twitterClient;
    List<Tweet> tweets;
    String Tag = "TweetAdapter";


    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
        twitterClient = TwitterApp.getRestClient(context);

    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(context).inflate(R.layout.item_tweet,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    //cleans
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    //adds back in
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivView;
        ImageView twtivView;
        TextView tvBody;
        TextView tvUser;
        TextView tvScreen;
        TextView tvCreated;
        TextView tvLikes;
        TextView tvRtNum;
        ImageButton btRetweet;
        ImageButton btLike;
        ImageButton btReply;

        TimelineActivity timelineActivity;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivView = itemView.findViewById(R.id.userPic);
            twtivView = itemView.findViewById(R.id.image);
            tvBody = itemView.findViewById(R.id.twtBody);
            tvUser = itemView.findViewById(R.id.twtUser);
            tvScreen = itemView.findViewById(R.id.twtAt);
            tvLikes = itemView.findViewById(R.id.TwtLikedNum);
            tvRtNum = itemView.findViewById(R.id.TwtRtNum);
            tvCreated = itemView.findViewById(R.id.twtCreated);
            btRetweet = itemView.findViewById(R.id.retweetButton);
            btLike = itemView.findViewById(R.id.likeButton);
            btReply = itemView.findViewById(R.id.replyButton);
            twtivView = itemView.findViewById(R.id.image);

        }

        @SuppressLint("SetTextI18n")
        public void bind(final Tweet tweet) {
            //grab liked tweets
            //editor encounters error when I try to make these boolean


            tvBody.setText(tweet.body);
            tvScreen.setText("@" + tweet.user.screenName);
            tvCreated.setText(tweet.createdAt);
            tvUser.setText(tweet.user.name);
            tvLikes.setText(String.valueOf(tweet.likedByNum));
            tvRtNum.setText(String.valueOf(tweet.retweetedByNum));
            if(!tweet.imagePath.isEmpty()){
                Glide.with(context).load(tweet.imagePath).into(twtivView);
            }
            else{
                twtivView.setVisibility(View.GONE);
            }
            Glide.with(context).load(tweet.user.privateImgURL).transform(new RoundedCornersTransformation(radius, margin)).into(ivView);

            if(tweet.liked){
                btLike.setImageResource(R.drawable.ic_vector_heart);
            }

            if(tweet.rted){
                btRetweet.setImageResource(R.drawable.ic_vector_retweet);
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailView.class);
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    startActivity(context,intent,null);
                    // send to detail view here with tweet info
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

            btReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ComposeActivity.class);
                    intent.putExtra("replyTo", tweet.user.screenName);
                    startActivity(context, intent, null);
                }
            });



        }
    }
}
