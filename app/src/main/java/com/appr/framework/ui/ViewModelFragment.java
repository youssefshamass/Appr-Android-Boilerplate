package com.appr.framework.ui;

import android.util.Log;

import com.appr.framework.BuildConfig;
import com.appr.framework.R;
import com.appr.framework.messages.ResponseWrapper;
import com.appr.framework.viewmodels.AbstractViewModel;

import androidx.lifecycle.Observer;

public abstract class ViewModelFragment extends BaseFragment implements Observer<ResponseWrapper> {
    //region Variables

    private AbstractViewModel mViewModel;
    protected boolean mIsLoading;

    //endregion

    //region Abstract members

    public abstract AbstractViewModel getViewModel();

    @Override
    public void onResume() {
        super.onResume();

        init();
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

    //region Public members

    public void init() {
        mViewModel = getViewModel();
        mViewModel.subscribe(this, this);
    }

    public void onSucceed(Object result) {

    }

    public void onError(Throwable throwable, String message) {
        //TODO: Handle Generic Errors
    }

    public void onLoading(boolean isLoading) {
        mIsLoading = isLoading;
    }

    //endregion
}
