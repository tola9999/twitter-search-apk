package com.nemov.egor.twittersearch.presenter;

import com.nemov.egor.twittersearch.model.Authenticated;
import com.nemov.egor.twittersearch.model.SearchMetadata;
import com.nemov.egor.twittersearch.model.Tweet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by egor.nemov on 03.02.16.
 */
public class SearchMapping {
    public static boolean mPaddingUpdating = false;
    public static int mQueryId;
    public static Authenticated mAuth;
    public static SearchMetadata mSearchMetadata;
    static String mQuery;
    static List<Tweet> mSearchedTweets = new LinkedList<>();

    public static boolean isAuthenticated() {
        return mAuth != null;
    }

    public static void setQuery(String query) {
        if(mQuery == null) {
            mQuery = query;
        }
        if(mQuery != null && !mQuery.equals(query)) {
            mQuery = query;
            wipeSearchResults();
        }
    }

    public static String getQuery() {
        return mQuery;
    }

    public static String[] getSearchedTweetsArray() {
        String[] tweetsArr = new String[mSearchedTweets.size()];
        List<String> tweetsList = new ArrayList<>(mSearchedTweets.size());
        for (Tweet tweet: mSearchedTweets) {
            tweetsList.add(tweet.toString());
        }
        return tweetsList.toArray(tweetsArr);
    }

    public static void setSearchedTweets(List<Tweet> updateTweets, boolean ignoreOldData) {
        wipeSearchResults(ignoreOldData);
        if(updateTweets != null) {
            mSearchedTweets.addAll(updateTweets);
        }
    }

    public static void wipeSearchResults() {
        mSearchedTweets.clear();
    }

    public static void wipeSearchResults(boolean ignoreOldData) {
        if(ignoreOldData) {
            wipeSearchResults();
        }
    }
}
