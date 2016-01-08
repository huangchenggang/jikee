package com.extensivepro.mxl.widget;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.bean.Templates;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.ImageDownloader.DownloadCallback;

public class TemplateGroupAdapter extends BaseAdapter
{
    private List<Templates> lists;

    private int mSelectPos = -1;
    
    private LayoutInflater inflater;

    private ImageDownloader mImageDownloader = new ImageDownloader();
//    private ImageDownloader mImageDownloader = ImageDownloader.getInstance();
    private SparseArray<WeakReference<Bitmap>> mImageMapping = new SparseArray<WeakReference<Bitmap>>();

    public TemplateGroupAdapter(Context context, List<Templates> lists)
    {
        super();
        this.lists = lists;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setViewSelect(int position)
    {
        mSelectPos = position;
    }

    @Override
    public int getCount()
    {

        return lists.size();
    }

    @Override
    public Object getItem(int position)
    {

        return lists.get(position);
    }

    @Override
    public long getItemId(int position)
    {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHoler holder = null;
        if(convertView == null)
        {
            holder = new ViewHoler();
            convertView = inflater.inflate(R.layout.templategroup_grid_item, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.group_item_iv);
            holder.tv = (TextView) convertView.findViewById(R.id.group_item_tv);
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb_item_status);
            convertView.setTag(holder);
        }
        else 
        {
            holder = (ViewHoler) convertView.getTag();
        }
        Templates templates = lists.get(position);
        holder.tv.setText(templates.getName());
        Bitmap bmp = null;
        if (mImageMapping != null && mImageMapping.indexOfKey(position) != -1
                && mImageMapping.get(position) != null && mImageMapping.get(position).get() != null)
        {
            bmp = mImageMapping.get(position).get();
        }
        if(bmp == null)
        {
            String iconUrl = Const.BASE_URI + templates.getImage();
            if (!TextUtils.isEmpty(iconUrl))
            {
                final int maxWidth = ClientManager.getInstance()
                        .getScreenWidth() / 6;
                bmp = mImageDownloader.downloadImage(new DownloadCallback() {

                    @Override
                    public void onLoadSuccess(Bitmap bitmap)
                    {
                        if (bitmap != null)
                        {
                            mImageMapping.put(position,
                                    new WeakReference<Bitmap>(bitmap));
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
                    }
                }, iconUrl, maxWidth);

                if (bmp != null)
                {
                    mImageMapping.put(position, new WeakReference<Bitmap>(bmp));
                }
            }

        }
        if (bmp != null)
        {
            holder.iv.setImageBitmap(bmp);
        }
        if(mSelectPos == position)
        {
            holder.cb.setChecked(true);
        }
        else 
        {
            holder.cb.setChecked(false);
        }
        return convertView;
    }

    public Templates backdata(int arg2)
    {
        return (Templates) getItem(arg2);
    }

    static class ViewHoler
    {
        ImageView iv;

        CheckBox cb;

        TextView tv;
    }
    
    public void clearCache()
    {
        mImageDownloader.clearCache();
//        synchronized(mImageMapping)
//        {
//            if (mImageMapping!=null&&mImageMapping.size()!=0)
//            {
//             
//                for (int i = 0; i < mImageMapping.size(); i++)
//                {
//                    if (mImageMapping.get(i) != null
//                            && mImageMapping.get(i).get() != null
//                            && !mImageMapping.get(i).get().isRecycled())
//                    {
//                        mImageMapping.get(i).get().recycle();
//                        System.gc();
//                        mImageMapping.remove(i);
//                    }
//                }
//                mImageMapping.clear();
//            }
//        }
    }
    
    public void pauseDownload()
    {
        mImageDownloader.pauseDownload();
    }
    
    public void resumeDownload()
    {
        mImageDownloader.resumeDownload();
    }

    

}
