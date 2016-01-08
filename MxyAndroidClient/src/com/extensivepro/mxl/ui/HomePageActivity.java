package com.extensivepro.mxl.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.FreeSale;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.app.provider.MxlTables.TCarousel;
import com.extensivepro.mxl.app.provider.MxlTables.TGoodsCategory;
import com.extensivepro.mxl.product.ProductManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.ImageDownloader.DownloadCallback;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.HomeNavigatorGallery;
import com.extensivepro.mxl.widget.HomePageNavigatorAdapter;
import com.extensivepro.mxl.widget.MaskableImageView;
import com.extensivepro.mxl.widget.WidgetUtil;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 16, 2013 11:18:24 AM
 * @version V1.3.1
 */
public class HomePageActivity extends BaseActivity implements OnItemSelectedListener,OnItemClickListener,OnClickListener
{
    private static final String TAG = HomePageActivity.class.getSimpleName();

    private HomeNavigatorGallery mGallery;

    private HomePageNavigatorAdapter mAdapter;
    
    private Cursor mCursor;
    
    private CarouselObserver mCarouselObserver;
    
    private ViewGroup mNavigateCountGroup;
    
    private View mSelectCountView;
    
    private FreeSale mFreeSale;
    
    private ImageView mFreeImage;
    
    private ImageDownloader mImageDownloader;
    
    /**
     * 连续按两次返回键就退出
     */
    private int keyBackClickCount = 0;
    
    private Handler mHander = new Handler()
    {
        public void handleMessage(android.os.Message msg) 
        {
            String url = (String) msg.obj;
            final int maxWidth = ClientManager.getInstance().getScreenWidth();
            Bitmap bitmap = mImageDownloader.downloadImage(new DownloadCallback() {
                @Override
                public void onLoadSuccess(Bitmap bitmap)
                {
                    if (bitmap != null)
                    {
                        setImageFullScreen(bitmap);
                    }
                }
                
                @Override
                public void onLoadFailed()
                {
                    
                }
            }, url, maxWidth);
            if(bitmap != null)
            {
                setImageFullScreen(bitmap);
            }
        }
    };
    
    private void setImageFullScreen(Bitmap bmp)
    {
        int imageW = ClientManager.getInstance().getScreenWidth();
        int imageH = WidgetUtil.getDispFullImageHeight(HomePageActivity.this, bmp.getHeight()) - 300;
        mFreeImage.setLayoutParams(new FrameLayout.LayoutParams(imageW,imageH));
        mFreeImage.setImageBitmap(bmp);
    }
    
    private class CarouselObserver extends ContentObserver
    {
        public CarouselObserver(Handler handler)
        {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange)
        {
            Logger.d(TAG, "CarouselObserver.onChange()[selfChange:"
                    + selfChange + "]");
            super.onChange(selfChange);
            mCursor = getContentResolver().query(TCarousel.CONTENT_URI, null,
                    null, null, null);
            mAdapter.changeCursor(mCursor);
            createCountImages();
        }
    }
    

    private StateReceiver mStateReceiver;
    
    private class StateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.d(TAG, "StateReceiver.onReceive()[action:"+action+"]");
            if(action!=null && action.equals(Const.ACTION_LOAD_FREE_SALE_SUCCESS))
            {
                Object obj = intent.getSerializableExtra(Const.EXTRA_OBJ_FREE_SALE);
                if(obj instanceof FreeSale)
                {
                    mFreeSale = (FreeSale) obj;
                    Message msg = mHander.obtainMessage();
                    msg.obj = ((FreeSale) mFreeSale).getImageSrc();
                    msg.sendToTarget();
                }
            }
//            else if(action!=null && action.equals(Const.ACTION_LOGIN_SUCCESS))
//            {
//                findViewById(R.id.get_it_now).setEnabled(true);
//            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Logger.d(TAG, "onCreate()[access]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        mImageDownloader = new ImageDownloader();
//        mImageDownloader =  ImageDownloader.getInstance();
        ClientManager.getInstance().validateCallback();
        mGallery = (HomeNavigatorGallery) findViewById(R.id.navigate_gallery);
        mCursor = getContentResolver().query(TCarousel.CONTENT_URI, null, null, null, null);
        if(mCursor != null)
        {
            mAdapter = new HomePageNavigatorAdapter(this,mCursor);
            mGallery.setAdapter(mAdapter);
            startManagingCursor(mCursor);
        }
        mFreeImage = (ImageView) findViewById(R.id.free_sale_image);
        createCountImages();
        mGallery.setOnItemSelectedListener(this);
        mGallery.setOnItemClickListener(this);
        findViewById(R.id.pay_now).setOnClickListener(this);
        findViewById(R.id.get_it_now).setOnClickListener(this);
        regReceiver();
        regObserver();
        ProductManager.getInstance().loadFreeSale();
        findViewById(R.id.get_it_now).setEnabled(true);
    }
    
    @Override
    protected void onDestroy()
    {
        Logger.d(TAG, "onDestroy()[access]");
        super.onDestroy();
        unregObserver();
        unregReceiver();
        mImageDownloader.clearCache();
        mAdapter.clearCache();
        mGallery.pauseAudoPlay();
        mImageDownloader = null;
        mAdapter = null;
        mGallery = null;
        if(mCursor != null && !mCursor.isClosed())
        {
            mCursor.close();
        }
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
        if(mCarouselObserver == null)
        {
            mCarouselObserver = new CarouselObserver(new Handler());
            getContentResolver().registerContentObserver(TCarousel.CONTENT_URI, true, mCarouselObserver);
        }
    }
    
    private void unregObserver()
    {
        Logger.d(TAG, "unregObserver()[access]");
        if(mCarouselObserver != null)
        {
            getContentResolver().unregisterContentObserver(mCarouselObserver);
        }
    }
    
    private void createCountImages()
    {
        mNavigateCountGroup = (ViewGroup) findViewById(R.id.navigate_image_count_group);
        mNavigateCountGroup.removeAllViews();
        if(mAdapter.getCount() == 0)
        {
            return;
        }
        mNavigateCountGroup.setVisibility(View.VISIBLE);
        for(int i=0 ;i < mAdapter.getCount();i++)
        {
            ImageView countBoll = (ImageView) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.home_navigate_count_item, null);
            mNavigateCountGroup.addView(countBoll);
        }
        mSelectCountView = mNavigateCountGroup.getChildAt(0);
        mSelectCountView.setSelected(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3)
    {
        if(arg2 < mAdapter.getCount())
        {
            if(mSelectCountView != null)
            {
                mSelectCountView.setSelected(false);
                MaskableImageView view = (MaskableImageView) mSelectCountView.findViewById(R.id.navigate_image);
                if (view != null)
                {
                    view.clearMask();
                }
            }
            mSelectCountView = mNavigateCountGroup.getChildAt(arg2);
            mSelectCountView.setSelected(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {

    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        mImageDownloader.pauseDownload();
        mAdapter.pauseDownload();
        mGallery.pauseAudoPlay();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        mImageDownloader.resumeDownload();
        mAdapter.resumeDownload();
        mGallery.resumeAudoPlay();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        Logger.d(TAG, "onItemClick()[access]");
        if (getParent() instanceof HomeActivity)
        {
            Intent intent = new Intent();
            Class<?> clazz = ProductViewActivity.class;
            intent.setClass(this, clazz);
            Cursor cursor = getContentResolver().query(
                    TGoodsCategory.CONTENT_URI,
                    new String[] { TGoodsCategory.CATEGORY_ID }, null, null,
                    null);
            int posInGoodsCategoryDB = getGalleryItemPosInGoodsCategory(arg2);
            cursor.moveToPosition(posInGoodsCategoryDB);
            String id = cursor.getString(cursor
                    .getColumnIndex(TGoodsCategory.CATEGORY_ID));
            cursor.close();
            if (!TextUtils.isEmpty(id))
            {
                intent.putExtra(Const.EXTRA_GOODS_CATEGORY_ID, id);
            }
            ((HomeActivity) getParent()).setCurrentTab(HomeActivity.CUR_TAB_ID_HOME_PRODUCT);
            ((HomeActivity) getParent()).startActivityPushHistoryWithGuideBar(clazz,
                    intent,ProductActivity.class);
        }
    }
    
    private static final int POS_CANVAS_FRAME_CAROUSEL = 0;
    private static final int POS_MINI_ALBUM_CAROUSEL = 1;
    private static final int POS_PHOTO_BALLON_CAROUSEL = 2;
    private static final int POS_BOOKLET_CAROUSEL = 3;
    private static final int POS_PHOTO_CANLENDAR_CAROUSEL = 4;
    private static final int POS_BOOKLET2_CAROUSEL = 5;
    
    private static final int POS_PHOTO_BALLON_CATEGORY = 0;
    private static final int POS_MINI_ALBUM_CATEGORY = 1;
    private static final int POS_PHOTO_CANLENDAR_CATEGORY = 2;
    private static final int POS_CANVAS_FRAME_GOODS_CATEGORY = 3;
    private static final int POS_BOOKLET_CATEGORY = 4;
    
    private int getGalleryItemPosInGoodsCategory(int pos)
    {
        int posInGoodsCategoryDB = 0;
        switch(pos)
        {
            case POS_CANVAS_FRAME_CAROUSEL:
                posInGoodsCategoryDB = POS_CANVAS_FRAME_GOODS_CATEGORY;
                break;
            case POS_MINI_ALBUM_CAROUSEL:
                posInGoodsCategoryDB = POS_MINI_ALBUM_CATEGORY;
                break;
            case POS_PHOTO_BALLON_CAROUSEL:
                posInGoodsCategoryDB = POS_PHOTO_BALLON_CATEGORY;
                break;
            case POS_BOOKLET_CAROUSEL:
            case POS_BOOKLET2_CAROUSEL:
                posInGoodsCategoryDB = POS_BOOKLET_CATEGORY;
                break;
            case POS_PHOTO_CANLENDAR_CAROUSEL:
                posInGoodsCategoryDB = POS_PHOTO_CANLENDAR_CATEGORY;
                break;
        }
        return posInGoodsCategoryDB;
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.pay_now:
                if(!AccountManager.getInstance().hasLogined())
                {//If not login , go to the account page.
                    if(getParent() instanceof HomeActivity)
                    {
                        Intent intent = new Intent();
                        Class<?> clazz = null;
                        clazz = LoginActivity.class;
                        ((HomeActivity) getParent()).setCurrentTab(2);
                        intent.setClass(this, clazz);
                        ((HomeActivity) getParent()).startActivityWithGuideBar(
                                clazz, intent);
                    }
                }
                else if(AccountManager.getInstance().hasLogined())
                {
                    if (getParent() instanceof HomeActivity)
                    {
                        ((HomeActivity) getParent()).setCurrentTab(2);
                        ((HomeActivity) getParent())
                                .startActivityPushHistoryWithGuideBar(
                                        AccountRechargeActivity.class, null,
                                        new Class[] { AccountActivity.class,
                                                AccountRechargeActivity.class });
                    }
                }
                break;
            case R.id.get_it_now:
                if(!AccountManager.getInstance().hasLogined()){
                    Toast.makeText(this, R.string.toast_has_not_loggin,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                if (AccountManager.getInstance().getCurrentAccount() != null){
                    if(!AccountManager.getInstance().getCurrentAccount()
                                .isHasGotFreeSale()){
                        Toast.makeText(this, R.string.toast_has_got_free_sale,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ProductManager.getInstance().setCurSelectGoodsId(
                                mFreeSale.getSaleId());
                        
                        ((HomeActivity) getParent()).setIsFreeGet(true);
                        
                        Intent intent = new Intent(HomePageActivity.this, PhotoAlbumActivity.class);
                        ((HomeActivity) getParent()).setCurrentTab(1);
                        ((HomeActivity) getParent())
                                .startActivityPushHistoryWithGuideBar(
                                        PhotoAlbumActivity.class, intent, new Class[] {
                                                ProductActivity.class,
                                                ProductViewActivity.class,
                                                PhotoAlbumActivity.class });
                    }
                }
                       
               
                break;
        }
    }
    
    private void regReceiver()
    {
        if(mStateReceiver == null)
        {
            mStateReceiver = new StateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_LOAD_FREE_SALE_SUCCESS);
            filter.addAction(Const.ACTION_LOAD_FREE_SALE_FAILED);
            filter.addAction(Const.ACTION_LOGIN_SUCCESS);
            filter.addAction(Const.ACTION_LOAD_PRODUCTDETAIL_SUCCESS);
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
                HomePageActivity.this.finish();
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
