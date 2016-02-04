package com.nemov.egor.twittersearch;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by egor.nemov on 04.02.16.
 */
public class DebugApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
