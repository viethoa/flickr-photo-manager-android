package com.viethoa.siliconstraits.testing.utils;

import android.util.Log;

import com.viethoa.siliconstraits.testing.configs.AppConfigs;


public final class LogUtils {

    public static void logInDebug(String logTag, String message) {
        if (AppConfigs.isDebug()) {
            Log.w(logTag, message);
        }
    }

    public static void logErrorDebug(String logTag, String message) {
        if (AppConfigs.isDebug()) {
            Log.e(logTag, message);
        }
    }

}
