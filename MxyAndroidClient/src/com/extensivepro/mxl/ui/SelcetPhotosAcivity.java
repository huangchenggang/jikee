package com.extensivepro.mxl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.extensivepro.model.Bucket;
import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.widget.BucketListAdapter;
import com.extensivepro.util.Constant;
import com.extensivepro.util.ImageManager;

public class SelcetPhotosAcivity extends BaseActivity
{
    private static final String TAG = "SelcetPhotosAcivity";

    private ListView bucktList;

    private TextView back;

    private TextView flash;

    private BucketListAdapter bucketListAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos);
        Constant.loader.startLoading();
        ImageManager.bucketList = ImageManager.loadAllBucketList(this);
        bucktList = (ListView) findViewById(R.id.bucket_list);
        bucketListAdapter = new BucketListAdapter(this);
        bucktList.setAdapter(bucketListAdapter);
        back = (TextView) findViewById(R.id.back);
        flash = (TextView) findViewById(R.id.flash);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        bucktList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                Bucket bucket = ImageManager.bucketList.get(position);
                Intent intent = getIntent();
                intent.setClass(SelcetPhotosAcivity.this,
                        BucketDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bucket", bucket);
                bundle.putString("itemName", getIntent().getStringExtra("itemName"));
                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, Const.BUCKETDETAILACTIVITY_BACK);
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case Const.BUCKETDETAILACTIVITY_BACK:
                if (data != null)
                {
                    setResult(RESULT_OK, data);
                    finish();
                }else{
                    //没有选择不做任何操作
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Constant.loader.cancelLoading();
        Constant.loader.clear();
        
    }
}
