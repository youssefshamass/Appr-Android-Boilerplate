package com.appr.framework.network.rest;

import com.appr.framework.network.base.EHttpMethod;

import io.reactivex.Single;
import retrofit2.Retrofit;

public interface IRestMethodFactory {
    Single build(Retrofit retrofit, EHttpMethod httpMethod, String actionName);
}
