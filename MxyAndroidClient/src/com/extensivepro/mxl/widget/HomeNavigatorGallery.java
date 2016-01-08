package com.extensivepro.mxl.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.util.Logger;

public class HomeNavigatorGallery extends Gallery implements android.widget.AdapterView.OnItemLongClickListener
{

    private static final String TAG = HomeNavigatorGallery.class.getSimpleName();
    
    public HomeNavigatorGallery(Context context, AttributeSet attrs,
            int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public HomeNavigatorGallery(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public HomeNavigatorGallery(Context context)
    {
        super(context);
        init();
    }
    
    private void init()
    {
        setOnItemLongClickListener(this);
        sendMsg(AUDO_PLAY_START, AUDO_PLAY_DURATION);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Logger.d(TAG, "onTouchEvent():"+event.getAction());
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                sendMsg(AUDO_PLAY_PAUSE,0);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_MASK:
                sendMsg(AUDO_PLAY_START,AUDO_PLAY_DURATION);
                break;
            default:
                sendMsg(AUDO_PLAY_START,AUDO_PLAY_DURATION);
                break;
        }
        if(getSelectedView() != null)
        {
            View view = getSelectedView().findViewById(R.id.navigate_image);
            if(view != null)
            {
                view.onTouchEvent(event);
            }
        }
        return super.onTouchEvent(event);
    }
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY)
    {
        int kEvent;
        if(isScrollingLeft(e1, e2))
        {
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
        }
        else
        {
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        return true;
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }
    
    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2)
    {
        return e2.getX() > e1.getX();
    }
    
    private static final int AUDO_PLAY_START = 1;
    private static final int AUDO_PLAY_PAUSE = 2;
    private static final long AUDO_PLAY_DURATION = 4000;
    
    private void sendMsg(int msgWhat,long delay)
    {
        mHandler.removeMessages(AUDO_PLAY_PAUSE);
        mHandler.removeMessages(AUDO_PLAY_START);
        mHandler.sendEmptyMessageDelayed(msgWhat, delay);
    }
    
    private Handler mHandler = new Handler()
    {
      public void handleMessage(android.os.Message msg) 
      {
          Logger.d(TAG, "mHandler.handleMessage()[msg.what:"+msg.what+"]");
          switch(msg.what)
            {
                case AUDO_PLAY_START:
                    if (getSelectedItemPosition() < getCount() - 1)
                    {
                        onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                    }
                    else
                    {
                        setSelection(0, true);
                    }
                    sendMsg(AUDO_PLAY_START, AUDO_PLAY_DURATION);
                    break;
                case AUDO_PLAY_PAUSE:
                    mHandler.removeMessages(AUDO_PLAY_START);
                    break;
            }
      }  
    };
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        sendMsg(AUDO_PLAY_PAUSE,0);
    }
    
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
            long arg3)
    {
        ((MaskableImageView)arg1.findViewById(R.id.navigate_image)).drawMask(R.drawable.navigate_item_mask);
        return false;
    }
    
    public void pauseAudoPlay()
    {
        sendMsg(AUDO_PLAY_PAUSE,0);
    }
    
    public void resumeAudoPlay()
    {
        sendMsg(AUDO_PLAY_START,AUDO_PLAY_DURATION);
    }
}
