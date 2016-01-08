package com.extensivepro.mxl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.extensivepro.mxl.app.MxlApplication;
import com.extensivepro.mxl.app.client.ClientManager;

public class DataUtil
{
    private static final String TAG = DataUtil.class.getSimpleName();
    
    public static String streamToString(InputStream is)
    {
        StringBuffer buffer = new StringBuffer();
        if (is == null)
        {
            return buffer.toString();
        }
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new InputStreamReader(is));
            char buf[] = new char[1024*2];
            int tmpLength = -1;
            tmpLength = br.read(buf);
            while (tmpLength != -1)
            {
                buffer.append(buf, 0, tmpLength);
                tmpLength = br.read(buf);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Logger.e(TAG, "getMockJson()[err:" + e.getMessage() + "]");
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }
    
    public static boolean isNetworkAvilable()
    {
        ConnectivityManager cm = (ConnectivityManager) ClientManager
                .getInstance().getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null ? networkInfo.isConnected() : false;
    }
    
}
