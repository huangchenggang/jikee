package com.extensivepro.mxl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.TitleBar;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 16, 2013 11:18:15 AM
 * @version V1.3.1
 */
public class OrderStateActivity extends BaseActivity implements OnClickListener
{
    private static final String TAG = OrderStateActivity.class.getSimpleName();

    
    private TitleBar mTitleBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate()[access]");
        setContentView(R.layout.order_state);
        findViewById(R.id.unpay_orders).setOnClickListener(this);
        findViewById(R.id.order_history).setOnClickListener(this);
        mTitleBar = (TitleBar)findViewById(R.id.title_bar);
        mTitleBar.setTitle(R.string.order_state_txt);
        
    }

    @Override
    protected void onDestroy()
    {
        Logger.d(TAG, "onDestroy()[access]");
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Logger.d(TAG, "onNewIntent()[access]");
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View v)
    {
        Logger.d(TAG, "onClick()[access]");
        Intent intent = null;
        switch (v.getId())
        {
            case R.id.unpay_orders:
                Logger.d(TAG, "onClick()[order_state]");
                intent = new Intent(this, OrderListActivity.class);
                intent.putExtra(Const.EXTRA_START_ORDER_LIST_KEY,
                        Const.EXTRA_START_ORDER_LIST_VALUE_UNPAY);
                startNextActivity(OrderListActivity.class, intent);
                break;
            case R.id.order_history:
                Logger.d(TAG, "onClick()[delivery_address]");
                intent = new Intent(this, OrderListActivity.class);
                intent.putExtra(Const.EXTRA_START_ORDER_LIST_KEY,
                        Const.EXTRA_START_ORDER_LIST_VALUE_HISTORY);
                startNextActivity(OrderListActivity.class, intent);
                break;
            default:
                break;
        }
    }
    
    private void startNextActivity(Class<?> clazz,Intent intent)
    {
        if (getParent() instanceof HomeActivity)
        {
            ((HomeActivity) getParent()).startActivityWithGuideBar(clazz,
                    intent);
        }
        else
        {
            Logger.e(TAG, "startNextActivity()[failed]");
        }
    }
    
}
