package com.appr.framework.messages;

import com.appr.framework.network.base.EHttpMethod;

import java.util.HashMap;

import androidx.annotation.Nullable;

public class Request {
    //region Variables

    private String mID;
    private Class mForClass;
    private EHttpMethod mEHttpMethod;
    @Nullable
    private String mActionName;
    private HashMap<String, Object> mParams;

    //endregion

    //region Constructor

    public Request() {
    }

    public Request(Class forClass, EHttpMethod method) {
        this();
        mForClass = forClass;
        mEHttpMethod = method;
    }

    //endregion

    //region Getters && Setters
    public String getID() {
        return mID;
    }

    public void setID(String ID) {
        mID = ID;
    }

    public String getActionName() {
        return mActionName;
    }

    public void setActionName(String actionName) {
        mActionName = actionName;
    }

    public HashMap<String, Object> getParams() {
        return mParams;
    }

    public void setParams(HashMap<String, Object> params) {
        mParams = params;
    }

    public EHttpMethod getEHttpMethod() {
        return mEHttpMethod;
    }

    public void setEHttpMethod(EHttpMethod EHttpMethod) {
        mEHttpMethod = EHttpMethod;
    }

    public Class getForClass() {
        return mForClass;
    }

    public void setForClass(Class forClass) {
        mForClass = forClass;
    }

    //endregion
}
