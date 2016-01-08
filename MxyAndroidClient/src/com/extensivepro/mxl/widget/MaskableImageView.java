package com.extensivepro.mxl.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.extensivepro.mxl.util.Logger;

public class MaskableImageView extends ImageView
{

    private static final String TAG = MaskableImageView.class.getSimpleName();
    
    private boolean mNeedMask = false;
    
    private Bitmap mMaskBmp;
    private Rect mMaskSrc;
    
    private int mMaskBmpId;
    
    public MaskableImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public MaskableImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MaskableImageView(Context context)
    {
        super(context);
    }

    
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(mNeedMask && mMaskBmp != null)
        {
            canvas.save();
            Rect dst = new Rect(0,0,getWidth(),getHeight());
            canvas.drawBitmap(mMaskBmp, mMaskSrc, dst, new Paint());
            canvas.restore();
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Logger.d(TAG, "onTouchEvent():" + event.getAction());
        switch (event.getAction())
        {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_MASK:
                clearMask();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
    
    public void clearMask()
    {
        mNeedMask = false;
        postInvalidate();
    }
    
    private void drawMask()
    {
        mNeedMask = true;
        postInvalidate();
    }
    
    public void drawMask(int maskBmpId)
    {
        if(mMaskBmpId != maskBmpId)
        {
            mMaskBmpId = maskBmpId;
            mMaskBmp = BitmapFactory.decodeResource(getResources(), maskBmpId);
            mMaskSrc = new Rect(0, 0, mMaskBmp.getWidth(), mMaskBmp.getHeight());
            drawMask();
            return;
        }
        if(mMaskBmpId == maskBmpId)
        {
            drawMask();
            return;
        }
    }
}
