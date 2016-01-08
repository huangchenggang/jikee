package com.extensivepro.mxl.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.MxlApplication;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.app.provider.MxlTables.TGoodsCategory;
import com.extensivepro.mxl.product.ProductManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.util.SharedPreferenceUtil;
import com.extensivepro.mxl.widget.ProductListAdapter;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 16, 2013 11:18:29 AM
 * @version V1.3.1
 */
public class ProductActivity extends BaseActivity implements TextWatcher
{
    private static final String TAG = ProductActivity.class.getSimpleName();
    
    /**
     * 连续按两次返回键就退出
     */
    private int keyBackClickCount = 0;
    
    private class GoodsCategoryObserver extends ContentObserver
    {
        public GoodsCategoryObserver(Handler handler)
        {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange)
        {
            Logger.d(TAG, "CarouselObserver.onChange()[selfChange:"
                    + selfChange + "]");
            super.onChange(selfChange);
            mCursor = getContentResolver().query(TGoodsCategory.CONTENT_URI, null,
                    null, null, null);
            mAdapter.changeCursor(mCursor);
            setListViewHeightBasedOnChildren(mListView);
        }
    }
    
    private Cursor mCursor;
    private ProductListAdapter mAdapter;
    
    private GoodsCategoryObserver mObserver;
    
    private ListView mListView;

    private EditText mSearchEdit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Logger.d(TAG, "onCreate()[access]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);
        //每次重新进入需要加载
        boolean needLoadCategoryFromLocal = SharedPreferenceUtil
                .isNeedLoadGoodsCategoryFromLocal()
                && !SharedPreferenceUtil
                        .isLoadGoodsCategoryFromNetworkSuccess();
        ProductManager.getInstance().loadGoodsCategory(needLoadCategoryFromLocal);
        mListView = (ListView) findViewById(R.id.product_list);
        mSearchEdit = (EditText) findViewById(R.id.search_edit);
        mSearchEdit.addTextChangedListener(this);
        mCursor = getContentResolver().query(TGoodsCategory.CONTENT_URI, null,
                null, null, null);
        mAdapter = new ProductListAdapter(this, mCursor);
        mListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListView);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3)
            {
                if(arg2>5)
                {
                    return;
                }
                Logger.d(TAG, "onItemClick()[access]");
                if(getParent() instanceof HomeActivity)
                {
                    Intent intent = new Intent();
                    Class<?> clazz = null;
                    if(arg2 == 5)
                    {
                        clazz = LoginActivity.class;
                        ((HomeActivity)getParent()).setCurrentTab(2);
                    }
                    else 
                    {
                        clazz = ProductViewActivity.class;
                    }
                    intent.setClass(ProductActivity.this,clazz);
                    Object obj = mAdapter.getItem(arg2);
                    if(obj instanceof String)
                    {
                        intent.putExtra(Const.EXTRA_GOODS_CATEGORY_ID, (String)obj);
                    }
                    ((HomeActivity)getParent()).startActivityWithGuideBar(clazz, intent);
                }
//                mAdapter.showProductViewActivity(arg2);
//                ProductManager.getInstance().loadGoods("402880f33e1177bf013e11d849fb0000");
//                ProductManager.getInstance().loadGoods("402880f33e1177bf013e11d849fb0000");
            }
        });
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                switch(scrollState)
                {
                    case OnScrollListener.SCROLL_STATE_IDLE:
                        mAdapter.clearInVisibleImageCache(
                                mListView.getFirstVisiblePosition(),
                                mListView.getLastVisiblePosition());
                        break;
                }
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount)
            {
                
            }
        });
        startManagingCursor(mCursor);
        regObserver();
    }
    
    @Override
    protected void onDestroy()
    {
        Logger.d(TAG, "onDestroy()[access]");
        super.onDestroy();
        unRegObserver();
//        if(mCursor != null && !mCursor.isClosed())
//        {
//            mCursor.close();
//        }
        mSearchEdit.removeTextChangedListener(this);
        mAdapter.clearCache();
        mAdapter = null;
    }
    
    @Override
    protected void onNewIntent(Intent intent)
    {
        Logger.d(TAG, "onNewIntent()[access]");
        super.onNewIntent(intent);
    }
    
    private void regObserver()
    {
        Logger.d(TAG, "regObserver()[access]");
        if(mObserver == null)
        {
            mObserver = new GoodsCategoryObserver(new Handler());
            getContentResolver().registerContentObserver(TGoodsCategory.CONTENT_URI, true, mObserver);
        }
    }
    
    private void unRegObserver()
    {
        Logger.d(TAG, "unRegObserver()[access]");
        if(mObserver != null)
        {
            getContentResolver().unregisterContentObserver(mObserver);
        }
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        mAdapter.pauseDownload();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        mAdapter.resumeDownload();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        Logger.d(TAG, "s:" + s.toString());
        if (s.length() == 0 || TextUtils.isEmpty(s.toString()))
        {
            mCursor = getContentResolver().query(TGoodsCategory.CONTENT_URI,
                    null, null, null, null);
            mAdapter.changeCursor(mCursor);
            setListViewHeightBasedOnChildren(mListView);
            return;
        }
        String upperCase = s.toString().toUpperCase();
        String lowerCase = s.toString().toLowerCase();
        mCursor = getContentResolver().query(
                TGoodsCategory.CONTENT_URI,
                null,
                TGoodsCategory.CATEGORY_NAME + " like ? or "
                        + TGoodsCategory.CATEGORY_NAME + " like ?",
                new String[] { "%" + upperCase + "%", "%" + lowerCase + "%" },
                null);
        mAdapter.changeCursor(mCursor);
        setListViewHeightBasedOnChildren(mListView);
    }
    
    private void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null)
        {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        if(listAdapter.getCount() <= 4)
        {
            totalHeight = ClientManager.getInstance().getScreenHeight();
        }
        else 
        {
            totalHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("zhh-->", "product");
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
                ProductActivity.this.finish();
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
