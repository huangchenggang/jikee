package com.extensivepro.mxl.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.extensivepro.model.Bucket;
import com.extensivepro.model.Images;
import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.GalleryAdapter;
import com.extensivepro.mxl.widget.GridViewAdapter;
import com.extensivepro.util.CallbackImpl;
import com.extensivepro.util.Constant;

public class BucketDetailActivity extends BaseActivity
{
    protected static final String TAG = "BucketDetailActivity";
    private Bucket bucket;
    private List<Images> imageChose = new ArrayList<Images>();
    private TextView back;
    private TextView start;
    private TextView choseCount;
    private GridView gridview;
    private GridViewAdapter gridViewAdapter;
    
    private GridView gallery;
    private GalleryAdapter galleryAdapter;
    
    private int maxSelectLimit = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucketdetail);
        Constant.loader.startLoading();
        Intent intent = getIntent();
        if (intent.hasExtra("bundle")){
            Bundle bd = intent.getBundleExtra("bundle");
            bucket = (Bucket) bd.getSerializable("bucket");
        }
        if(intent.hasExtra(Const.EXTRA_MUILT_SELECT_MAX_NUM))
        {
            maxSelectLimit = intent.getIntExtra(Const.EXTRA_MUILT_SELECT_MAX_NUM, 1);
        }
        back = (TextView) findViewById(R.id.back);
        start = (TextView) findViewById(R.id.start);
        choseCount = (TextView) findViewById(R.id.chose_count);
        gridview = (GridView)findViewById(R.id.chose_picture_grid);
        
        choseCount.setText(getResources().getString(
                R.string.buket_detial_select_pic_txt, 0, maxSelectLimit));
        
        gridViewAdapter = new GridViewAdapter(this,bucket.getImages());
        gridview.setAdapter(gridViewAdapter);
        gallery = (GridView)findViewById(R.id.Gallery);
        galleryAdapter = new GalleryAdapter(this,imageChose);
        gallery.setAdapter(galleryAdapter);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {               
                finish();
            }
        });
        
        gridview.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                Images image = bucket.getImages().get(position);
                String thumUrl = image.get_data();
               
                try
                {
                    if (getIntent().getStringExtra("itemName").contains("横幅"))
                    {
                        ExifInterface exif = new ExifInterface(thumUrl);
                        int w = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION, 0);
                        if (w != 6)
                        {
                            Toast.makeText(BucketDetailActivity.this,
                                    getResString(R.string.add_horizontal_pic), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else {
                        if (getIntent().getStringExtra("itemName").contains("竖幅"))
                        {
                            ExifInterface exif = new ExifInterface(thumUrl);
                            int w = exif.getAttributeInt(
                                    ExifInterface.TAG_ORIENTATION, 0);
                            if (w == 6)
                            {
                                Toast.makeText(BucketDetailActivity.this,
                                        getResString(R.string.add_vertical_pic), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }
                catch (Exception ee)
                {
                }
                if(imageChose.size()<maxSelectLimit)
                {
                    imageChose.add(image);
                    choseCount.setText(getResources().getString(
                            R.string.buket_detial_select_pic_txt,
                            imageChose.size(), maxSelectLimit));
                    galleryAdapter.notifyDataSetChanged();
                }
            }
            
        });
        gallery.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                Images image = imageChose.get(position);
                imageChose.remove(image);
                choseCount.setText(getResources().getString(
                        R.string.buket_detial_select_pic_txt,
                        imageChose.size(), maxSelectLimit));
                galleryAdapter.notifyDataSetChanged();
            }
            
        });
        
        start.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {               
                if (imageChose.size() < maxSelectLimit)
                {
                    Toast.makeText(
                            BucketDetailActivity.this,
                            getResources().getString(
                                    R.string.buket_detial_select_un_full_txt,
                                    maxSelectLimit), Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<String>  imagePaths=new ArrayList<String>();
               for (int i=0;i<imageChose.size();i++){
                  String path= imageChose.get(i).get_data();
                  imagePaths.add(path);
                  path=null;
               }
               Intent dataIntent =new Intent();
               dataIntent.putStringArrayListExtra(Const.ALBUM_IMAGEPATHSFROMPHOTOS, imagePaths);
               setResult(Const.BUCKETDETAILACTIVITY_BACK, dataIntent);
               finish();
            }
        });
        
    }
    
    private String getResString(int id)
    {
        return getResources().getString(id);
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        galleryAdapter = null;
        Constant.loader.cancelLoading();
        Constant.loader.clear();
    }
}
