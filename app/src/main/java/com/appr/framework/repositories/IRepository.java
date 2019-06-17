package com.appr.framework.repositories;

import com.appr.framework.messages.ResponseWrapper;

import io.reactivex.subjects.Subject;

public interface IRepository {
    Subject<ResponseWrapper> getPublishSubject();

    //TODO: Add default execute network request call.
}
