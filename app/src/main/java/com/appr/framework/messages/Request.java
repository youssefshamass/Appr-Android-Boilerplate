package com.appr.framework.messages;

import java.util.HashMap;

public class Request {
    private String mID;
    private String mActionName;
    private HashMap<String, Object> mParams;

    public Request() {
    }

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
}
