package com.appr.framework.ui.navigation;

import com.appr.framework.ui.base.FragmentProperties;

import androidx.fragment.app.Fragment;

public interface IUINavigation {
    void pushFragment(Fragment fragment, FragmentProperties fragmentProperties);

    void popupFragment();
}
