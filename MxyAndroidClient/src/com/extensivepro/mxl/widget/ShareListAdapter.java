package com.extensivepro.mxl.widget;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.WeakHashMap;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.bean.ShareItem;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.app.share.ShareManager;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.ImageDownloader.DownloadCallback;

/**
 * @Description
 * @author Admin
 * @date 2013-5-9 下午5:07:23
 * @version V1.3.1
 */

public class ShareListAdapter extends BaseAdapter
{

    private Context context;

    private ImageDownloader mImageDownloader = new ImageDownloader();
//   private ImageDownloader mImageDownloader = ImageDownloader.getInstance();

    private WeakHashMap<Integer,WeakReference<Bitmap>> mImageMapping = new WeakHashMap<Integer,WeakReference<Bitmap>>();

    private LayoutInflater inflater;

    private List<ShareItem> shareItems;

    public List<ShareItem> getShareItems()
    {
        return shareItems;
    }

    public void setShareItems(List<ShareItem> shareItems)
    {
        this.shareItems = shareItems;
        mImageMapping.clear();
    }

    public ShareListAdapter(Context context, List<ShareItem> shareItems)
    {
        super();
        this.context = context;
        this.shareItems = shareItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return shareItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return shareItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View view;
        ShareItem item = shareItems.get(position);
        final ViewHolder holder;
        if (convertView == null)
        {
            view = inflater.inflate(R.layout.share_list_item, null);
            holder = new ViewHolder();
            holder.tv_username = (TextView) view.findViewById(R.id.tv_username);
            holder.tv_post_time = (TextView) view
                    .findViewById(R.id.tv_post_time);
            holder.post_item_content = (TextView) view
                    .findViewById(R.id.post_item_content);
            holder.tv_good = (TextView) view.findViewById(R.id.tv_good);
            holder.post_item_picture = (ImageView) view
                    .findViewById(R.id.post_item_picture);

            holder.like = (ImageView) view
                    .findViewById(R.id.post_item_like_picture);
            view.setTag(holder);
        }
        else
        {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
         String imageUri = item.getImage();
        holder.post_item_picture.setTag(position);
        holder.tv_username.setText(item.getUsername());
        holder.tv_post_time.setText(item.getCreateDate().getGSM8Date());
        holder.post_item_content.setText(item.getContent());
        holder.tv_good.setText(String.valueOf(item.getGood()));
        if(item.isMarkGood())
        {
            holder.like.setBackgroundResource(R.drawable.btn_like_dark);
        }
        else 
        {
            holder.like.setBackgroundResource(R.drawable.btn_like_light);
        }

        if (!TextUtils.isEmpty(imageUri))
        {
            final int maxWidth = ClientManager.getInstance().getScreenWidth()/4;
            Bitmap bmp = mImageDownloader.downloadImage(new DownloadCallback() {

                @Override
                public void onLoadSuccess(Bitmap bitmap)
                {
                    if (bitmap != null)
                    {
                        mImageMapping.put(position, new WeakReference<Bitmap>(
                                bitmap));
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
                }
            }, imageUri, maxWidth);
            
            if(bmp != null)
            {
                mImageMapping.put(position, new WeakReference<Bitmap>(bmp));
            }
            if (mImageMapping != null && mImageMapping.containsKey(position)
                    && mImageMapping.get(position) != null
                    && mImageMapping.get(position).get() != null)
            {
                holder.post_item_picture.setImageBitmap(mImageMapping.get(
                        position).get());
            }
        }

        holder.like.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if (AccountManager.getInstance().hasLogined())
                {
                    if(!shareItems.get(position).isMarkGood())
                    {
                        // 并且把该项目的like喜欢改成2
                        ShareManager.getInstance().goodMessage(
                                shareItems.get(position).getId(), true);
                        shareItems.get(position).setMarkGood(true);
                        shareItems.get(position).setGood(shareItems.get(position).getGood()+1);
                        notifyDataSetChanged();
                    }
                }
                else
                {
                    Toast.makeText(context, R.string.please_login, Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.post_item_picture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                openPopupwin(v);
            }
        });

        return view;
    }

    private PopupWindow popupWindow;

    protected void openPopupwin(View v)
    {
        ImageView mimageView = (ImageView) v;
        Drawable bitmap = mimageView.getDrawable();
        View menuView = (View) inflater.inflate(R.layout.pop_popwindow, null,
                true);
        ImageView imgview = (ImageView) menuView
                .findViewById(R.id.pop_popwindowimage);
        imgview.setPadding(30, 30, 30, 30);

        imgview.setImageDrawable(bitmap);
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (popupWindow != null && popupWindow.isShowing())
                {
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT, true);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.showAtLocation(menuView, Gravity.CENTER | Gravity.CENTER,
                0, 0);
        popupWindow.setFocusable(true);
        // 设置点击其他地方 就消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

    }

    static class ViewHolder
    {
        TextView tv_username;

        TextView tv_post_time;

        TextView post_item_content;

        TextView tv_good;

        ImageView post_item_picture;

        ImageView like;
    }
    
    public void clearCache()
    {
        mImageDownloader.clearCache();
    }

}
