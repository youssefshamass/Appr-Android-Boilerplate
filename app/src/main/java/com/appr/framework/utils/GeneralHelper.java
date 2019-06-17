package com.appr.framework.utils;

import android.text.TextUtils;

import java.util.List;
import java.util.UUID;

public class GeneralHelper {
    public static boolean isNullOrEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static boolean isNullOrEmpty(String text) {
        return TextUtils.isEmpty(text);
    }

    public static String concatenate(List<String> list, String delimiter) {
        if (isNullOrEmpty(list))
            return null;

        return TextUtils.join(delimiter, list);
    }

    public static String concatenate(List<String> list) {
        return concatenate(list, ",");
    }

    public static String generateUniqueID() {
        return UUID.randomUUID().toString();
    }
}
