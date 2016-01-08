package com.extensivepro.mxl.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.GuideBar;
import com.extensivepro.mxl.widget.GuideBar.OnTabChangeListener;

/**
 * 
 * @Description The home page.
 * @author damon
 * @date Apr 16, 2013 10:45:16 AM
 * @version V1.3.1
 */
public class HomeActivity extends ActivityGroup implements OnTabChangeListener
{
    private static final String TAG = HomeActivity.class.getSimpleName();

    private ViewGroup mBodyContainer;

    public static final int CUR_TAB_ID_HOME_PAGE = 0;

    public static final int CUR_TAB_ID_HOME_PRODUCT = 1;

    public static final int CUR_TAB_ID_HOME_ACCOUNT = 2;

    public static final int CUR_TAB_ID_HOME_SHOPPING_TROLLEY = 3;

    public static final int CUR_TAB_ID_HOME_SHARE = 4;

    private static final String TAB_ID_HOME_PAGE = "TAB_ID_HOME_PAGE";

    private static final String TAB_ID_HOME_PRODUCT = "TAB_ID_HOME_PRODUCT";

    private static final String TAB_ID_HOME_ACCOUNT = "TAB_ID_HOME_ACCOUNT";

    private static final String TAB_ID_HOME_SHOPPING_TROLLERY = "TAB_ID_HOME_SHOPPING_TROLLERY";

    private static final String TAB_ID_HOME_SHARE = "TAB_ID_HOME_SHARE";

    /**
     * 连续按两次返回键就退出
     */
    static int keyBackClickCount = 0;

    private StateReceiver mStateReceiver;

    private GuideBar mGuideBar;

    private HashMap<Integer, Stack<Activity>> mActivityStackMap = new HashMap<Integer, Stack<Activity>>();

    private SparseArray<String> mChildActivityMap = new SparseArray<String>();

    private int mCurTab = CUR_TAB_ID_HOME_PAGE;
    
    private boolean isFromShoppTrolley = false;
    
    
    public boolean setISFromShoppTrolley(boolean flag){
        return isFromShoppTrolley = flag;
    }
    
    private boolean isFreeGet = false;
    public boolean setIsFreeGet (boolean flag){
        return isFreeGet = flag;
    }
    

    private class StateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.d(TAG, "StateReceiver.onReceive()[action:" + action + "]");
            if (action != null && action.equals(Const.ACTION_LOGIN_SUCCESS))
            {
                mGuideBar.setTabIconBackground(CUR_TAB_ID_HOME_ACCOUNT,
                        R.drawable.account_btn_online_selector);
                mGuideBar.setTabEnable(CUR_TAB_ID_HOME_SHOPPING_TROLLEY, true);
            }
            else if (action != null && action.equals(Const.ACTION_LOGIN_FAILED))
            {
                mGuideBar.setTabIconBackground(CUR_TAB_ID_HOME_ACCOUNT,
                        R.drawable.account_btn_selector);
                mGuideBar.setTabEnable(CUR_TAB_ID_HOME_SHOPPING_TROLLEY, false);
            }
            else if (action != null
                    && action.equals(Const.ACTION_LOGOUT_SUCCESS))
            {
                mGuideBar.setTabIconBackground(CUR_TAB_ID_HOME_ACCOUNT,
                        R.drawable.account_btn_selector);
                mGuideBar.setTabEnable(CUR_TAB_ID_HOME_SHOPPING_TROLLEY, false);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Logger.d(TAG, "onCreate()[access]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mChildActivityMap.put(CUR_TAB_ID_HOME_PAGE,
                HomePageActivity.class.getName());
        mChildActivityMap.put(CUR_TAB_ID_HOME_PRODUCT,
                ProductActivity.class.getName());
        mChildActivityMap.put(CUR_TAB_ID_HOME_ACCOUNT,
                LoginActivity.class.getName());
        mChildActivityMap.put(CUR_TAB_ID_HOME_SHOPPING_TROLLEY,
                ShoppingTrolleyActivity.class.getName());
        mChildActivityMap.put(CUR_TAB_ID_HOME_SHARE,
                ShareActivity.class.getName());
        mActivityStackMap.put(CUR_TAB_ID_HOME_PAGE, new Stack<Activity>());
        mActivityStackMap.put(CUR_TAB_ID_HOME_PRODUCT, new Stack<Activity>());
        mActivityStackMap.put(CUR_TAB_ID_HOME_ACCOUNT, new Stack<Activity>());
        mActivityStackMap.put(CUR_TAB_ID_HOME_SHOPPING_TROLLEY,
                new Stack<Activity>());
        mActivityStackMap.put(CUR_TAB_ID_HOME_SHARE, new Stack<Activity>());
        initViews();
        regReceiver();

        if (getIntent() != null)
        {
            String extra = getIntent().getStringExtra(
                    Const.EXTRA_HOME_ACTIVITY_KEY);
            if (extra != null
                    && extra.equals(Const.EXTRA_HOME_ACTIVITY_VALUE_START_ACCOUNT_ACTIVITY))
            {
                mCurTab = CUR_TAB_ID_HOME_ACCOUNT;
                mChildActivityMap.put(CUR_TAB_ID_HOME_ACCOUNT,
                        AccountActivity.class.getName());
            }
        }
        mGuideBar.setTabEnable(CUR_TAB_ID_HOME_SHOPPING_TROLLEY, false);
        mGuideBar.setCurrentTab(mCurTab);
    }

    private String getActivtyTabIdByIndex(int index)
    {
        String id = TAB_ID_HOME_PAGE;
        switch (index)
        {
            case CUR_TAB_ID_HOME_PAGE:
                id = TAB_ID_HOME_PAGE;
                break;
            case CUR_TAB_ID_HOME_PRODUCT:
                id = TAB_ID_HOME_PRODUCT;
                break;
            case CUR_TAB_ID_HOME_ACCOUNT:
                id = TAB_ID_HOME_ACCOUNT;
                break;
            case CUR_TAB_ID_HOME_SHOPPING_TROLLEY:
                id = TAB_ID_HOME_SHOPPING_TROLLERY;
                break;
            case CUR_TAB_ID_HOME_SHARE:
                id = TAB_ID_HOME_SHARE;
                break;
            default:
                break;
        }
        Logger.d(TAG, "getActivtyTabIdByIndex()[index:" + index + ",id" + id
                + "]");
        return id;

    }

    private void initViews()
    {
        Logger.d(TAG, "initViews()[access]");
        mGuideBar = (GuideBar) findViewById(R.id.m_guide_bar);
        mBodyContainer = (ViewGroup) findViewById(R.id.body_container);
        mGuideBar.setOnTabChangeListener(this);
        mGuideBar.setUpTabs(R.layout.guide_bar);
    }

    public void startActivityWithGuideBar(Class<?> clazz, Intent intent)
    {
        mBodyContainer.removeAllViews();
        String id = getActivtyTabIdByIndex(mCurTab);
        if (intent == null)
        {
            mBodyContainer.addView(getLocalActivityManager().startActivity(id,
                    new Intent(HomeActivity.this, clazz)).getDecorView());
        }
        else
        {
            mBodyContainer.addView(getLocalActivityManager().startActivity(id, intent).getDecorView());
        }
        if (getLocalActivityManager().getCurrentActivity() != null)
        {
            mActivityStackMap.get(mGuideBar.getCurrentTabIndex()).push(
                    getLocalActivityManager().getCurrentActivity());
            Logger.d(
                    TAG,
                    "onTabChange()[mGuideBar.getCurrentTab:"
                            + mGuideBar.getCurrentTabIndex() + "]");
            mChildActivityMap.put(mGuideBar.getCurrentTabIndex(),
                    getLocalActivityManager().getCurrentActivity().getClass()
                            .getName());
        }
    }

    private void pushClassToStatck(Class<?>... pushClazzes)
    {
        if (pushClazzes == null || pushClazzes.length == 0)
        {
            return;
        }
        for (int i = 0; i < pushClazzes.length; i++)
        {
            mChildActivityMap.put(mGuideBar.getCurrentTabIndex(),
                    pushClazzes[i].getName());
        }
    }

    /**
     * 
     * @Description 当启动一个activity需要生成上一级历史，但历史activity不想启动时，使用此方法
     * @author damon
     * @param clazz
     * @param intent
     * @param pushClazzes
     */
    public void startActivityPushHistoryWithGuideBar(Class<?> clazz,
            Intent intent, Class<?>... pushClazzes)
    {
        pushClassToStatck(pushClazzes);
        startActivityWithGuideBar(clazz, intent);
    }

    public void setGuideVisiblityWithAnim(final int visibility)
    {
        int animResId = R.anim.push_down_out;
        if (visibility == View.VISIBLE)
        {
            animResId = R.anim.push_down_in;
        }
        else if (visibility == View.INVISIBLE)
        {
            animResId = R.anim.push_down_out;
        }
        mGuideBar.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(this, animResId);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mGuideBar.setVisibility(visibility);
            }
        });
        mGuideBar.setAnimation(animation);
        mGuideBar.startAnimation(animation);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Logger.d(TAG, "onNewIntent()[access]");
        super.onNewIntent(intent);

    }

    public void setCurrentTab(int index)
    {
        mGuideBar.setCurrentTab(index);
    }

    public void refreshShoppingTrolleryCount(int count)
    {
        TextView trolleyNumView = (TextView) mGuideBar
                .findViewById(R.id.trolley_num);
        if (count == 0)
        {
            trolleyNumView.setVisibility(View.GONE);
        }
        else if (count > 0)
        {
            trolleyNumView.setVisibility(View.VISIBLE);
            trolleyNumView.setText(String.valueOf(count));
        }
    }

    @Override
    public void onTabChange(int index, View v)
    {
        Logger.d(TAG, "onTabChange()[index:" + index + "]");
        mBodyContainer.removeAllViews();
        mCurTab = index;
        try
        {
            switch (v.getId())
            {
                case R.id.home_page:
                    Logger.d(TAG, "onTabChange()[home_page]");
                    startActivityWithGuideBar(Class.forName(mChildActivityMap
                            .get(CUR_TAB_ID_HOME_PAGE)), null);
                    break;
                case R.id.product:
                    Logger.d(TAG, "onTabChange()[product]");
                    startActivityWithGuideBar(Class.forName(mChildActivityMap
                            .get(CUR_TAB_ID_HOME_PRODUCT)), null);
                    break;
                case R.id.account:
                    Logger.d(TAG, "onTabChange()[account]");
                    startActivityWithGuideBar(Class.forName(mChildActivityMap
                            .get(CUR_TAB_ID_HOME_ACCOUNT)), null);
                    break;
                case R.id.shopping_trolley:
                    Logger.d(TAG, "onTabChange()[shopping_trolley]");
                    startActivityWithGuideBar(Class.forName(mChildActivityMap
                            .get(CUR_TAB_ID_HOME_SHOPPING_TROLLEY)), null);
                    break;
                case R.id.share:
                    Logger.d(TAG, "onTabChange()[share]");
                    startActivityWithGuideBar(Class.forName(mChildActivityMap
                            .get(CUR_TAB_ID_HOME_SHARE)), null);
                    break;
                default:
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

//    public void backByShoppingTrolley(String flag)
//    {
//        Stack<Activity> curStack1 = mActivityStackMap.get(1);
//        if (!curStack1.isEmpty())
//        {
//            while(curStack1.peek().getClass().getName().equals(ProductViewActivity.class.getName()))
//            {
//                 curStack1.remove(curStack1.size()-1);
//            }
//        }
//    }
    
    
    @Override
    public void onBackPressed()
    {
        Stack<Activity> curStack = mActivityStackMap.get(mGuideBar.getCurrentTabIndex());
        if (!curStack.isEmpty())
        {
            curStack.pop();
        }
        if (!curStack.isEmpty())
        {
            if (isFromShoppTrolley == true)
            {
                while(curStack.peek().getClass().getName().equals(ProductViewActivity.class.getName()))
                {
                     curStack.pop();
                }
                isFromShoppTrolley = false;
            }
            if (isFreeGet == true)
            {
                while(curStack.peek().getClass().getName().equals(PhotoAlbumActivity.class.getName()))
                {
                     curStack.pop();
                }
                isFreeGet = false;
            }
            
            Activity activity = curStack.pop();
            
            if (!isBottomActivity(getCurrentActivity().getClass()))
            {
                startActivityWithGuideBar(activity.getClass(), null);
            }
            else
            {
                finishAllActivity();
                getCurrentActivity().onBackPressed();
            }
        }
        else
        {
            finishAllActivity();
            getCurrentActivity().onBackPressed();
        }
    }

    @Override
    public void finish()
    {
        Logger.d(TAG, "finish()");
        finishAllActivity();
        super.finish();
    }

    private void finishAllActivity()
    {
        Logger.d(TAG, "finishAllActivity()");
        Set<Integer> keys = mActivityStackMap.keySet();
        if (keys != null && !keys.isEmpty())
        {
            for (Iterator<Integer> iter = keys.iterator(); iter.hasNext();)
            {
                Stack<Activity> activities = mActivityStackMap.get(iter.next());
                if (activities == null || activities.isEmpty())
                {
                    continue;
                }
                while (!activities.isEmpty())
                {
                    Activity activity = activities.pop();
                    if (activity != null && !activity.isFinishing())
                    {
                        activity.finish();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        Logger.d(TAG, "onDestroy()[access]");
        super.onDestroy();
        unregReceiver();
        getLocalActivityManager().destroyActivity(
                getActivtyTabIdByIndex(CUR_TAB_ID_HOME_PAGE), true);
        getLocalActivityManager().destroyActivity(
                getActivtyTabIdByIndex(CUR_TAB_ID_HOME_PRODUCT), true);
        getLocalActivityManager().destroyActivity(
                getActivtyTabIdByIndex(CUR_TAB_ID_HOME_ACCOUNT), true);
        getLocalActivityManager().destroyActivity(
                getActivtyTabIdByIndex(CUR_TAB_ID_HOME_SHOPPING_TROLLEY), true);
        getLocalActivityManager().destroyActivity(
                getActivtyTabIdByIndex(CUR_TAB_ID_HOME_SHARE), true);
        if (getLocalActivityManager().getCurrentActivity() != null)
        {
            getLocalActivityManager().getCurrentActivity().finish();
        }
        mChildActivityMap.clear();
        mActivityStackMap.clear();
        ClientManager.getInstance().clearCache();
    }

    private boolean isBottomActivity(Class<?> clazz)
    {
        if (clazz.getName().equals(HomePageActivity.class.getName()))
        {
            return true;
        }
        if (clazz.getName().equals(ProductActivity.class.getName()))
        {
            return true;
        }
        if (clazz.getName().equals(AccountActivity.class.getName()))
        {
            return true;
        }
        if (clazz.getName().equals(ShoppingTrolleyActivity.class.getName()))
        {
            return true;
        }
        if (clazz.getName().equals(ShareActivity.class.getName()))
        {
            return true;
        }
        return false;
    }

    private void regReceiver()
    {
        if (mStateReceiver == null)
        {
            mStateReceiver = new StateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_LOGIN_SUCCESS);
            filter.addAction(Const.ACTION_LOGIN_FAILED);
            filter.addAction(Const.ACTION_LOGOUT_SUCCESS);
            registerReceiver(mStateReceiver, filter);
        }
    }

    private void unregReceiver()
    {
        if (mStateReceiver != null)
        {
            unregisterReceiver(mStateReceiver);
            mStateReceiver = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            switch (keyBackClickCount++)
            {
                case 0:
                    Toast.makeText(getApplicationContext(), getString(R.string.exit_again),
                            Toast.LENGTH_SHORT).show();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run()
                        {
                            keyBackClickCount = 0;
                        }
                    }, 2222);
                    break;
                case 1:
                    HomeActivity.this.finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    break;
                case 3:
                    onBackPressed();
                    break;
                default:
                    break;
            }
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_MENU)
        {
        }
        return super.onKeyDown(keyCode, event);
    }

}