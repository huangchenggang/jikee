package com.extensivepro.mxl.ui;

import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.TitleBar;

/**
 * @Description
 * @author Admin
 * @date 2013-5-11 上午11:21:04
 * @version V1.3.1
 */

public class AboutMxlAppActivity extends BaseActivity implements
        OnClickListener
{

    private static final String TAG = AboutMxlAppActivity.class.getSimpleName();

    private TitleBar mTitleBar;
    private Bitmap mBitmap ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_mxl_app);

        findViewById(R.id.software_license_agreement).setOnClickListener(this);
        findViewById(R.id.special_instruction).setOnClickListener(this);
        findViewById(R.id.use_help).setOnClickListener(this);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle(R.string.about_mxl_app_txt);
        ImageView img = (ImageView) findViewById(R.id.version_icon);
        InputStream is = getResources().openRawResource(R.drawable.ic_launcher);    
        mBitmap  = toRoundCorner(BitmapFactory.decodeStream(is),50);
        img.setImageBitmap(mBitmap);
    }

    private Bitmap toRoundCorner(Bitmap bitmap, int pixels)
    {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        final float roundPx = pixels;

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;

    }

    @Override
    protected void onDestroy()
    {
        if(mBitmap!=null&&!mBitmap.isRecycled()){
            mBitmap.recycle();
        }
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.software_license_agreement:
                startNextActivity(SoftwareLicenseAgereementActivity.class, null);
                break;
            case R.id.special_instruction:
                startNextActivity(SpecialInstructionActivity.class, null);
                break;
            case R.id.use_help:
                startNextActivity(HelpActivity.class, null);
                break;
            default:
                break;
        }
    }

    private void startNextActivity(Class<?> clazz, Intent intent)
    {
        if (getParent() instanceof HomeActivity)
        {
            ((HomeActivity) getParent()).startActivityWithGuideBar(clazz,
                    intent);
        }
        else
        {
            Logger.e(TAG, "startNextActivity()[failed]");
        }
    }
}
