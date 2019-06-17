package com.appr.framework.viewmodels;

import android.content.Context;

import com.appr.framework.network.base.EHttpMethod;

import java.util.HashMap;

public interface IViewModel {
    void onStart(Context context);

    void onResume(Context context);

    void onPause(Context context);

    void onStop(Context context);

    void executeRequest(Class forClass, EHttpMethod httpMethod, HashMap<String, Object> requestCriteria);

    void executeRequest(Class forClass, EHttpMethod httpMethod, String actionName,
                        HashMap<String, Object> requestCriteria);
}
