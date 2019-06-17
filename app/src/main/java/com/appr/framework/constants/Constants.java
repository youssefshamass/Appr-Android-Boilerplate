package com.appr.framework.constants;

import android.content.Context;

import com.appr.framework.BuildConfig;
import com.appr.framework.network.rest.IRestMethodFactory;
import com.appr.framework.network.rest.RestFactory;
import com.appr.framework.network.schedulers.ApprScheduler;
import com.appr.framework.network.schedulers.IScheduler;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Constants {

    //region Constants

    private static final long CACHE_SIZE = 10 * 1024 * 1024;
    private static final long DEFAULT_CONNECT_NETWORK_TIMEOUT = 10; //Seconds
    private static final long DEFAULT_WRITE_NETWORK_TIMEOUT = 30; //Seconds
    private static final long DEFAULT_READ_NETWORK_TIMEOUT = 30; //Seconds

    //endregion

    //region Variables

    private static final Object mLock = new Object();
    private static Constants mInstance;
    public static String BASE_URL;
    public Context context;
    public IScheduler scheduler;
    public Picasso picasso;
    public Retrofit retrofit;
    public Gson gson;
    public OkHttpClient okHttpClient;
    public RestFactory restFactory;

    //endregion

    //region Constructor

    private Constants() {
        BASE_URL = BuildConfig.BASE_URL;
    }

    //endregion

    //region Public members

    public static Constants getInstance() {
        synchronized (mLock) {
            if (mInstance == null)
                mInstance = new Constants();
        }
        return mInstance;
    }

    public void init(Context context, @Nullable List<Interceptor> networkInterceptors) {
        this.context = context;

        initAppModule();
        initImageModule();
        initNetworkModule(networkInterceptors);
    }

    public void resetNetworkModule(String url) {
        BASE_URL = url;

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    //endregion

    //region Private members

    private void initAppModule() {
        scheduler = new ApprScheduler();
    }

    private void initNetworkModule(List<Interceptor> interceptors) {
        //init cache
        Cache cache = new Cache(context.getCacheDir(), Constants.CACHE_SIZE);

        //init GSON
        gson = new Gson();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

        //Init OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(DEFAULT_CONNECT_NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_NETWORK_TIMEOUT, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG)
            builder.addNetworkInterceptor(new StethoInterceptor());

        //3rd Party Interceptors
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }

        okHttpClient = builder.build();

        //Init retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        //Init RestMethodFactory
        restFactory = RestFactory.getInstance();
    }

    private void initImageModule() {
        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(context);

        picasso = new Picasso.Builder(context)
                .downloader(okHttp3Downloader)
                .build();
    }


    //endregion
}
