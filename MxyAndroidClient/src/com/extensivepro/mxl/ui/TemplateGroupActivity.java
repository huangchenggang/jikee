package com.extensivepro.mxl.ui;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.Goods;
import com.extensivepro.mxl.app.bean.Group;
import com.extensivepro.mxl.app.bean.Templates;
import com.extensivepro.mxl.product.ProductManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.MyGridView;
import com.extensivepro.mxl.widget.TemplateGroupAdapter;
import com.extensivepro.mxl.widget.TitleBar;

public class TemplateGroupActivity extends BaseActivity implements
        OnClickListener
{

    private static final String TAG = "TemplateGroupActivity";

    private TitleBar mTitleBar;

    private MyGridView mGridView;

    private String groupid;

    private Templates dataItem;

    private Group backGroup;

    private GroupReceiver receiver;

    private TemplateGroupAdapter adapter;

    private Context mcontext;

    private String postion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mcontext = this;
        setContentView(R.layout.template_group);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitleVisibility(View.INVISIBLE);
        mTitleBar.setBackBtnText(R.string.reg_cancel);
        mTitleBar.setEditBtnVisibility(View.VISIBLE);
        mTitleBar.setEditBtnText(R.string.reg_confirm);
        mTitleBar.findViewById(R.id.back_btn).setOnClickListener(this);
        mTitleBar.findViewById(R.id.edit_btn).setOnClickListener(this);
        mGridView = (MyGridView) findViewById(R.id.mGridView);
        groupid = getIntent().getStringExtra(Const.GROUPID_OBJ);
        postion = getIntent().getStringExtra(Const.POSITION);

        ProductManager.getInstance().loadTemplateGroup(groupid);

        regReceiver();

    }

    private void regReceiver()
    {
        if (receiver == null)
        {
            receiver = new GroupReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_LOAD_GROUP_SUCCESS);
            registerReceiver(receiver, filter);
        }
    }

    private void unregReceiver()
    {
        if (receiver != null)
        {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregReceiver();
        if(adapter != null)
        {
            adapter.clearCache();
            adapter = null;
        }
    }

    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        if(adapter != null)
        {
            adapter.pauseDownload();
        }
    }
    
    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        if(adapter != null)
        {
            adapter.resumeDownload();
        }
    }
    
    private class GroupReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (action != null
                    && action.equals(Const.ACTION_LOAD_GROUP_SUCCESS))
            {
                backGroup = (Group) intent
                        .getSerializableExtra(Const.GROUPBACKBEAN_OBJ);
                initUI();
            }

        }
    }

    /**
     * @Description 初始化界面
     * @author
     */
    public void initUI()
    {
        List<Templates> lists = backGroup.getTemplates();
        adapter = new TemplateGroupAdapter(mcontext, lists);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int arg2,
                    long arg3)
            {
                Logger.d(TAG, "onItemClick()[arg2:"+arg2+"]");
                dataItem = adapter.backdata(arg2);
                adapter.setViewSelect(arg2);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                finish();

                break;
            case R.id.edit_btn:
                Intent groupFinishIntent = new Intent();
                groupFinishIntent.putExtra(Const.TEMPLATEGROUP_OBJ,dataItem);
                groupFinishIntent.putExtra(Const.POSITION,postion);
                setResult(RESULT_OK, groupFinishIntent);
                finish();

            default:
                break;
        }

    }

}
