package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
@Parcel
public class Tweet {
    public String body;
    public User user;
    public String createdAt;
    public long id;
    public int likedByNum;
    public int retweetedByNum;
    public boolean liked;
    public boolean rted;
    public static Tweet fromJson(JSONObject jObj) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jObj.getString("text");
        tweet.createdAt = Tweet.getRelativeTimeAgo(jObj.getString("created_at"));
        tweet.user = User.fromJson(jObj.getJSONObject("user"));
        tweet.id =jObj.getLong("id");
        tweet.rted = jObj.getBoolean("retweeted");
        tweet.retweetedByNum = jObj.getInt("retweet_count");
        tweet.likedByNum = jObj.getInt("favorite_count");

        return tweet;
    }

    //needed by parcel
    public  Tweet(){

    }
    public static List<Tweet> fromJsonArr(JSONArray jArr) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i =0; i<jArr.length();i++){
            tweets.add(fromJson(jArr.getJSONObject(i)));
        }
        return tweets;
    }
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
