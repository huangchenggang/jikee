package com.extensivepro.mxl.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import com.extensivepro.model.Bucket;
import com.extensivepro.mxl.R;
import com.extensivepro.util.CallbackImpl;
import com.extensivepro.util.Constant;
import com.extensivepro.util.ImageManager;


/**
 * ͼƬ�б�Adapter
 * @author admin
 *
 */
public class BucketListAdapter extends BaseAdapter{

	private Context context;
	
	public BucketListAdapter(Context context){
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ImageManager.bucketList.size();
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
		Bucket bucket = ImageManager.bucketList.get(position);
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.bucketitem, null);
		}		
		ImageView photo = (ImageView) convertView.findViewById(R.id.photo);
		TextView name = (TextView)convertView.findViewById(R.id.name);
		TextView count = (TextView)convertView.findViewById(R.id.count);
		TextView path = (TextView)convertView.findViewById(R.id.path);
		if(bucket!=null)
		{
			name.setText(bucket.getName());
			count.setText("("+bucket.getImageCount()+")");
			path.setText(bucket.getPath());				
			String imageUri = "";
			if(bucket.getImages().get(0)!=null && bucket.getImages().get(0).getThumbnails()!=null){
				   imageUri = bucket.getImages().get(0).getThumbnails().get_data();
			}
			else if(bucket.getImages().get(0)!=null && bucket.getImages().get(0).getThumbnails()==null)
			{
			    imageUri = bucket.getImages().get(0).get_data();
			}
			if(!TextUtils.isEmpty(imageUri))
			{
			    CallbackImpl callbackImpl = new CallbackImpl(photo);
			    Bitmap cacheImage = Constant.loader.loadDrawable(imageUri, callbackImpl);
			    if (cacheImage != null) {
			        photo.setImageBitmap(cacheImage);
			    }
			}
		 
		}	
		return convertView;
	}
	
	

}
