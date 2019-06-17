package com.appr.framework;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class ApprApplication extends Application {
    //region Application members

    @Override
    public void onCreate() {
        super.onCreate();

        initStetho();
    }

    //endregion

    //region Private members

    private void initStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    //endregion
}
