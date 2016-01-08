package com.extensivepro.util;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;




import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

//该类的主要作用是实现图片的异步加载
public class AsyncImageLoader {
	//图片缓存对象
	//键是图片的URL，值是一个WeakReference对象，该对象指向一个Drawable对象
	private static Map<String, WeakReference<Bitmap>> imageCache = 
		new HashMap<String, WeakReference<Bitmap>>();
	
	private boolean mCancel;
	
	//实现图片的异步加载
	public Bitmap loadDrawable(final String imageUrl,final ImageCallback callback){
		//查询缓存，查看当前需要加载的图片是否已经存在于缓存当中
		if(imageCache.containsKey(imageUrl)){
			WeakReference<Bitmap> WeakReference=imageCache.get(imageUrl);
			if(WeakReference.get() != null){
				return WeakReference.get();
			}
		}
		
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Bitmap) msg.obj);
			}
		};
		//新开辟一个线程，该线程用于进行图片的加载
		new Thread(){
			public void run() {
			    if(mCancel)
			    {
			        return;
			    }
				Bitmap drawable=loadImageFromUrl(imageUrl);
				imageCache.put(imageUrl, new WeakReference<Bitmap>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			};
		}.start();
		return null;
	}
	
	protected Bitmap loadImageFromUrl(String imageUrl) {
		try {
			Bitmap bitmap = ImageUtil.getImageThumbnail(imageUrl, 
					Constant.width, 
					Constant.height);
			return bitmap;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void cancelLoading()
	{
	    mCancel = true;
	}
	
	public void startLoading()
	{
	    mCancel = false;
	}
	
	//回调接口
	public interface ImageCallback{
		public void imageLoaded(Bitmap imageDrawable);
	}
	
	public static void clear(){
	 
	    synchronized(imageCache)
        {
            if (!imageCache.isEmpty())
            {
                Set<String> key = imageCache.keySet();
                for (int i = 0; i < key.size(); i++)
                {
                    if (imageCache.get(key) != null
                            && imageCache.get(key).get() != null
                            && !imageCache.get(key).get().isRecycled())
                    {
                        imageCache.get(key).get().recycle();
                        System.gc();
                        imageCache.remove(key);
                    }
                }
                imageCache.clear();
            }
        }
	}
}
