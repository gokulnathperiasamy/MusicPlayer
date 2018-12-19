package com.kpgn.musicplayersample.helper;

import android.util.Log;

public abstract class LogHelper {

    private static final String TAG = LogHelper.class.getSimpleName();

    private LogHelper() {
        // Do Nothing...
    }

    public static void log(Exception exception) {
        Log.d(TAG, exception.getLocalizedMessage());
        exception.printStackTrace();
    }

}
