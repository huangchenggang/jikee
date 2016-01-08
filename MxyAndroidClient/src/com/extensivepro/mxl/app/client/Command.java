package com.extensivepro.mxl.app.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

import com.extensivepro.mxl.util.Logger;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 20, 2013 10:34:10 AM
 * @version V1.3.1
 */
public class Command
{
    private static final String TAG = Command.class.getSimpleName();

    private List<NameValuePair> mNameValuePars = new ArrayList<NameValuePair>();
    private List<NameValuePair> mHeaders = new ArrayList<NameValuePair>();
    private boolean doGet;
    
    private boolean loadFromLocal = false;

    public void commit(ClientThread clientThread)
    {
        Logger.d(TAG, "Commit()");
        ThreadExector.execute(this, clientThread);
    }

    public void addAttribute(String key,String value)
    {
        Logger.d(TAG, "addAttribute()[key:" + key + ",value:" + value + "]");
        mNameValuePars.add(new BasicNameValuePair(key, value));
    }
    
    public void addRequestHead(String field,String newValue)
    {
        mHeaders.add(new BasicNameValuePair(field, newValue));
    }
    
    public String appendAttribute(String url)
    {
        if (!isDoGet())
        {
            return url;
        }
        StringBuffer buffer = new StringBuffer(url);
        if (!TextUtils.isEmpty(url))
        {
            for (int i = 0; i < mNameValuePars.size(); i++)
            {
                if (i == 0)
                {
                    buffer.append("?" + mNameValuePars.get(i).getName() + "="
                            + mNameValuePars.get(i).getValue());
                }
                else
                {
                    buffer.append("&" + mNameValuePars.get(i).getName() + "="
                            + mNameValuePars.get(i).getValue());
                }
            }
        }
        Logger.d(TAG, "appendAttribute()[" + buffer.toString() + "]");
        return buffer.toString();
    }
    
    public void setLoadFromLocal(boolean fromLocal)
    {
        loadFromLocal = fromLocal;
    }
    
    public boolean isLoadFromLocal()
    {
        return loadFromLocal;
    }

    public boolean isDoGet()
    {
        return doGet;
    }

    public void setDoGet(boolean doGet)
    {
        this.doGet = doGet;
    }
    
    public List<NameValuePair> getNameValuePairs()
    {
        return mNameValuePars;
    }
}
