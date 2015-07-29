package com.viethoa.siliconstraits.testing.utils;

import android.util.Log;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.viethoa.siliconstraits.testing.MyApplication;

public final class ToastUtils {

    public static void showToastMessageWithSuperToast(String message) {
        if (message == null || message.isEmpty())
            return;

        SuperToast.create(MyApplication.getAppContext(), message, SuperToast.Duration.MEDIUM, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP)).show();
    }

    public static void showToastMessageWithSuperToastDelay(String message, int timeDuration) {
        if (message == null || message.isEmpty())
            return;
        SuperToast.create(MyApplication.getAppContext(), message, timeDuration, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP)).show();
    }

    public static void showErrorMessageWithSuperToast(String message, String logTag) {
        if (message == null || message.isEmpty())
            return;

        SuperToast.create(MyApplication.getAppContext(), message, SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();

        Log.e(logTag, message);
        AppUtils.writeToLogFile(logTag, message);
    }
}
