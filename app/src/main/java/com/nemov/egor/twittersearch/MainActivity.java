package com.nemov.egor.twittersearch;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.nemov.egor.twittersearch.presenter.SearchMapping;
import com.nemov.egor.twittersearch.presenter.TweetAdapter;
import com.nemov.egor.twittersearch.presenter.TwitterServices;
import com.nemov.egor.twittersearch.utils.EndlessRecyclerViewScrollListener;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity {

    EditText mSearchQuery;
    RecyclerView mTweetList;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    WeakReference<RecyclerView.Adapter> mWeakAdapter;

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if(checkConnection() && !s.toString().equals(SearchMapping.getQuery())) {
                SearchMapping.setQuery(s.toString());
                TwitterServices.search(mWeakAdapter);
            }
            else if(!checkConnection()) {
                onUnavailableConnection();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchQuery = (EditText) findViewById(R.id.et_search_query );
        mSearchQuery.addTextChangedListener(mTextWatcher);

        mTweetList = (RecyclerView) findViewById(R.id.tweet_list);
        mTweetList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mTweetList.setLayoutManager(mLayoutManager);
        mAdapter = new TweetAdapter(SearchMapping.getSearchedTweetsArray());
        mWeakAdapter = new WeakReference<RecyclerView.Adapter>(mAdapter);
        mTweetList.setAdapter(mAdapter);
        mTweetList.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(checkConnection()) {
                    TwitterServices.paddingUpdate(mWeakAdapter);
                }
                else {
                    onUnavailableConnection();
                }
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mSearchQuery.removeTextChangedListener(mTextWatcher);
        super.onDestroy();
    }

    boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    void onUnavailableConnection() {
        Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_SHORT).show();
        ((TweetAdapter)mAdapter).swap(new String[]{});
        SearchMapping.wipeSearchResults();
    }
}
