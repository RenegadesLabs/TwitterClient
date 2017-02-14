package com.renegades.labs.twitterclient;

import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Виталик on 13.02.2017.
 */

public class MyApplication extends Application {

    private static final String TWITTER_KEY = "FCMymWvHACXqBVDFahsgZIM66";
    private static final String TWITTER_SECRET = "at9FK4ytF5hOPOFpJ0UKMmVzfWi9KEf6ODQNGEbYIz5GX19NDu";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new TweetComposer());
    }
}
