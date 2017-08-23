package com.egkhan.redditapp.Comments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egkhan.redditapp.FeedAPI;
import com.egkhan.redditapp.Model.Feed;
import com.egkhan.redditapp.Model.entry.Entry;
import com.egkhan.redditapp.R;
import com.egkhan.redditapp.URLS;
import com.egkhan.redditapp.Utils.ExtractXML;
import com.egkhan.redditapp.WebViewActivity;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by EgK on 8/23/2017.
 */

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";

    static String postUrl;
    static String postThumbnailUrl;
    static String postTitle;
    static String postAuthor;
    static String postUpdated;

    int defaultImage;
    String currentFeed;
    ArrayList<Comment> mCommments;
    ListView mListView;
    ProgressBar mCommentsProgressBar;
    TextView progressText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Log.d(TAG, "onCreate: Stared");
        mCommentsProgressBar = (ProgressBar)findViewById(R.id.progressBarCommentLoading);
        mCommentsProgressBar.setVisibility(View.VISIBLE);
        progressText = (TextView) findViewById(R.id.commentsLoadingTV);

        setupImageLoader();
        initPost();
        initRetrofit();

    }

    void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLS.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call = feedAPI.getFeed(currentFeed);
        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                //Log.d(TAG, "onResponse: feed: "+ response.body().toString());
                Log.d(TAG, "onResponse: Server Response: " + response);

                mCommments = new ArrayList<Comment>();
                List<Entry> entries = response.body().getEntryList();

                for (int i = 0; i < entries.size(); i++) {
                    ExtractXML extractXML = new ExtractXML(entries.get(i).getContent(), "<div class=\"md\"><p>", "</p>");
                    List<String> commentDetails = extractXML.start();
                    try {
                        mCommments.add(new Comment(
                                entries.get(i).getId(),
                                entries.get(i).getAuthor().getName(),
                                commentDetails.get(0),
                                entries.get(i).getUpdated()
                        ));
                    } catch (IndexOutOfBoundsException e) {
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException: " + e.getMessage());
                        mCommments.add(new Comment(
                                "Error reading comment",
                                "NONE",
                                "NONE",
                                "NONE"
                        ));
                    }catch (NullPointerException e) {
                        Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage());
                        mCommments.add(new Comment(
                                entries.get(i).getId(),
                                "NULL",
                                commentDetails.get(0),
                                entries.get(i).getUpdated()
                        ));
                    }
                    mListView = (ListView)findViewById(R.id.commentLV);
                    CommentsListAdapter adapter = new CommentsListAdapter(CommentActivity.this,R.layout.comments_layout,mCommments);
                    mListView.setAdapter(adapter);

                    mCommentsProgressBar.setVisibility(View.GONE);
                    progressText.setText("");

                }
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(CommentActivity.this, "An Error Occured: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initPost() {
        Intent incomingIntent = getIntent();
        postUrl = incomingIntent.getStringExtra(getString(R.string.post_url));
        postThumbnailUrl = incomingIntent.getStringExtra(getString(R.string.post_thumbnail));
        postTitle = incomingIntent.getStringExtra(getString(R.string.post_title));
        postAuthor = incomingIntent.getStringExtra(getString(R.string.post_author));
        postUpdated = incomingIntent.getStringExtra(getString(R.string.post_updated));

        TextView title = (TextView) findViewById(R.id.postTitle);
        TextView author = (TextView) findViewById(R.id.postAuthor);
        TextView updated = (TextView) findViewById(R.id.postUpdated);
        ImageView thumbnail = (ImageView) findViewById(R.id.postThumbnail);
        Button replyBtn = (Button) findViewById(R.id.postReplyBtn);
        ProgressBar postLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBarPostLoading);

        title.setText(postTitle);
        author.setText(postAuthor);
        updated.setText(postUpdated);
        displayImage(postThumbnailUrl, thumbnail, postLoadingProgressBar);

        try {
            String[] splitUrl = postUrl.split(URLS.BASE_URL);
            currentFeed = splitUrl[1];
            Log.d(TAG, "initPost: current feed: " + currentFeed);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "initPost: ArrayIndexOutOfBoundsException: " + e.getMessage());
        }

        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: opening url in webview: "+postUrl);
                Intent intent = new Intent(CommentActivity.this,WebViewActivity.class);
                intent.putExtra("url",postUrl);
                startActivity(intent);
            }
        });
    }

    private void displayImage(String imgUrl, ImageView imageView, final ProgressBar progressBar) {
        ImageLoader imageLoader = ImageLoader.getInstance();

        int defaultImage = this.getResources().getIdentifier("@drawable/reddit_alien", null, this.getPackageName());

        //create display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        //download and display image from url
        imageLoader.displayImage(imgUrl, imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Required for setting up the Universal Image loader Library
     */
    private void setupImageLoader() {
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
        defaultImage = this.getResources().getIdentifier("@drawable/reddit_alien", null, this.getPackageName());
    }
}
