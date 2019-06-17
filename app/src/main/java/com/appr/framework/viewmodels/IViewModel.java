package com.appr.framework.viewmodels;

import android.content.Context;

public interface IViewModel {
    void onStart(Context context);

    void onResume(Context context);

    void onPause(Context context);

    void onStop(Context context);
}
