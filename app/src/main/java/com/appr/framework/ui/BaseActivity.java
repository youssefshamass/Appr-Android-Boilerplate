package com.appr.framework.ui;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    //region Constants

    public final String TAG = this.getClass().getSimpleName();

    //endregion

    //region Activity members

    @Override
    public void onBackPressed() {
        if (!onBackPress())
            super.onBackPressed();
    }


    //endregion

    //region Public members

    public void hideSoftKeyboard() {
        View view = getCurrentFocus();

        if (view == null) return;

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager == null) return;

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public boolean onBackPress() {
        return false;
    }

    //endregion
}
