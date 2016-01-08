package com.extensivepro.mxl.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.extensivepro.mxl.app.client.ClientManager;

public class SharedPreferenceUtil
{
    private static final String CONFIG_NAME = "MXL_CONFIG";
    private static final String KEY_LOAD_CAROUSEL_SUCCESS = "KEY_LOAD_CAROUSEL_SUCCESS";
    
    /**
     * 标记是否需要从本地读取商品分类信息
     */
    private static final String KEY_NEED_LOAD_GOODS_CAGETORY_FROM_LOCAL = "KEY_NEED_LOAD_GOODS_CAGETORY_FROM_LOCAL";

    /**
     * 标记商品分类信息是否已经从网络侧获取成功
     */
    private static final String KEY_LOAD_GOODS_CAGETORY_FROM_NETWORK_SUCCESS = "KEY_LOAD_GOODS_CAGETORY_FROM_NETWORK_SUCCESS";
    
    private static final String KEY_LASTEST_USERNAME = "KEY_LASTEST_USERNAME";
    
    private static final String KEY_LASTEST_PASSWD = "KEY_LASTEST_PASSWD";

    public static void setLoadCarouselSuccess(boolean successful)
    {
        setBoolean(KEY_LOAD_CAROUSEL_SUCCESS, successful);
    }

    public static boolean isLoadCarouselSuccess()
    {
        return getBoolean(KEY_LOAD_CAROUSEL_SUCCESS, false);
    }
    
    public static void setNeedLoadGoodsCategoryFromLocal(boolean successful)
    {
        setBoolean(KEY_NEED_LOAD_GOODS_CAGETORY_FROM_LOCAL, successful);
    }
    
    public static boolean isNeedLoadGoodsCategoryFromLocal()
    {
        return getBoolean(KEY_NEED_LOAD_GOODS_CAGETORY_FROM_LOCAL, true);
    }
    
    public static void setLoadGoodsCategoryFromNetworkSuccess(boolean successful)
    {
        setBoolean(KEY_LOAD_GOODS_CAGETORY_FROM_NETWORK_SUCCESS, successful);
    }
    
    public static boolean isLoadGoodsCategoryFromNetworkSuccess()
    {
        return getBoolean(KEY_LOAD_GOODS_CAGETORY_FROM_NETWORK_SUCCESS, false);
    }
    
    public static void setLastestUserName(String userName)
    {
        setString(KEY_LASTEST_USERNAME, userName);
    }
    
    public static String getLastestUserName()
    {
        return getString(KEY_LASTEST_USERNAME, "");
    }
    
    public static void setLastestPassword(String password)
    {
        setString(KEY_LASTEST_PASSWD, password);
    }
    
    public static void removeLastestUserInfo()
    {
        removeSP(KEY_LASTEST_USERNAME);
        removeSP(KEY_LASTEST_PASSWD);
    }
    
    public static String getLastestPassword()
    {
        return getString(KEY_LASTEST_PASSWD, "");
    }

    private static boolean getBoolean(String key, boolean defValue)
    {
        SharedPreferences sp = ClientManager.getInstance().getAppContext()
                .getSharedPreferences(CONFIG_NAME,
                        Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    private static void setBoolean(String key, boolean value)
    {
        SharedPreferences sp = ClientManager.getInstance().getAppContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    
    private static String getString(String key, String defValue)
    {
        SharedPreferences sp = ClientManager.getInstance().getAppContext()
                .getSharedPreferences(CONFIG_NAME,
                        Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }
    
    private static void setString(String key, String value)
    {
        SharedPreferences sp = ClientManager.getInstance().getAppContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    
    private static void removeSP(String key)
    {
        SharedPreferences sp = ClientManager.getInstance().getAppContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

}
