package com.appr.framework.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.appr.framework.ui.base.EActionBarStyle;
import com.appr.framework.ui.base.FragmentProperties;
import com.appr.framework.ui.navigation.ApprNavigationController;
import com.appr.framework.ui.navigation.IUINavigation;
import com.appr.framework.utils.Stack;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

public abstract class BottomNavigationActivity extends BaseActivity implements IUINavigation,
        ApprNavigationController.TransactionListener, ApprNavigationController.RootFragmentListener {
    //region Constants

    private final static String EXTRA_STACK_HISTORY = BottomNavigationActivity.class.getSimpleName() + ".EXTRA_STACK_HISTORY";
    private final static String EXTRA_ALLOW_STACK_HISTORY = BottomNavigationActivity.class.getSimpleName() + ".EXTRA_ALLOW_STACK_HISTORY";
    private final static String EXTRA_SELECTED_TAB_INDEX = BottomNavigationActivity.class.getSimpleName() + ".EXTRA_SELECTED_TAB_INDEX";

    //endregion

    //region Variables

    protected ApprNavigationController mNavigationController;
    private Stack mStackHistory;
    private FragmentProperties mCurrentFragmentProperties;

    private int mSelectedTabIndex = 0;
    private boolean mIsTabPressed = true;

    //endregion

    //region Abstract Members

    @IdRes
    public abstract int getContainerID();

    @NonNull
    public abstract BottomNavigationView getBottomNavigationView();

    @NonNull
    public abstract Pair<Fragment, FragmentProperties> getTabFragment(int index);

    @Nullable
    public abstract Toolbar getToolBar();

    //endregion

    //region Activity Members

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStackHistory = new Stack();

        setActionBar();
        attachListener();
        restoreInstanceState(savedInstanceState);
        initNavigationController(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onBackPress() {
        boolean returnValue;
        Fragment currentFragment;
        do {
            currentFragment = getCurrentFragment();
            if (currentFragment instanceof BaseFragment && ((BaseFragment) currentFragment).onBackPressed()) {
                returnValue = true;
                break;
            }

            if (!mNavigationController.isRootFragment()) {
                mNavigationController.popFragment(1);
                returnValue = true;
            } else {
                if (mStackHistory.isEmpty()) {
                    returnValue = false;
                } else {
                    if (mStackHistory.getStackSize() > 1) {

                        int position = mStackHistory.popPrevious();

                        mIsTabPressed = false;
                        updateTabSelection(position);

                    } else {
                        updateTabSelection(0);

                        mStackHistory.emptyStack();
                    }
                    returnValue = true;
                }
            }
        } while (false);
        return returnValue;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mStackHistory != null)
            outState.putSerializable(EXTRA_STACK_HISTORY, mStackHistory);

        outState.putBoolean(EXTRA_ALLOW_STACK_HISTORY, mIsTabPressed);

        outState.putInt(EXTRA_SELECTED_TAB_INDEX, mSelectedTabIndex);

        if (mNavigationController != null) {
            mNavigationController.onSaveInstanceState(outState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Fragment currentFragment;
        do {
            if (getSupportActionBar() == null) break;
            if (mCurrentFragmentProperties == null) break;
            if (mCurrentFragmentProperties.getMenuResID() == null) break;

            getMenuInflater().inflate(mCurrentFragmentProperties.getMenuResID(), menu);

            currentFragment = mNavigationController.getCurrentFragment();

            if (currentFragment == null) break;

            //if (currentFragment instanceof BaseFragment) {
            //  ((BaseFragment) currentFragment).onActionBarMenuCreated(menu);
            //}
        } while (false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mNavigationController.getCurrentFragment() != null)
            mNavigationController.getCurrentFragment().onActivityResult(requestCode, resultCode, data);
    }

    //endregion

    //region Public Members

    public void updateTabSelection(int tabIndex) {
        Menu menu = getBottomNavigationView().getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (tabIndex == i) {
                getBottomNavigationView().setSelectedItemId(menuItem.getItemId());
                break;
            }
        }
    }

    @Nullable
    public Fragment getCurrentFragment() {
        return mNavigationController.getCurrentFragment();
    }

    public boolean switchTab(int position) {
        return mNavigationController.switchTab(position);
    }

    //endregion

    //region Private Members


    private void updateToolbar(String title, String subTitle, boolean hideToolBar, EActionBarStyle actionBarStyle) {
        if (getSupportActionBar() == null) return;

        getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavigationController.isRootFragment());
        getSupportActionBar().setDisplayShowHomeEnabled(!mNavigationController.isRootFragment());

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subTitle);

        if (hideToolBar)
            hideActionBar();
        else
            showActionBar();
    }

    private void initNavigationController(Bundle savedInstanceState) {
        mNavigationController = ApprNavigationController.newBuilder(savedInstanceState, getSupportFragmentManager(), getContainerID())
                .transactionListener(this)
                .rootFragmentListener(this, getBottomNavigationView().getMenu().size())
                .build();

        switchTab(mSelectedTabIndex);

        FragmentProperties fragmentProperties = getTabFragment(mSelectedTabIndex).second;

        assert fragmentProperties != null;

        updateToolbar(fragmentProperties.getTitle(), fragmentProperties.getSubtitle(), fragmentProperties.isHideActionBar(), fragmentProperties.getActionBarStyle());
    }

    private void attachListener() {
        getBottomNavigationView().setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean result = switchTab(getMenuItemIndex(item));

                if (mIsTabPressed && result) {
                    mStackHistory.push(getMenuItemIndex(item));
                    mSelectedTabIndex = getMenuItemIndex(item);
                    mIsTabPressed = true;

                    return true;
                }

                return result;
            }
        });

        getBottomNavigationView().setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {

            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                mNavigationController.clearStack();
                int index = getMenuItemIndex(item);

                BottomNavigationActivity.this.onNavigationItemReselected(index);
                switchTab(index);
            }
        });
    }

    public void onNavigationItemReselected(int index) {

    }

    private int getMenuItemIndex(MenuItem item) {
        int returnValue = 0;
        do {
            if (getBottomNavigationView().getMenu().size() == 0) break;

            for (int i = 0; i < getBottomNavigationView().getMenu().size(); i++) {
                MenuItem menuItem = getBottomNavigationView().getMenu().getItem(i);

                if (menuItem.getItemId() == item.getItemId()) {
                    returnValue = i;
                    break;
                }
            }
        } while (false);
        return returnValue;
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        do {
            if (savedInstanceState == null) break;

            if (savedInstanceState.containsKey(EXTRA_STACK_HISTORY))
                mStackHistory = (Stack) savedInstanceState.getSerializable(EXTRA_STACK_HISTORY);

            mIsTabPressed = savedInstanceState.getBoolean(EXTRA_ALLOW_STACK_HISTORY, true);
            mSelectedTabIndex = savedInstanceState.getInt(EXTRA_SELECTED_TAB_INDEX, 0);
        } while (false);
    }

    private void hideActionBar() {
        if (getSupportActionBar() == null) return;

        getSupportActionBar().hide();
    }

    private void showActionBar() {
        if (getSupportActionBar() == null) return;

        getSupportActionBar().show();
    }

    @SuppressLint("RestrictedApi")
    private void setActionBar() {
        if (getToolBar() != null) {
            setSupportActionBar(getToolBar());
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    //endregion

    //region FragmentNavigation

    @Override
    public void pushFragment(Fragment fragment, FragmentProperties fragmentProperties) {
        if (mNavigationController != null) {
            mNavigationController.pushFragment(fragment, fragmentProperties);
        }
    }

    @Override
    public void popupFragment() {
        mNavigationController.popFragment(1);
    }

    //endregion

    //region TransactionListener

    @Override
    public void onTabTransaction(FragmentProperties fragmentProperties, int index) {
        mCurrentFragmentProperties = fragmentProperties;

        if (getSupportActionBar() != null && mNavigationController != null) {
            invalidateOptionsMenu();
            updateToolbar(fragmentProperties.getTitle(), fragmentProperties.getSubtitle(), fragmentProperties.isHideActionBar(), fragmentProperties.getActionBarStyle());
        }
    }

    @Override
    public void onFragmentTransaction(FragmentProperties fragmentProperties, ApprNavigationController.TransactionType transactionType) {
        mCurrentFragmentProperties = fragmentProperties;

        if (getSupportActionBar() != null && mNavigationController != null) {
            invalidateOptionsMenu();
            updateToolbar(fragmentProperties.getTitle(), fragmentProperties.getSubtitle(), fragmentProperties.isHideActionBar(), fragmentProperties.getActionBarStyle());
        }
    }

    @Override
    public void beforeFragmentTransaction() {
        //Fragment currentFragment;
        do {
            hideSoftKeyboard();

            //currentFragment = getCurrentFragment();

            //if (currentFragment != null && currentFragment instanceof BaseFragment)
            //    ((BaseFragment) currentFragment).onHide();
        } while (false);
    }

    //endregion

    //region RootFragmentListener

    @Override
    public Pair<Fragment, FragmentProperties> getRootFragment(int index) {
        return getTabFragment(index);
    }

    //endregion
}
