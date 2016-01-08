package com.extensivepro.mxl.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.app.client.IBusinessCallback;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.util.DataUtil;
import com.extensivepro.mxl.widget.ContentItem;
import com.extensivepro.mxl.widget.ContentListAdapter;
import com.extensivepro.mxl.widget.EllipsizeableTextView;
import com.extensivepro.mxl.widget.TitleBar;
import com.google.gson.JsonArray;

public class AccountNotifyMsgActivity extends BaseActivity implements
        OnClickListener, IBusinessCallback
{
    private TitleBar mTitleBar;
    private AccountManager mAccountManager = AccountManager.getInstance();
    private ClientManager mClientManager  = ClientManager.getInstance();
    private ListView mListView;
    private ContentListAdapter mAdapter; 
    private List<ContentItem> mList= new ArrayList<ContentItem>();
    protected void onCreate(android.os.Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mClientManager.regCallback(this);
        mAccountManager.getNotifyMsgs();
        setContentView(R.layout.account_msgs);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle(R.string.notify_msg_txt);
        mListView = (ListView) findViewById(R.id.msg_list);
      
        
    };

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyMgr(InputStream stream, int reqCode)
    {
        // TODO Auto-generated method stub
        String resp = DataUtil.streamToString(stream);
//        for(int i=0;i<9;i++){
//            String content = "hhhhhhfdsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
//            String date =  "2012-02";
//            ContentItem item = new ContentItem();
//            item.setContent(content);
//            item.setDate(date);
//            mList.add(item);
//       }
//        mHandler.sendEmptyMessage(0);
      
        try
        {
            JSONObject obj =  new JSONObject(resp);         
            String status = obj.optString("status");
            mList.clear();
            if("success".equals(status)){
                 JSONArray array = obj.getJSONArray("list");
                 for(int i=0;i<array.length();i++){
                      JSONObject jobj = array.getJSONObject(i);
                      String content = jobj.optString("content");
                      String date =  jobj.optString("create_date");
                      ContentItem item = new ContentItem();
                      item.setContent(content);
                      item.setDate(date);
                      mList.add(item);
                 }
            }
            
            mHandler.sendEmptyMessage(0);
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
        
    }

    @Override
    public void notifyError(int reqCode, int errorCode)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        mClientManager.unregCallback();
    }
    
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mAdapter = new ContentListAdapter(AccountNotifyMsgActivity.this,mList);
            mListView.setAdapter(mAdapter);
        };
    };

   
}
