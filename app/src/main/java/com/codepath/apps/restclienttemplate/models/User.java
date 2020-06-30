package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {
    public String name;
    public String screenName;
    public String privateImgURL;

    //needed by parcel
    public User(){}

    public static User fromJson(JSONObject jObj) throws JSONException {
        User user = new User();
        user.name = jObj.getString("name");
        user.screenName = jObj.getString("screen_name");
        user.privateImgURL = jObj.getString("profile_image_url_https");
        return user;
    }
}
