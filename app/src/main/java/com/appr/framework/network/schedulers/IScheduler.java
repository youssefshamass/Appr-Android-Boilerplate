package com.appr.framework.network.schedulers;

public interface IScheduler {
    io.reactivex.Scheduler mainThread();

    io.reactivex.Scheduler io();
}
