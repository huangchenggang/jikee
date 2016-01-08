package com.extensivepro.mxl.widget;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.bean.CartItem;
import com.extensivepro.mxl.app.bean.Goods;
import com.extensivepro.mxl.app.bean.ImageStore;
import com.extensivepro.mxl.app.bean.Product;
import com.extensivepro.mxl.app.cart.CartManager;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.ImageDownloader.DownloadCallback;
import com.extensivepro.mxl.util.Logger;

public class ShoppingTrolleryListAdapter extends BaseAdapter
{

    private static final String TAG = "ShoppingTrolleryListAdapter";
    
    private LayoutInflater mInflater;
    private List<CartItem> mCartItems;
    
    private ImageDownloader mImageDownloader;
    
    private Context mContext;
    
    private WeakHashMap<Integer,WeakReference<Bitmap>> mImageMapping;
    
    public ShoppingTrolleryListAdapter(Context context,List<CartItem> mCartItems)
    {
        super();
        mContext = context;
        this.mCartItems = mCartItems;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageDownloader = new ImageDownloader();
//        mImageDownloader = ImageDownloader.getInstance();
        mImageMapping = new WeakHashMap<Integer, WeakReference<Bitmap>>();
    }
    
    public void notifyDataChanged(List<CartItem> cartItems)
    {
        mCartItems = cartItems;
        notifyDataSetChanged();
    }
    
    public void notifyDataChanged(int index,CartItem item)
    {
        mCartItems.remove(index);
        mCartItems.add(index, item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return mCartItems == null ? 0 : mCartItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mCartItems == null ? null : mCartItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    public int getCartItemTotalCount()
    {
        if(mCartItems == null || mCartItems.isEmpty())
            return 0;
        int totalCount = 0;
        for(int i=0;i<mCartItems.size();i++)
        {
            totalCount += mCartItems.get(i).getQuantity();
        }
        return totalCount;
    }
    
    private Holder mHolder;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            mHolder = new Holder();
            convertView = mInflater.inflate(
                    R.layout.shopping_trolley_list_item, null);

            mHolder.goodsCategoryName = (TextView) convertView
                    .findViewById(R.id.goods_category_name);
            mHolder.goodsName = (TextView) convertView
                    .findViewById(R.id.goods_name);
            mHolder.desc = (TextView) convertView.findViewById(R.id.goods_desc);

            mHolder.image = (ImageView) convertView
                    .findViewById(R.id.goods_image);
            mHolder.count = (EditText) convertView.findViewById(R.id.count);
            mHolder.sendDate = (TextView) convertView
                    .findViewById(R.id.send_date);
            mHolder.price = (TextView) convertView.findViewById(R.id.price);
            mHolder.itemRoot = convertView.findViewById(R.id.item_root);
        }
        else
        {
            mHolder = (Holder) convertView.getTag();
        }

        final CartItem item = mCartItems.get(position);
        mHolder.goodsName.setText(item.getGoodsName());
        mHolder.goodsCategoryName.setText(item.getGoodsCategoryName());
        if (item.getProduct() != null)
        {
            Product product = item.getProduct();
            double price = product.getPrice();
            int count = item.getQuantity();
            mHolder.desc.setText(product.getGoodsDesc());
            mHolder.count.setText(String.valueOf(count));
            mHolder.price.setText(String.valueOf(price * count));

            String imageUri = item.getProductImage();
            if (!TextUtils.isEmpty(imageUri))
            {
                Logger.d(TAG, "getView()[imageUri:" + imageUri + "]");
                final int maxWidth = ClientManager.getInstance()
                        .getScreenWidth() / 5;
                Bitmap bmp = mImageDownloader.downloadImage(
                        new DownloadCallback() {

                            @Override
                            public void onLoadSuccess(Bitmap bitmap)
                            {
                                if (bitmap != null)
                                {
                                    if (bitmap != null)
                                    {
                                        mImageMapping.put(position,
                                                new WeakReference<Bitmap>(
                                                        bitmap));
                                        notifyDataSetChanged();
                                    }
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
                        }, imageUri, maxWidth);
                if (bmp != null)
                {
                    mHolder.image.setImageBitmap(bmp);
                    mImageMapping.put(position, new WeakReference<Bitmap>(bmp));
                }

                if (mImageMapping != null
                        && mImageMapping.containsKey(position)
                        && mImageMapping.get(position) != null)
                {
                    if (mImageMapping.get(position) != null)
                    {
                        mHolder.image.setImageBitmap(mImageMapping
                                .get(position).get());
                    }
                }

                mHolder.count
                        .setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus)
                            {
                                try
                                {
                                    int newCount = Integer
                                            .parseInt(mHolder.count.getText()
                                                    .toString());
                                    if (!hasFocus
                                            && newCount != item.getQuantity())
                                    {
                                        CartManager.getInstance().editCartItem(
                                                item.getId(),
                                                mHolder.count.getText()
                                                        .toString());
                                    }
                                }
                                catch (NumberFormatException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
                mHolder.itemRoot
                        .setOnLongClickListener(new OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v)
                            {
                                Logger.d(TAG, "onLongClick()[]");
                                showDeleteCartItemDialog(position);
                                return true;
                            }
                        });
            }
        }
        convertView.setTag(mHolder);
        return convertView;
    }
    
    private void  showDeleteCartItemDialog(int position)
    {
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.toast_delete_cart_item);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setNeutralButton(R.string.confirm, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String cartItemId = mCartItems.get(pos).getId();
                CartManager.getInstance().delCartItem(cartItemId);
            }
        });
        builder.create().show();
    }

    
    static class Holder
    {
        View itemRoot;
        ImageView image;
        EditText count;
        TextView goodsName;
        TextView goodsCategoryName;
        TextView desc;
        TextView sendDate;
        TextView price;
    }
    
    public void clearCache()
    {
        mImageDownloader.clearCache();
        
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

}
