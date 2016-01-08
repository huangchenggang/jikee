package com.extensivepro.mxl.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.Goods;
import com.extensivepro.mxl.app.bean.Product;
import com.extensivepro.mxl.app.bean.SpecificationSet;
import com.extensivepro.mxl.app.bean.SpecificationValueStore;
import com.extensivepro.mxl.app.bean.Templates;
import com.extensivepro.mxl.app.cart.CartManager;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.app.client.IBusinessCallback;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.app.provider.MxlTables.TGoodsCategory;
import com.extensivepro.mxl.product.ProductManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.DataUtil;
import com.extensivepro.mxl.util.DialogHelper;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.ImageDownloader.DownloadCallback;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.util.OSSUploadImageTask;
import com.extensivepro.mxl.util.OSSUploadSingleImageTask;
import com.extensivepro.mxl.widget.GuideBar;
import com.extensivepro.mxl.widget.PhotoAlbumAdapter;
import com.extensivepro.mxl.widget.TitleBar;

public class PhotoAlbumActivity extends BaseActivity implements OnClickListener
{
    private static final String TAG = "PhotoAlbumActivity";

    private TitleBar mTitleBar;

    private ListView mListView;

    private PhotoAlbumAdapter adapter;

    private int type; // 判断传进来的 类型

    private ProductReceiver receiver;

    private TextView change_tv, mPrize, tv_metaDescription, add_shoppingcart;

    private Goods curGoods;

    private ImageView iv_album;

    private String id; // 商品的id 由上一个界面传过来的

    private ProgressBar progressBar;

    private String name;

    private String metaDescription;

    private List<Product> productSetlists;

    private String photoStyleID;

    private GuideBar mGuideBar;

    private int vis;


    private String groupID;

    private ArrayList<String> lists;

    private Templates backTemplates;

    private List<String> images;

    private List<SpecificationSet> specificationSets;

    private String myID;

    private String specificationId;

    private List<String> myIDs;

    private List<String> specificationIds;

    private int mSelectPos;

    private View mPriceLayout;
    
    private ArrayList<String> imagePaths;

    private ProgressDialog dialogView;

    private HashMap<String, String> ossLocalImageMapping = new HashMap<String, String>();
    
    private TextView mDetialName;
    private TextView mDetialDesc;
    
    private UploadState mUploadState = UploadState.IDEL;
    
    private String mCurChooseItemName = "";
    
    private ImageDownloader mImageDownloader;
    
    /**
     * 连续按两次返回键就退出
     */
    private int keyBackClickCount = 0;
    
    private int imagePathsNum = 0;
    
    private int tempNum = 0;
    
    private int picNum = 0;
    
    enum UploadState
    {
        IDEL,BUSY,CANCEl;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mImageDownloader = new ImageDownloader();
//        mImageDownloader = ImageDownloader.getInstance();
        setContentView(R.layout.photo_album);
        mGuideBar = (GuideBar) getParent().findViewById(R.id.m_guide_bar);

        progressBar = (ProgressBar) findViewById(R.id.load_progress);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mListView = (ListView) findViewById(R.id.photo_album_listview);
        change_tv = (TextView) findViewById(R.id.change_photo_color);
        iv_album = (ImageView) findViewById(R.id.iv_album);
        add_shoppingcart = (TextView) findViewById(R.id.add_shoppingcart);

        mPrize = (TextView) findViewById(R.id.tv_prize);
        mPriceLayout = findViewById(R.id.price_layout);
        tv_metaDescription = (TextView) findViewById(R.id.tv_metaDescription);
        mDetialName = (TextView) findViewById(R.id.detial_name);
        mDetialDesc = (TextView) findViewById(R.id.detial_desc);
        change_tv.setOnClickListener(this);
        findViewById(R.id.add_shoppingcart).setOnClickListener(this);
        regReceiver();
        // 初始化界面本地数据
        Intent icomingIntent = getIntent();
        Goods curGoods = (Goods) icomingIntent
                .getSerializableExtra(Const.EXTRA_GOODS_OBJ);
        if(curGoods != null)
        {
            id = curGoods.getId();
            name = curGoods.getName();
            if(name.equals("mObjname")){
                PhotoAlbumAdapter.mObjname =1;
            } else {
                PhotoAlbumAdapter.mObjname =0;
            }
           
            metaDescription = curGoods.getMetaDescription();
            ProductManager.getInstance().loadProductDeatail(curGoods.getId());
            String CategoryId = curGoods.getGoodsCategory().getId();
            initTypeByCategoryID(CategoryId);
            // 判断id过滤3个 不需要 选择样式的
            changButtonIsShow();
            showBasicInfo(curGoods);
            showLoadProgress();
        }
        else if(!TextUtils.isEmpty(ProductManager.getInstance().getCurSelectGoodsId()))
        {
            mTitleBar.setTitle("");
            showLoadProgress();
            String goodId = ProductManager.getInstance().getCurSelectGoodsId();
            ProductManager.getInstance().loadProductDeatail(goodId);
           findViewById(R.id.free_icon).setVisibility(View.VISIBLE);
//           ClientManager.getInstance().regCallback(new IBusinessCallback() {
//            
//            @Override
//            public void notifyMgr(InputStream stream, int reqCode)
//            {
//                // TODO Auto-generated method stub
//                String resp = DataUtil.streamToString(stream);
//               
//                lists = new ArrayList<String>();
//                try
//                {
//                    JSONObject obj = new JSONObject(resp);
//                    JSONObject goods =  obj.optJSONObject("goods");
//                    JSONArray goodarray= goods.optJSONArray("specificationSet");
//                  
//                  
////                    JSONArray array = goodarray.optJSONArray("specificationValueStore");
//                    for(int i=0;i<goodarray.length();i++){
//                        android.util.Log.e("minrui", "goods="+goodarray.optJSONObject(i).toString());
////                        String groupid = goodarray.optJSONObject(i).optString("id");
////                       
//                       JSONArray arr = goodarray.optJSONObject(i).optJSONArray("specificationValueStore");
//                       for(int j=0;j<arr.length();j++){
//                           groupID = arr.optJSONObject(j).optString("templateGroupId");
//                        if (groupID != null && !"无模板选择".equals(groupID)){
//                            lists.add(groupID);
//                        }
//                        }
//                    }
//                }
//                catch (JSONException e)
//                {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
             mHandler.sendEmptyMessage(0);
//                
//            }
//            
//            @Override
//            public void notifyError(int reqCode, int errorCode)
//            {
//                // TODO Auto-generated method stub
//                
//            }
//        });
//           adapter = new PhotoAlbumAdapter(
//                   PhotoAlbumActivity.this, 1, ItemName, lists);
//           mListView.setAdapter(adapter);
        }
        
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                switch(scrollState)
                {
                    case OnScrollListener.SCROLL_STATE_IDLE:
                        adapter.clearInVisibleImageCache(
                                mListView.getFirstVisiblePosition(),
                                mListView.getLastVisiblePosition());
                        break;
                }
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount)
            {
                // TODO Auto-generated method stub
                
            }
        });
        

    }

    private Handler mHandler = new Handler(){
       public void handleMessage(android.os.Message msg) {
           int type=2;
           String ItemName=getResources().getString(R.string.transparent_small_square);

           lists = new ArrayList();
           lists.add("8a049ab53ea40953013ea73ec21d0058");      
           lists.add("8a049ab53ea40953013ea73ec21d0058");     
           adapter = new PhotoAlbumAdapter(
                   PhotoAlbumActivity.this, type, ItemName, lists);
           mListView.setAdapter(adapter);
           setListViewHeightBasedOnChildren(mListView);
           change_tv.setVisibility(View.GONE);
           add_shoppingcart.setVisibility(View.VISIBLE);
           findViewById(R.id.hide_list_margin).setVisibility(View.GONE);
       };
    };
    private void showBasicInfo(Goods goods)
    {
        if(goods == null )
        {
            return;
        }
        id = goods.getId();
        name = goods.getName();
        metaDescription = goods.getMetaDescription();
        tv_metaDescription.setText(metaDescription);
        mDetialDesc.setText(name);
        if(goods.getPrice() == 0 && !goods.isSaleOnlyOne())
        {
            mPriceLayout.setVisibility(View.INVISIBLE);
        }
        else if(goods.getPrice() != 0 && change_tv.getVisibility() == View.GONE)
        {
            mPriceLayout.setVisibility(View.VISIBLE);
            mPrize.setText(String.valueOf(goods.getPrice()));
        }
        else if(goods.getPrice() == 0 && goods.isSaleOnlyOne())
        {
            mPriceLayout.setVisibility(View.VISIBLE);
            mPrize.setText(String.valueOf(goods.getPrice()));
        }
        mTitleBar.setTitle(goods.getName());
        if(goods.getGoodsCategory()!=null)
        {
            Cursor cursor = getContentResolver().query(TGoodsCategory.CONTENT_URI,
                    new String[] { TGoodsCategory.CATEGORY_NAME },
                    TGoodsCategory.CATEGORY_ID + "='" + goods.getGoodsCategory().getId() + "'", null,
                    null);
            try
            {
                if(cursor.getCount() > 0)
                {
                    cursor.moveToNext();
                    String title = cursor.getString(cursor
                            .getColumnIndex(TGoodsCategory.CATEGORY_NAME));
                    mDetialName.setText(title);
                }
            }
            finally
            {
                if(cursor != null)
                {
                    cursor.close();
                }
            }
        }
        else 
        {
            mDetialDesc.setText(name);
        }
    }
    
    private void showLoadProgress()
    {
        dialogView = new ProgressDialog(this);
        dialogView.setMessage(getString(R.string.dialog_title_load));
        dialogView.show();
    }
    
    private void changButtonIsShow()
    {
        if(getString(R.string.photo_frame_canvas).equals(name)) {
            change_tv.setText(getString(R.string.photo_frame_colour_choose));
        }else if(getString(R.string.circular_balloon).equals(name)||getString(R.string.heart_balloon).equals(name)||getString(R.string.star_balloon).equals(name)){
            change_tv.setText(getString(R.string.balloon_size_choose));
        }else if(getString(R.string.paper_desk_calendar).equals(name)||getString(R.string.transparent_box_desk_calendar).equals(name)){
            change_tv.setText(getString(R.string.desk_calendar_size_choose));
        }else if(getString(R.string.jingxuan_photo).equals(name)){
            change_tv.setText(getString(R.string.photo_colour_choose));
        }
        
        if (getString(R.string.no_frame_canvas).equals(name) || getString(R.string.hava_support_canvas).equals(name) || getString(R.string.jingxuanzhangzhongbao).equals(name))
        {
            change_tv.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            findViewById(R.id.hide_list_margin).setVisibility(View.GONE);
            add_shoppingcart.setVisibility(View.VISIBLE);
            if (adapter == null)
            {
                adapter = new PhotoAlbumAdapter(PhotoAlbumActivity.this, type,
                        null, null);
                mListView.setAdapter(adapter);
            }
            else
            {
                adapter.setItemName(null);
                adapter.setType(type);
                adapter.notifyDataSetChanged();
            }
            setListViewHeightBasedOnChildren(mListView);
        }
        else
        {
            change_tv.setVisibility(View.VISIBLE);
            add_shoppingcart.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            findViewById(R.id.hide_list_margin).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        HomeActivity.keyBackClickCount=3;
        vis = mGuideBar.getVisibility();
        // 4 是无 0是有
        if (vis == 4)
        {
            mGuideBar.setVisibility(View.VISIBLE);
        }
        mImageDownloader.resumeDownload();
        if(adapter != null)
        {
            adapter.resumeDownload();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (vis == 4)
        {
            mGuideBar.setVisibility(View.INVISIBLE);
        }
        if(adapter != null)
        {
            adapter.clearInVisibleImageCache(mListView.getFirstVisiblePosition(),
                    mListView.getLastVisiblePosition());
            adapter.pauseDownload();
        }
        mImageDownloader.pauseDownload();
    }

    private void initTypeByCategoryID(String CategoryId)
    {
        if ("402880e83e779c62013e77e94fd40005".equals(CategoryId))
        {
            type = 1;
        }
        else if ("402880e83e779c62013e77f9d3100027".equals(CategoryId))
        {
            type = 2;
        }
        else if ("402880e83e779c62013e77fa90a30029".equals(CategoryId))
        {
            type = 3;
        }
        else if ("402880e83e779c62013e77f898da0023".equals(CategoryId))
        {
            type = 4;
        }
        else
        { // 默认值
            type = 5;
        }

    }

    private void regReceiver()
    {
        if (receiver == null)
        {
            receiver = new ProductReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_LOAD_PRODUCTDETAIL_SUCCESS);
            filter.addAction(Const.ACTION_ADD_CART_ITEM_SUCCSEE);
            filter.addAction(Const.ACTION_LOAD_PRODUCTDETAIL_FAILED);
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
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.change_photo_color: // 相册边框选择界面
                Intent selectIntent = new Intent(this,
                        SelectPhotoColorActivity.class);
                selectIntent.putExtra(Const.PRODUCTNAME, name);
                startActivityForResult(selectIntent, Const.SELECT_ALBUM);
                break;
            case R.id.add_shoppingcart: // 添加购物车
                //add start
            	if (adapter.getType() == 1) {
    				if (backTemplates == null) {
    					Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_temp),Toast.LENGTH_SHORT).show();
    					return;
    				}
    				if(images == null){
    					Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic),Toast.LENGTH_SHORT).show();
    					return;
    				}
    				}
    				
    			if (adapter.getType() == 2) {
    				if(mCurChooseItemName.contains(getString(R.string.small_square))){//小方形32x40mm
    					if(adapter.getItemName().contains(getString(R.string.colour_transparent)+getString(R.string.small_square))){//透明色小方形32x40mm
    						if (backTemplates == null) {
    							Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_temp),Toast.LENGTH_SHORT).show();           //桃红色小方形32x40mm   透明色小方形32x40mm 
    							return;                                                                                                              //桃红色大方形51x68mm   透明色大方形51x68mm
    						}                                                                                                                         //桃红色小心形35x48mm   透明色小心形35x48mm
    						if(tempNum<2){                                                                                                            //桃红色大心形50x65mm   桃红色大心形50x65mm
    							Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_temp_continue),Toast.LENGTH_SHORT).show();
    							return;
    						}
    						if(picNum<2){
    							Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
    							return;
    						}
    						if(imagePathsNum != 14){
    							Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
    							return;
    						}
    					}
    					
    					if(imagePaths == null){
    						Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic),Toast.LENGTH_SHORT).show();
    						return;
    					}
    					if(imagePathsNum != 14){
    						Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
    						return;
    				}
    				}else if(mCurChooseItemName.contains(getString(R.string.big_square))){//大方形51x68mm
    				    if(adapter.getItemName().contains(getString(R.string.colour_transparent)+getString(R.string.big_square))){//透明色大方形51x68mm
                            if (backTemplates == null) {
                                Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_temp),Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(tempNum<2){
                                Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_temp_continue),Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(picNum<2){
                                Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(imagePathsNum != 12){
                                Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
                                return;
                            }
    				    }
    				    if(imagePaths == null){
                            Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(imagePathsNum != 12){
                            Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
                            return;
    				    }
    				}else if(mCurChooseItemName.contains(getString(R.string.small_heart_shaped))){//小心形35x48mm
                        if(adapter.getItemName().contains(getString(R.string.colour_transparent)+getString(R.string.small_heart_shaped))){//透明色小心形35x48mm
                            if (backTemplates == null) {
                                Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_temp),Toast.LENGTH_SHORT).show();           //桃红色小方形32x40mm   透明色小方形32x40mm 
                                return;                                                                                                              //桃红色大方形51x68mm   透明色大方形51x68mm
                            }                                                                                                                         //桃红色小心形35x48mm   透明色小心形35x48mm
                            if(tempNum<2){                                                                                                            //桃红色大心形50x65mm   桃红色大心形50x65mm
                                Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_temp_continue),Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(picNum<2){
                                Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(imagePathsNum != 10){
                                Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        
                        if(imagePaths == null){
                            Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(imagePathsNum != 10){
                            Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
                            return;
                    }
                    }else if(mCurChooseItemName.contains(getString(R.string.big_heart_shaped))){//大心形50x65mm
                        if(adapter.getItemName().contains(getString(R.string.colour_transparent)+getString(R.string.big_heart_shaped))){//透明色大心形50x65mm
                            if (backTemplates == null) {
                                Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_temp),Toast.LENGTH_SHORT).show();           //桃红色小方形32x40mm   透明色小方形32x40mm 
                                return;                                                                                                              //桃红色大方形51x68mm   透明色大方形51x68mm
                            }                                                                                                                         //桃红色小心形35x48mm   透明色小心形35x48mm
                            if(tempNum<2){                                                                                                            //桃红色大心形50x65mm   桃红色大心形50x65mm
                                Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_temp_continue),Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(picNum<2){
                                Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(imagePathsNum != 12){
                                Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        
                        if(imagePaths == null){
                            Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(imagePathsNum != 12){
                            Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
                            return;
                    }
                    }else{
    					if(imagePathsNum != 10){
    						Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
    						return;
    				}
    			}
    			}
    			if (adapter.getType() == 3) {
    				if(backTemplates == null){
    					Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic),Toast.LENGTH_SHORT).show();
    					return;
    				}
    				if(tempNum<12){
    					Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_temp_continue),Toast.LENGTH_SHORT).show();
    					return;
    				}
    				if(picNum<12){
    					Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
    					return;
    				}
    				
    			}
    			if (adapter.getType() == 4) {
    				if(images == null){
    					Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic),Toast.LENGTH_SHORT).show();
    					return;
    				}
    			}
    			if (adapter.getType() == 5) {
    				if (name.contains(getString(R.string.jingxuanzhangzhongbao))) {//晶炫掌中宝
    					if(images == null){
    						Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic),Toast.LENGTH_SHORT).show();
    						return;
    					}
    					if(picNum != 2){
    						Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
    						return;
    					}
    					if(imagePathsNum != 24){
    						Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
    						return;
    					}
    				}else {
    					if(images == null){
    						Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic),Toast.LENGTH_SHORT).show();
    						return;
    					}
    					if(imagePathsNum != 14){
    						Toast.makeText(PhotoAlbumActivity.this,getString(R.string.please_add_pic_continue),Toast.LENGTH_SHORT).show();
    						return;
    					}
    				}
    			}
    			//add end
                if (AccountManager.getInstance().hasLogined())
                {
                    if(mUploadState == UploadState.IDEL)
                    {
                        CartManager.getInstance().addShoppingTrollery(photoStyleID,1,adapter.getCarItemCustomValues(specificationIds));
                    }
                    else if(mUploadState == UploadState.BUSY)
                    {
                        DialogHelper
                                .createDialog(
                                        this,
                                        getString(R.string.photo_album_dialog_un_finish_upload_pic),
                                        "", -1).show();
                    }
                }
                else
                {
                    if (getParent() instanceof HomeActivity)
                    {
                        Intent intent = new Intent(PhotoAlbumActivity.this,
                                LoginActivity.class);
                        ((HomeActivity) getParent()).setCurrentTab(2);
                        ((HomeActivity) getParent()).startActivityWithGuideBar(
                                LoginActivity.class, intent);
                    }
                }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case Const.SELECT_PHOTO: // 相册单张选择
                if (data != null)
                {
                    mSelectPos = adapter.getmSelectPos();
                    Uri selectedImage = data.getData();
                    Logger.d(TAG, "onActivityResult()[selectedImageUri:"
                            + selectedImage + "]");
                    String[] filePathColumns = { MediaStore.Images.Media.DATA };
                    Cursor c = this.getContentResolver().query(selectedImage,
                            filePathColumns, null, null, null);
                    String picturePath = "";
                    if (c != null && c.getCount() > 0)
                    {
                        try
                        {
                            c.moveToFirst();
                            int columnIndex = c
                                    .getColumnIndex(filePathColumns[0]);
                            picturePath = c.getString(columnIndex);
                        }
                        finally
                        {
                            c.close();
                        }
                    }
                    else
                    {
                        picturePath = selectedImage.getPath();
                    }
                    images = new ArrayList<String>();// 构造定制项目.传入
                    images.add(picturePath);
                    adapter.changePic(mSelectPos, adapter.isSelectRightItem(), images);
                    adapter.notifyDataSetChanged();
                    
                    picNum +=1;//add
                    
                    uploadImage(mSelectPos, adapter.isSelectRightItem());
                }
                break;
            case Const.SELECT_PHOTOS: // 自定义多选相册

                mSelectPos = adapter.getmSelectPos();
                if (data != null)
                {
                    imagePaths = data
                            .getStringArrayListExtra(Const.ALBUM_IMAGEPATHSFROMPHOTOS);

                    if(imagePaths != null && !imagePaths.isEmpty())
                    {
                        adapter.changePic(mSelectPos, adapter.isSelectRightItem(), imagePaths);
                        adapter.notifyDataSetChanged();
                        
                        imagePathsNum +=imagePaths.size();
                        
                        uploadImage(mSelectPos, adapter.isSelectRightItem());
                    }
                }

                break;
            case Const.SELECT_ALBUM: // 选择相框边框

                if (data != null&& productSetlists != null && data.getStringExtra(Const.ITEMNAME) != null)
                {
                    String ItemName = data.getStringExtra(Const.ITEMNAME)
                            .replaceAll(" ", "");
                    if(ItemName != null && !ItemName.equals(mCurChooseItemName))
                    {
                        if(mOSSUploadImageTask != null)
                        {
                            mOSSUploadImageTask.cancelUpload();
                            mOSSUploadImageTask = null;
                        }
                        if(mOssUploadSingleImageTask != null)
                        {
                            mOssUploadSingleImageTask.cancelUpload();
                            mOssUploadSingleImageTask = null;
                        }
                        if(adapter != null)
                        {
                            adapter.clearData();
                        }
                    }
                    else if(ItemName != null && ItemName.equals(mCurChooseItemName))
                    {
                        return;
                    }
                    mCurChooseItemName = ItemName;
                    int ItemPath = Integer.valueOf(data
                            .getStringExtra(Const.ITEMPATH));
                    iv_album.setImageResource(ItemPath); // 查找规格id

                    for (int i = 0; i < productSetlists.size(); i++)
                    {
                        Product set = productSetlists.get(i);
                        Boolean isgood = true;
                        List<SpecificationValueStore> specificationValueStores = set
                                .getSpecificationValueStore();
                        for (SpecificationValueStore specificationValueStore : specificationValueStores)
                        {
                            //add 两面同一张照片印刷
                            if (!(ItemName + getString(R.string.two_face_only_one_photo)).contains((specificationValueStore
                                    .getName().replaceAll(" ", "")))
                                    && specificationValueStore
                                            .getTemplateGroupId() == null
                                    && specificationValueStore.getType() == null)
                            {
                                isgood = false;
                                break;
                            }

                        }
                        if (isgood)
                        {
                            specificationIds = null;
                            photoStyleID = null;
                            myIDs = null;
                            photoStyleID = set.getId();
                            List<SpecificationValueStore> valueStores = set
                                    .getSpecificationValueStore();
                            for (SpecificationValueStore specificationValueStore : valueStores)
                            {
                                if (specificationValueStore.getType() != null)
                                {
                                    if ("custom".equals(specificationValueStore
                                            .getType())
                                            && ((specificationValueStore
                                                    .getTemplateGroupId() != null) || (specificationValueStore
                                                    .getImageCount() != null))
                                            && !getString(R.string.no_temp)
                                                    .equals(specificationValueStore
                                                            .getName()))
                                    {

                                        myID = specificationValueStore.getId();
                                        if (myIDs == null)
                                        {
                                            myIDs = new ArrayList<String>();
                                        }
                                        myIDs.add(myID);
                                        myID = null;
                                    }
                                }
                            }
                            for (int j = 0; j < specificationSets.size(); j++)
                            {
                                SpecificationSet specifiset = specificationSets
                                        .get(j);
                                List<SpecificationValueStore> out_valueStores = specifiset
                                        .getSpecificationValueStore();
                                for (SpecificationValueStore specificationValueStore : out_valueStores)
                                {
                                    for (int k = 0; k < myIDs.size(); k++)
                                    {
                                        String curremtString = myIDs.get(k);
                                        if (curremtString
                                                .equals(specificationValueStore
                                                        .getId()))
                                        {
                                            specificationId = specifiset
                                                    .getId();
                                            if (specificationIds == null)
                                            {
                                                specificationIds = new ArrayList<String>();
                                            }
                                            specificationIds
                                                    .add(specificationId);
                                        }
                                    }

                                }

                            }
                            Logger.i(TAG, "find out specificationIds="
                                    + specificationIds.toString());
                            mPriceLayout.setVisibility(View.VISIBLE);
                            mPrize.setText(String.valueOf(set.getPrice()));
                            lists = new ArrayList<String>();
                            for (SpecificationValueStore specificationValueStore : specificationValueStores)
                            {
                                groupID = specificationValueStore
                                        .getTemplateGroupId();
                                if (groupID != null && !getString(R.string.no_temp_choose).equals(groupID))
                                {
                                    lists.add(groupID);
                                    groupID = null;
                                }
                            }
                            Logger.d(TAG,"temp lists=:" + lists);
                        }
                    }

                    add_shoppingcart.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.VISIBLE);
                    findViewById(R.id.hide_list_margin).setVisibility(View.GONE);
                    selectBtTextTOBlue();
                    // 将ItemName传入adapter进行 选择模板按钮适配
                    if (adapter == null)
                    {
                        android.util.Log.e("minrui","ItemName="+ItemName);
                        android.util.Log.e("minrui","type="+type);
                        for(int i=0;i<lists.size();i++){
                        android.util.Log.e("minrui","lists="+lists.get(i));
                        }
                        adapter = new PhotoAlbumAdapter(
                                PhotoAlbumActivity.this, type, ItemName, lists);
                        mListView.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(mListView);
                    }
                    else
                    {
                        adapter.setLists(lists);
                        adapter.setItemName(ItemName);
                        adapter.setType(type);
                        adapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(mListView);
                    }

                }
                break;
            case Const.SELECT_TEMPLATEGROUP: // 选择模板组
                if (data != null)
                {
                    backTemplates = (Templates) data
                            .getSerializableExtra(Const.TEMPLATEGROUP_OBJ);
                    // 反查出一个参数
                    // int poisiton = Integer.valueOf(data
                    // .getStringExtra(Const.POSITION));

                    // String iconUrl = Const.BASE_URI+backTemplates.getImage();
                    adapter.changeTemp(adapter.getmSelectPos(),
                            adapter.isSelectRightItem(), backTemplates);
                    adapter.notifyDataSetChanged();
                    tempNum +=1;
                    Logger.i(TAG,
                            "choose temp group is:" + backTemplates);
                    // backTemplateslists.put(poisiton, backTemplates);

                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private OSSUploadSingleImageTask mOssUploadSingleImageTask;

    private OSSUploadImageTask mOSSUploadImageTask;
    
    
    private void uploadImage(final int pos,final boolean isRightItem)
    {
        // 调用OSS上传图片
        List<String> imageAbsPath = adapter.getUploadItemImagePath(pos, isRightItem);
        if (imageAbsPath != null && !imageAbsPath.isEmpty())
        {
            mUploadState = UploadState.BUSY;
            final String uploadUrl = imageAbsPath.get(0);
            if (imageAbsPath.size() == 1)
            { // 单张上传
                if (!ossLocalImageMapping.containsKey(uploadUrl))
                {
                    mOssUploadSingleImageTask = new  OSSUploadSingleImageTask(
                            new OSSUploadSingleImageTask.IUploadCallback() {

                                @Override
                                public void onUploadSuccess(String ossPaths)
                                {
                                    synchronized(mUploadState)
                                    {
                                        mUploadState = UploadState.IDEL;
                                    }
                                    ossLocalImageMapping.put(uploadUrl,
                                            ossPaths);
                                    List<String> newList = new ArrayList<String>();
                                    newList.add(ossPaths);
                                    if(adapter != null)
                                    {
                                        adapter.changeAlreadyUploadImage(
                                                mSelectPos, isRightItem,
                                                newList);
                                    }
                                    Logger.d(TAG,
                                            "OSSUploadSingleImageTask.onUploadSuccess()[ossPaths:"
                                                    + ossPaths + "]");
                                }

                                @Override
                                public void onUploadFailed()
                                {
                                    Logger.e(TAG,
                                            "OSSUploadSingleImageTask.onUploadFailed()");
                                }

                                @Override
                                public void onProgress(int progress, int progressCount)
                                {
                                    Logger.d(TAG,
                                            "OSSUploadSingleImageTask.onProgress()[progress:"
                                                    + progress + ",progressCount:"
                                                    + progressCount + "]");
                                    if(adapter != null)
                                    {
                                        adapter.updateProgress(pos, isRightItem,
                                                progress, progressCount);
                                    }
                                }
                            }, uploadUrl);
                    mOssUploadSingleImageTask.startUploadDelay(2000);
                }
            }
            else if (imageAbsPath.size() > 1)
            {
                // 此时做批量上传
                mOSSUploadImageTask = new OSSUploadImageTask(
                        new OSSUploadImageTask.IUploadCallback() {

                            @Override
                            public void onUploadSuccess(List<String> ossPaths)
                            {
                                Logger.d(TAG,
                                        "OSSUploadImageTask.onUploadSuccess()[ossPaths:"
                                                + ossPaths + "]");
                                synchronized(mUploadState)
                                {
                                    mUploadState = UploadState.IDEL;
                                }
                                if (ossPaths != null && !ossPaths.isEmpty() && adapter != null)
                                {
                                    adapter.changeAlreadyUploadImage(
                                            mSelectPos, isRightItem, ossPaths);
                                }
                            }

                            @Override
                            public void onUploadFailed()
                            {
                                Logger.e(TAG,
                                        "OSSUploadImageTask.onUploadFailed()");
                            }

                            public void onProgress(int progress, int progressCount)
                            {
                                Logger.d(TAG,
                                        "OSSUploadImageTask.onProgress()[progress:"
                                                + progress + ",progressCount:"
                                                + progressCount + "]");
                                if(adapter != null)
                                {
                                    adapter.updateProgress(pos, isRightItem,
                                            progress, progressCount);
                                }
                            }

                        }, imageAbsPath);
                    mOSSUploadImageTask.startUploadDelay(2000);
            }
        }
    }
    

    /**
     * @Description 设置选择条字体变绿
     * @author Administrator
     */
    private void selectBtTextTOBlue()
    {
        SpannableStringBuilder builder = new SpannableStringBuilder(change_tv
                .getText().toString());
        builder.replace(builder.length() - 3, builder.length() - 1, getString(R.string.change));
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources()
                .getColor(R.color.photo_album_detial_desc_txt_color_modify));
        builder.setSpan(greenSpan, builder.length() - 3, builder.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        change_tv.setText(builder);
    }

    private void selectBtTextTOBlack()
    {
        SpannableStringBuilder builder = new SpannableStringBuilder(change_tv
                .getText().toString());
        builder.replace(builder.length() - 3, builder.length() - 1, getString(R.string.select));
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(0xff000000);
        builder.setSpan(greenSpan, builder.length() - 3, builder.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        change_tv.setText(builder);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
//        iv_album.setVisibility(View.INVISIBLE);
//        progressBar.setVisibility(View.VISIBLE);
//        change_tv.setVisibility(View.VISIBLE);
//        selectBtTextTOBlack();
//        Intent icomingIntent = intent;
//        curGoods = (Goods) icomingIntent
//                .getSerializableExtra(Const.EXTRA_GOODS_OBJ);
//        if (curGoods != null)
//        {
//            id = curGoods.getId();
//            name = curGoods.getName();
//            mTitleBar.setTitle(name);
//            metaDescription = curGoods.getMetaDescription();
//            tv_metaDescription.setText(metaDescription);
//            String CategoryId = curGoods.getGoodsCategory().getId();
//            initTypeByCategoryID(CategoryId);
//            changButtonIsShow();
//            ProductManager.getInstance().loadProductDeatail(id);
//        }
        super.onNewIntent(intent);
    }

    private class ProductReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(dialogView != null)
            {
                dialogView.dismiss();
            }
            String action = intent.getAction();
            if (action != null
                    && action.equals(Const.ACTION_LOAD_PRODUCTDETAIL_SUCCESS))
            {
                Object obj = intent
                        .getSerializableExtra(Const.EXTRA_GOODS_OBJ);
                if(obj instanceof Goods)
                {
                    curGoods = (Goods) intent
                            .getSerializableExtra(Const.EXTRA_GOODS_OBJ);
                    productSetlists = curGoods.getProductSet();
                    specificationSets = curGoods.getSpecificationSet();
                    if ((type == 4 && !getString(R.string.photo_frame_canvas).equals(name))||getString(R.string.jingxuanzhangzhongbao).equals(name)) // 唯一的id
                    {
                        specificationIds = new ArrayList<String>();
                        for(int i=0;i<specificationSets.size();i++){
                            specificationIds.add( specificationSets.get(i).getId());
                        }
                        photoStyleID = productSetlists.get(0).getId();
                    }
                    refreshData();
                }
            }
            if (action != null && action.equals(Const.ACTION_ADD_CART_ITEM_SUCCSEE))
            {
//                (new HomeActivity()).backByShoppingTrolley("add_succsee");
                ((HomeActivity) getParent()).setISFromShoppTrolley(true);
                getParent().onBackPressed();
                goToShoppingTrollery();
            }

        }
    }

    public void refreshData()
    {
        if(curGoods == null)
        {
            return;
        }
        
        String imageUri = "";
        if(curGoods.getGoodsImageStore().size()>1)
        {
            imageUri =  curGoods.getGoodsImageStore().get(1)
                    .generateUrl();
        }
        else 
        {
            imageUri = curGoods.getGoodsImageStore().get(0)
                    .generateUrl();
        }

        // 接收过来的的参数，向服务器获取数据 刷新在界面上
        final int maxWidth = ClientManager.getInstance().getScreenWidth()/2;
        Bitmap bmp = mImageDownloader.downloadImage(new DownloadCallback() {

            @Override
            public void onLoadSuccess(Bitmap bitmap)
            {
                if (bitmap != null)
                {
                    progressBar.setVisibility(View.GONE);
                    iv_album.setVisibility(View.VISIBLE);
                    iv_album.setImageBitmap(bitmap);
                }

            }

            @Override
            public void onLoadFailed()
            {

            }
        }, imageUri, maxWidth);
        if(bmp != null)
        {
            progressBar.setVisibility(View.GONE);
            iv_album.setVisibility(View.VISIBLE);
            iv_album.setImageBitmap(bmp);
        }
        showBasicInfo(curGoods);
    }

    /**
     * @Description 动态设置listview的高度，解决scrollview和listview的冲突问题
     * @author Administrator
     * @param listView
     */
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
    protected void onDestroy()
    {
    	imagePathsNum = 0;
    	tempNum = 0;
    	picNum = 0;
        HomeActivity.keyBackClickCount=0;
        
        mImageDownloader.clearCache();
        if(adapter != null)
        {
            adapter.clearCache();
        }
        mImageDownloader = null;
        adapter = null;
        if(mOSSUploadImageTask != null)
        {
            mOSSUploadImageTask.cancelUpload();
        }
        if(mOssUploadSingleImageTask != null)
        {
            mOssUploadSingleImageTask.cancelUpload();
        }
        unregReceiver();
        super.onDestroy();
    }

    private void goToShoppingTrollery()
    {
        if (getParent() instanceof HomeActivity)
        {
            ((HomeActivity) getParent()).setCurrentTab(3);
            ((HomeActivity) getParent()).startActivityWithGuideBar(
                    ShoppingTrolleyActivity.class, null);
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("zhh-->", "photoAlbumActivity");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (keyBackClickCount++) {
            case 0:
                Toast.makeText(getApplicationContext(), getString(R.string.exit_again), Toast.LENGTH_SHORT).show(); 
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        keyBackClickCount = 0;
                    }
                }, 2222);
                break;
            case 1:
                PhotoAlbumActivity.this.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            default:
                break;
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
        }
        return super.onKeyDown(keyCode, event);
    }
    
}
