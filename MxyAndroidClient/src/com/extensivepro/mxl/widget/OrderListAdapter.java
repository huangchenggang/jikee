package com.extensivepro.mxl.widget;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.bean.Order;
import com.extensivepro.mxl.app.bean.Order.ShippingStatus;
import com.extensivepro.mxl.app.bean.OrderItem;
import com.extensivepro.mxl.app.bean.PaymentConfig;
import com.extensivepro.mxl.app.cart.CartManager;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.ImageDownloader.DownloadCallback;
import com.extensivepro.mxl.util.Logger;

/** 
 * @Description 
 * @author Admin
 * @date 2013-5-14 下午5:09:30 
 * @version V1.3.1
 */

public class OrderListAdapter extends BaseAdapter
{
    private static final String TAG = OrderListAdapter.class.getSimpleName();

    private LayoutInflater mInflater;

    private boolean mIsUnpaid;
    
    private List<Order> mOrders;
    
    private Context mContext;
    
    private HashMap<String, View> mOrderItemCache;
    private HashMap<String, WeakReference<Bitmap>> mOrderItemImageMapping;
    
    private ImageDownloader mImageDownloader;
    
    public OrderListAdapter(Context context,List<Order> orders,boolean isUnpaid)
    {
        super();
         mImageDownloader = new ImageDownloader();
//        mImageDownloader = ImageDownloader.getInstance();
        mIsUnpaid = isUnpaid;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mOrders = orders;
        mContext = context;
        mOrderItemCache = new HashMap<String, View>();
        mOrderItemImageMapping = new HashMap<String, WeakReference<Bitmap>>();
    }
    
    public void notifyListType(boolean isUnpaid)
    {
        mIsUnpaid = isUnpaid;
        mOrderItemCache.clear();
        notifyDataSetChanged();
    }

    public void notifyDateSetChanged(List<Order> orders)
    {
        mOrders = orders;
        mOrderItemCache.clear();
        notifyDataSetChanged();
    }
    
    @Override
    public int getCount()
    {
        return mOrders == null ? 0:mOrders.size();
    }

    @Override
    public Object getItem(int arg0)
    {
        return mOrders == null ? null : mOrders.get(arg0);
    }


    @Override
    public long getItemId(int arg0)
    {
        return arg0;
    }


    
    OrderListItemHolder holder;
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = mInflater.inflate(R.layout.order_list_item, null);
            holder = new OrderListItemHolder();
            holder.orderDealDate = (TextView) convertView.findViewById(R.id.order_deal_date);
            holder.orderSn = (TextView) convertView.findViewById(R.id.order_sn);
            holder.orderReceiver = (TextView) convertView.findViewById(R.id.order_receiver);
            holder.orderDeliveryAddressMain = (TextView) convertView.findViewById(R.id.order_delivery_address_main);
            holder.orderDeliveryAddressDetial = (TextView) convertView.findViewById(R.id.order_delivery_address_detial);
            holder.orderTotalPayment = (TextView) convertView.findViewById(R.id.order_total_payment);
            holder.expressCost = (TextView) convertView.findViewById(R.id.express_cost);
            holder.orderUnshipInfo = (ImageView) convertView.findViewById(R.id.order_unship_info);
            holder.orderGoodsItemLayout = (LinearLayout) convertView.findViewById(R.id.order_goods_item_layout);
            holder.orderItemOptBtnLayout = (LinearLayout) convertView.findViewById(R.id.order_item_opt_btn_layout);
        }
        else 
        {
            holder = (OrderListItemHolder) convertView.getTag();
        }
        convertView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        if(mIsUnpaid)
        {
            holder.orderItemOptBtnLayout.setVisibility(View.VISIBLE);
            holder.orderItemOptBtnLayout.findViewById(R.id.give_up_order)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            Logger.d(TAG, "holder.onClick()[invalidOrder]");
                            showInvalidOrderDialog(position);
                        }
                    });
            holder.orderItemOptBtnLayout.findViewById(R.id.confirm_payment)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            Logger.d(TAG, "holder.onClick()[confirm_payment]");
                            List<PaymentConfig> paymentConfigs = CartManager.getInstance().getPaymentConfigs();
                            PaymentConfig paymentConfig = null;
                            if(paymentConfigs != null && !paymentConfigs.isEmpty())
                            {
                                for(PaymentConfig config:paymentConfigs)
                                {
                                    if(config!= null && config.getId().equals(mOrders.get(position).getPaymentConfig().getId()))
                                    {
                                        paymentConfig = config;
                                        break;
                                    }
                                }
                            }
                            showPayOrderDialog(position,paymentConfig);
                        }
                    });
        }
        else
        {
            holder.orderItemOptBtnLayout.setVisibility(View.GONE);
        }
        Order order = mOrders.get(position);
        Logger.d(TAG, "getView()[order:"+order+"]");
        Logger.d(TAG, "getView()[order date:"+order.getCreateDate().getGSM8Date()+"]");
        holder.orderDealDate.setText(mContext.getResources().getString(
                R.string.deal_date, order.getCreateDate().getGSM8Date()));
        holder.orderSn.setText(mContext.getResources().getString(
                R.string.order_num, order.getOrderSn()));
//        holder.alipayOrderSn.setVisibility(View.GONE);
        holder.orderReceiver.setText(mContext.getResources().getString(
                R.string.order_receiver, order.getShipName()));
        holder.orderDeliveryAddressMain.setText(order.getShipAreaStore().getDisplayName());
        holder.orderDeliveryAddressDetial.setText(order.getShipAddress());
        holder.orderTotalPayment.setText(mContext.getResources().getString(
                R.string.order_total_pay_price,order.getTotalAmount()));
        holder.expressCost.setText(mContext.getResources().getString(
                R.string.order_express_fee,order.getDeliveryFee()));
        if(!mIsUnpaid)
        {
            holder.orderUnshipInfo
            .setVisibility((order.getShippingStatus() == ShippingStatus.unshipped) ? View.VISIBLE
                    : View.GONE);
        }
        else 
        {
            holder.orderUnshipInfo.setVisibility(View.GONE);
        }
        if(mOrderItemCache.isEmpty())
        {
            holder.orderGoodsItemLayout.removeAllViews();
        }
        holder.orderGoodsItemLayout.removeAllViews();
        if(order.getOrderItemSet() != null)
        {
            for(int i=0;i<order.getOrderItemSet().size();i++)
            {
                OrderItem item = order.getOrderItemSet().get(i);
                if (item == null)
                {
                    continue;
                }
                View goodsItemView = null;
                if (mOrderItemCache.containsKey(item.getId()))
                {
                    goodsItemView = mOrderItemCache.get(item.getId());
                }
                else
                {
//                    goodsItemView = mInflater.inflate(
//                            R.layout.order_list_goods_item, null);
//                    holder.orderGoodsItemLayout.addView(goodsItemView);
                }
                goodsItemView = mInflater.inflate(
                        R.layout.order_list_goods_item, null);
                holder.orderGoodsItemLayout.addView(goodsItemView);
                mOrderItemCache.put(item.getId(), goodsItemView);
                ((TextView) (goodsItemView
                        .findViewById(R.id.order_goods_item_name)))
                        .setText(item.getGoodsName());
                ((TextView) (goodsItemView
                        .findViewById(R.id.order_goods_item_category_name)))
                        .setText(item.getGoodsCategoryName());
                ((TextView) (goodsItemView
                        .findViewById(R.id.order_goods_item_count)))
                        .setText(String.valueOf(item.getProductQuantity()));
                ((TextView) (goodsItemView
                        .findViewById(R.id.order_goods_payment)))
                        .setText(mContext.getResources().getString(
                                R.string.order_goods_payment_txt,
                                String.valueOf(item.getProductQuantity()
                                        * item.getProductPrice())));
                if (item.getProduct() != null)
                {
                    ((TextView) (goodsItemView
                            .findViewById(R.id.order_goods_item_desc)))
                            .setText(item.getProduct().getGoodsDesc());
                }
                setImage((ImageView) goodsItemView.findViewById(R.id.image),
                        item);
            }
            setViewHeightBasedOnChildren((LinearLayout) convertView);
        }
        convertView.setTag(holder);
        return convertView;
    }
    
    
    private void setViewHeightBasedOnChildren(LinearLayout group) {
        if(group == null)
        {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < group.getChildCount(); i++) {
           View listItem = group.getChildAt(i);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                LayoutParams.MATCH_PARENT, totalHeight + 100);
        group.setLayoutParams(params);
    }
    
    private void setImage(ImageView image,final OrderItem orderItem)
    {
        if(image == null || orderItem == null)
        {
            return;
        }
        final String itemId = orderItem.getId();
        Bitmap bmp = null;
        if(mOrderItemImageMapping.containsKey(itemId) && mOrderItemImageMapping.get(itemId) != null && 
                mOrderItemImageMapping.get(itemId).get() != null)
        {
            bmp = mOrderItemImageMapping.get(itemId).get();
        }
        if(bmp != null)
        {
            image.setImageBitmap(bmp);
        }
        else 
        {
            final int maxWidth = ClientManager.getInstance().getScreenWidth() / 6;
            bmp = mImageDownloader.downloadImage(
                    new DownloadCallback() {

                        @Override
                        public void onLoadSuccess(Bitmap bitmap)
                        {
                            Logger.d(TAG, "downloadImage.onLoadSuccess()");
                            if (bitmap != null)
                            {
                                mOrderItemImageMapping.put(itemId,
                                        new WeakReference<Bitmap>(bitmap));
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onLoadFailed()
                        {
                            if (mOrderItemImageMapping.containsKey(itemId))
                            {
                                mOrderItemImageMapping.put(orderItem.getId(),
                                        null);
                                notifyDataSetChanged();
                            }
                            Logger.e(TAG, "onLoadFailed()");
                        }
                    }, orderItem.getProductImage(), maxWidth);
            if(bmp != null)
            {
                mOrderItemImageMapping.put(itemId, new WeakReference<Bitmap>(bmp));
                image.setImageBitmap(bmp);
            }
        }
    }
    
    private void  showInvalidOrderDialog(int position)
    {
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.dialog_title_invalid_order);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setNeutralButton(R.string.confirm, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                CartManager.getInstance().invalidOrder(
                        mOrders.get(pos).getId());
            }
        });
        builder.create().show();
    }
    
    private void showPayOrderDialog(int position,PaymentConfig paymentConfig)
    {
        final int pos = position;
        final PaymentConfig paymentCfg = paymentConfig;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.dialog_title_confirm_pay_order);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setNeutralButton(R.string.confirm, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch(paymentCfg.getPaymentConfigType())
                {
                    case mobile:
                        // 支付宝支付
                        CartManager.getInstance().payOrder(
                                mOrders.get(pos).getId());
                        break;
                    case deposit:
                        // 预存款支付
                        CartManager.getInstance().payOrder(
                                mOrders.get(pos).getId());
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }
    
    static class OrderListItemHolder
    {
        TextView orderDealDate;
        TextView orderSn;
        TextView alipayOrderSn;
        TextView orderReceiver;
        TextView orderDeliveryAddressMain;
        TextView orderDeliveryAddressDetial;
        TextView orderTotalPayment;
        TextView expressCost;
        ImageView orderUnshipInfo;
        LinearLayout orderGoodsItemLayout;
        LinearLayout orderItemOptBtnLayout;
    }

    public void clearCache()
    {
        mImageDownloader.clearCache();
        synchronized(mOrderItemImageMapping)
        {
            if (!mOrderItemImageMapping.isEmpty())
            {
                Set<String> key = mOrderItemImageMapping.keySet();
                for (int i = 0; i < key.size(); i++)
                {
                    if (mOrderItemImageMapping.get(key) != null
                            && mOrderItemImageMapping.get(key).get() != null
                            && !mOrderItemImageMapping.get(key).get().isRecycled())
                    {
                        mOrderItemImageMapping.get(key).get().recycle();
                        System.gc();
                        mOrderItemImageMapping.remove(key);
                    }
                }
                mOrderItemImageMapping.clear();
            }
        }
    }
}
