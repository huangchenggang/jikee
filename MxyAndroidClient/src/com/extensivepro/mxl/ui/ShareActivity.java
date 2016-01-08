package com.extensivepro.mxl.ui;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.ShareItem;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.app.share.CompareByDate;
import com.extensivepro.mxl.app.share.CompareByLike;
import com.extensivepro.mxl.app.share.ShareManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.widget.PopMenu;
import com.extensivepro.mxl.widget.PopMenu.OnItemClickListener;
import com.extensivepro.mxl.widget.ShareListAdapter;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 16, 2013 11:18:34 AM
 * @version V1.3.1
 */
public class ShareActivity extends BaseActivity implements OnClickListener,
        OnItemClickListener
{
    private static final String TAG = ShareActivity.class.getSimpleName();

    private TextView toPostTextView, sortTextView, blogTextView;

    private ListView mListView;

    private List<ShareItem> shareItems;

    private ShareItemReceiver receiver;

    private ShareListAdapter mAdapter;

    private PopMenu popMenu;
    
    /**
     * 连续按两次返回键就退出
     */
    private int keyBackClickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
        toPostTextView = (TextView) findViewById(R.id.to_post);
        sortTextView = (TextView) findViewById(R.id.to_post_sort);
        blogTextView = (TextView) findViewById(R.id.to_blog);
        mListView = (ListView) findViewById(R.id.share_listView);

        toPostTextView.setOnClickListener(this);
        sortTextView.setOnClickListener(this);
        blogTextView.setOnClickListener(this);
        popMenu = new PopMenu(this);
        String items[] = getResources().getStringArray(
                R.array.share_choose_order_type);
        popMenu.addItems(items);
        popMenu.setOnItemClickListener(this);
        regReceiver();
        ShareManager.getInstance().getShareInfos(null, "15");

    }

    private void regReceiver()
    {
        if (receiver == null)
        {
            receiver = new ShareItemReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_GETSHARE_SUCCESS);
            filter.addAction(Const.ACTION_SAVESHARE_SUCCESS);
            filter.addAction(Const.ACTION_SAVESHARE_FAILED);
            registerReceiver(receiver, filter);
        }
    }

    private void unregReceiver()
    {
        if (receiver != null)
        {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private class ShareItemReceiver extends BroadcastReceiver
    {

        @SuppressWarnings("unchecked")
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (action != null && action.equals(Const.ACTION_GETSHARE_SUCCESS))
            {
                shareItems = (List<ShareItem>) intent
                        .getSerializableExtra(Const.GROUPBACKBEAN_OBJ);

                iniUI();
            }
            if (action != null
                    && (action.equals(Const.ACTION_SAVESHARE_SUCCESS) || action
                            .equals(Const.ACTION_SAVESHARE_FAILED)))
            {
                String message = intent.getStringExtra(Const.ShareItemModuel.MESSAGE_OBJ);
                Toast.makeText(context, message, 0).show();
            }

        }
    }

    @Override
    protected void onDestroy()
    {
        if(mAdapter!=null){
            mAdapter.clearCache();
            }
        super.onDestroy();
        unregReceiver();
        
    }


    /**
     * @Description 初始化ui界面
     * @author Administrator
     */
    public void iniUI()
    {
        mAdapter = new ShareListAdapter(this, shareItems);
        mListView.setAdapter(mAdapter);

    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart()
    {
        ShareManager.getInstance().getShareInfos(null, "10");
        super.onStart();
    }

    @Override
    public void onClick(View v)
    {

        Intent intent = new Intent();

        switch (v.getId())
        {
            case R.id.to_post:
                // 如果已经登录了进入发言界面
                if (AccountManager.getInstance().hasLogined())
                {
                    intent.setClass(ShareActivity.this, PostActivity.class);
                    startActivity(intent);
                }
                else
                {
                    if (getParent() instanceof HomeActivity)
                    {
                        intent = new Intent(ShareActivity.this,
                                LoginActivity.class);
                        ((HomeActivity) getParent()).setCurrentTab(2);
                        ((HomeActivity) getParent()).startActivityWithGuideBar(
                                LoginActivity.class, intent);
                    }
                }

                break;
            case R.id.to_blog:
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Const.ShareItemModuel.WEIBO_URL));
                startActivity(intent);
                break;

            case R.id.to_post_sort: // 排序的
                popMenu.showAsDropDown(v);
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(int index)
    {
        switch (index)
        {
            case 0:
                Collections.sort(shareItems, new CompareByDate());
                mAdapter.setShareItems(shareItems);
                mAdapter.notifyDataSetChanged();
                break;

            case 1:
                Collections.sort(shareItems, new CompareByLike());
                mAdapter.setShareItems(shareItems);
                mAdapter.notifyDataSetChanged();
                break;
        }

    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
        Log.d("zhh-->", "shareActivity");
        
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (keyBackClickCount++) {
            case 0:
                Toast.makeText(getApplicationContext(), getString(R.string.exit_again), Toast.LENGTH_SHORT).show(); 
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        keyBackClickCount = 0;
                    }
                }, 2222);
                break;
            case 1:
                ShareActivity.this.finish();
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
