package com.egkhan.redditapp;

import com.egkhan.redditapp.Account.CheckLogin;
import com.egkhan.redditapp.Model.Feed;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by EgK on 8/22/2017.
 */

public interface FeedAPI {

    String BASE_URL="https://www.reddit.com/r/";

    //non-static feed name
    @GET("{feed_name}/.rss")
    Call<Feed> getFeed(@Path("feed_name") String feed_name);

//    @GET("earthporn/.rss")
//    Call<Feed> getFeed();

    @POST("{user}")
    Call<CheckLogin> signIn(
            @HeaderMap Map<String,String> headers,
            @Path("user") String username,
            @Query("user") String user,
            @Query("paswd") String password,
            @Query("api_type") String type
            );
}
