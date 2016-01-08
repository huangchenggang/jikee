package com.extensivepro.mxl.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.os.Handler;

import com.extensivepro.mxl.util.OSSUploadObjectAsyncTask.UploadObjectType;

public class OSSUploadImageTask
{
    
    private static final String TAG = "OSSUploadImageTask";
    public interface IUploadCallback
    {
        void onUploadSuccess(List<String> ossPaths);

        void onUploadFailed();
        
        void onProgress(int progress, int progressCount);
    }

    private Queue<String> taskQueue = new LinkedList<String>();

    private IUploadCallback callback;
    
    private int total;

    private List<String> results = new ArrayList<String>();
    
    private int count = 0;
    
    private boolean mCancel;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg)
        {
            startUpload();
        }
    };
    
    public OSSUploadImageTask(IUploadCallback callback, List<String> imageAbsPath)
    {
        super();
        this.callback = callback;
        if(imageAbsPath == null || imageAbsPath.isEmpty())
        {
            if(callback != null)
                callback.onUploadFailed();
            return;
        }
        taskQueue.clear();
        taskQueue.addAll(imageAbsPath);
        total = taskQueue.size();
    }

    public void startUpload()
    {
        doExecute();
    }
    
    public void startUploadDelay(int delayMillis)
    {
        if(callback != null)
        {
            callback.onProgress(0,0);
        }
        mHandler.sendEmptyMessageDelayed(0, delayMillis);
    }
    
    private void doExecute()
    {
        if (!taskQueue.isEmpty() && !mCancel)
        {
            String imageAbsPath = taskQueue.poll();
            try
            {
                String fileName = FileMD5.getFileMD5String(imageAbsPath);
                Logger.d(TAG, "fileNameMd5:"+fileName+",imageAbsPath:"+imageAbsPath);
                byte[] bytes = FileUtil.readBytes(imageAbsPath);
                if (bytes == null)
                {
                    taskQueue.clear();
                    if (callback == null)
                        callback.onUploadFailed();
                    return;
                }
                new OSSUploadObjectAsyncTask(Const.OSSValues.ACCESS_ID,
                        Const.OSSValues.ACCESS_KEY,
                        Const.OSSValues.BUCKET_NAME, fileName,
                        UploadObjectType.IMAGE) {
                    @Override
                    protected void onPostExecute(String result)
                    {
                        Logger.d(TAG, "result:"+result);
                        count++;
                        results.add(Const.OSSValues.OSS_FILE_PREFIX
                                + result.toLowerCase());
                        if(callback != null)
                        {
                            if(count == total)
                            {
                                callback.onProgress(100,count);
                            }
                            else 
                            {
                                float tmpCount = count;
                                float tmpTotal = total;
                                float progress = (tmpCount/tmpTotal) *100;
                                callback.onProgress((int)progress,count);
                            }
                        }
                        if (taskQueue.isEmpty() && callback != null)
                        {
                            callback.onUploadSuccess(results);
                            return;
                        }
                        doExecute();
                    }
                }.execute(bytes);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public void cancelUpload()
    {
        mCancel = true;
        callback = null;
    }
}
