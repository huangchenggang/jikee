package com.extensivepro.mxl.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.bean.Goods;
import com.extensivepro.mxl.app.bean.ImageStore;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.ui.AccountActivity;
import com.extensivepro.mxl.ui.HomeActivity;
import com.extensivepro.mxl.ui.PhotoAlbumActivity;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.ImageDownloader.DownloadCallback;
import com.extensivepro.mxl.util.Logger;

public class ProductViewItemLayout extends LinearLayout
{

    private static final String TAG = ProductViewItemLayout.class.getSimpleName();

    private int mScreenWidth;
    private int mScreenHeight;
    
    private ImageDownloader mImageDownloader;
    
    private List<Goods> mGoods;
    
    public ProductViewItemLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ProductViewItemLayout(Context context)
    {
        super(context);
        init();
    }
    
    private void init()
    {
        mImageDownloader = new ImageDownloader();
//        mImageDownloader = ImageDownloader.getInstance();
        mGoods = new ArrayList<Goods>();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        setLayoutParams(params);
        setOrientation(LinearLayout.VERTICAL);
        if(getContext() instanceof Activity)
        {
            Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
            mScreenWidth = display.getWidth();
            mScreenHeight = display.getHeight();
        }
    }
    
    public void setUpLayout(List<Goods> goodsList)
    {
        mGoods = goodsList;
        removeAllViews();
        if(mGoods != null && mGoods.size() >0)
        {
            int itemHeight = mScreenHeight/3;
            int itemWidth = mScreenWidth/3;
            int row = 1,col =3;
            if(mGoods.size() >0 && mGoods.size() <=3)
            {
                row = 1;
                col = mGoods.size();
                itemWidth = mScreenWidth/col;
            }
            else if(mGoods.size() ==4 )
            {
                row = 2;
                col = 2;
                itemWidth = mScreenWidth/2;
            }
            else if(mGoods.size() >4 )
            {
                row = mGoods.size()/2;
                col = 3;
                itemWidth = mScreenWidth/3;
            }
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int count = 0;
            for(int i=0;i<row;i++)
            {
                LinearLayout rowLayout = new LinearLayout(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                rowLayout.setLayoutParams(params);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j < col; j++)
                {
                    if(count>=mGoods.size())
                    {
                        break;
                    }
                    final FrameLayout item = (FrameLayout) inflater.inflate(
                            R.layout.product_view_navigate_item, null);
                    FrameLayout.LayoutParams itemParams = new FrameLayout.LayoutParams(itemWidth, itemHeight);
                    itemParams.gravity = Gravity.CENTER;
                    item.setLayoutParams(itemParams);
                    item.setClickable(true);
                    rowLayout.addView(item);
                    item.findViewById(R.id.name).setOnTouchListener(new OnTouchListener() {
                        
                        @Override
                        public boolean onTouch(View v, MotionEvent event)
                        {
                            drawMask(event, item);
                            return false;
                        }
                    });
                    item.findViewById(R.id.name_en).setOnTouchListener(new OnTouchListener() {
                        
                        @Override
                        public boolean onTouch(View v, MotionEvent event)
                        {
                            drawMask(event, item);
                            return false;
                        }
                    });
                    
                    item.findViewById(R.id.mask_bg).setOnClickListener(
                            new OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {

                                    Object obj = item.getTag();
                                    Logger.d(TAG, "onClick");
                                    if (obj instanceof Goods
                                            && getContext() instanceof Activity)
                                    {
                                        Goods curGoods = (Goods) obj;
                                        Logger.d(TAG, "cur goods:" + curGoods);
                                        Activity activity = (Activity) getContext();
                                        if (activity.getParent() instanceof HomeActivity)
                                        {
                                            Intent intent = new Intent(getContext(),PhotoAlbumActivity.class);
                                            intent.putExtra(
                                                    Const.EXTRA_GOODS_OBJ,
                                                    curGoods);
                                            ((HomeActivity) activity
                                                    .getParent())
                                                    .startActivityWithGuideBar(
                                                            PhotoAlbumActivity.class,
                                                            intent);
                                        }
                                    }

                                }
                            });
                    
                    
                    if(mGoods.size() > count)
                    {
                        final Goods goods = mGoods.get(count);
                        ((TextView) item.findViewById(R.id.name))
                                .setText(goods.getName());
                        List<ImageStore> goodsImageStore = goods.getGoodsImageStore();
                        if(goodsImageStore.size()>0)
                        {
                            String imageUri = goodsImageStore.get(0).generateUrl();
                            Logger.d(TAG, "setUpLayout()[imageUri:"+imageUri+"]");
                            final int maxWidth = ClientManager.getInstance().getScreenWidth()/(col*2);
                            final ImageView itemImage = (ImageView) item.findViewById(R.id.item_image);
                            Bitmap bmp = mImageDownloader.downloadImage(new DownloadCallback() {
                                
                                @Override
                                public void onLoadSuccess(Bitmap bitmap)
                                {
                                    if(bitmap != null)
                                    {
                                        itemImage.setImageBitmap(bitmap);
                                    }
                                }
                                
                                @Override
                                public void onLoadFailed()
                                {
                                    
                                }
                            }, imageUri, maxWidth);
                            if(bmp != null)
                            {
                                itemImage.setImageBitmap(bmp);
                            }
                        }
                        item.setTag(goods);
                    }
                    count++;
                }
                addView(rowLayout);
            }
            if(mGoods.size() == 5 && getChildCount()>1)
            {
                ViewGroup layout = (ViewGroup) getChildAt(1);
                for(int i=0;i<layout.getChildCount();i++)
                {
                    layout.getChildAt(i).getLayoutParams().width = mScreenWidth/2;
                    layout.getChildAt(i).getLayoutParams().height = itemHeight;
                }
            }
        }
    }
    
    public void clearCache()
    {
        mImageDownloader.clearCache();
    }
    
    public void pauseDownload()
    {
        mImageDownloader.pauseDownload();
    }
    
    public void resumeDownload()
    {
        mImageDownloader.resumeDownload();
    }
    
    private void drawMask(MotionEvent event,View item)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_MASK:
                item.setSelected(false);
                break;
            case MotionEvent.ACTION_DOWN:
                item.setSelected(true);
                break;
            default:
                break;
        }
    }

}
