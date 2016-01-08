package com.extensivepro.mxl.widget;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.provider.MxlTables.TCarousel;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.ImageDownloader.DownloadCallback;
import com.extensivepro.mxl.util.Logger;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 22, 2013 4:58:20 PM
 * @version V1.3.1
 */
public class HomePageNavigatorAdapter extends CursorAdapter
{
    private static final String TAG = HomePageNavigatorAdapter.class
            .getSimpleName();

    private Context mContext;

    private Cursor mCursor;

    private ImageDownloader mImageDownloader;

    public HomePageNavigatorAdapter(Context context, Cursor c)
    {
        super(context, c);
        mContext = context;
        mImageDownloader = new ImageDownloader();
//        mImageDownloader = ImageDownloader.getInstance();
        mCursor = c;
    }

    @Override
    public int getCount()
    {
        return mCursor.getCount();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        Logger.d(TAG, "bindView()[position:"+cursor.getPosition()+"]");
        Object tag = view.getTag();
        if (view.getTag() instanceof Holder)
        {
            final Holder holder = (Holder) tag;
            String url = cursor.getString(cursor
                    .getColumnIndex(TCarousel.IMAGE_SRC));
            Logger.d(TAG, "bindView()[url:"+url+"]");
            Bitmap bmp = mImageDownloader.downloadImageFromAsset(new DownloadCallback() {

                @Override
                public void onLoadSuccess(Bitmap bitmap)
                {
                    if (bitmap != null)
                    {
                        holder.navigateImage.setImageBitmap(bitmap);
                        holder.progressBar.setVisibility(View.GONE);
                    }
                    else 
                    {
                        holder.progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onLoadFailed()
                {
                    Logger.e(TAG, "onLoadFailed()[access]");
                }
            }, url);
            if (bmp != null)
            {
                holder.navigateImage.setImageBitmap(bmp);
                holder.progressBar.setVisibility(View.GONE);
            }
            else 
            {
                holder.progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View convertView = null;
        Holder holder = new Holder();
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.home_navigate_gallery_item,
                null);
        holder.navigateImage = (ImageView) convertView
                .findViewById(R.id.navigate_image);
        holder.progressBar = (ProgressBar) convertView
                .findViewById(R.id.load_progress);
        convertView.setTag(holder);
        return convertView;
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

    static class Holder
    {
        ImageView navigateImage;
        ProgressBar progressBar;
    }
}
