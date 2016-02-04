package com.nemov.egor.twittersearch.presenter;

import android.os.AsyncTask;
import android.util.Base64;
import android.widget.TextView;

import com.nemov.egor.twittersearch.model.Authenticated;
import com.nemov.egor.twittersearch.model.QueryScheme;
import com.nemov.egor.twittersearch.model.SearchResponse;
import com.nemov.egor.twittersearch.utils.JsonToPojo;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by egor.nemov on 03.02.16.
 */
public class TwitterServices {

    static OkHttpClient mClient = new OkHttpClient();
    static boolean mIsTaskPostponed = false;

    static class PosponedTaskArgs {
        static WeakReference mTargetViewRef;
    }

    static class AuthenticationTask extends AsyncTask<Void, Void, String> {

        final MediaType JSON = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
        final String BASE64_ENCODED = Base64.encodeToString((QueryScheme.TWITTER_APP_CONSUMER_KEY + ":" + QueryScheme.TWITTER_APP_CONSUMER_SECRET).getBytes(), Base64.NO_WRAP);
        final Headers POST_OATH_TOKEN = new Headers.Builder().add("Authorization: Basic " + BASE64_ENCODED).build();

        @Override
        protected String doInBackground(Void... params) {
            String responseBody;
            try {
                responseBody = post(QueryScheme.TWITTER_APP_BASEURL + QueryScheme.TWITTER_APP_OAUTH, "grant_type=client_credentials");
            } catch (IOException e) {
                responseBody = QueryScheme.TWITTER_ERROR + ":" + e.getMessage().toString();
                e.printStackTrace();
            }
            return responseBody;
        }

        @Override
        protected void onPostExecute(String response) {
            if (!response.contains(QueryScheme.TWITTER_ERROR)) {
                SearchMapping.mAuth = new JsonToPojo<Authenticated>().convert(response, Authenticated.class);
                if(mIsTaskPostponed) {
                    search(PosponedTaskArgs.mTargetViewRef);
                }
            } else {
                SearchMapping.mAuth = null;
            }
        }

        String post(String url, String data) throws IOException {
            RequestBody body = RequestBody.create(JSON, data);
            Request request = new Request.Builder()
                    .url(url)
                    .headers(POST_OATH_TOKEN)
                    .post(body)
                    .build();

            Response response = mClient.newCall(request).execute();
            return response.body().string();
        }
    }

    static class SearchTask extends AsyncTask<String, Void, String> {

        static int counter = 0;
        int taskId;
        WeakReference<TweetAdapter> mTargetViewRef;
        boolean mIgnoreOldData;

        SearchTask(WeakReference targetViewRef, boolean ignoreOldData) {
            taskId = counter++;
            mTargetViewRef = targetViewRef;
            mIgnoreOldData = ignoreOldData;
        }

        @Override
        protected String doInBackground(String... params) {
            String responseBody;
            try {
                responseBody = get(QueryScheme.TWITTER_APP_BASEURL + QueryScheme.TWITTER_APP_SEARCH, params[0]);
            } catch (IOException e) {
                responseBody = QueryScheme.TWITTER_ERROR + ":" + e.getMessage().toString();
                e.printStackTrace();
            }
            return responseBody;
        }

        @Override
        protected void onPostExecute(String response) {
            SearchMapping.mPaddingUpdating = false;
            if (!response.contains(QueryScheme.TWITTER_ERROR) || taskId > SearchMapping.mQueryId) {
                SearchResponse searchResponse = new JsonToPojo<SearchResponse>().convert(response, SearchResponse.class);
                SearchMapping.mQueryId = taskId;
                SearchMapping.setSearchedTweets(searchResponse.statuses, mIgnoreOldData);
                SearchMapping.mSearchMetadata = searchResponse.search_metadata;


                if (mTargetViewRef != null) {
                    TweetAdapter adapter = mTargetViewRef.get();
                    if (adapter != null) {
                        adapter.swap(SearchMapping.getSearchedTweetsArray());
                    }
                }
            }
        }

        String get(String url, String query) throws IOException {
            final Headers headers = new Headers.Builder().add("Authorization: Bearer " + SearchMapping.mAuth.access_token).build();
            Request request = new Request.Builder()
                    .url(url + query)
                    .headers(headers)
                    .build();

            Response response = mClient.newCall(request).execute();
            return response.body().string();
        }
    }

    public static void authenticate() { new AuthenticationTask().execute(); }

    public static void search(WeakReference targetViewRef) {
        if(SearchMapping.isAuthenticated()) {
            mIsTaskPostponed = false;
            boolean ignoreOldData = true;
            SearchMapping.mPaddingUpdating = true;
            new SearchTask(targetViewRef, ignoreOldData)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "?q=" + SearchMapping.getQuery());
        }
        else {
            mIsTaskPostponed = true;
            PosponedTaskArgs.mTargetViewRef = targetViewRef;
            authenticate();
        }
    }

    public static void paddingUpdate(WeakReference targetViewRef) {
        boolean ignoreOldData = false;
        if (!SearchMapping.mPaddingUpdating && SearchMapping.mSearchMetadata.next_results != null) {
            SearchMapping.mPaddingUpdating = true;
            new SearchTask(targetViewRef, ignoreOldData)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, SearchMapping.mSearchMetadata.next_results);
        }
    }
}
