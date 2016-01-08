package com.extensivepro.mxl.app.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.util.Logger;

public class MockData
{
    public static final boolean MOCK_ENABLE = false;
    
    private static final String TAG = MockData.class.getSimpleName();

    public static String getMockJson(String path, Context context)
    {
        StringBuffer buffer = new StringBuffer();
        if (path == null || context == null)
        {
            return buffer.toString();
        }
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new InputStreamReader(context.getAssets()
                    .open(path)));
            String tmp = br.readLine();
            while (tmp != null)
            {
                buffer.append(tmp);
                tmp = br.readLine();
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }
    
    public static InputStream getMockInputStream(String assetFilePath)
    {
        if(assetFilePath == null)
        {
            return null;
        }
        InputStream is = null;
        try
        {
            is = ClientManager.getInstance().getAppContext().getAssets().open(assetFilePath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Logger.e(TAG, "getMockInputStream()[err:" + e.getMessage() + "]");
        }
        return is;
    }
    
    public static void testPrint(InputStream is)
    {
        if (is == null)
        {
            return;
        }
        BufferedReader br = null;
        
        try
        {
            br=        new BufferedReader(new InputStreamReader(is));
            StringBuffer buffer = new StringBuffer();
            String tmp = br.readLine();
            while (tmp != null)
            {
                buffer.append(tmp);
                tmp = br.readLine();
            }
            Logger.d(TAG, "testPrint()[testString:" + buffer.toString() + "]");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Logger.e(TAG, "testPrint()[err:" + e.getMessage() + "]");
        }
        finally
        {
            if(br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
