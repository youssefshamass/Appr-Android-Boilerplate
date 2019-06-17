package com.appr.framework.ui;

import android.util.Log;

import com.appr.framework.BuildConfig;
import com.appr.framework.R;
import com.appr.framework.messages.ResponseWrapper;
import com.appr.framework.viewmodels.AbstractViewModel;

import androidx.annotation.CallSuper;
import androidx.lifecycle.Observer;

public abstract class ViewModelFragment extends BaseFragment implements Observer<ResponseWrapper> {
    //region Variables

    private AbstractViewModel mViewModel;
    protected boolean mIsLoading;

    //endregion

    //region Fragment members

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();

        if (mViewModel != null)
            mViewModel.onStart(getContext());
    }

    @Override
    @CallSuper
    public void onResume() {
        super.onResume();

        if (mViewModel != null)
            mViewModel.onResume(getContext());
    }

    @Override
    @CallSuper
    public void onPause() {
        super.onPause();

        if (mViewModel != null)
            mViewModel.onPause(getContext());
    }

    @Override
    @CallSuper
    public void onStop() {
        super.onStop();

        if (mViewModel != null)
            mViewModel.onStop(getContext());
    }

    //endregion

    //region Observer members

    @Override
    public void onChanged(ResponseWrapper tResource) {
        if (tResource instanceof ResponseWrapper.Success) {
            onSucceed(((ResponseWrapper.Success) tResource).mResponse.getData());
        } else if (tResource instanceof ResponseWrapper.Loading) {
            onLoading(((ResponseWrapper.Loading) tResource).isLoading);
        } else if (tResource instanceof ResponseWrapper.Error) {
            String originalMessage = ((ResponseWrapper.Error) tResource).exception.getMessage();
            String message = originalMessage;

            if (!BuildConfig.DEBUG) {
                message = getString(R.string.check_network_error_msg);
            }

            Log.e(TAG, message, ((ResponseWrapper.Error) tResource).exception);
            onError(((ResponseWrapper.Error) tResource).exception, originalMessage);
        }
    }


    //endregion

    //region Abstract members

    abstract AbstractViewModel getViewModel();

    //endregion

    //region Public members

    public void init() {
        mViewModel = getViewModel();
        mViewModel.subscribe(this, this);
    }

    protected void onSucceed(Object result) {

    }

    protected void onError(Throwable throwable, String message) {
        //TODO: Handle Generic Errors
    }

    protected void onLoading(boolean isLoading) {
        mIsLoading = isLoading;
    }

    //endregion
}
