package com.extensivepro.mxl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;

/** 
 * @Description 
 * @author Admin
 * @date 2013-5-11 上午10:56:55 
 * @version V1.3.1
 */

public class FeedBackMeActivity extends BaseActivity implements OnClickListener
{

    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_me);
        findViewById(R.id.feedback_me_sure).setOnClickListener(this);
        findViewById(R.id.feedback_me_del).setOnClickListener(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.feedback_me_sure:
                finish();
                break;
            case R.id.feedback_me_del:
                finish();
                break;
            default:
                break;
        }
    }

}
