package com.extensivepro.mxl.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.extensivepro.mxl.app.service.ClientService;
import com.extensivepro.mxl.util.FileUtil;
/**
 * 
 * @Description 
 * @author damon
 * @date Apr 16, 2013 10:44:48 AM 
 * @version V1.3.1
 */
public class MxlApplication extends Application
{
    
//    private static MxlApplication mInstance;
    @Override
    public void onCreate()
    {
        super.onCreate();
        FileUtil.createCacheDir();
        Intent clientService = new Intent(this, ClientService.class);
        startService(clientService);
//        mInstance = this;
    }
    
//    public static Context getAppContext()
//    {
//        return mInstance;
//    }
}
