package com.extensivepro.mxl.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.extensivepro.mxl.util.Logger;

/**
 * 
 * @Description User defined guide bar,instead of android.widget.TabHost
 * @author damon
 * @date Apr 19, 2013 4:30:12 PM
 * @version V1.3.1
 */
public class GuideBar extends LinearLayout implements OnClickListener
{
    private static final String TAG = GuideBar.class.getSimpleName();

    private int mSelectTabIndex = -1;

    private ViewGroup mTabContianer;

    private OnTabChangeListener mTabChangeListener;

    private int[] mTabIndexs;

    public GuideBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GuideBar(Context context)
    {
        super(context);
    }

    public void setUpTabs(int layoutId)
    {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTabContianer = (ViewGroup) inflater.inflate(layoutId, null);
        addView(mTabContianer);
        mTabIndexs = new int[mTabContianer.getChildCount()];
        for (int i = 0; i < mTabContianer.getChildCount(); i++)
        {
            mTabIndexs[i] = i;
            mTabContianer.getChildAt(i).setTag(mTabIndexs[i]);
            mTabContianer.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v)
    {
        Logger.d(TAG, "onClick()");
        Object obj = v.getTag();
        int index = -1;
        if (obj instanceof Integer)
        {
            index = (Integer) obj;
            Logger.d(TAG, "onClick()[clicked tab index:" + index + "]");
        }
        setCurrentTab(index);
    }

    public void setCurrentTab(int index)
    {
        Logger.d(TAG, "setCurrentTab()[index:" + index + ",mSelectTabIndex:"
                + mSelectTabIndex + ",mTabContianer is null:"
                + (mTabContianer == null) + "]");
        if (index == mSelectTabIndex || index == -1 || mTabContianer == null)
        {
            return;
        }
        if (mSelectTabIndex != -1
                && mSelectTabIndex < mTabContianer.getChildCount())
        {
            mTabContianer.getChildAt(mSelectTabIndex).setSelected(false);
        }
        mTabContianer.getChildAt(index).setSelected(true);
        mSelectTabIndex = index;
        if (mTabChangeListener != null)
        {
            mTabChangeListener.onTabChange(index,
                    mTabContianer.getChildAt(index));
        }
    }
    
    public int getCurrentTabIndex()
    {
        if(mSelectTabIndex != -1)
        {
            return mSelectTabIndex;
        }
        return 0;
    }

    public void setOnTabChangeListener(OnTabChangeListener tabChangeListener)
    {
        mTabChangeListener = tabChangeListener;
    }

    public interface OnTabChangeListener
    {
        void onTabChange(int index, View v);
    }
    
    public void setTabIconBackground(int index,int resId)
    {
        mTabContianer.getChildAt(index).setBackgroundResource(resId);
    }
    
    public void setTabEnable(int index,boolean enabled)
    {
        mTabContianer.getChildAt(index).setEnabled(enabled);
    }
}
