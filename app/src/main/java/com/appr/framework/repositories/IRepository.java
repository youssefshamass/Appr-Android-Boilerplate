package com.appr.framework.repositories;

import com.appr.framework.messages.Request;
import com.appr.framework.messages.ResponseWrapper;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.Subject;

public interface IRepository {
    Subject<ResponseWrapper> getPublishSubject();

    void executeRequest(Request request, CompositeDisposable compositeDisposable);
}
