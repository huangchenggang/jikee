package com.extensivepro.mxl.widget;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.bean.DataItem;
import com.extensivepro.mxl.util.Const;

public class SeclectPhotoStyleAdapter extends BaseAdapter
{
    private String productName;

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }
    
    private int mSelectPos = -1;

    private HashMap<Integer, Boolean> isSelected;

    private LayoutInflater inflater;

    private ArrayList<DataItem> lists;
    
    private Context mContext;

    private HashMap<Integer, SoftReference<Bitmap>> mImageMapping;
    
    public SeclectPhotoStyleAdapter(Context context, String productName)
    {
        super();
        this.productName = productName;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mImageMapping = new LinkedHashMap<Integer, SoftReference<Bitmap>>();
        init();
    }

    public void setViewSelect(int position)
    {
        mSelectPos = position;
        isSelected.put(position, true);
    }

    // 初始化 设置所有checkbox都为未选择
    public void init()
    {
        initViewRes();
        setAllCheckboxs();

    }

    private Boolean isShow = true;// listview是否显示图片，默认显示

    /**
     * @Description 初始化资源
     * @author Administrator
     */
    private void initViewRes()
    {
        lists = new ArrayList<DataItem>();
        if (Const.Str_Moduel.heart_balloon.equals(productName) || Const.Str_Moduel.star_balloon.equals(productName)
                || Const.Str_Moduel.round_balloon.equals(productName))
        {
//            isShow = false;
            if (Const.Str_Moduel.heart_balloon.equals(productName))
            {
                lists.add(new DataItem("18cm" + productName,
                        R.drawable.love_18cm, null));
                lists.add(new DataItem("28cm" + productName,
                        R.drawable.love_28cm, null));
            }
            if (Const.Str_Moduel.star_balloon.equals(productName))
            {
                lists.add(new DataItem("18cm" + productName,
                        R.drawable.start_18cm, null));
                lists.add(new DataItem("28cm" + productName,
                        R.drawable.start_28cm, null));
            }
            if (Const.Str_Moduel.round_balloon.equals(productName))
            {
                lists.add(new DataItem("18cm" + productName,
                        R.drawable.circle_18, null));
                lists.add(new DataItem("28cm" + productName,
                        R.drawable.circle_28, null));
            }
        }
        else if (mContext.getString(R.string.square).equals(productName) || mContext.getString(R.string.heart_shaped).equals(productName))//方形、心形
        {
            if (mContext.getString(R.string.square).equals(productName))//方形
            {
                lists.add(new DataItem(mContext.getString(R.string.peachblossom), R.drawable.pic_small_square_001,
                        mContext.getString(R.string.small_square)));//桃红色        小方形32x40mm
                lists.add(new DataItem(mContext.getString(R.string.colour_yellow), R.drawable.pic_small_square_002,
                        mContext.getString(R.string.small_square)));//黄色
                lists.add(new DataItem(mContext.getString(R.string.colour_blue), R.drawable.pic_small_square_003,
                        mContext.getString(R.string.small_square)));//蓝色
                lists.add(new DataItem(mContext.getString(R.string.colour_big_red), R.drawable.pic_small_square_004,
                        mContext.getString(R.string.small_square)));//大红色
                lists.add(new DataItem(mContext.getString(R.string.colour_green), R.drawable.pic_small_square_005,
                        mContext.getString(R.string.small_square)));//绿色
                lists.add(new DataItem(mContext.getString(R.string.colour_transparent), R.drawable.pic_small_square_006,
                        mContext.getString(R.string.small_square)));//透明色
                lists.add(new DataItem(mContext.getString(R.string.colour_symphony_of_silver), R.drawable.pic_small_square_007,
                        mContext.getString(R.string.small_square)));//幻彩银
                lists.add(new DataItem(mContext.getString(R.string.colour_magic_winnings), R.drawable.pic_small_square_008,
                        mContext.getString(R.string.small_square)));//幻彩金
                lists.add(new DataItem(mContext.getString(R.string.colour_huan_cai_hong), R.drawable.pic_small_square_009,
                        mContext.getString(R.string.small_square)));//幻彩红
                lists.add(new DataItem(mContext.getString(R.string.colour_huan_cai_hei), R.drawable.pic_small_square_010,
                        mContext.getString(R.string.small_square)));//幻彩黑
                lists.add(new DataItem(mContext.getString(R.string.hide), 1, mContext.getString(R.string.big_square)));//隐藏
                lists.add(new DataItem(mContext.getString(R.string.peachblossom), R.drawable.pic_big_square_001,
                        mContext.getString(R.string.big_square)));//桃红色
                lists.add(new DataItem(mContext.getString(R.string.colour_yellow), R.drawable.pic_big_square_002,
                        mContext.getString(R.string.big_square)));//黄色
                lists.add(new DataItem(mContext.getString(R.string.colour_blue), R.drawable.pic_big_square_003,
                        mContext.getString(R.string.big_square)));//蓝色
                lists.add(new DataItem(mContext.getString(R.string.colour_big_red), R.drawable.pic_big_square_004,
                        mContext.getString(R.string.big_square)));//大红色
                lists.add(new DataItem(mContext.getString(R.string.colour_green), R.drawable.pic_big_square_005,
                        mContext.getString(R.string.big_square)));//绿色
                lists.add(new DataItem(mContext.getString(R.string.colour_transparent), R.drawable.pic_big_square_006,
                        mContext.getString(R.string.big_square)));//透明色
            }
            if (mContext.getString(R.string.heart_shaped).equals(productName))//心形
            {
                lists.add(new DataItem(mContext.getString(R.string.peachblossom), R.drawable.pic_small_heart_001,
                        mContext.getString(R.string.small_heart_shaped)));//桃红色
                lists.add(new DataItem(mContext.getString(R.string.colour_yellow), R.drawable.pic_small_heart_002,
                        mContext.getString(R.string.small_heart_shaped)));//黄色
                lists.add(new DataItem(mContext.getString(R.string.colour_blue), R.drawable.pic_small_heart_003,
                        mContext.getString(R.string.small_heart_shaped)));//蓝色
                lists.add(new DataItem(mContext.getString(R.string.colour_big_red), R.drawable.pic_small_heart_004,
                        mContext.getString(R.string.small_heart_shaped)));//大红色
                lists.add(new DataItem(mContext.getString(R.string.colour_green), R.drawable.pic_small_heart_005,
                        mContext.getString(R.string.small_heart_shaped)));//绿色
                lists.add(new DataItem(mContext.getString(R.string.colour_transparent), R.drawable.pic_small_heart_006,
                        mContext.getString(R.string.small_heart_shaped)));//透明色
                lists.add(new DataItem(mContext.getString(R.string.hide), 1, mContext.getString(R.string.big_heart_shaped)));//隐藏

                lists.add(new DataItem(mContext.getString(R.string.peachblossom), R.drawable.pic_big_heart_001,
                        mContext.getString(R.string.big_heart_shaped)));//桃红色
                lists.add(new DataItem(mContext.getString(R.string.colour_yellow), R.drawable.pic_big_heart_002,
                        mContext.getString(R.string.big_heart_shaped)));//黄色
                lists.add(new DataItem(mContext.getString(R.string.colour_blue), R.drawable.pic_big_heart_003,
                        mContext.getString(R.string.big_heart_shaped)));//蓝色
                lists.add(new DataItem(mContext.getString(R.string.colour_big_red), R.drawable.pic_big_heart_004,
                        mContext.getString(R.string.big_heart_shaped)));//大红色
                lists.add(new DataItem(mContext.getString(R.string.colour_green), R.drawable.pic_big_heart_005,
                        mContext.getString(R.string.big_heart_shaped)));//绿色
                lists.add(new DataItem(mContext.getString(R.string.colour_transparent), R.drawable.pic_big_heart_006,
                        mContext.getString(R.string.big_heart_shaped)));//透明色

            }
        }
        else if (mContext.getString(R.string.transparent_box_desk_calendar).equals(productName) || mContext.getString(R.string.paper_desk_calendar).equals(productName))//透明盒装台历    纸质台历
        {
            if (mContext.getString(R.string.transparent_box_desk_calendar).equals(productName))//透明盒装台历
            {
                lists.add(new DataItem(mContext.getString(R.string.four_multiply_six), R.drawable.pic_calendar_4x6_001,
                        productName));
                lists.add(new DataItem(mContext.getString(R.string.eight_multiply_ten_A4),
                        R.drawable.pic_calendar_8x10_001, productName));
            }
            if (mContext.getString(R.string.paper_desk_calendar).equals(productName))//纸质台历
            {
                lists.add(new DataItem(mContext.getString(R.string.colour_blue), R.drawable.paper_calendar_007,
                        productName + mContext.getString(R.string.four_multiply_six)));//蓝色
                lists.add(new DataItem(mContext.getString(R.string.colour_green), R.drawable.paper_calendar_008,
                        productName + mContext.getString(R.string.four_multiply_six)));//绿色
                lists.add(new DataItem(mContext.getString(R.string.colour_yellow), R.drawable.paper_calendar_009,
                        productName + mContext.getString(R.string.four_multiply_six)));//黄色
                lists.add(new DataItem(mContext.getString(R.string.colour_orange), R.drawable.paper_calendar_010,
                        productName + mContext.getString(R.string.four_multiply_six)));//橙色
                lists.add(new DataItem(mContext.getString(R.string.colour_white), R.drawable.paper_calendar_011,
                        productName + mContext.getString(R.string.four_multiply_six)));//白色
                lists.add(new DataItem(mContext.getString(R.string.colour_black), R.drawable.paper_calendar_012,
                        productName + mContext.getString(R.string.four_multiply_six)));//黑色
                lists.add(new DataItem(mContext.getString(R.string.hide), 1, mContext.getString(R.string.paper_desk_calendar)+mContext.getString(R.string.five_multiply_eight)));//隐藏   纸质台历5x8寸
                lists.add(new DataItem(mContext.getString(R.string.colour_blue), R.drawable.paper_calendar_001,
                        productName + mContext.getString(R.string.five_multiply_eight)));//5x8寸
                lists.add(new DataItem(mContext.getString(R.string.colour_green), R.drawable.paper_calendar_002,
                        productName + mContext.getString(R.string.five_multiply_eight)));//绿色
                lists.add(new DataItem(mContext.getString(R.string.colour_yellow), R.drawable.paper_calendar_003,
                        productName + mContext.getString(R.string.five_multiply_eight)));//黄色
                lists.add(new DataItem(mContext.getString(R.string.colour_orange), R.drawable.paper_calendar_004,
                        productName + mContext.getString(R.string.five_multiply_eight)));//橙色
                lists.add(new DataItem(mContext.getString(R.string.colour_white), R.drawable.paper_calendar_005,
                        productName + mContext.getString(R.string.five_multiply_eight)));//白色
                lists.add(new DataItem(mContext.getString(R.string.colour_black), R.drawable.paper_calendar_006,
                        productName + mContext.getString(R.string.five_multiply_eight)));//黑色
            }

        }
        else if (mContext.getString(R.string.photo_frame_canvas).equals(productName))//相框油画
        {
            lists.add(new DataItem(mContext.getString(R.string.deep_wood_lines), R.drawable.painting_01, null));//深木纹
            lists.add(new DataItem(mContext.getString(R.string.light_wood_lines), R.drawable.painting_02, null));//浅木纹
            lists.add(new DataItem(mContext.getString(R.string.silver_gray), R.drawable.painting_04, null));//银灰色
            lists.add(new DataItem(mContext.getString(R.string.colour_white), R.drawable.painting_03, null));//白色
        }
        else if (mContext.getString(R.string.jingxuan_photo).equals(productName))//晶炫相片册
        {
            lists.add(new DataItem(mContext.getString(R.string.transparent_matting), R.drawable.custphoto_01, null));//透明磨砂
            lists.add(new DataItem(mContext.getString(R.string.black_leechee_lines), R.drawable.custphoto_02, null));//黑色荔枝纹
        }
    }

    /**
     * @Description 设置所有的checkbox状态 未选状态.
     * @author Administrator
     */

    public void setAllCheckboxs()
    {
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < lists.size(); i++)
        {
            isSelected.put(i, false);
        }
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
    public View getView(int position, View convertView, ViewGroup arg2)
    {
        ViewHolder holder = null;
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.select_photostyle_item, null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_item_icon);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_item_name);
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb_item_status);
            holder.rootView = convertView;
        }
        else 
        {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setTag(holder);
        DataItem item = lists.get(position);

        String Itemname = item.getName();
        int ItemPath = item.getPath();
        holder.cb.setChecked(isSelected.get(position));
        if (position == 0)
        {
            holder.rootView.setBackgroundResource(R.drawable.body_box_btn_1_selector);
        }
        else if (position < lists.size() - 1)
        {

            int nextPosition = position + 1;
            DataItem nextItem = lists.get(nextPosition);
            String nextName = nextItem.getName();
            int prePosition = position - 1;
            DataItem PreItem = lists.get(prePosition);
            String preName = PreItem.getName();
            if (mContext.getString(R.string.hide).equals(nextName))//隐藏
            {
                holder.rootView.findViewById(R.id.view_line).setVisibility(View.INVISIBLE);
                holder.rootView.setBackgroundResource(R.drawable.body_box_btn_3_selector);

            }
            else if (mContext.getString(R.string.hide).equals(preName))//隐藏
            {
                holder.rootView.setBackgroundResource(R.drawable.body_box_btn_1_selector);

            }

            else
            {
                holder.rootView.setBackgroundResource(R.drawable.body_box_btn_2_selector);
            }
        }
        else if (position == lists.size() - 1)
        {
            holder.rootView.findViewById(R.id.view_line).setVisibility(View.INVISIBLE);
            holder.rootView.setBackgroundResource(R.drawable.body_box_btn_3_selector);
        }

        if (mContext.getString(R.string.hide).equals(Itemname))//隐藏
        {
            holder.rootView.setBackgroundColor(Color.TRANSPARENT);
            holder.rootView.setClickable(false);
            holder.rootView.findViewById(R.id.view_line).setVisibility(View.INVISIBLE);

            holder.iv.setVisibility(View.GONE);
            holder.tv_name.setText(item.getGourp());
            holder.tv_name.setPadding(10, 0, 0, 0);
        }
        else
        {
            holder.tv_name.setText(Itemname);
            if (!isShow)
            {
                LayoutParams lp = holder.tv_name.getLayoutParams();
                lp.width = LayoutParams.WRAP_CONTENT;
                holder.tv_name.setPadding(10, 0, 20, 0);
                holder.tv_name.setTextSize(14);
                holder.tv_name.setLayoutParams(lp);
                holder.iv.setVisibility(View.GONE);

            }
            else
            {

                Bitmap bmp = null;
                if (!mImageMapping.containsKey(position)
                        && mImageMapping.get(position) != null
                        && mImageMapping.get(position).get() != null)
                {
                    bmp = mImageMapping.get(position).get();
                }
                else
                {
                    bmp = BitmapFactory.decodeResource(
                            mContext.getResources(), ItemPath);
                    mImageMapping.put(position, new SoftReference<Bitmap>(bmp));
                    bmp = mImageMapping.get(position).get();
                }
                if (bmp != null)
                {
                    holder.iv.setImageBitmap(bmp);
                }
            }
        }

        return convertView;
    }

    public DataItem backdata(int arg2)
    {
        return (DataItem) getItem(arg2);
    }
    
    public DataItem getSelectItemData()
    {
        if(mSelectPos != -1 && mSelectPos < lists.size())
        {
            return lists.get(mSelectPos);
        }
        return null;
    }
    
    static class ViewHolder 
    {
        View rootView;
        ImageView iv;
        TextView tv_name;
        CheckBox cb;
    }
    
    public void clear(){
        
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
