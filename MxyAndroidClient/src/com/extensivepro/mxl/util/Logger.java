package com.extensivepro.mxl.util;

import android.util.Log;

/**
 * 
 * @Description Use this to instead of android.util.Log.
 * @author damon
 * @date Apr 16, 2013 11:36:50 AM 
 * @version V1.3.1
 */
public class Logger
{
    private static final boolean DEBUG_ENABLE = true;
    private static final String TAG = "MxlAndroid";

    public static void i(String tag, String msg)
    {
        if (DEBUG_ENABLE)
//            Log.i(tag, msg);
        Log.i(TAG, tag+"["+msg+"]");
    }

    public static void e(String tag, String msg)
    {
        if (DEBUG_ENABLE)
//            Log.e(tag, msg);
        Log.e(TAG, tag+"["+msg+"]");
    }

    public static void w(String tag, String msg)
    {
        if (DEBUG_ENABLE)
//            Log.w(tag, msg);
        Log.w(TAG, tag+"["+msg+"]");
    }

    public static void d(String tag, String msg)
    {
        if (DEBUG_ENABLE)
//            Log.d(tag, msg);
        Log.d(TAG, tag+"["+msg+"]");
    }

    public static void v(String tag, String msg)
    {
        if (DEBUG_ENABLE)
//            Log.v(tag, msg);
        Log.v(TAG, tag+"["+msg+"]");
    }
}
