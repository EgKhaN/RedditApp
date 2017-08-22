package com.egkhan.redditapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.egkhan.redditapp.Model.Feed;
import com.egkhan.redditapp.Model.entry.Entry;
import com.egkhan.redditapp.Utils.ExtractXML;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String BASE_URL = "https://www.reddit.com/r/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call = feedAPI.getFeed();
        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                //Log.d(TAG, "onResponse: feed: "+ response.body().toString());
                Log.d(TAG, "onResponse: Server Response: " + response);

                List<Entry> entries = response.body().getEntryList();
                Log.d(TAG, "onResponse: enries: " + entries);
//                Log.d(TAG, "onResponse: author"+entries.get(0).getAuthor().getName());
//                Log.d(TAG, "onResponse: updated"+entries.get(0).getUpdated());
//                Log.d(TAG, "onResponse: title"+entries.get(0).getTitle());

                ArrayList<Post> posts = new ArrayList<Post>();
                for (int i = 0; i < entries.size(); i++) {
                    ExtractXML extractXML1 = new ExtractXML(entries.get(i).getContent(), "<a href=");
                    List<String> postContent = extractXML1.start();

                    ExtractXML extractXML2 = new ExtractXML(entries.get(i).getContent(), "<img src=");
                    try {
                        postContent.add(extractXML2.start().get(0));
                    } catch (NullPointerException e) {
                        postContent.add(null);
                        Log.e(TAG, "onResponse: NullPointerException(thumbnail)" + e.getMessage());
                    } catch (IndexOutOfBoundsException e) {
                        postContent.add(null);
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException(thumbnail)" + e.getMessage());
                    }
                    int lastPosition = postContent.size() - 1;
                    posts.add(new Post(
                            entries.get(i).getTitle(),
                            entries.get(i).getAuthor().getName(),
                            entries.get(i).getUpdated(),
                            postContent.get(0),
                            postContent.get(lastPosition)
                    ));
                }
//                for (int i = 0; i < posts.size(); i++) {
//                    Log.d(TAG, "onResponse: \n" + "PostUrl :" + posts.get(i).getPostUrl() + "\n "
//                            + "ThumbnailUrl :" + posts.get(i).getThumbnailUrl() + "\n "
//                            + "Title :" + posts.get(i).getTitle() + "\n "
//                            + "Author :" + posts.get(i).getAuthor() + "\n "
//                            + "Updated :" + posts.get(i).getDate_updated() + "\n ");
//                }
                ListView listView = (ListView)findViewById(R.id.listView);
                CustomListAdapter customListAdapter = new CustomListAdapter(MainActivity.this,R.layout.card_layout_main,posts);
                listView.setAdapter(customListAdapter);
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(MainActivity.this, "An Error Occured: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
