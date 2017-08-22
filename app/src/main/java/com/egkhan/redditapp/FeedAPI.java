package com.egkhan.redditapp;

import com.egkhan.redditapp.Model.Feed;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by EgK on 8/22/2017.
 */

public interface FeedAPI {

    String BASE_URL="https://www.reddit.com/r/";

    @GET("earthporn/.rss")
    Call<Feed> getFeed();
}
