package com.appr.framework.ui;

import android.os.Bundle;

import com.appr.framework.R;
import com.appr.framework.ui.base.FragmentProperties;
import com.appr.framework.ui.navigation.IUINavigation;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class SingleFragmentActivity extends BaseActivity implements IUINavigation {

    //region Activity members

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(getFragmentContainerID());
        if (fragment == null) {
            fragment = getFragment();
            fragmentManager.beginTransaction()
                    .add(getFragmentContainerID(), fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            popupFragment();
            return;
        }
        super.onBackPressed();
    }

    //endregion

    //region IUINavigation members

    @Override
    public void pushFragment(Fragment fragment, FragmentProperties fragmentProperties) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(getFragmentContainerID(), fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void popupFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0)
            fragmentManager.popBackStack();
    }

    @Override
    public boolean onBackPress() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(getFragmentContainerID());
        if (currentFragment instanceof BaseFragment && ((BaseFragment) currentFragment).onBackPressed()) {
            return true;
        }

        return super.onBackPress();
    }

    //endregion

    //region Public members

    @IdRes
    public int getFragmentContainerID() {
        return R.id.fragment_holder;
    }

    @LayoutRes
    public int getLayoutResID() {
        return R.layout.activity_single_fragment;
    }

    //endregion

    //region Abstract members

    public abstract Fragment getFragment();

    //endregion
}
