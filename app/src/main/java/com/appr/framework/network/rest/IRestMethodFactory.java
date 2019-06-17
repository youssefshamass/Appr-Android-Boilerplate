package com.appr.framework.network.rest;

import com.appr.framework.messages.Request;

import io.reactivex.Single;
import retrofit2.Retrofit;

public interface IRestMethodFactory {
    Single build(Retrofit retrofit, Request request);
}
