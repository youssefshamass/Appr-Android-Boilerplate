package com.appr.framework.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.appr.framework.R;
import com.appr.framework.ui.navigation.IUINavigation;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    //region Constants

    public final String TAG = getClass().getSimpleName();

    //endregion

    //region Variables

    protected IUINavigation mUINavigation;

    //endregion

    //region Fragment members

    @Override
    @CallSuper
    public void onAttach(Context context) {
        if (context instanceof IUINavigation) {
            mUINavigation = (IUINavigation) context;
        }

        super.onAttach(context);
    }


    //endregion

    //region public members

    public boolean onBackPressed() {
        return false;
    }

    public void showMessageDialog(String message) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        @ColorInt int color = typedValue.data;

        showMessageDialog(message, color);
    }

    public void showMessageDialog(String message, @ColorInt int color) {
        //TODO: Implement
    }

    public void showErrorDialog(String message) {
        showMessageDialog(message, ContextCompat.getColor(getContext(),
                R.color.dark_red));
    }

    public void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();

        if (view == null) return;

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager == null) return;

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //endregion
}
