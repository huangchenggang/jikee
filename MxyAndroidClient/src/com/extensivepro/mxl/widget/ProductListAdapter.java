package com.extensivepro.mxl.widget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.app.provider.MxlTables.TGoodsCategory;
import com.extensivepro.mxl.ui.ProductViewActivity;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.ImageDownloader.DownloadCallback;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.util.SharedPreferenceUtil;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 22, 2013 4:58:20 PM
 * @version V1.3.1
 */
public class ProductListAdapter extends CursorAdapter
{
    private static final String TAG = ProductListAdapter.class
            .getSimpleName();

    private Context mContext;

    private ImageDownloader mImageDownloader;
    
    private WeakHashMap<Integer,WeakReference<Bitmap>> mImageMapping;

    public ProductListAdapter(Context context, Cursor c)
    {
        super(context, c);
        mContext = context;
        mImageDownloader = new ImageDownloader();
//        mImageDownloader = ImageDownloader.getInstance();
        mImageMapping = new WeakHashMap<Integer,WeakReference<Bitmap>>();
    }

    @Override
    public int getCount()
    {
        return getCursor().getCount();
    }

    @Override
    public Object getItem(int position)
    {
        getCursor().moveToPosition(position);
        return getCursor().getString(getCursor()
                .getColumnIndex(TGoodsCategory.CATEGORY_ID));
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    @Override
    public void changeCursor(Cursor cursor)
    {
        Logger.d(TAG, "changeCursor()");
        clearImgMap();
      
        super.changeCursor(cursor);
    }
    
    private void clearImgMap()
    {
        // TODO Auto-generated method stub
        synchronized(mImageMapping)
        {
            if (!mImageMapping.isEmpty())
            {
                 Set<Integer> key = mImageMapping.keySet();
                for (int i = 0; i < key.size(); i++)
                {
                    if (mImageMapping.get(key) != null
                            && mImageMapping.get(key).get() != null
                            && !mImageMapping.get(key).get().isRecycled())
                    {
                        mImageMapping.get(key).get().recycle();
                        System.gc();
                        mImageMapping.remove(key);
                    }
                }
                mImageMapping.clear();
            }
        }
        
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        Logger.d(TAG, "bindView()[position:"+cursor.getPosition()+"]");
        Object tag = view.getTag();
        if (view.getTag() instanceof Holder)
        {
            final Holder holder = (Holder) tag;
            final String url = cursor.getString(cursor
                    .getColumnIndex(TGoodsCategory.PATH));
            Logger.d(TAG, "bindView()[url:"+url+"]");
            final int position = cursor.getPosition();
            String title = cursor.getString(cursor
                    .getColumnIndex(TGoodsCategory.CATEGORY_NAME));
            String detial = cursor.getString(cursor
                    .getColumnIndex(TGoodsCategory.META_DESCRIPTION));
            if(!TextUtils.isEmpty(title))
            {
                holder.productTitle.setText(title);
            }
            if(!TextUtils.isEmpty(detial))
            {
                holder.productDetial.setText(detial);
            }
            Bitmap bmp = null;
            if (mImageMapping.containsKey(position)
                    && mImageMapping.get(position) != null
                    && mImageMapping.get(position).get() != null)
            {
                bmp = mImageMapping.get(position).get();
            }
            if(bmp != null)
            {
                holder.progressBar.setVisibility(View.GONE);
                holder.productImage.setVisibility(View.VISIBLE);
                if(mImageMapping.get(position) != null)
                {
                    holder.productImage.setImageBitmap(bmp);
                }
            }
            else 
            {
                if (SharedPreferenceUtil.isLoadGoodsCategoryFromNetworkSuccess())
                {
                    final int maxWidth = ClientManager.getInstance().getScreenWidth()/4;
                    bmp = mImageDownloader.downloadImage(new DownloadCallback() {
                        
                        @Override
                        public void onLoadSuccess(Bitmap bitmap)
                        {
                            Logger.d(TAG, "downloadImage.onLoadSuccess()[bmp:" + bitmap + "]");
                            if(bitmap != null)
                            {
                                mImageMapping.put(position,new WeakReference<Bitmap>(bitmap));
                                bitmap = null;
                                notifyDataSetChanged();
                            }
                        }
                        
                        @Override
                        public void onLoadFailed()
                        {
                            if (mImageMapping.containsKey(position)
                                    && mImageMapping.get(position) != null)
                            {
                                mImageMapping.put(position, null);
                                notifyDataSetChanged();
                            }
                            Logger.e(TAG, "onLoadFailed()[access]");
                        }
                    }, url, maxWidth);
                }
                else
                {
                    bmp = mImageDownloader.downloadImageFromAsset(new DownloadCallback() {
                        
                        @Override
                        public void onLoadSuccess(Bitmap bitmap)
                        {
                            Logger.d(TAG, "downloadImageFromAsset.onLoadSuccess()[bmp:" + bitmap + "]");
                            if(bitmap != null)
                            {
                                mImageMapping.put(position,new WeakReference<Bitmap>(bitmap));
                                bitmap = null;
                                notifyDataSetChanged();
                            }
                        }
                        
                        @Override
                        public void onLoadFailed()
                        {
                            if (mImageMapping.containsKey(position)
                                    && mImageMapping.get(position) != null)
                            {
                                mImageMapping.put(position, null);
                                notifyDataSetChanged();
                            }
                            Logger.e(TAG, "onLoadFailed()[access]");
                        }
                    }, url);
                }
                if(bmp != null)
                {
                    mImageMapping.put(position, new WeakReference<Bitmap>(bmp));
                    holder.progressBar.setVisibility(View.GONE);
                    holder.productImage.setVisibility(View.VISIBLE);
                    if(mImageMapping.get(position) != null)
                    {
                        holder.productImage.setImageBitmap(bmp);
                    }
                }
                else 
                {
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.productImage.setVisibility(View.GONE);
                }
            }
        }
    }
    
    public void clearInVisibleImageCache(int firstVisiblePos,int lastVisiblePos)
    {
        Logger.d(TAG, "clearInVisibleImageCache()[firstVisiblePos:"
                + firstVisiblePos + ",lastVisiblePos:" + lastVisiblePos + "]");
        if(firstVisiblePos == lastVisiblePos)
        {
            return;
        }
        List<Integer> clearList = new ArrayList<Integer>();
        
        while(firstVisiblePos>0)
        {
            firstVisiblePos--;
            clearList.add(firstVisiblePos);
        }
        
        while(lastVisiblePos<getCount())
        {
            lastVisiblePos++;
            clearList.add(lastVisiblePos);
        }
        if(clearList.isEmpty() )
        {
            return;
        }
        for(int i=0;i<clearList.size();i++)
        {
            int key = clearList.get(i);
            if (mImageMapping.containsKey(key)
                    && mImageMapping.get(key) != null
                    && mImageMapping.get(key).get() != null
                    && !mImageMapping.get(key).get().isRecycled())
            {
                mImageMapping.remove(key);
            }
        }
    }
    
    public void showProductViewActivity(int position)
    {
        Intent intent = new Intent(mContext, ProductViewActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View convertView = null;
        Holder holder = new Holder();
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.product_list_item,
                null);
        holder.productImage = (ImageView) convertView
                .findViewById(R.id.product_list_image);
        holder.productTitle = (TextView) convertView
                .findViewById(R.id.product_list_item_title);
        holder.productDetial = (TextView) convertView
                .findViewById(R.id.product_list_item_detial);
        holder.progressBar = (ProgressBar) convertView
                .findViewById(R.id.load_progress);
        convertView.setTag(holder);
        return convertView;
    }
    
    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    break;
    
                default:
                    break;
            }
            
        }
        
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub
            
        }
    };
    
    public void clearCache()
    {
        mImageDownloader.clearCache();
        clearImgMap();
    }
    
    public void pauseDownload()
    {
        mImageDownloader.pauseDownload();
    }
    
    public void resumeDownload()
    {
        mImageDownloader.resumeDownload();
    }
    
    static class Holder
    {
        ImageView productImage;
        TextView productTitle;
        TextView productDetial;
        ProgressBar progressBar;
    }
}
