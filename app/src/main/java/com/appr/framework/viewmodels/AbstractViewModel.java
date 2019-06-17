package com.appr.framework.viewmodels;

import com.appr.framework.messages.ResponseWrapper;
import com.appr.framework.repositories.AbstractRepository;

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
        this.mCompositeDisposable.add(this.mAbstractRepository.getPublishSubject().subscribe(listResource ->
                ((MutableLiveData<ResponseWrapper>) mDataSource).setValue(listResource)));
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
