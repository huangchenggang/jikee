package com.extensivepro.mxl.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.os.Handler;
import android.widget.Toast;

import com.extensivepro.mxl.app.MxlApplication;
import com.extensivepro.mxl.util.OSSUploadObjectAsyncTask.UploadObjectType;

public class OSSUploadSingleImageTask
{

    private static final String TAG = "OSSUploadImageTask";
    private static final int MSG_START_UPLOAD = 1;
    private static final int MSG_UPDATE_PROGRESS = 2;

    public interface IUploadCallback
    {
        void onUploadSuccess(String ossPaths);

        void onUploadFailed();

        void onProgress(int progress, int progressCount);
    }

    private String imageAbsPath;

    private IUploadCallback callback;

    private List<String> results = new ArrayList<String>();

    private int count = 0;

    private int curProgress;
    
    private boolean mCancel;
    
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg)
        {
            if(mCancel)
            {
                callback = null;
                return;
            }
            switch(msg.what)
            {
                case MSG_START_UPLOAD:
                    startUpload();
                    break;
                case MSG_UPDATE_PROGRESS:
                    if(callback != null && curProgress < 100)
                    {
                        callback.onProgress(curProgress, 0);
                        curProgress += 15;
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, 500);
                    }
                    break;
            }
        }
    };
    
    public OSSUploadSingleImageTask(IUploadCallback callback,
            String imageAbsPath)
    {
        super();
        this.callback = callback;
        if (imageAbsPath == null)
        {
            if (callback != null)
                callback.onUploadFailed();
            return;
        }
        this.imageAbsPath = imageAbsPath;
    }

    public void startUpload()
    {
        doExecute();
    }
    
    public void startUploadDelay(int delayMillis)
    {
        mHandler.sendEmptyMessageDelayed(MSG_START_UPLOAD, delayMillis);
        if(callback != null)
        {
            curProgress = 0;
            callback.onProgress(curProgress, 0);
        }
    }
    
    private void doExecute()
    {
        try
        {
            String fileName = FileMD5.getFileMD5String(imageAbsPath);
            Logger.d(TAG, "fileNameMd5:" + fileName + ",imageAbsPath:"
                    + imageAbsPath);
            byte[] bytes = FileUtil.readBytes(imageAbsPath);
            if (bytes == null)
            {
                if (callback == null)
                    callback.onUploadFailed();
                return;
            }
            new OSSUploadObjectAsyncTask(Const.OSSValues.ACCESS_ID,
                    Const.OSSValues.ACCESS_KEY, Const.OSSValues.BUCKET_NAME,
                    fileName, UploadObjectType.IMAGE) {
                
                @Override
                protected void onPostExecute(String result)
                {
                    Logger.d(TAG, "result:" + result);
                    count++;
                    results.add(Const.OSSValues.OSS_FILE_PREFIX
                            + result.toLowerCase());
                    if(!mCancel && callback != null)
                    {
                        callback.onUploadSuccess(result);
                    }
                    return;
                }

                @Override
                protected String doInBackground(byte[]... params)
                {
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, 500);
                    String resultString = super.doInBackground(params);
                    curProgress = 100;
                    publishProgress(curProgress);
                    return Const.OSSValues.OSS_FILE_PREFIX
                            + resultString.toLowerCase();
                }

                @Override
                protected void onProgressUpdate(Integer... values)
                {
                   if(callback != null && values != null && values.length>0 && !mCancel)
                   {
                       if(values[0] == 100)
                       {
                           callback.onProgress(values[0], 1);
                       }
                       else 
                       {
                           callback.onProgress(values[0], 0);
                       }
                   }
                }
                   
                
                
            }.execute(bytes);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void cancelUpload()
    {
        mCancel = true;
    }
}
