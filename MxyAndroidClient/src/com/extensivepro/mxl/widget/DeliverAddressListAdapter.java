package com.extensivepro.mxl.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.android.oss.task.GetServiceTask;
import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.bean.Receiver;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 22, 2013 4:58:20 PM
 * @version V1.3.1
 */
public class DeliverAddressListAdapter extends BaseAdapter
{
    private static final String TAG = DeliverAddressListAdapter.class.getSimpleName();

    private List<Receiver> mReceivers;
    
    private LayoutInflater mInflater;
    
    private Context mmContext;

    static class Holder
    {
        ImageView isDefault;
        TextView deliverAddress;
        TextView deliverName;
        TextView deliverPhoneNumber;
    }
    
    

    public DeliverAddressListAdapter(List<Receiver> mReceivers, Context mContext)
    {
        super();
        mmContext = mContext;
        this.mReceivers = mReceivers;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount()
    {
        return mReceivers != null ? mReceivers.size():0;
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

    public void notifyDataSetChange(List<Receiver> receivers)
    {
        mReceivers = receivers;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Holder holder = null;
        if (convertView == null)
        {
            convertView = mInflater
                    .inflate(R.layout.deliver_address_item, null);
            holder = new Holder();
            holder.deliverAddress = (TextView) convertView
                    .findViewById(R.id.deliver_address);
            holder.deliverName = (TextView) convertView
                    .findViewById(R.id.deliver_name);
            holder.deliverPhoneNumber = (TextView) convertView
                    .findViewById(R.id.deliver_phone_number);
            holder.isDefault = (ImageView) convertView
                    .findViewById(R.id.default_address);
        }
        else 
        {
            holder = (Holder) convertView.getTag();
        }
        Receiver receiver = mReceivers.get(position);
        holder.deliverAddress.setText(mmContext.getString(R.string.delivery_address)+receiver.getAreaStore().getDisplayName());
        holder.deliverName.setText(mmContext.getString(R.string.consignee)+receiver.getName());
        holder.deliverPhoneNumber.setText(mmContext.getString(R.string.photo_number)+receiver.getMobile());
        holder.isDefault.setVisibility(receiver.isDefault()?View.VISIBLE:View.INVISIBLE);
        convertView.setTag(holder);
        return convertView;
    }
}
