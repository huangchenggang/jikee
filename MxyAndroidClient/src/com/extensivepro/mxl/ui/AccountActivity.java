package com.extensivepro.mxl.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.EventDispacher;
import com.extensivepro.mxl.app.bean.Member;
import com.extensivepro.mxl.app.cart.CartManager;
import com.extensivepro.mxl.app.client.ClientThread;
import com.extensivepro.mxl.app.client.Command;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.DialogHelper;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.TitleBar;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 16, 2013 11:18:15 AM
 * @version V1.3.1
 */
public class AccountActivity extends BaseActivity implements OnClickListener
{
    private static final String TAG = AccountActivity.class.getSimpleName();

    private TextView mCurAccount;
    private TextView mAvailableBalance;
    
    private TitleBar mTitleBar;
    
    private StateReceiver mStateReceiver;

    private ProgressDialog mLogoutProgressDialog;
    
    private class StateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.d(TAG, "StateReceiver.onReceive()[action:"+action+"]");
            dismissLogoutProgress();
            if(action!=null && action.equals(Const.ACTION_LOGOUT_SUCCESS))
            {
                if (getParent() instanceof HomeActivity)
                {
                    ((HomeActivity) getParent()).startActivityWithGuideBar(
                            LoginActivity.class, null);
                }
                return;
            }
            if(action!=null && action.equals(Const.ACTION_LOGOUT_FAILED))
            {
                Toast.makeText(AccountActivity.this, "Logout Failed", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate()[access]");
        setContentView(R.layout.account);
        findViewById(R.id.order_state).setOnClickListener(this);
        findViewById(R.id.delivery_address).setOnClickListener(this);
        findViewById(R.id.account_recharge).setOnClickListener(this);
        findViewById(R.id.notify_msg).setOnClickListener(this);
        findViewById(R.id.about_mxl_app).setOnClickListener(this);
        findViewById(R.id.scroe_me).setOnClickListener(this);
        findViewById(R.id.feedback_me).setOnClickListener(this);
        findViewById(R.id.logout_btn).setOnClickListener(this);
        mTitleBar = (TitleBar)findViewById(R.id.title_bar);
        mCurAccount = (TextView) findViewById(R.id.cur_account);
        mAvailableBalance = (TextView) findViewById(R.id.available_balance);
        
        mTitleBar.setTitle(R.string.account);
        mTitleBar.setBackBtnVisibility(View.INVISIBLE);
        
        Member member = AccountManager.getInstance().getCurrentAccount();
        if(member != null)
        {
            mCurAccount.setText(getResources().getString(
                    R.string.cur_account_txt, member.getUsername()));
            mAvailableBalance.setText(getResources().getString(
                    R.string.available_balance_txt, member.getDeposit()));
        }
        regReceiver();
        CartManager.getInstance().loadPaymentConfig();
        AccountManager.getInstance().getAllDeliverAddress();
    }

    @Override
    protected void onDestroy()
    {
        Logger.d(TAG, "onDestroy()[access]");
        super.onDestroy();
        unregReceiver();
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
        

        switch (v.getId())
        {
            case R.id.order_state:
                Logger.d(TAG, "onClick()[order_state]");
                startNextActivity(OrderStateActivity.class,null);
                break;
            case R.id.delivery_address:
                startNextActivity(DeliverAddressActivity.class,null);
                Logger.d(TAG, "onClick()[delivery_address]");
                break;
            case R.id.account_recharge:
                Logger.d(TAG, "onClick()[account_recharge]");
                startNextActivity(AccountRechargeActivity.class,null);
                break;
            case R.id.notify_msg:
                Logger.d(TAG, "onClick()[notify_msg]");
                startNextActivity(AccountNotifyMsgActivity.class,null);
                
                break;
            case R.id.about_mxl_app:
                Logger.d(TAG, "onClick()[about_mxl_app]");
                startNextActivity(AboutMxlAppActivity.class,null);
                break;
            case R.id.scroe_me:
                Logger.d(TAG, "onClick()[scroe_me]");
                break;
            case R.id.feedback_me:
                Logger.d(TAG, "onClick()[feedback_me]");
               // startNextActivity(FeedBackMeActivity.class,null);
                Intent intent = new Intent();
                intent.setClass(this, FeedBackMeActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_btn:
                Logger.d(TAG, "onClick()[logout_btn]");
                showLogoutDialog();
                break;
            default:
                break;
        }
    }
    
 
    
    private void  showLogoutDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.logout_alert_txt);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setNeutralButton(R.string.confirm, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                showLogoutProgress();
                AccountManager.getInstance().logout();
            }
        });
        builder.create().show();
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
    
    private void regReceiver()
    {
        if(mStateReceiver == null)
        {
            mStateReceiver = new StateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_LOGOUT_SUCCESS);
            filter.addAction(Const.ACTION_LOGOUT_FAILED);
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
    
    private void showLogoutProgress()
    {
        if (mLogoutProgressDialog == null)
        {
            mLogoutProgressDialog = DialogHelper.createProgressDialog(this,
                    R.string.logout_dialog_title);
        }
        mLogoutProgressDialog.show();
    }
    
    private void dismissLogoutProgress()
    {
        if(mLogoutProgressDialog != null && mLogoutProgressDialog.isShowing())
        {
            mLogoutProgressDialog.dismiss();
        }
    }
}
