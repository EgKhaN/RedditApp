package com.egkhan.redditapp;

import com.egkhan.redditapp.Model.Feed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

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
}
