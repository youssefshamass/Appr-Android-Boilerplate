package com.appr.framework.utils;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GeneralHelper {
    public static boolean isNullOrEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static String concatenate(List<String> list, String delimiter) {
        if (isNullOrEmpty(list))
            return null;

        return TextUtils.join(delimiter, list);
    }

    public static String concatenate(List<String> list) {
        return concatenate(list, ",");
    }
}
