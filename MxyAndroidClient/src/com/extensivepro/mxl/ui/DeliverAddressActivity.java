package com.extensivepro.mxl.ui;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.Receiver;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.DeliverAddressListAdapter;
import com.extensivepro.mxl.widget.TitleBar;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 16, 2013 11:18:15 AM
 * @version V1.3.1
 */
public class DeliverAddressActivity extends BaseActivity implements
        OnClickListener
{
    private static final String TAG = DeliverAddressActivity.class
            .getSimpleName();

    private TitleBar mTitleBar;

    private DeliverAddressListAdapter mAdapter;

    private ListView mDeliverAddrList;

    private List<Receiver> mReceivers;
    
    private Receiver mReceiver;
    
    private StateReceiver mStateReceiver;
    
    private Context context;
    
    private boolean mChangeDeliverAddr;

    private class StateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.d(TAG, "StateReceiver.onReceive()[action:" + action + "]");
            if (action != null
                    && action.equals(Const.ACTION_GET_ALL_DELIVER_ADDR_SUCCESS))
            {
                mReceivers = AccountManager.getInstance().getCurrentAccount()
                        .getReceivers();
                mAdapter.notifyDataSetChange(mReceivers);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate()[access]");
        setContentView(R.layout.deliver_address);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle(R.string.delivery_address_txt);
        mTitleBar.setBackBtnText(R.string.account);
        mTitleBar.setEditBtnText(R.string.add_deliver_address_txt);
        mTitleBar.setEditBtnVisibility(View.VISIBLE);
        mTitleBar.findViewById(R.id.edit_btn).setOnClickListener(this);
        mDeliverAddrList = (ListView) findViewById(R.id.m_deliver_address_list);
        mAdapter = new DeliverAddressListAdapter(mReceivers, this);
        mDeliverAddrList.setAdapter(mAdapter);
        regReceiver();
        mChangeDeliverAddr = getIntent().getBooleanExtra(Const.EXTRA_CHANGE_DELIVER_ADDRESS, false);
        if(mChangeDeliverAddr)
        {
            mTitleBar.setEditBtnVisibility(View.GONE);
            mTitleBar.setBackBtnVisibility(View.GONE);
        }else{
            mTitleBar.setEditBtnVisibility(View.VISIBLE);
            mTitleBar.setBackBtnVisibility(View.VISIBLE);
        }
        context = DeliverAddressActivity.this;
        
        Logger.d(TAG, "onCreate()[mChangeDeliverAddr]"+mChangeDeliverAddr);


            mDeliverAddrList.setOnItemClickListener(new OnItemClickListener() {
                
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                        long arg3)
                {
                    if (mChangeDeliverAddr && getParent() instanceof HomeActivity)
                    {
                        Logger.d(TAG, "position=" + position);
                        Intent intent = new Intent(context,ShoppingTrolleyActivity.class);
                        intent.putExtra(Const.EXTRA_SELECTED_DELIVER_ADDRESS_POS,position);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        intent.putExtra("From", "DeliverAddressActivity");
                        ((HomeActivity) getParent()).setCurrentTab(HomeActivity.CUR_TAB_ID_HOME_SHOPPING_TROLLEY);
                        ((HomeActivity) getParent()).startActivityWithGuideBar(
                                ShoppingTrolleyActivity.class, intent);
                    }
                  if (!mChangeDeliverAddr && getParent() instanceof HomeActivity)
                    {
                       Intent intent = new Intent(context, EditDeliverAddressActivity.class);
                       mReceiver = mReceivers.get(position);
                       Logger.d(TAG, "[id]="+mReceiver.getId());
                       Bundle bundle = new Bundle();
                       bundle.putString(Const.EXTRA_SELECTED_DELIVER_ADDRESS_ID, mReceiver.getId());
                       bundle.putInt(Const.EXTRA_SELECTED_DELIVER_ADDRESS_POS, position);
                       intent.putExtras(bundle);
                       ((HomeActivity) getParent()).startActivityWithGuideBar(
                              EditDeliverAddressActivity.class, intent);
                    }
                    
                }
            });
    }

    @Override
    protected void onDestroy()
    {
        Logger.d(TAG, "onDestroy()[access]");
        super.onDestroy();
        unregReceiver();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        loadDeliverAddress();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Logger.d(TAG, "onNewIntent()[access]");
        super.onNewIntent(intent);
        mChangeDeliverAddr = intent.getBooleanExtra(Const.EXTRA_CHANGE_DELIVER_ADDRESS, false);
        if(mChangeDeliverAddr)
        {
            mTitleBar.setEditBtnVisibility(View.GONE);
            mTitleBar.setBackBtnVisibility(View.GONE);
        }else{
            mTitleBar.setEditBtnVisibility(View.VISIBLE);
            mTitleBar.setBackBtnVisibility(View.VISIBLE);
        }
    }

    private void loadDeliverAddress()
    {
        AccountManager.getInstance().getAllDeliverAddress();
    }

    @Override
    public void onClick(View v)
    {
        Logger.d(TAG, "onClick()[access]");
        switch (v.getId())
        {
            case R.id.edit_btn:
                Logger.d(TAG, "onClick()[edit_btn]");
                if (getParent() instanceof HomeActivity)
                {
                    Intent intent = new Intent(this,
                            AddNewDeliverAddressActivity.class);
                    ((HomeActivity) getParent()).startActivityWithGuideBar(
                            AddNewDeliverAddressActivity.class, intent);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        mTitleBar.onBackBtnClick();
    }

    
    private void regReceiver()
    {
        if(mStateReceiver == null)
        {
            mStateReceiver = new StateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_GET_ALL_DELIVER_ADDR_SUCCESS);
            filter.addAction(Const.ACTION_GET_ALL_DELIVER_ADDR_FAILED);
            registerReceiver(mStateReceiver, filter);
        }
    }
    
    private void unregReceiver()
    {
        if(mStateReceiver != null)
        {
            unregisterReceiver(mStateReceiver);
            mStateReceiver = null;
        }
    }
}
