package com.appr.framework.repositories;

import android.annotation.SuppressLint;

import com.appr.framework.constants.Constants;
import com.appr.framework.messages.Request;
import com.appr.framework.messages.Response;
import com.appr.framework.messages.ResponseWrapper;
import com.appr.framework.network.rest.IRestMethodFactory;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class AbstractRepository implements IRepository {
    //region Variables

    protected Subject<ResponseWrapper> mPublishSubject;

    //endregion

    //region Constructor

    public AbstractRepository() {
        mPublishSubject = PublishSubject.create();
    }


    //endregion

    //region IRepository members

    @Override
    public Subject<ResponseWrapper> getPublishSubject() {
        return mPublishSubject;
    }

    @SuppressLint("CheckResult")
    @Override
    public void executeRequest(Request request, CompositeDisposable compositeDisposable) {
        IRestMethodFactory restFactory;
        Single apiRequest;
        Disposable disposable;
        Constants constants;

        if (request == null)
            throw new IllegalArgumentException("Fatal Error, Null request");

        if (request.getForClass() == null)
            throw new IllegalArgumentException("Fatal Error, request not linked to a certain model");

        constants = Constants.getInstance();
        restFactory = constants.restFactory.get(request.getForClass());

        if (restFactory == null)
            throw new IllegalArgumentException("Fatal Error, no rest factory defined for class " + request.getForClass().getSimpleName());

        publishLoading(true);

        apiRequest = restFactory.build(constants.retrofit, request);
        disposable = apiRequest.observeOn(constants.scheduler.mainThread())
                .subscribeOn(constants.scheduler.io())
                .subscribe(result -> {
                    Response response = new Response(result);
                    //TODO: cache response if model is annotated as cacheable.

                    publishResult(response, request);
                }, error -> {
                    publishError((Throwable) error, request);
                });

        compositeDisposable.add(disposable);
    }

    //endregion

    //region Private members

    protected Subject<ResponseWrapper> publishLoading(boolean isLoading) {
        mPublishSubject.onNext(new ResponseWrapper.Loading(isLoading));
        return mPublishSubject;
    }

    protected Subject<ResponseWrapper> publishResult(Response result, Request request) {
        publishLoading(false);
        ResponseWrapper.Success publishObject = new ResponseWrapper.Success(result);
        publishObject.setRequestID(request.getID());

        mPublishSubject.onNext(publishObject);
        return mPublishSubject;
    }

    protected Subject<ResponseWrapper> publishError(Throwable error, Request request) {
        publishLoading(false);
        ResponseWrapper.Error publishObject = new ResponseWrapper.Error(error);
        publishObject.setRequestID(request.getID());

        mPublishSubject.onNext(publishObject);
        return mPublishSubject;
    }

    //endregion
}
