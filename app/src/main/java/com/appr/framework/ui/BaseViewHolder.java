package com.appr.framework.ui;

import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    //region Variables

    private Object mDataSource;

    //endregion

    //region Constructor

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    //endregion

    //region Public members

    @CallSuper
    public void bindObject(Object object) {
        mDataSource = object;
    }

    //endregion

    //region Getters

    public Object getDataSource() {
        return mDataSource;
    }

    //endregion
}
