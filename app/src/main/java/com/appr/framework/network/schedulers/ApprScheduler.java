package com.appr.framework.network.schedulers;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ApprScheduler implements IScheduler {
    @Override
    public io.reactivex.Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public io.reactivex.Scheduler io() {
        return Schedulers.io();
    }
}
