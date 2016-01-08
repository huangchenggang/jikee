/**   
 * @Title: PostActivity.java 
 * @Package: com.extensivepro.mxl.ui 
 * @Description: TODO
 * @author Admin  
 * @date 2013-5-9 上午10:44:43 
 * @version 1.3.1 
 */

package com.extensivepro.mxl.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.LeaveMessage;
import com.extensivepro.mxl.app.share.ShareManager;
import com.extensivepro.mxl.util.Const;

/**
 * @Description
 * @author Admin
 * @date 2013-5-9 上午10:44:43
 * @version V1.3.1
 */

public class PostActivity extends BaseActivity implements OnClickListener
{

    private TextView sure_post, del_post;

    private ImageView camera_post, photo_post, pic_content;

    private EditText post_content;

    private String content;

    private LeaveMessage msg;


    private String picturePath;

    private List<String>  upLoadPicPaths;
    private File photoDir;
    Bitmap mBitmap ;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);
        sure_post = (TextView) findViewById(R.id.post_sure_post);
        del_post = (TextView) findViewById(R.id.post_del_post);
        camera_post = (ImageView) findViewById(R.id.post_camera);
        photo_post = (ImageView) findViewById(R.id.post_photo);
        pic_content = (ImageView) findViewById(R.id.pic_content);
//        pic_content.setScaleType(ScaleType.FIT_XY);
        post_content = (EditText) findViewById(R.id.post_content);
        sure_post.setOnClickListener(this);
        del_post.setOnClickListener(this);
        camera_post.setOnClickListener(this);
        photo_post.setOnClickListener(this);
        upLoadPicPaths=new ArrayList();
        msg = new LeaveMessage();
    }

    

  

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        Intent intent = new Intent();
        switch (id)
        {
            case R.id.post_sure_post:

                content = post_content.getText().toString();

                msg.setContact("aaa");

                msg.setGood("2");

                msg.setContent(content);

                ShareManager.getInstance().postMessage(msg,upLoadPicPaths);
                finish();
                
                break;
            case R.id.post_del_post:
                this.finish();
                break;
            case R.id.post_camera:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, Const.TAKE_PIC);
                break;
            case R.id.post_photo:
                intent.setAction("android.intent.action.PICK");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setType("image/*");
                this.startActivityForResult(intent, Const.SELECT_PHOTO);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case Const.SELECT_PHOTO:
                if (data != null)
                {
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = { MediaStore.Images.Media.DATA };
                    Cursor c = this.getContentResolver().query(selectedImage,
                            filePathColumns, null, null, null);
                    if (c != null && c.getCount() > 0)
                    {
                        try
                        {
                            c.moveToFirst();
                            int columnIndex = c
                                    .getColumnIndex(filePathColumns[0]);
                            picturePath = c.getString(columnIndex);
                            
                        }
                        finally
                        {
                            c.close();
                        }
                    }
                    else
                    {
                        picturePath = selectedImage.getPath();
                    }
                    pic_content.setVisibility(View.VISIBLE);
                    pic_content.setImageBitmap(null);
//                  if(mBitmap!=null&&!mBitmap.isRecycled()){
//                      mBitmap.recycle();
//                      System.gc();
//                  }

               

                final BitmapFactory.Options options = new BitmapFactory.Options();  
                options.inJustDecodeBounds = false;

                options.inSampleSize = 10; 
                Bitmap bm =  BitmapFactory.decodeFile(picturePath, options);
                    pic_content.setImageBitmap(bm);
                    
                    upLoadPicPaths.clear();
                    upLoadPicPaths.add(picturePath);
                }
                else
                {

                }

                break;
            case Const.TAKE_PIC:
                if (data != null &&resultCode!=0)
                { 
                    String sdState = Environment.getExternalStorageState();
                    if (!sdState.equals(Environment.MEDIA_MOUNTED))
                    {
                        Toast.makeText(this, R.string.sdCard_isNot_found, Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    photoDir = new File(Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                            + "/DCIM/Camera/"
                            + DateFormat.format("yyyyMMdd_hhmmss",
                                    Calendar.getInstance(Locale.CHINA))
                            + ".jpg");
                    mBitmap = (Bitmap) data.getExtras().get("data");
                    try
                    {
                        photoDir.createNewFile();

                        BufferedOutputStream os = new BufferedOutputStream(
                                new FileOutputStream(photoDir));

                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

                        os.flush();

                        os.close();
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    pic_content.setVisibility(View.VISIBLE);
                    pic_content.setImageBitmap(mBitmap);
                    System.out
                            .println("photoDir=" + photoDir.getAbsolutePath());
                    upLoadPicPaths.clear();
                    upLoadPicPaths.add(photoDir.getAbsolutePath());
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    public Bitmap transImage(String fromFile,  int width, int height)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        // 缩放图片的尺寸
        float scaleWidth = (float) width / bitmapWidth;
        float scaleHeight = (float) height / bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 产生缩放后的Bitmap对象
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
        
        if(!bitmap.isRecycled()){
            bitmap.recycle();//记得释放资源，否则会内存溢出
        }
        return resizeBitmap;
    }
}
