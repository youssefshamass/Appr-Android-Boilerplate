package com.appr.framework.ui.base;

import java.io.Serializable;

import androidx.annotation.MenuRes;

public class FragmentProperties implements Serializable {

    //region Variables

    @MenuRes
    private Integer mMenuResID;

    private String mTitle;
    private String mSubtitle;
    private String mFragmentTag;
    private boolean mHideActionBar;
    private boolean mResetHeaderToDefault = true;

    private EActionBarStyle mActionBarStyle = EActionBarStyle.NORMAL;

    //endregion

    //region Constructor

    private FragmentProperties(Builder builder) {
        mMenuResID = builder.mMenuResID;
        mTitle = builder.mTitle;
        mSubtitle = builder.mSubtitle;
        mFragmentTag = builder.mFragmentTag;
        mHideActionBar = builder.mHideActionBar;
        mActionBarStyle = builder.mActionBarStyle;
        mResetHeaderToDefault = builder.mResetHeaderToDefault;
    }

    //endregion

    //region Public Members

    public static Builder newBuilder() {
        return new Builder();
    }

    //endregion

    //region Getters && Setters

    public Integer getMenuResID() {
        return mMenuResID;
    }

    public void setMenuResID(Integer menuResID) {
        mMenuResID = menuResID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public void setSubtitle(String subtitle) {
        mSubtitle = subtitle;
    }

    public String getFragmentTag() {
        return mFragmentTag;
    }

    public void setFragmentTag(String fragmentTag) {
        mFragmentTag = fragmentTag;
    }

    public boolean isHideActionBar() {
        return mHideActionBar;
    }

    public void setHideActionBar(boolean hideActionBar) {
        mHideActionBar = hideActionBar;
    }

    public EActionBarStyle getActionBarStyle() {
        return mActionBarStyle;
    }

    public void setActionBarStyle(EActionBarStyle actionBarStyle) {
        mActionBarStyle = actionBarStyle;
    }

    public boolean isResetHeaderToDefault() {
        return mResetHeaderToDefault;
    }

    public void setResetHeaderToDefault(boolean resetHeaderToDefault) {
        mResetHeaderToDefault = resetHeaderToDefault;
    }

    //endregion

    //region Builder

    public static final class Builder {

        //region Variables

        @MenuRes
        private Integer mMenuResID;

        private String mTitle;
        private String mSubtitle;
        private String mFragmentTag;
        private boolean mHideActionBar;
        private boolean mResetHeaderToDefault = true;

        private EActionBarStyle mActionBarStyle;

        //endregion

        //region Constructor

        public Builder() {
        }

        //endregion

        //region Public Members

        public Builder menuResID(@MenuRes Integer menuResID) {
            mMenuResID = menuResID;

            return this;
        }

        public Builder title(String title) {
            mTitle = title;

            return this;
        }

        public Builder subTitle(String subtitle) {
            mSubtitle = subtitle;

            return this;
        }

        public Builder fragmentTag(String fragmentTag) {
            mFragmentTag = fragmentTag;

            return this;
        }

        public Builder hideActionBar(boolean hideActionBar) {
            mHideActionBar = hideActionBar;

            return this;
        }

        public Builder actionBarStyle(EActionBarStyle actionBarStyle) {
            mActionBarStyle = actionBarStyle;

            return this;
        }

        public Builder resetHeaderToDefault(boolean resetHeaderToDefault) {
            mResetHeaderToDefault = resetHeaderToDefault;

            return this;
        }

        public FragmentProperties build() {
            return new FragmentProperties(this);
        }

        //endregion
    }

    //endregion
}
