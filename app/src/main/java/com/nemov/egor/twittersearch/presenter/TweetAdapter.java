package com.nemov.egor.twittersearch.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nemov.egor.twittersearch.R;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by egor.nemov on 04.02.16.
 */
public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    List<String> mDataset = new LinkedList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTweet;
        public ViewHolder(TextView v) {
            super(v);
            mTweet = v;
        }
    }

    public TweetAdapter(String[] dataset) {
        mDataset.addAll(Arrays.asList(dataset));
    }

    public void swap(String[] data) {
        mDataset.clear();
        mDataset.addAll(Arrays.asList(data));
        notifyDataSetChanged();
    }

    public void updateDataset(String[] update) {
        mDataset.addAll(Arrays.asList(update));
        notifyDataSetChanged();
    }

    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tweet_item, parent, false);
        ViewHolder vh = new ViewHolder((TextView) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTweet.setText(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
