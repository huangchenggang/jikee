package com.extensivepro.mxl.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.Member;
import com.extensivepro.mxl.app.login.AccountManager;
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
public class RegisterActivity extends BaseActivity implements OnClickListener
{
    private static final String TAG = RegisterActivity.class.getSimpleName();

    private TitleBar mTitleBar;
    
    private EditText mUserName;
    private EditText mPassword;
    private EditText mPasswordConfirm;
    private EditText mNickName;
    private StateReceiver mStateReceiver;
    
    private class StateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.d(TAG, "StateReceiver.onReceive()[action:"+action+"]");
            if(action!=null && action.equals(Const.ACTION_LOGIN_SUCCESS))
            {
                Toast.makeText(RegisterActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                Intent accountIntent = new Intent(RegisterActivity.this,HomeActivity.class);
                accountIntent.putExtra(Const.EXTRA_HOME_ACTIVITY_KEY, Const.EXTRA_HOME_ACTIVITY_VALUE_START_ACCOUNT_ACTIVITY);
                startActivity(accountIntent);
                finish();
                return;
            }
            if(action!=null && action.equals(Const.ACTION_LOGIN_FAILED))
            {
                Toast.makeText(RegisterActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate()[access]");
        setContentView(R.layout.register);
        mTitleBar = (TitleBar)findViewById(R.id.title_bar);
        mTitleBar.setTitle(R.string.reg_title);
        mTitleBar.setBackBtnText(R.string.reg_cancel);
        findViewById(R.id.comfirm).setOnClickListener(this);
        
        mUserName = (EditText) findViewById(R.id.account_edit);
        mPassword = (EditText) findViewById(R.id.passwd_edit);
        mPasswordConfirm = (EditText) findViewById(R.id.confirm_passwd_edit);
        mNickName = (EditText) findViewById(R.id.nickname_edit);
        regReceiver();
    }

    @Override
    protected void onDestroy()
    {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
            case R.id.comfirm:
                Logger.d(TAG, "onClick()[comfirm]");
                if(TextUtils.isEmpty(mUserName.getText()))
                {
                    Toast.makeText(this, "User name can't be none.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(mPassword.getText()))
                {
                    Toast.makeText(this, "Password can't be none.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(mPasswordConfirm.getText()))
                {
                    Toast.makeText(this, "You should retype the password to confirm it.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!mPassword.getText().toString().equals(mPasswordConfirm.getText().toString()))
                {
                    Toast.makeText(this, "The password the type twice should be the same!", Toast.LENGTH_LONG).show();
                    return;
                }
                Member member = new Member();
                member.setUsername(mUserName.getText().toString());
                member.setEmail(mUserName.getText().toString());
                member.setPassword(mPassword.getText().toString());
                member.setName(mNickName.getText().toString());
                AccountManager.getInstance().register(member);
                break;
        }
    }
    
    private void regReceiver()
    {
        if(mStateReceiver == null)
        {
            mStateReceiver = new StateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_LOGIN_SUCCESS);
            filter.addAction(Const.ACTION_LOGIN_FAILED);
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
