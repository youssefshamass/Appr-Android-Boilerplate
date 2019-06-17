package com.appr.framework.viewmodels;

import android.content.Context;

import com.appr.framework.messages.Request;
import com.appr.framework.messages.ResponseWrapper;
import com.appr.framework.network.base.EHttpMethod;
import com.appr.framework.repositories.AbstractRepository;
import com.appr.framework.utils.GeneralHelper;

import java.util.HashMap;

import androidx.annotation.CallSuper;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

public abstract class AbstractViewModel extends ViewModel implements IViewModel {
    //region Variables

    protected CompositeDisposable mCompositeDisposable;
    protected AbstractRepository mAbstractRepository;
    public LiveData<ResponseWrapper> mDataSource;

    //endregion

    //region Constructor

    public AbstractViewModel() {
        mAbstractRepository = getAbstractRepository();
        mCompositeDisposable = new CompositeDisposable();

        mDataSource = new MutableLiveData<>();
        this.mCompositeDisposable.add(this.mAbstractRepository.getPublishSubject().subscribe(listResource -> {
            //TODO: purge pending requests map.
            ((MutableLiveData<ResponseWrapper>) mDataSource).setValue(listResource);
        }));
    }

    //endregion

    //region IViewModel members

    @Override
    @CallSuper
    public void onStart(Context context) {

    }

    @Override
    @CallSuper
    public void onStop(Context context) {

    }

    @Override
    @CallSuper
    public void onResume(Context context) {

    }

    @Override
    @CallSuper
    public void onPause(Context context) {

    }

    @Override
    public void executeRequest(Class forClass, EHttpMethod httpMethod, HashMap<String, Object> requestCriteria) {
        executeRequest(forClass, httpMethod, null, requestCriteria);
    }

    @Override
    public void executeRequest(Class forClass, EHttpMethod httpMethod, String actionName, HashMap<String, Object> requestCriteria) {
        String requestID = forClass.getSimpleName() + "-" + httpMethod.toString();
        if (!GeneralHelper.isNullOrEmpty(actionName))
            requestID += "-" + actionName;

        //TODO: Append Request Params to ID.

        Request request = new Request(forClass, httpMethod);
        request.setID(requestID);
        request.setActionName(actionName);
        request.setParams(requestCriteria);

        //TODO: break; if request id is persisted in pending requests map.

        mAbstractRepository.executeRequest(request, mCompositeDisposable);
    }

    //endregion

    //region Public members

    public void subscribe(LifecycleOwner lifecycleOwner, Observer<ResponseWrapper> observer) {
        mDataSource.observe(lifecycleOwner, observer);
    }

    public void replyOldValue() {
        ((MutableLiveData<ResponseWrapper>) mDataSource).setValue(mDataSource.getValue());
    }

    //endregion

    //region Abstract members

    abstract AbstractRepository getAbstractRepository();

    //endregion

    //region ViewModel members

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.clear();
    }

    //endregion
}
