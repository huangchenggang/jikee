package com.extensivepro.mxl.app;

import android.app.Activity;

import com.extensivepro.mxl.util.Logger;

/**
 * 
 * @Description All UI activities extends from this.
 * @author damon
 * @date Apr 16, 2013 11:11:35 AM 
 * @version V1.3.1
 */
public class BaseActivity extends Activity
{
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Logger.d("BaseActivity", "clearCache()");
        System.gc();
    }
}
