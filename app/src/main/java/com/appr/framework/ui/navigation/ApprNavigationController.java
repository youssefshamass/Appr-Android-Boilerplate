package com.appr.framework.ui.navigation;

import android.os.Bundle;

import com.appr.framework.R;
import com.appr.framework.ui.base.FragmentProperties;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ApprNavigationController {

    //region Constants

    private static final String EXTRA_TAG_COUNT = ApprNavigationController.class.getSimpleName() + ".EXTRA_TAG_COUNT";
    private static final String EXTRA_SELECTED_TAB_INDEX = ApprNavigationController.class.getSimpleName() + ".EXTRA_SELECTED_TAB_INDEX";
    private static final String EXTRA_CURRENT_FRAGMENT = ApprNavigationController.class.getSimpleName() + ".EXTRA_CURRENT_FRAGMENT";
    private static final String EXTRA_FRAGMENT_STACK = ApprNavigationController.class.getSimpleName() + ".EXTRA_FRAGMENT_STACK";
    private static final String EXTRA_FRAGMENT_PROPERTIES_MAP = ApprNavigationController.class.getSimpleName() + ".EXTRA_FRAGMENT_PROPERTIES_MAP";

    //endregion

    //region Variables

    @IdRes
    private int mContainerId;
    private int mSelectedTabIndex = 0;
    private int mTagCount;
    private boolean mAnimateTabSwitch = false;

    private List<Stack<Fragment>> mFragmentStacks;
    private RootFragmentListener mRootFragmentListener;
    private TransactionListener mTransactionListener;
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private HashMap<String, FragmentProperties> mFragmentPropertiesMap;
    private boolean mExecutingTransaction;

    //endregion

    //region Constructor

    private ApprNavigationController(Builder builder, @Nullable Bundle savedInstanceState) {
        mFragmentManager = builder.mFragmentManager;
        mContainerId = builder.mContainerId;
        mFragmentStacks = new ArrayList<>(builder.mNumberOfTabs);
        mRootFragmentListener = builder.mRootFragmentListener;
        mTransactionListener = builder.mTransactionListener;
        mSelectedTabIndex = builder.mSelectedTabIndex;
        mFragmentPropertiesMap = new HashMap<>();

        if (!restoreFromBundle(savedInstanceState)) {

            for (int i = 0; i < builder.mNumberOfTabs; i++) {
                Stack<Fragment> stack = new Stack<>();

                mFragmentStacks.add(stack);
            }

            initialize(builder.mSelectedTabIndex);
        }
    }

    //endregion

    //region Public Members

    public static Builder newBuilder(@Nullable Bundle savedInstanceState, FragmentManager fragmentManager, int containerId) {
        return new Builder(savedInstanceState, fragmentManager, containerId);
    }

    public boolean switchTab(int index) throws IndexOutOfBoundsException {
        FragmentTransaction fragmentTransaction;
        Fragment fragment = null;
        String fragmentTag;
        FragmentProperties fragmentProperties;
        int enterAnimationID = R.anim.slide_in_left;
        int exitAnimationID = R.anim.slide_out_right;
        do {
            if (index >= mFragmentStacks.size()) {
                throw new IndexOutOfBoundsException("Can't switch to a tab that hasn't been initialized, " +
                        "Index : " + index + ", current stack size : " + mFragmentStacks.size() +
                        ". Make sure to create all of the tabs you need in the Constructor or provide a way for them to be created via RootFragmentListener.");
            }

            if (mSelectedTabIndex == index)
                return false;

            fragmentProperties = mRootFragmentListener.getRootFragment(index).second;

            if (mTransactionListener != null) {
                if (!mTransactionListener.beforeTabTransaction(fragmentProperties, index))
                    return false;
            }

            if (mAnimateTabSwitch) {
                if (mSelectedTabIndex > index) {
                    enterAnimationID = R.anim.slide_in_left;
                    exitAnimationID = R.anim.slide_out_right;
                } else {
                    enterAnimationID = R.anim.slide_in_right;
                    exitAnimationID = R.anim.slide_out_left;
                }
            }

            mSelectedTabIndex = index;

            fragmentTransaction = mFragmentManager.beginTransaction();
            if (mAnimateTabSwitch)
                fragmentTransaction.setCustomAnimations(enterAnimationID, exitAnimationID);
            detachCurrentFragment(fragmentTransaction);

            if (index == -1) {
                fragmentTransaction.commit();
            } else {
                fragment = reattachPreviousFragment(fragmentTransaction);
                if (fragment != null) {
                    fragmentTransaction.commit();
                } else {
                    fragment = getRootFragment(mSelectedTabIndex);
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.add(mContainerId, fragment, generateTag(fragment));
                    fragmentTransaction.commit();

                    fragmentTag = fragment.getTag();
                    fragmentProperties.setFragmentTag(fragmentTag);

                    mFragmentPropertiesMap.put(fragmentTag, fragmentProperties);
                }
            }

            executePendingTransactions();

            mCurrentFragment = fragment;

            fragmentProperties = mFragmentPropertiesMap.get(fragment.getTag());

            if (mTransactionListener != null) {
                mTransactionListener.onTabTransaction(fragmentProperties, mSelectedTabIndex);
            }

            return true;
        } while (false);
    }

    public void pushFragment(@Nullable Fragment fragment, FragmentProperties fragmentProperties) {
        FragmentTransaction fragmentTransaction;
        String fragmentTag;
        do {
            if (fragment != null && mSelectedTabIndex != -1) {
                if (mTransactionListener != null) {
                    mTransactionListener.beforeFragmentTransaction();
                }

                fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

                detachCurrentFragment(fragmentTransaction);
                fragmentTransaction.add(mContainerId, fragment, generateTag(fragment));
                fragmentTransaction.commit();

                executePendingTransactions();

                mFragmentStacks.get(mSelectedTabIndex).push(fragment);

                mCurrentFragment = fragment;

                fragmentTag = fragment.getTag();
                fragmentProperties.setFragmentTag(fragmentTag);

                mFragmentPropertiesMap.put(fragmentTag, fragmentProperties);

                if (mTransactionListener != null) {
                    mTransactionListener.onFragmentTransaction(fragmentProperties, TransactionType.PUSH);
                }
            }
        } while (false);
    }

    public void popFragment(int popDepth) throws UnsupportedOperationException {
        Fragment fragment;
        FragmentTransaction fragmentTransaction;
        boolean shouldPush = false;
        FragmentProperties fragmentProperties;
        do {
            if (isRootFragment()) {
                throw new UnsupportedOperationException(
                        "You can not popFragment the rootFragment. If you need to change this fragment, use replaceFragment(fragment)");
            } else if (popDepth < 1) {
                throw new UnsupportedOperationException("popFragments parameter needs to be greater than 0");
            } else if (mSelectedTabIndex == -1) {
                throw new UnsupportedOperationException("You can not pop fragments when no tab is selected");
            }

            if (popDepth >= mFragmentStacks.get(mSelectedTabIndex).size() - 1) {
                clearStack();
                break;
            }

            if (mTransactionListener != null) {
                mTransactionListener.beforeFragmentTransaction();
            }

            fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

            for (int i = 0; i < popDepth; i++) {
                fragment = mFragmentManager.findFragmentByTag(mFragmentStacks.get(mSelectedTabIndex).pop().getTag());
                if (fragment != null) {
                    fragmentTransaction.remove(fragment);
                    mFragmentPropertiesMap.remove(fragment.getTag());
                }
            }

            fragment = reattachPreviousFragment(fragmentTransaction);

            if (fragment != null) {
                fragmentTransaction.commit();
            } else {
                if (!mFragmentStacks.get(mSelectedTabIndex).isEmpty()) {
                    fragment = mFragmentStacks.get(mSelectedTabIndex).peek();
                    fragmentTransaction.add(mContainerId, fragment, fragment.getTag());
                    fragmentTransaction.commit();
                } else {
                    fragment = getRootFragment(mSelectedTabIndex);
                    fragmentTransaction.add(mContainerId, fragment, generateTag(fragment));
                    fragmentTransaction.commit();

                    shouldPush = true;
                }
            }

            executePendingTransactions();

            if (shouldPush) {
                mFragmentStacks.get(mSelectedTabIndex).push(fragment);
            }

            mCurrentFragment = fragment;

            fragmentProperties = mFragmentPropertiesMap.get(fragment.getTag());

            if (mTransactionListener != null) {
                mTransactionListener.onFragmentTransaction(fragmentProperties, TransactionType.POP);
            }
        } while (false);
    }

    public void clearStack() {
        Stack<Fragment> fragmentStack;
        Fragment fragment;
        FragmentTransaction fragmentTransaction;
        FragmentProperties fragmentProperties;
        String fragmentTag;
        boolean shouldPush = false;
        do {
            if (mSelectedTabIndex == -1) break;

            fragmentStack = mFragmentStacks.get(mSelectedTabIndex);

            if (fragmentStack.size() > 1) {
                if (mTransactionListener != null) {
                    mTransactionListener.beforeFragmentTransaction();
                }

                fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

                while (fragmentStack.size() > 1) {
                    fragment = mFragmentManager.findFragmentByTag(fragmentStack.pop().getTag());
                    if (fragment != null) {
                        fragmentTransaction.remove(fragment);
                    }
                }

                fragment = reattachPreviousFragment(fragmentTransaction);

                if (fragment != null) {
                    fragmentTransaction.commit();
                } else {
                    if (!fragmentStack.isEmpty()) {
                        fragment = fragmentStack.peek();
                        fragmentTransaction.add(mContainerId, fragment, fragment.getTag());
                        fragmentTransaction.commit();
                    } else {
                        fragment = getRootFragment(mSelectedTabIndex);
                        fragmentTransaction.add(mContainerId, fragment, generateTag(fragment));
                        fragmentTransaction.commit();

                        shouldPush = true;

                        fragmentTag = fragment.getTag();
                        fragmentProperties = mRootFragmentListener.getRootFragment(mSelectedTabIndex).second;
                        fragmentProperties.setFragmentTag(fragmentTag);

                        mFragmentPropertiesMap.put(fragmentTag, mRootFragmentListener.getRootFragment(mSelectedTabIndex).second);

                    }
                }

                executePendingTransactions();

                if (shouldPush) {
                    mFragmentStacks.get(mSelectedTabIndex).push(fragment);
                }

                mFragmentStacks.set(mSelectedTabIndex, fragmentStack);

                mCurrentFragment = fragment;

                fragmentProperties = mFragmentPropertiesMap.get(fragment.getTag());

                if (mTransactionListener != null) {
                    mTransactionListener.onFragmentTransaction(fragmentProperties, TransactionType.POP);
                }
            }
        } while (false);
    }

    public boolean isRootFragment() {
        Stack<Fragment> stack = getCurrentStack();

        return stack == null || stack.size() == 1;
    }

    public Stack<Fragment> getCurrentStack() {
        return getStack(mSelectedTabIndex);
    }

    public Stack<Fragment> getStack(int index) {
        if (index == -1) return null;

        if (index >= mFragmentStacks.size()) {
            throw new IndexOutOfBoundsException("Can't get an index that's larger than we've setup");
        }

        return (Stack<Fragment>) mFragmentStacks.get(index).clone();
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt(EXTRA_TAG_COUNT, mTagCount);

        outState.putInt(EXTRA_SELECTED_TAB_INDEX, mSelectedTabIndex);

        if (mCurrentFragment != null) {
            outState.putString(EXTRA_CURRENT_FRAGMENT, mCurrentFragment.getTag());
        }

        if (mFragmentPropertiesMap != null)
            outState.putSerializable(EXTRA_FRAGMENT_PROPERTIES_MAP, mFragmentPropertiesMap);

        try {
            final JSONArray stackArrays = new JSONArray();

            for (Stack<Fragment> stack : mFragmentStacks) {
                final JSONArray stackArray = new JSONArray();

                for (Fragment fragment : stack) {
                    stackArray.put(fragment.getTag());
                }

                stackArrays.put(stackArray);
            }

            outState.putString(EXTRA_FRAGMENT_STACK, stackArrays.toString());
        } catch (Throwable ignored) {
        }
    }

    //endregion

    //region Private Members

    private boolean restoreFromBundle(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return false;
        }

        mTagCount = savedInstanceState.getInt(EXTRA_TAG_COUNT, 0);

        mCurrentFragment = mFragmentManager.findFragmentByTag(savedInstanceState.getString(EXTRA_CURRENT_FRAGMENT));

        if (savedInstanceState.containsKey(EXTRA_FRAGMENT_PROPERTIES_MAP))
            mFragmentPropertiesMap = (HashMap<String, FragmentProperties>) savedInstanceState.getSerializable(EXTRA_FRAGMENT_PROPERTIES_MAP);

        try {
            final JSONArray stackArrays = new JSONArray(savedInstanceState.getString(EXTRA_FRAGMENT_STACK));

            for (int x = 0; x < stackArrays.length(); x++) {
                final JSONArray stackArray = stackArrays.getJSONArray(x);
                final Stack<Fragment> stack = new Stack<>();

                if (stackArray.length() == 1) {
                    final String tag = stackArray.getString(0);
                    final Fragment fragment;

                    if (tag == null || "null".equalsIgnoreCase(tag)) {
                        fragment = getRootFragment(x);

                    } else {
                        fragment = mFragmentManager.findFragmentByTag(tag);
                    }

                    if (fragment != null) {
                        stack.add(fragment);
                    }
                } else {
                    for (int y = 0; y < stackArray.length(); y++) {
                        final String tag = stackArray.getString(y);

                        if (tag != null && !"null".equalsIgnoreCase(tag)) {
                            final Fragment fragment = mFragmentManager.findFragmentByTag(tag);

                            if (fragment != null) {
                                stack.add(fragment);
                            }
                        }
                    }
                }

                mFragmentStacks.add(stack);
            }

            int selectedTabIndex = savedInstanceState.getInt(EXTRA_SELECTED_TAB_INDEX);
            switchTab(selectedTabIndex);

            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private void initialize(int index) {
        FragmentTransaction fragmentTransaction;
        Fragment fragment;
        FragmentProperties fragmentProperties;
        String fragmentTag;
        if (mSelectedTabIndex > mFragmentStacks.size()) {
            throw new IndexOutOfBoundsException("Starting index cannot be larger than the number of stacks");
        }

        clearFragmentManager();

        fragmentTransaction = mFragmentManager.beginTransaction();

        fragment = getRootFragment(index);
        fragmentTransaction.add(mContainerId, fragment, generateTag(fragment));
        fragmentTransaction.commit();

        fragmentTag = fragment.getTag();
        fragmentProperties = mRootFragmentListener.getRootFragment(index).second;
        fragmentProperties.setFragmentTag(fragmentTag);

        mFragmentPropertiesMap.put(fragmentTag, fragmentProperties);

        executePendingTransactions();

        mCurrentFragment = fragment;

        if (mTransactionListener != null) {
            mTransactionListener.onTabTransaction(fragmentProperties, mSelectedTabIndex);
        }
    }

    //Reason behind clunky transitions.
    private void executePendingTransactions() {
        //if (!mExecutingTransaction) {
        //    mExecutingTransaction = true;
        //    mFragmentManager.executePendingTransactions();
        //    mExecutingTransaction = false;
        //}
    }

    private String generateTag(@NonNull Fragment fragment) {
        return fragment.getClass().getName() + ++mTagCount;
    }

    public Fragment getCurrentFragment() {
        Fragment returnValue = null;
        Stack<Fragment> fragmentStack;

        if (mSelectedTabIndex == -1) return null;

        if (mCurrentFragment != null) {
            returnValue = mCurrentFragment;
        } else {
            fragmentStack = mFragmentStacks.get(mSelectedTabIndex);

            if (!fragmentStack.isEmpty()) {
                returnValue = mFragmentManager.findFragmentByTag(mFragmentStacks.get(mSelectedTabIndex).peek().getTag());
                mCurrentFragment = returnValue;
            }
        }

        return returnValue;
    }

    private void detachCurrentFragment(@NonNull FragmentTransaction fragmentTransaction) {
        Fragment oldFrag = getCurrentFragment();

        if (oldFrag != null) {
            fragmentTransaction.detach(oldFrag);
        }
    }

    private Fragment reattachPreviousFragment(@NonNull FragmentTransaction fragmentTransaction) {
        Fragment returnValue = null;
        Stack<Fragment> fragmentStack;
        do {
            fragmentStack = mFragmentStacks.get(mSelectedTabIndex);

            if (!fragmentStack.isEmpty()) {
                returnValue = mFragmentManager.findFragmentByTag(fragmentStack.peek().getTag());

                if (returnValue != null) {
                    fragmentTransaction.attach(returnValue);
                }
            }
        } while (false);
        return returnValue;
    }

    private Fragment getRootFragment(int index) throws IllegalStateException {
        Fragment returnValue = null;
        Pair<Fragment, FragmentProperties> fragmentPropertiesPair;

        if (!mFragmentStacks.get(index).isEmpty()) {
            returnValue = mFragmentStacks.get(index).peek();
        } else if (mRootFragmentListener != null) {
            fragmentPropertiesPair = mRootFragmentListener.getRootFragment(index);

            returnValue = fragmentPropertiesPair.first;

            if (mSelectedTabIndex != -1) {
                mFragmentStacks.get(mSelectedTabIndex).push(returnValue);
            }

        }

        if (returnValue == null) {
            throw new IllegalStateException("Either you haven't past in a fragment at this index in your constructor, or you haven't " +
                    "provided a way to create it while via your RootFragmentListener.getRootFragment(index)");
        }
        return returnValue;
    }

    private void clearFragmentManager() {
        FragmentTransaction fragmentTransaction;
        if (mFragmentManager.getFragments().size() == 0) return;

        fragmentTransaction = mFragmentManager.beginTransaction();

        for (Fragment fragment : mFragmentManager.getFragments()) {
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }
        }

        fragmentTransaction.commit();
        executePendingTransactions();
    }

    //endregion

    //region Enums

    //region TransactionType

    public enum TransactionType {
        PUSH,
        POP,
        REPLACE
    }

    //endregion

    //endregion

    //region Listeners

    //region RootFragmentListener

    public interface RootFragmentListener {

        Pair<Fragment, FragmentProperties> getRootFragment(int index);
    }

    //endregion

    //region TransactionListener

    public interface TransactionListener {
        void beforeFragmentTransaction();

        void onTabTransaction(FragmentProperties fragmentProperties, int index);

        boolean beforeTabTransaction(FragmentProperties fragmentProperties, int index);

        void onFragmentTransaction(FragmentProperties fragmentProperties, TransactionType transactionType);
    }

    //endregion

    //endregion

    //region Builder

    public static final class Builder {

        //region Variables

        @IdRes
        private int mContainerId;
        private int mSelectedTabIndex = 0;
        private int mNumberOfTabs = 0;

        private RootFragmentListener mRootFragmentListener;
        private TransactionListener mTransactionListener;
        private FragmentManager mFragmentManager;
        private Bundle mSavedInstanceState;

        //endregion

        //region Constructor

        public Builder(@Nullable Bundle savedInstanceState, FragmentManager mFragmentManager, @IdRes int mContainerId) {
            this.mSavedInstanceState = savedInstanceState;
            this.mFragmentManager = mFragmentManager;
            this.mContainerId = mContainerId;
        }

        //endregion

        //region Public Members

        public Builder rootFragmentListener(RootFragmentListener rootFragmentListener, int numberOfTabs) {
            mRootFragmentListener = rootFragmentListener;
            mNumberOfTabs = numberOfTabs;

            return this;
        }

        public Builder transactionListener(TransactionListener transactionListener) {
            mTransactionListener = transactionListener;

            return this;
        }

        public ApprNavigationController build() {
            if (mRootFragmentListener == null) {
                throw new IndexOutOfBoundsException("Either a fragment listener needs to be set");
            }

            return new ApprNavigationController(this, mSavedInstanceState);
        }

        //endregion
    }

    //endregion
}
