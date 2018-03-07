package com.zhanglin.aboutprocess.utils;

import android.text.TextUtils;

import com.zhanglin.aboutprocess.BuildConfig;


/**
 * 2015/9/6 14:38
 *
 * @author zhanglin
 * @email www.zhanglin01@100tal.com
 * todo
 */
public final class Logger {

    private static final String TAG = "aidl";

    public static void d() {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(TAG, getLocation());
        }
    }

    public static void d(String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(TAG, getLocation() + msg);
        }
    }

    public static void i(String TAG, String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(TAG, getLocation() + msg);
        }
    }

    public static void i(String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(TAG, getLocation() + msg);
        }
    }

    public static void i() {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(TAG, getLocation());
        }
    }

    public static void e(String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(TAG, getLocation() + msg);
        }
    }

    public static void e(String msg, Throwable e) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(TAG, getLocation() + msg, e);
        }
    }

    public static void e(Throwable e) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(TAG, getLocation(), e);
        }
    }

    public static void e() {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(TAG, getLocation());
        }
    }

    private static String getLocation() {
        final String className = Logger.class.getName();
        final StackTraceElement[] traces = Thread.currentThread()
                .getStackTrace();
        boolean found = false;

        for (StackTraceElement trace : traces) {
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":"
                                + trace.getMethodName() + ":"
                                + trace.getLineNumber() + "]: ";
                    }
                } else if (trace.getClassName().startsWith(className)) {
                    found = true;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }

}
