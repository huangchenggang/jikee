package com.extensivepro.mxl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.push.Utils;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 16, 2013 11:18:40 AM
 * @version V1.3.1
 */
public class LogoActivity extends BaseActivity
{
    private static final String TAG = LogoActivity.class.getSimpleName();
    
    private static final long SKIP_LOGO_DURATION = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Logger.d(TAG, "onCreate()[access]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        ClientManager.getInstance().setScreenWidth(outMetrics.widthPixels);
        ClientManager.getInstance().setScreenHeight(outMetrics.heightPixels);
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Utils.API_KEY);
        mHandler.sendEmptyMessageDelayed(0, SKIP_LOGO_DURATION);
        
       
    }
    
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg) 
        {
            Logger.d(TAG, "mHandler.handleMessage()[access]");
            Intent intent = new Intent(LogoActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
