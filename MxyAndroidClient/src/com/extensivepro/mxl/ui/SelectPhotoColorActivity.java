package com.extensivepro.mxl.ui;

import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.DataItem;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.widget.SeclectPhotoStyleAdapter;
import com.extensivepro.mxl.widget.TitleBar;

/**
 * @Description 相册边框选择页面
 * @author Administrator
 * @date 2013-5-5 下午2:39:59
 * @version V1.3.1
 */

public class SelectPhotoColorActivity extends BaseActivity implements
        OnClickListener
{
//    private DataItem dataItem;

    private ListView listView;

    private SeclectPhotoStyleAdapter adapter;
    
    private TitleBar mTitleBar;

    private TextView cancel_bt, finish_bt, select_tv_title;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_photo_color);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle("");
        cancel_bt = (TextView) mTitleBar.findViewById(R.id.back_btn);
        finish_bt = (TextView) mTitleBar.findViewById(R.id.edit_btn);
        mTitleBar.setEditBtnVisibility(View.VISIBLE);
        cancel_bt.setText(R.string.cancel);
        finish_bt.setText(R.string.Ensure);
        // 判断进来的参数 设置标题
        select_tv_title = (TextView) findViewById(R.id.select_tv_title);

        cancel_bt.setOnClickListener(this);
        finish_bt.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        Intent coming = getIntent();
        String productName = coming.getStringExtra(Const.PRODUCTNAME);
        initTitleView(productName);
        adapter = new SeclectPhotoStyleAdapter(this, productName);
        listView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int arg2,
                    long arg3)
            {
                adapter.init();
                adapter.setViewSelect(arg2);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * @Description 手动设置lsitview标题
     * @author Administrator
     * @param productName
     */
    private void initTitleView(String productName)
    {
        if (Const.Str_Moduel.heart_balloon.equals(productName) || Const.Str_Moduel.star_balloon.equals(productName)
                || Const.Str_Moduel.round_balloon.equals(productName))
        {
            mTitleBar.setTitle(getString(R.string.balloon_size));
            select_tv_title.setText(getString(R.string.please_choose_balloon_size));
        }
        else if (getString(R.string.square).equals(productName) || getString(R.string.heart_shaped).equals(productName))
        {
            if(getString(R.string.square).equals(productName)){
                select_tv_title.setText(getString(R.string.small) + productName + " 32x40mm");
            }else {
                select_tv_title.setText(getString(R.string.small) + productName + " 35x48mm");
            }
        }
        else if (getString(R.string.transparent_box_desk_calendar).equals(productName) || getString(R.string.paper_desk_calendar).equals(productName))
        {
            if(getString(R.string.transparent_box_desk_calendar).equals(productName)){
                select_tv_title.setText(productName + getString(R.string.size_choose));
            }else {
                select_tv_title.setText(productName + getString(R.string.four_multiply_six));
            }
        }
        else if (getString(R.string.photo_frame_canvas).equals(productName))
        {
            select_tv_title.setText(getString(R.string.please_choose_photo_frame_colour));
        }
        else if (getString(R.string.jingxuan_photo).equals(productName))
        {
            select_tv_title.setText(getString(R.string.please_choose_photo_frame_colour));
        }
    }
    
    public void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        listView.setLayoutParams(params);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn: // 取消按钮
                finish();
                break;
            case R.id.edit_btn: // 完成按钮
              
                Intent dataIntent = getIntent();
                DataItem dataItem = adapter.getSelectItemData();
                if(dataItem != null)
                {
                    if (dataItem.getGourp() == null)
                    {
                        String name = dataItem.getName();
                        dataIntent.putExtra(Const.ITEMNAME, name);
                    }else{
                        String name = dataItem.getName()+dataItem.getGourp();
                        dataIntent.putExtra(Const.ITEMNAME, name);
                    }
                    String path = String.valueOf(dataItem.getPath());
                    dataIntent.putExtra(Const.ITEMPATH, path);
                }
                setResult(1, dataIntent);
                finish();
                break;
            default:
                break;
        }

    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
       
        adapter.clear();
        adapter = null;
    };

}
