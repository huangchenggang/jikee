package com.extensivepro.mxl.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.extensivepro.mxl.R;

public class TitleBar extends FrameLayout implements OnClickListener
{

    private TextView mTitle;
    
    public TitleBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public TitleBar(Context context)
    {
        super(context);
        init();
    }
    
    private void init()
    {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.titlebar, null);
        mTitle = (TextView) view.findViewById(R.id.title);
        addView(view);
        findViewById(R.id.back_btn).setOnClickListener(this);
    }
    
    public void setTitle(int resourceId)
    {
        mTitle.setText(resourceId);
    }
    
    public void setTitle(String title)
    {
        mTitle.setText(title);
    }

    public String getTitle()
    {
        return mTitle.getText().toString();
    }
    
    @Override
    public void onClick(View v)
    {
        onBackBtnClick();
    }
    
    public void onBackBtnClick()
    {
        Context context = getContext();
        if(context instanceof Activity && ((Activity)context).getParent() != null)
        {
            ((Activity)context).getParent().onBackPressed();
            return;
        }
        else if(context instanceof Activity)
        {
            ((Activity)context).onBackPressed();
        }
    }
    
    public void setTitleVisibility(int visibility)
    {
        mTitle.setVisibility(visibility);
    }
    
    public void setBackBtnVisibility(int visibility)
    {
        findViewById(R.id.back_btn).setVisibility(visibility);
    }
    
    public void setBackBtnText(int resid)
    {
        ((TextView)findViewById(R.id.back_btn)).setText(resid);
    }
    
    public void setEditBtnVisibility(int visibility)
    {
        findViewById(R.id.edit_btn).setVisibility(visibility);
    }
    
    public void setEditBtnText(int resid)
    {
        ((TextView)findViewById(R.id.edit_btn)).setText(resid);
    }
}
