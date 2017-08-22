package com.egkhan.redditapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

    Button refreshBtn;
    EditText feedNameEt;

    String currentFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: starting");
        refreshBtn = (Button) findViewById(R.id.refreshFeedBtn);
        feedNameEt = (EditText) findViewById(R.id.feedNameEt);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedName = feedNameEt.getText().toString();
                if(!feedName.equals("")){
                    currentFeed = feedName;
                    initRetrofit();
                }
                else{
                    initRetrofit();
                }
            }
        });


        initRetrofit();

    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call = feedAPI.getFeed(currentFeed);
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

                final ArrayList<Post> posts = new ArrayList<Post>();
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
                    try {
                        posts.add(new Post(
                                entries.get(i).getTitle(),
                                entries.get(i).getAuthor().getName(),
                                entries.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPosition)
                        ));
                    } catch (NullPointerException e) {
                        posts.add(new Post(
                                entries.get(i).getTitle(),
                                null,
                                entries.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPosition)
                        ));
                        Log.e(TAG, "onResponse: NullPointerException(thumbnail)" + e.getMessage());
                    }

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

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "onItemClick: Clicked: "+posts.get(position).toString());
                        Intent intent = new Intent(MainActivity.this,CommentActivity.class);
                        intent.putExtra(getString(R.string.post_url),posts.get(position).getPostUrl());
                        intent.putExtra(getString(R.string.post_author),posts.get(position).getAuthor());
                        intent.putExtra(getString(R.string.post_title),posts.get(position).getTitle());
                        intent.putExtra(getString(R.string.post_thumbnail),posts.get(position).getThumbnailUrl());
                        intent.putExtra(getString(R.string.post_updated),posts.get(position).getDate_updated());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(MainActivity.this, "An Error Occured: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
