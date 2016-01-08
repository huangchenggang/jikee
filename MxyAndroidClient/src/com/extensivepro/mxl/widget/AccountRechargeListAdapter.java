package com.extensivepro.mxl.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.bean.DepositCard;
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
public class AccountRechargeListAdapter extends BaseAdapter
{
    private static final String TAG = AccountRechargeListAdapter.class
            .getSimpleName();


    private LayoutInflater mInflater;

    private ImageDownloader mImageDownloader;

    private SparseArray<Bitmap> mImageMapping;

    private List<DepositCard> mContents;

    public AccountRechargeListAdapter(Context context,
            List<DepositCard> contents)
    {
        mImageDownloader = new ImageDownloader();
//        mImageDownloader = ImageDownloader.getInstance();
        mImageMapping = new SparseArray<Bitmap>();
        mContents = contents;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public void notifyDataSetChanged(List<DepositCard> contents)
    {
        mContents = contents;
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return mContents == null ? 0 : mContents.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mContents == null ? null : mContents.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    static class Holder
    {
        ImageView image;

        TextView desc;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        Holder holder = null;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.account_recharge_item,
                    null);
            holder = new Holder();
            holder.image = (ImageView) convertView
                    .findViewById(R.id.account_recharge_item_image);
            holder.desc = (TextView) convertView
                    .findViewById(R.id.account_recharge_item_desc);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        if(mContents.size()==1)
        {
            convertView.setBackgroundResource(R.drawable.account_item_normal_selector);
        }
        else if(mContents.size() == 2 && position == 0)
        {
            convertView.setBackgroundResource(R.drawable.account_item_top_selector);
        }
        else if(mContents.size() == 2 && position == 1)
        {
            convertView.setBackgroundResource(R.drawable.account_item_bottom_selector);
        }
        else if(mContents.size() > 2 && position == 0)
        {
            convertView.setBackgroundResource(R.drawable.account_item_top_selector);
        }
        else if(mContents.size() > 2 && (position > 0 && position < mContents.size()-1))
        {
            convertView.setBackgroundResource(R.drawable.account_item_center_selector);
        }
        else if(mContents.size() > 2 && (position == mContents.size()-1))
        {
            convertView.setBackgroundResource(R.drawable.account_item_bottom_selector);
        }
        mImageDownloader.downloadImage(new DownloadCallback() {

            @Override
            public void onLoadSuccess(Bitmap bitmap)
            {
                Logger.d(TAG, "downloadImage.onLoadSuccess()[bmp:" + bitmap
                        + "]");
                if (bitmap != null)
                {
                    mImageMapping.put(position, bitmap);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onLoadFailed()
            {
                if (mImageMapping.indexOfKey(position) != -1
                        && mImageMapping.get(position) != null)
                {
                    mImageMapping.put(position, null);
                    notifyDataSetChanged();
                }
                Logger.e(TAG, "onLoadFailed()[access]");
            }
        }, mContents.get(position).getImagePath(), 93);

        if (mImageMapping != null && mImageMapping.indexOfKey(position) != -1
                && mImageMapping.get(position) != null)
        {
            // holder.progressBar.setVisibility(View.GONE);
            // holder.image.setVisibility(View.VISIBLE);
            holder.image.setImageBitmap(mImageMapping.get(position));
        }
        else
        {
            // holder.progressBar.setVisibility(View.VISIBLE);
            // holder.image.setVisibility(View.GONE);
        }
        holder.desc.setText(mContents.get(position).getName());
        convertView.setTag(holder);
        return convertView;
    }
    public void clearCache()
    {
        for(int i=0;i<mImageMapping.size();i++){
            Bitmap bitmap = mImageMapping.get(i);
            if(bitmap!=null&&!bitmap.isRecycled()){
                bitmap.recycle();
            }
        }
        mImageDownloader.clearCache();
    }
}
