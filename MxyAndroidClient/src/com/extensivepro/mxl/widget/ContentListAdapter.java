package com.extensivepro.mxl.widget;

import java.util.List;

import com.extensivepro.mxl.R;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/**
 * 
 * @Description 收文发文管理列表界面的adapter
 * @author damon
 * @date Apr 17, 2013 10:13:05 AM
 * @version V1.3.1
 */
public class ContentListAdapter extends BaseAdapter
{
    private static final String TAG = ContentListAdapter.class.getSimpleName();

    private Context mContext;

    private List<ContentItem> mContents;

    private LayoutInflater mInflater;

  
    
    public ContentListAdapter(Context context,List<ContentItem> contents)
    {
        super();
        mContext = context;
        mContents = contents;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i=0;i<contents.size();i++)
        {
            ContentItem item = contents.get(i);
            item.setContentCollapse(getClipString(item.getContent(), true));
            if(item.getContent().length() > 200)
            {
                item.setMore("更多");
            }
            else 
            {
                item.setMore("");
            }
        }
    }

    @Override
    public int getCount()
    {
        return (mContents == null )? 0 : mContents.size();
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

    
    private ContentHolder holder = null;
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.content_list_item, null);
            holder = new ContentHolder();
            holder.more = (TextView) convertView
                    .findViewById(R.id.more);
            holder.content = (EllipsizeableTextView) convertView
                    .findViewById(R.id.content);
            holder.title = (TextView) convertView
                    .findViewById(R.id.title);
            holder.date = (TextView) convertView
                    .findViewById(R.id.date);
        }
        else
        {
            holder = (ContentHolder) convertView.getTag();
        }
        final ContentItem item = mContents.get(position);
        holder.more.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                if(!TextUtils.isEmpty(item.getMore()))
                {
                    item.setMore(item.isCollapse()?"更多":"收起");
                }
                item.setCollapse(!item.isCollapse());
                notifyDataSetChanged();
                refreshUnreadMsg(position);
            }
        });
        holder.more.setText(item.getMore());
        holder.content.setText(!item.isCollapse()?item.getContentCollapse():item.getContent());
        if(!item.isUnread())
        {
            holder.content.setTextColor(Color.GRAY);
        }
        else 
        {
            holder.content.setTextColor(Color.BLACK);
        }
        holder.title.setText(item.getTitle());
        holder.date.setText(item.getDate());
        convertView.setTag(holder);
        return convertView;
    }
    
    public void allRead()
    {
        if(mContents == null || mContents.isEmpty())
        {
            return;
        }
        for(int i=0;i<mContents.size();i++)
        {
            mContents.get(i).setUnread(false);
        }
        notifyDataSetChanged();

    }
    
    public void refreshUnreadMsg(int position)
    {
        if(mContents == null || mContents.isEmpty())
        {
            return;
        }
        if(position != -1 && position <mContents.size() && mContents.get(position).isUnread())
        {
            mContents.get(position).setUnread(false);
        }
        notifyDataSetChanged();
    }
    

    private String getClipString(String str,boolean needClip)
    {
        
        if(TextUtils.isEmpty(str))
        {
            return "";
        }
        if(str.length()>200 && needClip)
        {
            str = str.substring(0, 200)+"...";
        }
        return str;
    }
    
    public void detialInfo(int pos, String title)
    {
//        Intent intent = null;
//        intent = new Intent(mContext, SendReceiveManageDetialActivity.class);
//        intent.putExtra(Const.EXTRA_STRING_SEND_RECEIVE_TYPE,
//                getSendReceiveType(title));
//        intent.putExtra(Const.EXTRA_STRING_SEND_RECEIVE_TITLE, title);
//        mContext.startActivity(intent);
    }

//    private int getSendReceiveType(String title)
//    {
//        if (TextUtils.isEmpty(title))
//            return -1;
//        if(mItemType == Const.EXTRA_STRING_SEND_RECEIVE_TYPE_RECEIVE)
//        {
//            if (title.equals(mContext.getResources()
//                    .getText(R.string.receive_over_manage).toString()))
//                return Const.EXTRA_STRING_SEND_RECEIVE_TYPE_RECEIVE_OVER_MANAGE;
//            if (title.equals(mContext.getResources()
//                    .getText(R.string.receive_already_manage).toString()))
//                return Const.EXTRA_STRING_SEND_RECEIVE_TYPE_RECEIVE_ALREADY_MANAGE;
//            if (title.equals(mContext.getResources()
//                    .getText(R.string.receive_wait_manage).toString()))
//                return Const.EXTRA_STRING_SEND_RECEIVE_TYPE_RECEIVE_WAIT_MANAGE;
//        }
//        else if(mItemType == Const.EXTRA_STRING_SEND_RECEIVE_TYPE_SEND)
//        {
//            if (title.equals(mContext.getResources()
//                    .getText(R.string.send_over_manage).toString()))
//                return Const.EXTRA_STRING_SEND_RECEIVE_TYPE_SEND_OVER_MANAGE;
//            if (title.equals(mContext.getResources()
//                    .getText(R.string.send_already_manage).toString()))
//                return Const.EXTRA_STRING_SEND_RECEIVE_TYPE_SEND_ALREADY_MANAGE;
//            if (title.equals(mContext.getResources()
//                    .getText(R.string.send_wait_manage).toString()))
//                return Const.EXTRA_STRING_SEND_RECEIVE_TYPE_SEND_WAIT_MANAGE;
//        }
//        return -1;
//    }

    static class ContentHolder
    {
        TextView title;
        EllipsizeableTextView content;
        TextView more;
        TextView date;
    }
}
