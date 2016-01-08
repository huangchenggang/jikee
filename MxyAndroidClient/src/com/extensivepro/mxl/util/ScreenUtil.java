package com.extensivepro.mxl.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

public class ScreenUtil {

    /** 
     * 截屏方法 
     * @return 
     */  
    public static Bitmap shot(Activity activity) {  
        View view = activity.getWindow().getDecorView();  
        Display display = activity.getWindowManager().getDefaultDisplay();  
        view.layout(0, 0, display.getWidth(), display.getHeight());  
        view.setDrawingCacheEnabled(true);//允许当前窗口保存缓存信息，这样getDrawingCache()方法才会返回一个Bitmap   
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());  
        return bmp;  
    }  

    /** 
     * 截屏方法 
     * @return 
     */  
    public static Bitmap shot(View view) {  
        view.setDrawingCacheEnabled(true);//允许当前窗口保存缓存信息，这样getDrawingCache()方法才会返回一个Bitmap 
        view.buildDrawingCache();  
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());  
        return bmp;  
    }
    
    
    DisplayMetrics dm = null;
	Display display = null;
	static ScreenUtil util = null;
	private ScreenUtil(Activity activity)
	{
	    dm = new DisplayMetrics();        
	    display = activity.getWindowManager().getDefaultDisplay();
	    display.getMetrics(dm);   
	}
	
	public static ScreenUtil getInStance(Activity activity)
	{
		if(util == null)
		{
			util = new ScreenUtil(activity);
		}
		return util;
	}
	
	/**
	 * 得到手机屏幕宽度
	 * @return
	 */
	public int getWidth()
	{
		return display.getWidth();
	}
	
	/**
	 * 获得手机屏幕宽度，单位dip
	 * @return
	 */
	public int getDipWidth(){
		return display.getWidth()/(int)dm.density;
	}
	
	/**
	 * 得到手机屏幕高度
	 * @return
	 */
	public int getHeight()
	{
		return display.getHeight();
	}
	
	/**
	 * 将dp转换成px
	 * @param dp_value
	 * @return
	 */
	public int getValue(int dp_value)
	{
		return (int) (dm.density * dp_value);
	}
	/**
	 * 将px转换成dp
	 * @param px_value
	 * @return
	 */
	public int getDpValue(int px_value){
		return (int)(px_value/dm.density);
	}
	/**
	 * 将480*800下的宽度转换为更高下的
	 * @param px_value
	 * @return
	 */
	public int getLargePxValue(int px_value)
	{
		int returnValue = px_value;
		returnValue = (int) (px_value/480f*getWidth());
		return returnValue;
	}
	/**
	 * 将480*800下的高度转换为更高下的
	 * @param px_value
	 * @return
	 */
	public int getLargePxValueHeight(int px_value)
	{
		int returnValue = px_value;
		returnValue = (int) (px_value/800f*getHeight());
		return returnValue;
	}

	/**
	 * 获取横屏还是竖屏
	 * @param activity
	 * @return	横屏为0,竖屏为1
	 */
	public int getPorL(Activity activity)
	{
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		double mWidth = dm.widthPixels;
		double mHeight = dm.heightPixels;
		if(mHeight>mWidth){
			return 1;
		}else if(mHeight<mWidth){
			return 0;
		}
		return 1;
	}
	
	/**
	 * 获取状态栏高度
	 * @param activity
	 * @return
	 */
	public int getStatuHeight(Activity activity)
	{
		Rect frame = new Rect();  
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}
	
	
	
}
