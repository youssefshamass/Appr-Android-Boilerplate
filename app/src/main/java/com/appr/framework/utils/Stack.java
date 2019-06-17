package com.appr.framework.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Stack implements Serializable {

    //region Variables

    private ArrayList<Integer> mStackArray;

    //endregion

    //region Constructor

    public Stack() {
        mStackArray = new ArrayList<>();
    }

    //endregion

    //region Public Members

    public void push(int entry) {

        remove(entry);

        mStackArray.add(entry);
    }

    public int pop() {

        int entry = -1;
        if (!isEmpty()) {

            entry = mStackArray.get(mStackArray.size() - 1);

            mStackArray.remove(mStackArray.size() - 1);
        }
        return entry;
    }

    public int popPrevious() {

        int entry = -1;

        if (!isEmpty()) {
            entry = mStackArray.get(mStackArray.size() - 2);
            mStackArray.remove(mStackArray.size() - 2);
        }
        return entry;
    }

    public int peek() {
        if (!isEmpty()) {
            return mStackArray.get(mStackArray.size() - 1);
        }

        return -1;
    }

    public boolean isEmpty() {
        return (mStackArray.size() == 0);
    }

    public int getStackSize() {
        return mStackArray.size();
    }

    public void emptyStack() {

        mStackArray.clear();
    }

    public void remove(int entry) {
        if (mStackArray.contains(entry))
            mStackArray.remove((Object)entry);
    }

    //endregion

    //region Private Members

    private boolean isAlreadyExists(int entry) {
        return (mStackArray.contains(entry));
    }

    //endregion
}
