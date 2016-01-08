package com.extensivepro.mxl.app.client;

import java.io.InputStream;

import android.content.Context;
import android.content.Intent;

import com.extensivepro.mxl.app.MxlApplication;
import com.extensivepro.mxl.app.cart.CartManager;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.app.service.ClientService;
import com.extensivepro.mxl.product.ProductManager;
import com.extensivepro.mxl.util.Logger;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 20, 2013 10:33:32 AM
 * @version V1.3.1
 */
public class ClientManager
{
    private static final String TAG = ClientManager.class.getSimpleName();

    private IBusinessCallback mCallback;

    private static ClientManager mInstance;

    private ClientThread mRetryThread;
    
    private int mScreenWidth;
    private int mScreenHeight;
    
    private Context mContext;
    
    private ClientManager()
    {
    }

    public static ClientManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new ClientManager();
        }
        return mInstance;
    }

    public void regCallback(IBusinessCallback callback)
    {
        Logger.d(TAG, "regCallback()[access]");
        mCallback = callback;
    }
    
    public void validateCallback()
    {
        Intent service = new Intent(ClientManager.getInstance().getAppContext(),ClientService.class);
        ClientManager.getInstance().getAppContext().startService(service);
    }

    public void unregCallback()
    {
        Logger.d(TAG, "unregCallback()[access]");
        mCallback = null;
    }

    public void notifyResp(InputStream stream, int reqCode)
    {
        Logger.d(TAG, "notifyResp()[reqCode:" + reqCode + ",mCallback is null:"
                + (mCallback == null) + "]");
        if (mCallback != null)
        {
            mCallback.notifyMgr(stream, reqCode);
        }
    }
    
    public void notifyError(int reqCode,int errorCode)
    {
        Logger.d(TAG, "notifyResp()[reqCode:" + reqCode + ",mCallback is null:"
                + (mCallback == null) + "]");
        if (mCallback != null)
        {
            mCallback.notifyError(reqCode, errorCode);
        }
    }
    
    public ClientThread getCurRetryThread()
    {
        return mRetryThread;
    }
    
    public void saveCurRetryThread(ClientThread thread)
    {
        mRetryThread = thread;
    }
    
    public void resetRetryThread()
    {
        mRetryThread = null;
    }
    
    public void clearCache()
    {
        AccountManager.getInstance().clearCache();
        ProductManager.getInstance().clearCache();
        CartManager.getInstance().clearCache();
        mRetryThread = null;
    }

    public int getScreenWidth()
    {
        return mScreenWidth;
    }

    public void setScreenWidth(int mScreenWidth)
    {
        this.mScreenWidth = mScreenWidth;
    }

    public int getScreenHeight()
    {
        return mScreenHeight;
    }

    public void setScreenHeight(int mScreenHeight)
    {
        this.mScreenHeight = mScreenHeight;
    }
    
    public Context getAppContext()
    {
        return mContext;
    }
    
    public void setAppContext(Context context)
    {
        this.mContext = context;
    }
    
    
}
