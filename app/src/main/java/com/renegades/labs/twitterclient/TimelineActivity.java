package com.renegades.labs.twitterclient;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class TimelineActivity extends ListActivity {

    EditText editText;
    Button newTweetButton;
    List<MyTweet> tweetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);

        tweetList = new ArrayList<>();

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");

        TwitterApiClient twitterApiClient = Twitter.getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<List<Tweet>> call = statusesService.userTimeline(null, userName, null, null, null, null,
                null, null, null);
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                List<Tweet> tweets = result.data;
                for (int i = 0; i < tweets.size(); i++) {
                    String userName = tweets.get(i).user.screenName;
                    String tweet = tweets.get(i).text;
                    tweetList.add(new MyTweet(userName, tweet));
                }

                MyListAdapter myListAdapter = new MyListAdapter(tweetList);
                TimelineActivity.this.setListAdapter(myListAdapter);
            }

            public void failure(TwitterException exception) {
            }
        });

        editText = (EditText) findViewById(R.id.editText);
        newTweetButton = (Button) findViewById(R.id.newTweetButton);

        newTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweet = editText.getText().toString();
                TweetComposer.Builder builder = new TweetComposer.Builder(TimelineActivity.this)
                        .text(tweet);
                builder.show();
            }
        });

    }

    class ViewHolder {
        TextView userName;
        TextView tweetText;
    }

    class MyListAdapter extends BaseAdapter {
        List<MyTweet> myTweetList;

        public MyListAdapter(List<MyTweet> myTweetList) {
            this.myTweetList = myTweetList;
        }

        @Override
        public int getCount() {
            return myTweetList.size();
        }

        @Override
        public MyTweet getItem(int i) {
            return myTweetList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (view == null){
                LayoutInflater inflater = (LayoutInflater) TimelineActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_item, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.userName = (TextView) view.findViewById(R.id.user_name);
                viewHolder.tweetText = (TextView) view.findViewById(R.id.tweet_text);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.userName.setText(getItem(i).username);

            int start = -1;
            int end = 0;
            final String text = getItem(i).tweet;
            for (int j = 0; j < text.length(); j++) {
                char c = text.charAt(j);
                if (c == '@'){
                    start = j;
                }
                if (start != -1){
                    end = j;
                }
                if ((end != 0) && ((!(c>47 && c<58)) && (!(c>63 && c<91)) && (!(c>94 && c<123)))){
                    break;
                }
            }

            if (start != -1) {
                viewHolder.tweetText.setMovementMethod(LinkMovementMethod.getInstance());
                viewHolder.tweetText.setText((text), TextView.BufferType.SPANNABLE);
                Spannable mySpannable = (Spannable) viewHolder.tweetText.getText();
                final int finalStart = start;
                final int finalEnd = end;
                ClickableSpan myClickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TimelineActivity.this, TimelineActivity.class);
                        String userName = text.substring(finalStart, finalEnd + 1);
                        intent.putExtra("userName", userName);
                        startActivity(intent);
                    }
                };
                mySpannable.setSpan(myClickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                viewHolder.tweetText.setText(getItem(i).tweet);
            }

            return view;
        }
    }
}