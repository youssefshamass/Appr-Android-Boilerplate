package com.appr.framework.messages;

import com.appr.framework.utils.GeneralHelper;

import java.util.List;

public class Response {
    private String mRequestID;
    private String mActionName;
    private Object mData;
    private List<String> mMessages;
    private boolean mIsSuccessful;

    public Response(Object data) {
        this();
        mData = data;
    }

    public Response() {
        mIsSuccessful = true;
    }

    public Object getData() {
        return mData;
    }

    public void setData(Object data) {
        mData = data;
    }

    public List<String> getMessages() {
        return mMessages;
    }

    public String getMessage() {
        return GeneralHelper.concatenate(mMessages);
    }

    public void setMessages(List<String> messages) {
        mMessages = messages;
    }

    public boolean isSuccessful() {
        return mIsSuccessful;
    }

    public void setSuccessful(boolean successful) {
        mIsSuccessful = successful;
    }

    public String getRequestID() {
        return mRequestID;
    }

    public void setRequestID(String requestID) {
        mRequestID = requestID;
    }

    public String getActionName() {
        return mActionName;
    }

    public void setActionName(String actionName) {
        mActionName = actionName;
    }
}
