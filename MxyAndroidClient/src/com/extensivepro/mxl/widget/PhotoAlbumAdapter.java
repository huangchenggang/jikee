package com.extensivepro.mxl.widget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.bean.CartItemCustomValue;
import com.extensivepro.mxl.app.bean.Templates;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.ui.SelcetPhotosAcivity;
import com.extensivepro.mxl.ui.TemplateGroupActivity;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.FileMD5;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.ImageDownloader.DownloadCallback;
import com.extensivepro.mxl.util.Logger;

public class PhotoAlbumAdapter extends BaseAdapter
{
    protected static final String TAG = "PhotoAlbumAdapter";
    public static int mObjname;
    private Context context;

    class Item
    {
        Templates templates;
        String content;
        int contentTxtColor;
        String picPath;
        String tempPath;
        String progressTxt;
        int progressCount;//表示已上传的图片张数
        int progress; 
        String title;
        int picMaxSize;
        boolean isRightItem;
        boolean isTemp;
        List<String> selectImages;
        List<String> alreadyUploadImages;
        
        List<String> getNeedUploadImagePath()
        {
            if(alreadyUploadImages == null || alreadyUploadImages.isEmpty())
            {
                return selectImages;
            }
            if(selectImages == null)
            {
                return selectImages;
            }
            List<String> list = new ArrayList<String>();
            try
            {
                for (int i = 0; i < selectImages.size(); i++)
                {
                    String selectImageMd5;
                    selectImageMd5 = FileMD5.getFileMD5String(selectImages
                            .get(i));
                    boolean needRemove = false;
                    for (int j = 0; j < alreadyUploadImages.size(); j++)
                    {
                        String uploadedMd5 = alreadyUploadImages.get(j)
                                .substring(
                                        alreadyUploadImages.get(j).lastIndexOf("/") + 1,
                                        alreadyUploadImages.get(j).length());
                        if (!TextUtils.isEmpty(selectImageMd5)
                                && !TextUtils.isEmpty(uploadedMd5)
                                && selectImageMd5.equals(uploadedMd5))
                        {
                            needRemove = true;
                            break;
                        }
                        
                    }
                    if(!needRemove)
                    {
                        list.add(selectImages.get(i));
                        needRemove = true;
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return list;
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
//        clearData();
        for(int i=0;i<clearList.size();i++)
        {
            int key = clearList.get(i);
            if (mLeftImageMapping.containsKey(key)
                    && mLeftImageMapping.get(key) != null
                    && mLeftImageMapping.get(key).get() != null
                    && !mLeftImageMapping.get(key).get().isRecycled())
            {
                mLeftImageMapping.remove(key);
            }
            if (mRightImageMapping.containsKey(key)
                    && mRightImageMapping.get(key) != null
                    && mRightImageMapping.get(key).get() != null
                    && !mRightImageMapping.get(key).get().isRecycled())
            {
                mRightImageMapping.remove(key);
            }
        }
    }
    
    class Group
    {
        Item left,right;
        boolean isFullTitle;
        String title;
        boolean isSingle;
    }
    
    public enum GroupType
    {
        // case 1: // 气球
        // break;
        // case 2:// 迷你相册
        // break;
        // case 3: // 月台历 12个月
        // break;
        // case 4: // 无边框的
        // break;
        // case 5: // 自定义的
        // break;
        PHOTO_BALLOON(1), MINI_ALBUM(2), PHOTO_CALENDAR(3), NO_FRAME_CANVAS(4), CUSTOM_CANVAS(
                5);
        int type;

        private GroupType(int value)
        {
            type = value;
        }

        public int getTypeValue()
        {
            return type;
        }
        
        public void setTypeValue(int typeValue)
        {
        }
        
        public static GroupType intToValue(int value)
        {
            GroupType type = PHOTO_BALLOON;
            if(PHOTO_BALLOON.getTypeValue() == value)
            {
                type = PHOTO_BALLOON;
            }
            else if(MINI_ALBUM.getTypeValue() == value)
            {
                type = MINI_ALBUM;
            }
            else if(PHOTO_CALENDAR.getTypeValue() == value)
            {
                type = PHOTO_CALENDAR;
            }
            else if(NO_FRAME_CANVAS.getTypeValue() == value)
            {
                type = NO_FRAME_CANVAS;
            }
            else if(CUSTOM_CANVAS.getTypeValue() == value)
            {
                type = CUSTOM_CANVAS;
            }
            return type;
        }
    }
    
    
    private GroupType mGroupType = GroupType.PHOTO_BALLOON;
    
    private SparseArray<Group> mItemGroups = new SparseArray<Group>();
    
    private WeakHashMap<Integer,WeakReference<Bitmap>> mRightImageMapping;
    private WeakHashMap<Integer,WeakReference<Bitmap>> mLeftImageMapping;
    
    private LayoutInflater inflater;
    
    private ImageDownloader mImageDownloader;

    /**
     * type:区别传入参数，并分别生成listview
     * 
     */
    private int type;

    private String ItemName;

    private List<String> lists;

    private int mSelectPos = 0;

    private boolean mIsSelectRightItem;
    
    public boolean isSelectRightItem()
    {
        return mIsSelectRightItem;
    }
    
    public int getmSelectPos()
    {
        return mSelectPos;
    }

    public void setmSelectPos(int mSelectPos)
    {
        this.mSelectPos = mSelectPos;
    }

    public String getItemName()
    {
        return ItemName;
    }

    public void setItemName(String itemName)
    {
        ItemName = itemName;
    }

    private LinearLayout ll1;

    private LinearLayout ll2;

    public List<String> getLists()
    {
        return lists;
    }

    public void setLists(List<String> lists)
    {
        this.lists = lists;
    }

    private String groupId;

    public PhotoAlbumAdapter(Context context, int type, String ItemName,
            List<String> lists)
    {
        super();
        mImageDownloader = new ImageDownloader();
//        mImageDownloader = ImageDownloader.getInstance();
        this.context = context;
        this.type = type;
        this.ItemName = ItemName;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
        mGroupType = GroupType.intToValue(type);
        mRightImageMapping = new WeakHashMap<Integer, WeakReference<Bitmap>>();
        mLeftImageMapping = new WeakHashMap<Integer, WeakReference<Bitmap>>();
        initDataByType(); 
    }

    public void clearData()
    {   
        synchronized(mRightImageMapping)
        {
            if (!mRightImageMapping.isEmpty())
            {
                 Set<Integer> key = mRightImageMapping.keySet();
                for (int i = 0; i < key.size(); i++)
                {
                    if (mRightImageMapping.get(key) != null
                            && mRightImageMapping.get(key).get() != null
                            && !mRightImageMapping.get(key).get().isRecycled())
                    {
                        mRightImageMapping.get(key).get().recycle();
                        System.gc();
                        mRightImageMapping.remove(key);
                    }
                }
                mRightImageMapping.clear();
            }
        }
        synchronized(mLeftImageMapping)
        {
            if (!mLeftImageMapping.isEmpty())
            {
                 Set<Integer> key = mLeftImageMapping.keySet();
                for (int i = 0; i < key.size(); i++)
                {
                    if (mLeftImageMapping.get(key) != null
                            && mLeftImageMapping.get(key).get() != null
                            && !mLeftImageMapping.get(key).get().isRecycled())
                    {
                        mLeftImageMapping.get(key).get().recycle();
                        System.gc();
                        mLeftImageMapping.remove(key);
                    }
                }
                mLeftImageMapping.clear();
            }
        }
//        mRightImageMapping.clear();
//        mLeftImageMapping.clear();
        mItemGroups.clear();
    }
    
    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
        mGroupType = GroupType.intToValue(type);
        initDataByType();
    }
    
    private String getResString(int id)
    {
        return context.getResources().getString(id);
    }
    
    private String getResString(int id,Object... formatArgs)
    {
        return context.getResources().getString(id, formatArgs);
    }
    
    
    private void initDataByType()
    {
        Group group = null;
        mItemGroups.clear();
        switch (mGroupType)
        {
            case PHOTO_BALLOON:
                if (ItemName != null)// 气球的
                {
                    mItemGroups
                            .put(0,
                                    createOneGroup(
                                            true,
                                            null,
                                            false,
                                            getResString(R.string.photo_album_add_temp),
                                            getResString(R.string.photo_album_add_pic)));
//                    mItemGroups
//                            .put(1,
//                                    createOneGroup(
//                                            true,
//                                            getResString(R.string.photo_album_choose_back_temp_pic),
//                                            false,
//                                            getResString(R.string.photo_album_add_temp),
//                                            getResString(R.string.photo_album_add_pic)));
//                }
//                else
//                {
//                    mItemGroups
//                            .put(0,
//                                    createOneGroup(
//                                            true,
//                                            getResString(R.string.photo_album_choose_front_temp_pic),
//                                            false,
//                                            getResString(R.string.photo_album_add_temp),
//                                            getResString(R.string.photo_album_add_pic)));
                }
                break;
            case MINI_ALBUM:// 迷你相册
                if (ItemName != null && ItemName.contains(context.getString(R.string.colour_transparent)))//透明色
                {
                    mItemGroups
                            .put(0,
                                    createOneGroup(
                                            true,
                                            null,
                                            true,
                                            getResString(R.string.photo_album_choose_cover_temp),
                                            getResString(R.string.photo_album_choose_cover_pic)));
                    mItemGroups
                            .put(1,
                                    createOneGroup(
                                            true,
                                            null,
                                            true,
                                            getResString(R.string.photo_album_choose_reverse_temp),
                                            getResString(R.string.photo_album_choose_reverse_pic)));
                    int leftPicCount = 0;
                    int rightPicCount = 0;
                    if (ItemName.contains(context.getString(R.string.small)+context.getString(R.string.square)))//小方形
                    {
                        leftPicCount = 4;
                        rightPicCount = 10;
                    }
                    else if(ItemName.contains(context.getString(R.string.small)+context.getString(R.string.heart_shaped)))//小心形
                    {
                        leftPicCount = 4;
                        rightPicCount = 6;
                    }
                    else if (ItemName.contains(context.getString(R.string.big)+context.getString(R.string.square)))//大方形
                    {
                        leftPicCount = 4;
                        rightPicCount = 8;
                    }
                    else if(ItemName.contains(context.getString(R.string.big)+context.getString(R.string.heart_shaped)))//大心形
                    {
                        leftPicCount = 4;
                        rightPicCount = 8;
                    }
                        
                    group = new Group();
                    group.left = createSingleItem(
                            null, leftPicCount, false, false,
                                    getResString(R.string.photo_album_n_land_pic,
                                            leftPicCount));
                    group.right = createSingleItem(
                            null, rightPicCount, true,
                            false, getResString(R.string.photo_album_n_page_pic,
                                    rightPicCount));
                    mItemGroups.put(2, group);
                }
                else
                {
                    if (ItemName != null)
                    {
                        int leftPicCount = 0;
                        int rightPicCount = 0;
                        if (ItemName.contains(context.getString(R.string.small)+context.getString(R.string.square)))
                        {
                            leftPicCount = 4;
                            rightPicCount = 10;
                        }
                        else if(ItemName.contains(context.getString(R.string.small)+context.getString(R.string.heart_shaped)))
                        {
                            leftPicCount = 4;
                            rightPicCount = 6;
                        }
                        else if (ItemName.contains(context.getString(R.string.big)+context.getString(R.string.square)) )
                        {
                            leftPicCount = 4;
                            rightPicCount = 8;
                        }
                        else if(ItemName.contains(context.getString(R.string.big)+context.getString(R.string.heart_shaped)))
                        {
                            leftPicCount = 4;
                            rightPicCount = 8;
                        }
                        group = new Group();
                        group.left = createSingleItem(
                                null, leftPicCount, false,
                                false,
                                getResString(R.string.photo_album_n_land_pic,
                                        leftPicCount));
                        group.right = createSingleItem(
                                null, rightPicCount, false,
                                true,
                                getResString(
                                        R.string.photo_album_n_page_pic,
                                        rightPicCount));
                        mItemGroups.put(0, group);
                    }
                }
                break;
            case PHOTO_CALENDAR:// 台历
                for (int i = 0; i < 12; i++)
                {
                    mItemGroups
                            .put(i,
                                    createOneGroup(
                                            true,
                                            null,
                                            false,
                                            getResString(R.string.photo_album_n_month_temp,i+1),
                                            getResString(R.string.photo_album_n_month_pic,i+1)));
                }
                break;
            case NO_FRAME_CANVAS:// 无边框油画
                group = new Group();
                group.left = createSingleItem(
                        null, 1, false,
                        false, getResString(R.string.photo_album_add_pic));
                mItemGroups.put(0, group);
                break;
            case CUSTOM_CANVAS:
                if (ItemName != null
                        && (ItemName.contains(context.getString(R.string.transparent_matting)) || ItemName
                                .contains(context.getString(R.string.black_leechee_lines))))//透明磨砂   黑色荔枝纹
                {
                    group = new Group();
                    group.left = createSingleItem(null,1, false, false,getResString(R.string.photo_album_cover_pic));
                    group.right = createSingleItem(null, 14, false, false, getResString(R.string.photo_album_n_inner_page_pic,14));
                    mItemGroups.put(0, group);
                }
                else
                {
                    group = new Group();
                    group.left = createSingleItem(null, 1,false, false,getResString(R.string.photo_album_cover_pic));
                    group.right = createSingleItem( null, 1, true, false,getResString(R.string.photo_album_reverse_pic));
                    mItemGroups.put(0, group);
                    group = new Group();
                    group.left = createSingleItem(null,4, false, false,getResString(R.string.photo_album_n_land_pic, 4));
                    group.right = createSingleItem(null, 20,true, false,getResString(R.string.photo_album_n_pic, 20));
                    mItemGroups.put(1, group);
                }
                break;
            default:
                break;
        }
    }

    private Item createSingleItem(String title,int picMaxSize,boolean isRightItem,boolean isTemp, String content)
    {
        Item item = new Item();
        item.isRightItem = isRightItem;
        item.title = title;
        item.content = content;
        item.picMaxSize = picMaxSize;
        item.progress = -1;
        item.contentTxtColor = R.color.photo_album_detial_desc_txt_color;
        return item;
    }
    
    private Group createOneGroup(boolean isFullTitle, String fullTitle,
            boolean childNoTitle, String leftContent, String rightContent)
    {
        Item left = new Item();
        left.isTemp = true;
        if (childNoTitle)
        {
            left.title = "";
        }
        left.content = leftContent;
        left.progress = -1;
        left.contentTxtColor = R.color.photo_album_detial_desc_txt_color;
        
        Group group = new Group();
        
        group.left = left;

        Item right = new Item();
        right.content = rightContent;
        if (childNoTitle)
        {
            right.title = "";
        }
        right.progress = -1;
        right.contentTxtColor = R.color.photo_album_detial_desc_txt_color;
        

        group.right = right;
        group.isFullTitle = isFullTitle;
        group.title = fullTitle;
        return group;
    }
    
    @Override
    public int getCount()
    {
        switch(mGroupType)
        {
            case PHOTO_BALLOON:
                if (ItemName != null && ItemName.contains(context.getString(R.string.difference)))// 气球的
                {
                    return 2;
                }
                return 1;
            case MINI_ALBUM:// 迷你相册
                if (ItemName != null && ItemName.contains(context.getString(R.string.colour_transparent)))//透明色
                {
                    return 3;
                }
                return 1;
            case PHOTO_CALENDAR://台历
                return 12;
            case NO_FRAME_CANVAS://无边框油画
                return 1;
            case CUSTOM_CANVAS:
            if (ItemName != null)
            {
                if (ItemName.contains(context.getString(R.string.transparent_matting)) || ItemName.contains(context.getString(R.string.black_leechee_lines)))
                {
                    return 1;
                }
            }
            return 2;
        }
        return 0;
    }
    
    public void changeAlreadyUploadImage(int pos,boolean isRightItem,List<String> results)
    {
        if (pos < mItemGroups.size() && isRightItem)
        {
            mItemGroups.get(pos).right.alreadyUploadImages = results;
        }
        else if (pos < mItemGroups.size() && !isRightItem)
        {
            mItemGroups.get(pos).left.alreadyUploadImages = results;
        }
    }
    
    public void changeTemp(int pos,boolean isRightItem,Templates template)
    {
        if(template == null)
        {
            return;
        }
        String tempUrl = Const.BASE_URI+template.getImage();
        if (pos < mItemGroups.size() && isRightItem)
        {
            mItemGroups.get(pos).right.templates = template;
            mItemGroups.get(pos).right.tempPath = tempUrl;
            mItemGroups.get(pos).right.content = getResString(R.string.photo_album_modify_temp);
            mItemGroups.get(pos).right.contentTxtColor = R.color.photo_album_detial_desc_txt_color_modify;
            mRightImageMapping.remove(pos);
        }
        else if (pos < mItemGroups.size() && !isRightItem)
        {
            mItemGroups.get(pos).left.templates = template;
            mItemGroups.get(pos).left.tempPath = tempUrl;
            mItemGroups.get(pos).left.content = getResString(R.string.photo_album_modify_temp);
            mItemGroups.get(pos).left.contentTxtColor = R.color.photo_album_detial_desc_txt_color_modify;
            mLeftImageMapping.remove(pos);
        }
    }
    
    public void changePic(int pos,boolean isRightItem,List<String> picPath)
    {
        if (pos < mItemGroups.size() && isRightItem && !picPath.isEmpty())
        {
            mItemGroups.get(pos).right.picPath = picPath.get(0);
            mItemGroups.get(pos).right.content = getResString(R.string.photo_album_modify_pic);
            mItemGroups.get(pos).right.contentTxtColor = R.color.photo_album_detial_desc_txt_color_modify;
            mItemGroups.get(pos).right.selectImages = picPath;
            mRightImageMapping.remove(pos);
            
        }
        else if (pos < mItemGroups.size() && !isRightItem && !picPath.isEmpty())
        {
            mItemGroups.get(pos).left.picPath = picPath.get(0);
            mItemGroups.get(pos).left.content = getResString(R.string.photo_album_modify_pic);
            mItemGroups.get(pos).left.contentTxtColor = R.color.photo_album_detial_desc_txt_color_modify;
            mItemGroups.get(pos).left.selectImages = picPath;
            mLeftImageMapping.remove(pos);
        }
    }

    public List<String> getUploadItemImagePath(int pos,boolean isRightItem)
    {
        if (pos < mItemGroups.size())
        {
            return isRightItem ? mItemGroups.get(pos).right
                    .getNeedUploadImagePath() : mItemGroups.get(pos).left
                    .getNeedUploadImagePath();
        }
        return null;
    }
    
    @Override
    public Object getItem(int arg0)
    {

        return arg0;
    }

    @Override
    public long getItemId(int arg0)
    {

        return arg0;
    }

    View view = null;

    private Activity a;
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final ViewHoler holder = new ViewHoler();
        Group group = mItemGroups.get(position);
        switch (mGroupType)
        {

            case PHOTO_BALLOON: // 气球
                view = inflater.inflate(R.layout.photo_album_list_item_double,
                        null);
                setItemBackgroundNull();
                holder.tv_title = (TextView) view
                        .findViewById(R.id.album_item_double_tv_title);
                holder.tv_title2 = (TextView) view
                        .findViewById(R.id.album_item_double_tv_title2);
                holder.hidetv = (TextView) view
                        .findViewById(R.id.album_item_double_tv_hide);
                holder.iv = view
                        .findViewById(R.id.album_item_double_tv_iv_pic);
                holder.iv2 = view
                        .findViewById(R.id.album_item_double_tv_iv_pic2);

               
                holder.tv_bottom = (TextView) view
                        .findViewById(R.id.album_item_double_tv_bottom);
                holder.tv_bottom2 = (TextView) view
                        .findViewById(R.id.album_item_double_tv_bottom2);
                ll1 = (LinearLayout) view.findViewById(R.id.ll_1);
                ll2 = (LinearLayout) view.findViewById(R.id.ll_2);

                ll1.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v)
                    {
                        Logger.i(TAG, "temp btn pos:" + position);
                        mSelectPos = position;
                        mIsSelectRightItem = false;
                        selectGroupByID(position);
                        
                    }

                });
                ll2.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v)
                    {
                        // selectPhotos(arg0);
                        mSelectPos = position;
                        mIsSelectRightItem = true;
                        choosePic(position);
                    }
                });
                break;
            case MINI_ALBUM:// 迷你相册
                view = inflater.inflate(R.layout.photo_album_list_item_double,
                        null);
                setItemBackgroundNull();
                holder.tv_title = (TextView) view
                        .findViewById(R.id.album_item_double_tv_title);
                holder.tv_title2 = (TextView) view
                        .findViewById(R.id.album_item_double_tv_title2);
                holder.iv = view
                        .findViewById(R.id.album_item_double_tv_iv_pic);
                holder.iv2 = view
                        .findViewById(R.id.album_item_double_tv_iv_pic2);
                holder.tv_bottom = (TextView) view
                        .findViewById(R.id.album_item_double_tv_bottom);
                holder.tv_bottom2 = (TextView) view
                        .findViewById(R.id.album_item_double_tv_bottom2);
                holder.hidetv = (TextView) view
                        .findViewById(R.id.album_item_double_tv_hide);
                holder.tv_bottom.setText("tv bottom");
                holder.tv_bottom2.setText("tv bottom2");
                ll1 = (LinearLayout) view.findViewById(R.id.ll_1);
                ll2 = (LinearLayout) view.findViewById(R.id.ll_2);
                if ((position == 0 || position == 1) && ItemName.contains(context.getString(R.string.colour_transparent)))//透明色
                {
                    ll1.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v)
                        {
                            mSelectPos = position;
                            mIsSelectRightItem = false;
                            selectGroupByID(position);
                        }
                    });
                    ll2.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v)
                        {
                            mSelectPos = position;
                            mIsSelectRightItem = true;
                            choosePic(position);
                        }
                    });
                }
                else
                {
                    ll1.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v)
                        {
                            mIsSelectRightItem = false;
                            mSelectPos = position;
                            selectPhotos(position,(String) holder.tv_bottom.getText());
                        }
                    });
                    ll2.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v)
                        {
                            mIsSelectRightItem = true;
                            mSelectPos = position;
                            selectPhotos(position,(String) holder.tv_bottom2.getText());
                        }
                    });
                }
                
                break;
            case PHOTO_CALENDAR: // 月台历 12个月

                view = inflater.inflate(R.layout.photo_album_list_item_double,
                        null);
                setItemBackgroundNull();
                holder.tv_title = (TextView) view
                        .findViewById(R.id.album_item_double_tv_title);
                holder.tv_title2 = (TextView) view
                        .findViewById(R.id.album_item_double_tv_title2);
                holder.hidetv = (TextView) view
                        .findViewById(R.id.album_item_double_tv_hide);
                holder.iv = view
                        .findViewById(R.id.album_item_double_tv_iv_pic);
                holder.iv2 = view
                        .findViewById(R.id.album_item_double_tv_iv_pic2);
                holder.tv_bottom = (TextView) view
                        .findViewById(R.id.album_item_double_tv_bottom);
                holder.tv_bottom2 = (TextView) view
                        .findViewById(R.id.album_item_double_tv_bottom2);
                view.findViewById(R.id.ll_1).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v)
                            {
                                selectGroupByID(position);
                                mSelectPos = position;
                                mIsSelectRightItem = false;
                            }
                        });
                view.findViewById(R.id.ll_2).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v)
                            {
                                choosePic(position);
                                mSelectPos = position;
                                mIsSelectRightItem = true;
                            }
                        });
                
                break;
            case NO_FRAME_CANVAS: // 无边框的
                view = inflater.inflate(R.layout.photo_album_list_item_single,null);
                setItemBackgroundNull();
                holder.tv_title = (TextView) view
                        .findViewById(R.id.album_item_single_tv_title);
                holder.iv = view
                        .findViewById(R.id.album_item_single_tv_iv_pic);
                holder.tv_bottom = (TextView) view
                        .findViewById(R.id.album_item_single_tv_bottom);

                LinearLayout ll_item_single = (LinearLayout) view
                        .findViewById(R.id.ll_item_single);
                ll_item_single.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v)
                    {
                        choosePic(position);
                        mSelectPos = position;
                        mIsSelectRightItem = false;
                    }
                });
                break;
            case CUSTOM_CANVAS: // 自定义的
                view = inflater.inflate(R.layout.photo_album_list_item_double,
                        null);
                setItemBackgroundNull();
                holder.tv_title = (TextView) view
                        .findViewById(R.id.album_item_double_tv_title);
                holder.iv = view
                        .findViewById(R.id.album_item_double_tv_iv_pic);
                holder.iv2 = view
                        .findViewById(R.id.album_item_double_tv_iv_pic2);
                holder.tv_bottom = (TextView) view
                        .findViewById(R.id.album_item_double_tv_bottom);
                holder.tv_bottom2 = (TextView) view
                        .findViewById(R.id.album_item_double_tv_bottom2);
                holder.tv_title = (TextView) view
                        .findViewById(R.id.album_item_double_tv_title);
                holder.tv_title2 = (TextView) view
                        .findViewById(R.id.album_item_double_tv_title2);
                view.findViewById(R.id.ll_1).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v)
                            {
                                mIsSelectRightItem = false;
                                mSelectPos = position;
                                TextView text = (TextView) v
                                .findViewById(R.id.album_item_double_tv_bottom);
                               String title =  text.getText().toString();
                              if(containsNum(title)){
                                  selectPhotos(position,(String) holder.tv_bottom.getText());
                              } else {                              
                                choosePic(position);                                                        
                                }
                                
                            }

                           
                        });
                view.findViewById(R.id.ll_2).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v)
                            {
                                mIsSelectRightItem = true;
                                mSelectPos = position;
                                TextView text = (TextView) v
                                        .findViewById(R.id.album_item_double_tv_bottom2);
                                       String title =  text.getText().toString();
                                      if(containsNum(title)){
                                          selectPhotos(position,(String) holder.tv_bottom2.getText());
                                      } else {                              
                                        choosePic(position);                                                        
                                        }

                            }
                        });
                break;
        }
        bindItemToView(group, holder, position);
        showUploadProgress(position, holder);
        return view;
    }
    private boolean containsNum(String title)
    {
        // TODO Auto-generated method stub
        if (title.contains("0") || title.contains("1") || title.contains("2")
                || title.contains("3") || title.contains("4")
                || title.contains("5") || title.contains("6")
                || title.contains("7") || title.contains("8")
                || title.contains("9") ){
            return true;
        }
            return false;
    }
    private void setItemImage(Item item,View parent,int position, boolean isRightItem)
    {
        if(item != null)
        {
            if(item.isTemp)
            {
                if(TextUtils.isEmpty(item.tempPath))
                {
                    ((ImageView)parent.findViewById(R.id.image_choose_pic)).
                    setBackgroundResource(R.drawable.addphoto3_btn_selector);
                }
                else 
                {
                    setImageView(parent, item.tempPath, position, isRightItem, item.isTemp);
                }
            }
            else 
            {
                if(TextUtils.isEmpty(item.picPath))
                {
                    ((ImageView)parent.findViewById(R.id.image_choose_pic)).
                    setBackgroundResource(R.drawable.addphoto1_btn_selector);
                }
                else 
                {
                    setImageView(parent, item.picPath, position, isRightItem, item.isTemp);
                }
            }
        }
    }
    
    private void bindItemToView(Group group,ViewHoler holder, final int position)
    {
        if(group == null || holder == null)
        {
            return;
        }
        if(holder.hidetv != null)
        {
            if(group.isFullTitle)
            {
                holder.hidetv.setVisibility(View.VISIBLE);
                holder.hidetv.setText(group.title);
            }
            else 
            {
                holder.hidetv.setVisibility(View.INVISIBLE);
                holder.hidetv.setText("");
            }
        }
        if(group.left != null)
        {
            setItemImage(group.left, holder.iv, position, false);
            holder.tv_bottom.setText(TextUtils.isEmpty(group.left.content) ? ""
                    : group.left.content);
            holder.tv_bottom.setTextColor(context.getResources().getColor(
                    group.left.contentTxtColor));
            holder.tv_title.setText(TextUtils.isEmpty(group.left.title) ? ""
                    : group.left.title);
        }
        if(group.right != null)
        {
            setItemImage(group.right, holder.iv2, position, true);
            holder.tv_bottom2
                    .setText(TextUtils.isEmpty(group.right.content) ? ""
                            : group.right.content);
            holder.tv_bottom2.setTextColor(context.getResources().getColor(group.right.contentTxtColor));
            holder.tv_title2.setText(TextUtils.isEmpty(group.right.title) ? ""
                    : group.right.title);
        }
    }
    
    private void showUploadProgress(int position,ViewHoler holder)
    {
        if(position < mItemGroups.size() && holder != null)
        {
            Group group = mItemGroups.get(position);
            if (group.left != null && !group.left.isTemp)
            {
                ProgressBar progressBar = (ProgressBar) holder.iv
                        .findViewById(R.id.progress_bar_choose_pic);
                if (group.left.progress == -1)
                {
                    progressBar.setVisibility(View.GONE);
                    holder.tv_bottom.setText(TextUtils
                            .isEmpty(group.left.content) ? ""
                            : group.left.content);
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(group.left.progress);
                    holder.tv_bottom.setText(group.left.progressCount + "/"
                            + group.left.selectImages.size());
                }
                if (100 == group.left.progress)
                {
                    Message msg = mHandler.obtainMessage();
                    msg.what = position;
                    msg.arg1 = DISMISS_LEFT_ITEM_PROGRESS;
                    mHandler.sendMessageDelayed(msg, 1000);
                }
            }
            if (group.right != null && !group.right.isTemp)
            {
                ProgressBar progressBar = (ProgressBar) holder.iv2
                        .findViewById(R.id.progress_bar_choose_pic);
                if (group.right.progress == -1)
                {
                    progressBar.setVisibility(View.GONE);
                    holder.tv_bottom2.setText(TextUtils
                            .isEmpty(group.right.content) ? ""
                            : group.right.content);
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(group.right.progress);
                    if (group.right.selectImages != null
                            && !group.right.selectImages.isEmpty())
                    {
                        holder.tv_bottom2.setText(group.right.progressCount
                                + "/" + group.right.selectImages.size());
                    }
                }
                if (100 == group.right.progress)
                {
                    Message msg = mHandler.obtainMessage();
                    msg.what = position;
                    msg.arg1 = DISMISS_RIGHT_ITEM_PROGRESS;
                    mHandler.sendMessageDelayed(msg, 1000);
                }
            }
        }
        
    }

    private void setItemBackgroundNull()
    {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }

    /**
     * @Description 选择模板组
     * @author Administrator
     * @param arg0 模板组id
     */
    private void selectGroupByID(final int arg0)
    {
        groupId = lists.get(arg0);
        Intent templateIntent = new Intent(context, TemplateGroupActivity.class);
        templateIntent.putExtra(Const.POSITION, String.valueOf(arg0));
        templateIntent.putExtra(Const.GROUPID_OBJ, groupId);
        a = (Activity) context;

        a.startActivityForResult(templateIntent, Const.SELECT_TEMPLATEGROUP);
    }

    /**
     * @Description 多选照片
     * @author Administrator
     */

    private void selectPhotos(final int arg0 ,String name)
    {
        Intent photosIntent = new Intent(context, SelcetPhotosAcivity.class);
        a = (Activity) context;
        if (mIsSelectRightItem && arg0 < mItemGroups.size()
                && mItemGroups.get(arg0).right != null)
        {
            photosIntent.putExtra(Const.EXTRA_MUILT_SELECT_MAX_NUM,
                    mItemGroups.get(arg0).right.picMaxSize);
        }
        else if (!mIsSelectRightItem && arg0 < mItemGroups.size()
                && mItemGroups.get(arg0).left != null)
        {
            photosIntent.putExtra(Const.EXTRA_MUILT_SELECT_MAX_NUM,
                    mItemGroups.get(arg0).left.picMaxSize);
            photosIntent.putExtra("itemName", name);
        }
        a.startActivityForResult(photosIntent, Const.SELECT_PHOTOS);
    }

    /**
     * @Description 选择一张照片
     * @author Administrator
     */
    public void choosePic(final int arg0)
    {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PICK");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("image/*");

        a = (Activity) context;
        a.startActivityForResult(intent, Const.SELECT_PHOTO);
    }

    static class ViewHoler
    {
        TextView tv_title;

        TextView tv_title2;

        TextView tv_bottom;

        TextView tv_bottom2;

        View iv;

        View iv2;

        TextView hidetv;
    }

    private void setImageView(View group,String url,final int position, boolean isRightView, final boolean isTemp)
    {
        final ImageView image = (ImageView) group.findViewById(R.id.image_choose_pic);
        image.setBackgroundColor(Color.TRANSPARENT);
        image.setScaleType(ScaleType.FIT_XY);
        if(isTemp)
        {
            image.setAdjustViewBounds(true);
        }
        else 
        {
            image.setAdjustViewBounds(false);
        }
        final boolean isRight = isRightView;
        Bitmap bmp = null;
        if(isRight)
        {
            if (mRightImageMapping != null && mRightImageMapping.containsKey(position)
                    && mRightImageMapping.get(position) != null)
            {
                if(mRightImageMapping.get(position) != null)
                {
                    bmp = mRightImageMapping.get(position).get();
                }
            }
        }
        else 
        {
            if (mLeftImageMapping != null && mLeftImageMapping.containsKey(position)
                    && mLeftImageMapping.get(position) != null)
            {
                if(mLeftImageMapping.get(position) != null)
                {
                    bmp = mLeftImageMapping.get(position).get();
                }
            }
        }
        if(bmp != null)
        {
            image.setImageBitmap(bmp);
        }
        else if (!TextUtils.isEmpty(url))
        {
            
            final int maxWidth = ClientManager.getInstance().getScreenWidth()/6;
            if(!isTemp)
            {
                bmp = mImageDownloader.downloadImageFromSDCard(
                                new DownloadCallback() {
                                    
                                    @Override
                                    public void onLoadSuccess(Bitmap bitmap)
                                    {
                                        Logger.d(TAG, "downloadImage.onLoadSuccess()[bmp:"
                                                + bitmap + "]");
                                        if (bitmap != null)
                                        {
                                            if (!isTemp)
                                            {
                                                bitmap = mImageDownloader
                                                        .creatThumnials(142, 142, bitmap);
                                            }
                                            if (isRight)
                                            {
                                                mRightImageMapping.put(position,
                                                        new WeakReference<Bitmap>(bitmap));
                                            }
                                            else
                                            {
                                                mLeftImageMapping.put(position,
                                                        new WeakReference<Bitmap>(bitmap));
                                            }
                                            notifyDataSetChanged();
                                        }
                                    }
                                    
                                    @Override
                                    public void onLoadFailed()
                                    {
                                        if (isRight)
                                        {
                                            if (mRightImageMapping.containsKey(position)
                                                    && mRightImageMapping.get(position) != null)
                                            {
                                                mRightImageMapping.put(position, null);
                                                notifyDataSetChanged();
                                            }
                                        }
                                        else
                                        {
                                            if (mLeftImageMapping.containsKey(position)
                                                    && mLeftImageMapping.get(position) != null)
                                            {
                                                mLeftImageMapping.put(position, null);
                                                notifyDataSetChanged();
                                            }
                                            
                                        }
                                        Logger.e(TAG, "onLoadFailed()[access]");
                                    }
                                }, url, maxWidth);
            }
            else 
            {

                bmp = mImageDownloader.downloadImage(
                                new DownloadCallback() {
                                    
                                    @Override
                                    public void onLoadSuccess(Bitmap bitmap)
                                    {
                                        Logger.d(TAG, "downloadImage.onLoadSuccess()[bmp:"
                                                + bitmap + "]");
                                        if (bitmap != null)
                                        {
                                            if (!isTemp)
                                            {
                                                bitmap = mImageDownloader
                                                        .creatThumnials(142, 142, bitmap);
                                            }
                                            if (isRight)
                                            {
                                                mRightImageMapping.put(position,
                                                        new WeakReference<Bitmap>(bitmap));
                                            }
                                            else
                                            {
                                                mLeftImageMapping.put(position,
                                                        new WeakReference<Bitmap>(bitmap));
                                            }
                                            notifyDataSetChanged();
                                        }
                                    }
                                    
                                    @Override
                                    public void onLoadFailed()
                                    {
                                        if (isRight)
                                        {
                                            if (mRightImageMapping.containsKey(position)
                                                    && mRightImageMapping.get(position) != null)
                                            {
                                                mRightImageMapping.put(position, null);
                                                notifyDataSetChanged();
                                            }
                                        }
                                        else
                                        {
                                            if (mLeftImageMapping.containsKey(position)
                                                    && mLeftImageMapping.get(position) != null)
                                            {
                                                mLeftImageMapping.put(position, null);
                                                notifyDataSetChanged();
                                            }
                                            
                                        }
                                        Logger.e(TAG, "onLoadFailed()[access]");
                                    }
                                }, url, maxWidth);
            
            }
            if (bmp != null)
            {
                if (!isTemp)
                {
                    bmp = mImageDownloader.creatThumnials(142,
                            142, bmp);
                }
                if (isRight)
                {
                    mRightImageMapping.put(position, new WeakReference<Bitmap>(
                            bmp));
                }
                else
                {
                    mLeftImageMapping.put(position, new WeakReference<Bitmap>(
                            bmp));
                }
                image.setImageBitmap(bmp);
            }
        }
    }
    
    public List<CartItemCustomValue> getCarItemCustomValues(List<String> specificationIds)
    {
        List<CartItemCustomValue> values = new ArrayList<CartItemCustomValue>();
        if(specificationIds == null || specificationIds.isEmpty() || specificationIds.size()< mItemGroups.size())
        {
            return values;
        }
        for(int i=0;i<mItemGroups.size();i++)
        {
            Group group = mItemGroups.get(i);
            if(group.left != null && group.left.isTemp )
            {//一行有模版的时候模版加上图片算一个定制项
                CartItemCustomValue itemCustomValue = new CartItemCustomValue();

                if(group.right != null && !group.right.isTemp)
                {
                    itemCustomValue.setImages(group.right.alreadyUploadImages);
                }
                itemCustomValue.setTemplate(group.left.templates);
                itemCustomValue.setSpecificationId(specificationIds
                        .get(i));
                values.add(itemCustomValue); 
            }
            else if(group.left != null && !group.left.isTemp)
            {// 一行无有模版时 图片单独算一个定制项，即一行可能产生两个定制项
                CartItemCustomValue itemCustomValue = new CartItemCustomValue();
                itemCustomValue.setImages(group.left.alreadyUploadImages);
                itemCustomValue.setSpecificationId(specificationIds.get(i));
                values.add(itemCustomValue);
                if (group.right != null && !group.right.isTemp)
                {// 一行无有模版时 图片单独算一个定制项，即一行可能产生两个定制项
                    itemCustomValue = new CartItemCustomValue();
                    itemCustomValue.setImages(group.right.alreadyUploadImages);
                    if (i + 1 < specificationIds.size())
                    {
                        itemCustomValue.setSpecificationId(specificationIds
                                .get(i + 1));
                    }
                    values.add(itemCustomValue);
                }
            }
        }
        return values;
    }
    
    
    public void setImageMappingRight()
    {
        
    }
    
    public void updateProgress(int pos,boolean isRightItem,int progress, int progressCount)
    {
        if(isRightItem)
        {
            mItemGroups.get(pos).right.progressTxt = String.valueOf(progress);
            mItemGroups.get(pos).right.progress = progress;
            mItemGroups.get(pos).right.progressCount = progressCount;
        }
        else
        {
            mItemGroups.get(pos).left.progressTxt = String.valueOf(progress);
            mItemGroups.get(pos).left.progress = progress;
            mItemGroups.get(pos).left.progressCount = progressCount;
        }
        notifyDataSetChanged();
    }
    
    private static final int DISMISS_LEFT_ITEM_PROGRESS = 0;
    private static final int DISMISS_RIGHT_ITEM_PROGRESS = 1;
    
    private Handler mHandler = new Handler()
    {
      public void handleMessage(android.os.Message msg) 
      {
          Logger.d(TAG, "mHandler.handleMessage()msg.arg1:"+msg.arg1);
          switch(msg.arg1)
          {
              case DISMISS_LEFT_ITEM_PROGRESS:
                  if(msg.what < mItemGroups.size())
                  {
                      mItemGroups.get(msg.what).left.progress = -1;
                  }
                  break;
              case DISMISS_RIGHT_ITEM_PROGRESS:
                  if(msg.what < mItemGroups.size())
                  {
                      mItemGroups.get(msg.what).right.progress = -1;
                  }
                  break;
          }
          notifyDataSetChanged();
      } 
    };
    
    public void clearCache()
    {
        mImageDownloader.clearCache();
        clearData();
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