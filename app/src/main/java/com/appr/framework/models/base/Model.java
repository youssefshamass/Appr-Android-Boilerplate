package com.appr.framework.models.base;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Model implements Serializable {
    //region Variables

    @SerializedName("id")
    private int mID;

    @SerializedName("name")
    private String mName;

    @SerializedName("code")
    private String mCode;

    //endregion

    //region Constructor

    public Model() {
    }

    //endregion

    //region Getters && Setters

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    //endregion
}
