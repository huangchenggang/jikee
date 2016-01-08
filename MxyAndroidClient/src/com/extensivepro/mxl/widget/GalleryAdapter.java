package com.extensivepro.mxl.widget;

import java.util.List;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.extensivepro.model.Images;
import com.extensivepro.mxl.R;
import com.extensivepro.mxl.ui.BucketDetailActivity;
import com.extensivepro.util.CallbackImpl;
import com.extensivepro.util.Constant;

/**
 * 已选图片Adapter
 * @author admin
 *
 */
public class GalleryAdapter extends BaseAdapter{
	
	private BucketDetailActivity bucketDetail;	
	private List<Images> imagesList;
	
	public GalleryAdapter(BucketDetailActivity bucketDetail,List<Images> imagesList){
		this.bucketDetail = bucketDetail;
		this.imagesList = imagesList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imagesList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Images image = imagesList.get(position);
		if(convertView == null)
		{
			convertView = LayoutInflater.from(bucketDetail).inflate(R.layout.galleryimageitem, null);
			convertView.findViewById(R.id.delete).setVisibility(View.VISIBLE);
		}		
		ImageView photo = (ImageView) convertView.findViewById(R.id.photo);
		String thumUrl = "";
		if(image!=null && image.getThumbnails()!=null)
		{			
		    thumUrl = image.getThumbnails().get_data();
		}
		else if(image != null && image.getThumbnails() == null)
        {
		    thumUrl = image.get_data();
        }
		if(!TextUtils.isEmpty(thumUrl))
		{
		    CallbackImpl callbackImpl = new CallbackImpl(photo);
		    Bitmap cacheImage = Constant.loader.loadDrawable(thumUrl, callbackImpl);
		    if (cacheImage != null) {
		        photo.setImageBitmap(cacheImage);
		    }
		}
		return convertView;
	}

}
