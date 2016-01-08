package com.extensivepro.util;




import com.extensivepro.util.AsyncImageLoader;

import android.app.Application;
import android.app.Dialog;

public class ImageApp extends Application{
	
	public static ImageApp imageApp = null;	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		imageApp = this;
		
		
	}
	
	

}
