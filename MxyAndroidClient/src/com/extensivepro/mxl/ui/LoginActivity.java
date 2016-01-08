package com.extensivepro.mxl.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.DialogHelper;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.util.SharedPreferenceUtil;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 16, 2013 11:18:15 AM
 * @version V1.3.1
 */
public class LoginActivity extends BaseActivity implements OnClickListener
{
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mUsername;

    private EditText mPasswd;
    
    private StateReceiver mStateReceiver;
    
    private CheckBox mRememberPasswd;

    private ProgressDialog mLoginProgressDialog;
    
    /**
     * 连续按两次返回键就退出
     */
    private int keyBackClickCount = 0;
    
    private class StateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.d(TAG, "StateReceiver.onReceive()[action:"+action+"]");
            dismissLoginDialog();
            if(action!=null && action.equals(Const.ACTION_LOGIN_SUCCESS))
            {
                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                if(getParent() instanceof HomeActivity)
                {
                    ((HomeActivity)getParent()).startActivityWithGuideBar(AccountActivity.class, null);
                }
                return;
            }
            if(action!=null && action.equals(Const.ACTION_LOGIN_FAILED))
            {
                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate()[access]");
        setContentView(R.layout.login);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        mUsername = (EditText) findViewById(R.id.account_edit);
        mPasswd = (EditText) findViewById(R.id.passwd_edit);
        mRememberPasswd = (CheckBox)findViewById(R.id.remember_passwd);
        mUsername.setText(SharedPreferenceUtil.getLastestUserName());
        mPasswd.setText(SharedPreferenceUtil.getLastestPassword());
        mRememberPasswd.setChecked(!TextUtils.isEmpty(SharedPreferenceUtil
                .getLastestPassword()));
        regReceiver();
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
        mUsername.setText(SharedPreferenceUtil.getLastestUserName());
        mPasswd.setText(SharedPreferenceUtil.getLastestPassword());
    }

    private void showLoginDialog()
    {
        if (mLoginProgressDialog == null)
        {
            mLoginProgressDialog = DialogHelper.createProgressDialog(this,
                    R.string.login_dialog_title);
        }
        mLoginProgressDialog.show();
    }
    
    private void dismissLoginDialog()
    {
        if(mLoginProgressDialog != null && mLoginProgressDialog.isShowing())
        {
            mLoginProgressDialog.dismiss();
        }
    }
    
    @Override
    public void onClick(View v)
    {
        Logger.d(TAG, "onClick()[access]");
        switch (v.getId())
        {
            case R.id.login:
                Logger.d(TAG, "onClick()[login]");
                showLoginDialog();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
                boolean isOpen=imm.isActive();
                if(isOpen&&getCurrentFocus()!=null){
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } 
                AccountManager.getInstance().login(
                        mUsername.getText().toString(),
                        mPasswd.getText().toString());
                AccountManager.getInstance().setRememberCurAccount(mRememberPasswd.isChecked());
                if(mRememberPasswd.isChecked())
                {
                    SharedPreferenceUtil.setLastestUserName(mUsername.getText().toString());
                    SharedPreferenceUtil.setLastestPassword(mPasswd.getText().toString());
                }
                else 
                {
                    SharedPreferenceUtil.removeLastestUserInfo();
                }
               
                break;
            case R.id.register:
                Logger.d(TAG, "onClick()[register]");
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
//                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
                break;
            default:
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
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("zhh-->", "login");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (keyBackClickCount++) {
            case 0:
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show(); 
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        keyBackClickCount = 0;
                    }
                }, 2222);
                break;
            case 1:
                LoginActivity.this.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            default:
                break;
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
        }
        return super.onKeyDown(keyCode, event);
    }
    
}
