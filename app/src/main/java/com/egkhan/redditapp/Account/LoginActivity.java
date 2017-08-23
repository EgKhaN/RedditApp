package com.egkhan.redditapp.Account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.egkhan.redditapp.FeedAPI;
import com.egkhan.redditapp.R;
import com.egkhan.redditapp.URLS;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by EgK on 8/23/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private ProgressBar mProgressBar;
    private EditText mUserName;
    private EditText mPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: started");

        mProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        mUserName = (EditText) findViewById(R.id.input_username);
        mPassword = (EditText) findViewById(R.id.input_password);
        Button loginBtn = (Button) findViewById(R.id.btn_login);

        mProgressBar.setVisibility(View.GONE);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in");
                String username = mUserName.getText().toString();
                String password = mPassword.getText().toString();

                if (!username.equals("") && !password.equals("")) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    login(username, password);
                }
            }
        });

    }

    private void login(final String username, final String passowrd) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLS.LOGIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");

        Call<CheckLogin> call = feedAPI.signIn(headerMap, username, username, passowrd, "json");
        call.enqueue(new Callback<CheckLogin>() {
            @Override
            public void onResponse(Call<CheckLogin> call, Response<CheckLogin> response) {
                Log.d(TAG, "onResponse: feed: " + response.body().toString());
                Log.d(TAG, "onResponse: Server Response: " + response);

            }

            @Override
            public void onFailure(Call<CheckLogin> call, Throwable t) {
                Log.e(TAG, "onFailure: unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "An Error Occured: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
