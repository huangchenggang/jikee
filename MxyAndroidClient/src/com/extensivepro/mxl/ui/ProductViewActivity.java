package com.extensivepro.mxl.ui;

import java.util.List;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.Goods;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.app.provider.MxlTables.TGoods;
import com.extensivepro.mxl.app.provider.MxlTables.TGoodsCategory;
import com.extensivepro.mxl.product.ProductManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.ImageDownloader.DownloadCallback;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.ProductViewItemLayout;
import com.extensivepro.mxl.widget.TitleBar;
import com.extensivepro.mxl.widget.WidgetUtil;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 16, 2013 11:18:29 AM
 * @version V1.3.1
 */
public class ProductViewActivity extends BaseActivity
{
    private static final String TAG = "ProductViewActivity";
    
    private ProductViewItemLayout navigateContianer;
    
    private TitleBar mTitleBar;
    
    private ImageView mDescImage;
    
    private String mCategoryId = "";
    
    private ViewGroup mBodyContainer;
    
    private float mScrollPosYPrevious = 0;
    
    private TaskState mTaskState = TaskState.IDEL;
    
    private static final int ANIM_POST_DELAY_MILLS = 0;
    
    private GoodsObserver mObserver; 
    
    private ImageDownloader mImageDownloader;
    
    private class GoodsObserver extends ContentObserver
    {
        public GoodsObserver(Handler handler)
        {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange)
        {
            Logger.d(TAG, "GoodsObserver.onChange()[selfChange:"
                    + selfChange + "]");
            super.onChange(selfChange);
            loadData();
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Logger.d(TAG, "onCreate()[access]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_view);
        mImageDownloader = new ImageDownloader();
//        mImageDownloader = ImageDownloader.getInstance();
        navigateContianer = (ProductViewItemLayout) findViewById(R.id.navigate_item_contianer);
        mBodyContainer = (ViewGroup) findViewById(R.id.body_container);
        mBodyContainer.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Logger.d(TAG, "onTouch()[Event action:"
                        + event.getAction() + "]");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                     View view = findViewById(R.id.title_bar);
                     sendMsg(view.getVisibility() ==
                     View.VISIBLE?View.INVISIBLE:View.VISIBLE,ANIM_POST_DELAY_MILLS);
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE && mBodyContainer.getScrollY() != mScrollPosYPrevious) {
                    Logger.d(TAG, "onTouch()[" + mBodyContainer.getScrollX() + ","
                            + mBodyContainer.getScrollY() + "]");
                    Logger.d(TAG, "onTouch()[mScrollPosYPrevious:"
                            + mScrollPosYPrevious + "]");
                    if (mBodyContainer.getScrollY() > mScrollPosYPrevious + 100
                            || (mBodyContainer.getScrollY() == mScrollPosYPrevious && mBodyContainer
                                    .getScrollY() != 0)) {
                        sendMsg(View.INVISIBLE, ANIM_POST_DELAY_MILLS);
                        mScrollPosYPrevious = mBodyContainer.getScrollY();
                    } else if (mBodyContainer.getScrollY() < mScrollPosYPrevious + 100) {
                        sendMsg(View.VISIBLE, ANIM_POST_DELAY_MILLS);
                        mScrollPosYPrevious = mBodyContainer.getScrollY();
                    }
                }
                return false;
            }
        });
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mDescImage = (ImageView) findViewById(R.id.desc_image);
        String categoryId = getIntent().getStringExtra(
                Const.EXTRA_GOODS_CATEGORY_ID);
        if(TextUtils.isEmpty(categoryId))
        {
            categoryId = ProductManager.getInstance().getCurSelectGoodsId();
        }
        else 
        {
            ProductManager.getInstance().setCurSelectGoodsId(categoryId);
        }
        regObserver();
        if(!TextUtils.isEmpty(categoryId))
        {
            mCategoryId = categoryId;
            loadData();
            ProductManager.getInstance().loadGoods(mCategoryId);
        }
    }
    


    private synchronized void sendMsg(int msgWhat, long delayMills) {
        if (mTaskState == TaskState.BUSY) {
            return;
        }
        mTaskState = TaskState.BUSY;
        mHandler.sendEmptyMessageDelayed(msgWhat, delayMills);
    }

    enum TaskState {
        BUSY, IDEL
    }

    private Handler mHandler = new Handler() {
        public synchronized void handleMessage(final android.os.Message msg) {
            synchronized (mTitleBar) {
                final int visibility = msg.what;
                int animResId = R.anim.push_up_out;
                if(visibility == View.VISIBLE)
                {
                    animResId = R.anim.push_up_in;
                }
                else if(visibility == View.INVISIBLE)
                {
                    animResId = R.anim.push_up_out;
                }
                mTitleBar.clearAnimation();
                Animation animation = AnimationUtils.loadAnimation(ProductViewActivity.this, animResId);
                animation.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                    }
                    
                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {
                    }
                    
                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        mTitleBar.setVisibility(visibility);
                        mTaskState = TaskState.IDEL;
                    }
                });
                mTitleBar.setAnimation(animation);
                if(getParent() instanceof HomeActivity)
                {
                    ((HomeActivity)getParent()).setGuideVisiblityWithAnim(visibility);
                }
                mTitleBar.startAnimation(animation);
            }
        }
    };
    
    private void loadData()
    {
        List<Goods> goodsList = ProductManager.getInstance().getGoodsByCategoryId(mCategoryId);
        if(goodsList.size() > 0)
        {
            unRegObserver();
        }
        else 
        {
            regObserver();
        }
        navigateContianer.setUpLayout(goodsList);
        Cursor cursor = getContentResolver().query(TGoodsCategory.CONTENT_URI,
                new String[] { TGoodsCategory.DISPLAY_IMAGE,TGoodsCategory.CATEGORY_NAME },
                TGoodsCategory.CATEGORY_ID + "='" + mCategoryId + "'", null,
                null);
        try
        {
            if (cursor.getCount() > 0)
            {
                cursor.moveToNext();
                String imageUrl = cursor.getString(cursor.getColumnIndex(TGoodsCategory.DISPLAY_IMAGE));
                String title = cursor.getString(cursor.getColumnIndex(TGoodsCategory.CATEGORY_NAME));
                Logger.d(TAG, "onCreate()[cursor item:" + imageUrl + "]");
                mTitleBar.setTitle(title);
                final int maxWidth = ClientManager.getInstance().getScreenWidth();
                Bitmap bmp = mImageDownloader.downloadImage(new DownloadCallback() {
                    
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
                        Logger.e(TAG, "imageDownloader.onLoadFailed()[access]");
                    }
                }, imageUrl, maxWidth);
                
                if(bmp != null)
                {
                    setImageFullScreen(bmp);
                    bmp.recycle();
                }
            }
        }
        finally
        {
            if(cursor != null)
            {
                cursor.close();
            }
        }
    }
    
    @Override
    protected void onDestroy()
    {
        Logger.d(TAG, "onDestroy()[access]");
        super.onDestroy();
        HomeActivity.keyBackClickCount=0;
        unRegObserver();
        mHandler.removeMessages(View.VISIBLE);
        mHandler.removeMessages(View.INVISIBLE);
        mImageDownloader.clearCache();
        navigateContianer.clearCache();
        navigateContianer.removeAllViews();
        mDescImage.setImageBitmap(null);
        mDescImage = null;
        mImageDownloader = null;
        navigateContianer = null;
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        HomeActivity.keyBackClickCount=3;
        mImageDownloader.resumeDownload();
        navigateContianer.resumeDownload();
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        mImageDownloader.pauseDownload();
        navigateContianer.pauseDownload();
    }
    
    @Override
    protected void onNewIntent(Intent intent)
    {
        Logger.d(TAG, "onNewIntent()[access]");
        super.onNewIntent(intent);
//        String categoryId = intent.getStringExtra(
//                Const.EXTRA_GOODS_CATEGORY_ID);
//        if(TextUtils.isEmpty(categoryId))
//        {
//            categoryId = ProductManager.getInstance().getCurSelectGoodsId();
//        }
//        else 
//        {
//            ProductManager.getInstance().setCurSelectGoodsId(categoryId);
//        }
//        if(!TextUtils.isEmpty(categoryId))
//        {
//            mCategoryId = categoryId;
//            loadData();
//            ProductManager.getInstance().loadGoods(mCategoryId);
//        }
    }
    
    private void regObserver()
    {
        Logger.d(TAG, "regObserver()[access]");
        if(mObserver == null)
        {
            mObserver = new GoodsObserver(new Handler());
            getContentResolver().registerContentObserver(TGoods.CONTENT_URI, true, mObserver);
        }
    }
    
    private void unRegObserver()
    {
        Logger.d(TAG, "unRegObserver()[access]");
        if(mObserver != null)
        {
            getContentResolver().unregisterContentObserver(mObserver);
            mObserver = null;
        }
    }
    
    private void setImageFullScreen(Bitmap bmp)
    {
        if(mDescImage == null || bmp == null)
        {
            return;
        }
        int imageW = ClientManager.getInstance().getScreenWidth();
        int imageH = WidgetUtil.getDispFullImageHeight(this, bmp.getHeight()) - 300;
        mDescImage.setLayoutParams(new LinearLayout.LayoutParams(imageW,imageH));
        mDescImage.setImageBitmap(bmp);
    }
    
}
