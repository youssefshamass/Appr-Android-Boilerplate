package com.appr.framework.repositories;

import com.appr.framework.messages.Response;
import com.appr.framework.messages.ResponseWrapper;

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

    //endregion

    //region Private members

    protected Subject<ResponseWrapper> publishLoading(boolean isLoading) {
        mPublishSubject.onNext(new ResponseWrapper.Loading(isLoading));
        return mPublishSubject;
    }

    protected Subject<ResponseWrapper> publishResult(Response result) {
        publishLoading(false);
        mPublishSubject.onNext(new ResponseWrapper.Success(result));
        return mPublishSubject;
    }

    protected Subject<ResponseWrapper> publishError(Throwable error) {
        publishLoading(false);

        mPublishSubject.onNext(new ResponseWrapper.Error(error));
        return mPublishSubject;
    }

    //endregion
}
