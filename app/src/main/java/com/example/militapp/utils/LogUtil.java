package com.example.militapp.utils;

import android.util.Log;

public class LogUtil {

    private static final String LOG_TAG = "myLogs";

    public static void debug(String msg) {
        Log.d(LOG_TAG, msg);
    }

    public static void error(String msg, Exception ex) {
        Log.e(LOG_TAG, msg, ex);
    }
}
