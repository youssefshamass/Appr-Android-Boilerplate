package com.appr.framework.network.rest;

import com.appr.framework.models.base.Model;

import java.util.HashMap;

import retrofit2.Retrofit;

public class RestFactory {
    //region Variables

    private static final Object mLock = new Object();
    private static RestFactory mInstance;
    private HashMap<Class, IRestMethodFactory> mFactoriesMap;

    //endregion

    //region Constructor

    private RestFactory() {
        mFactoriesMap = new HashMap<>();
    }

    //endregion

    //region Public members

    public static RestFactory getInstance() {
        synchronized (mLock) {
            if (mInstance == null)
                mInstance = new RestFactory();


        }

        return mInstance;
    }

    public void subscribe(Class className, IRestMethodFactory factory) {
        mFactoriesMap.put(className, factory);
    }

    public IRestMethodFactory get(Class className) {
        return mFactoriesMap.get(className);
    }

    //endregion
}
