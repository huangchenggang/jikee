package com.extensivepro.mxl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.extensivepro.mxl.app.mock.MockData;

public class ImageDownloader
{
    private static final String TAG = ImageDownloader.class.getSimpleName();
    
    public interface DownloadCallback
    {
        void onLoadSuccess(Bitmap bitmap);

        void onLoadFailed();
    }
    
 
    
    private boolean mCancel;
    
    private boolean mPause;
    
    private Queue<DownloadTask> mTaskQueue = new LinkedList<ImageDownloader.DownloadTask>();
    
    private TaskState mTaskState = TaskState.IDEL;
    
    private enum TaskState
    {
        IDEL,BUSY;
    }
    
    private static Map<String, WeakReference<Bitmap>> mImageCache = new LinkedHashMap<String, WeakReference<Bitmap>>();

    public Bitmap downloadImage(final DownloadCallback callback, final String url, final int maxWidth)
    {
        Logger.d(TAG, "downloadImage()[url:"+url+"]");
        if (callback == null || url == null)
        {
            return null;
        }
        if(mImageCache.containsKey(url) && mImageCache.get(url).get() != null)
        {
            return mImageCache.get(url).get();
        }
        else 
        {
            taskToQueue(new DownloadTask(callback,false,false,maxWidth, url));
        }
        return null;
    }
    
    private void taskToQueue(DownloadTask task)
    {
        mTaskQueue.add(task);
        doNext();
    }
    
    private void doNext()
    {
        synchronized(mTaskQueue)
        {
            if(mTaskState == TaskState.IDEL && !mCancel && !mPause)
            {
                if(mTaskQueue.isEmpty())
                {
                    return;
                }
                mTaskState = TaskState.BUSY;
                DownloadTask task = mTaskQueue.poll();
                task.execute(task.mUrl);
            }
        }
    }
    
    public Bitmap downloadImageFromSDCard(final DownloadCallback callback, final String url, int maxWidth)
    {
        Logger.d(TAG, "downloadImage()[url:"+url+"]");
        if (callback == null || url == null)
        {
            return null;
        }
        if(mImageCache.containsKey(url) && mImageCache.get(url).get() != null)
        {
            return mImageCache.get(url).get();
        }
        else 
        {
            taskToQueue(new DownloadTask(callback,false,true,maxWidth, url));
        }
        return null;
    }
    
    public Bitmap downloadImageFromAsset(final DownloadCallback callback, final String url)
    {
        Logger.d(TAG, "downloadImage()[url:"+url+"]");
        if (callback == null || url == null)
        {
            return null;
        }
        if(mImageCache.containsKey(url) && mImageCache.get(url).get() != null)
        {
            return mImageCache.get(url).get();
        }
        else 
        {
            taskToQueue(new DownloadTask(callback,true,false,0, url));
        }
        return null;
    }
    
    public void clearCache()
    {
        Logger.d(TAG, "clearCache()");
        mCancel = true;
        synchronized(mTaskQueue)
        {
            mTaskQueue.clear();
            mTaskState = TaskState.IDEL;
        }
        synchronized(mImageCache)
        {
            if (!mImageCache.isEmpty())
            {
                Set<String> key = mImageCache.keySet();
                for (int i = 0; i < key.size(); i++)
                {
                    if (mImageCache.get(key) != null
                            && mImageCache.get(key).get() != null
                            && !mImageCache.get(key).get().isRecycled())
                    {
                        mImageCache.get(key).get().recycle();
                        System.gc();
                        mImageCache.remove(key);
                    }
                }
                mImageCache.clear();
            }
        }
    }
    
    public void pauseDownload()
    {
        mPause = true;
    }
    
    public void resumeDownload()
    {
        mPause = false;
        doNext();
    }
    
    private class DownloadTask extends AsyncTask<String, Void, Bitmap>
    {

        private DownloadCallback mCallback;
        
        private boolean mFromAssets;
        
        private boolean mFromSDCard;
        
        private int mMaxWidth;
        
        private String mUrl;
        public DownloadTask(DownloadCallback mCallback,boolean fromAssets,boolean fromSDCard, int maxWidth, String url)
        {
            super();
            this.mCallback = mCallback;
            mFromAssets = fromAssets;
            mFromSDCard = fromSDCard;
            mMaxWidth = maxWidth;
            mUrl = url;
        }

        @Override
        protected synchronized Bitmap doInBackground(String... params)
        {
            if (params == null || params.length < 1)
            {
                return null;
            }
            if(mCancel)
            {
                mCallback = null;
                return null;
            }
            if(mPause)
            {
                return null;
            }
            String urlAddr = params[0];
            InputStream is = null;
            Bitmap bmp = null;
            try
            {
                if (MockData.MOCK_ENABLE || mFromAssets)
                {
                    is = MockData.getMockInputStream(urlAddr);
                    bmp = BitmapFactory.decodeStream(is);
                }
                else
                {
                    if(mFromSDCard)
                    {

                        File file = new File(urlAddr);
                        if(file!= null && file.exists())
                        {
                            bmp = makeBitmap(file.getAbsolutePath(), mMaxWidth);
                        }
                        else 
                        {
                            bmp = null;
                            Logger.e(TAG, "DownloadTask()[file not exist]");
                        }
                    
                    }
                    else if(DataUtil.isNetworkAvilable())
                    {
                        File cacheFile = downloadImageToCache(urlAddr);
                        Logger.d(TAG, "cacheFiel:"+cacheFile);
                        if(cacheFile != null && cacheFile.exists())
                        {
                            bmp = makeBitmap(cacheFile.getAbsolutePath(), mMaxWidth);
                            if(bmp == null)
                            {
                                bmp = makBitmapFromUrl(urlAddr, mMaxWidth);
                            }
                        }
                        else 
                        {
                            bmp = makBitmapFromUrl(urlAddr, mMaxWidth);
                        }
                    }
                    else
                    {
                        File cacheFile = getImageFromCache(urlAddr);
                        Logger.d(TAG, "cacheFiel:"+cacheFile);
                        if(cacheFile != null && cacheFile.exists())
                        {
                            bmp = makeBitmap(cacheFile.getAbsolutePath(), mMaxWidth);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            catch(OutOfMemoryError error)
            {
                error.printStackTrace();
            }
            finally
            {
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            mImageCache.put(urlAddr, new WeakReference<Bitmap>(bmp));
            bmp = null;
            if(mImageCache.containsKey(urlAddr) && mImageCache.get(urlAddr) != null)
            {
                return mImageCache.get(urlAddr).get();
            }
            else 
            {
                return null;
            }
        }
        
        @Override
        protected void onPostExecute(Bitmap result)
        {
            mTaskState = TaskState.IDEL;
            doNext();
            if(mCallback != null && result != null)
            {
                mCallback.onLoadSuccess(result);
            }
            else if(mCallback != null && result == null)
            {
                mCallback.onLoadFailed();
            }
        }
    }
    
    private File downloadImageToCache(String uri)
    {
        File cacheFile = null;
        if(FileUtil.isSDCardAviliable() && !TextUtils.isEmpty(uri))
        {
            HttpURLConnection conn = null;
            URL url = null;
            try
            {
                File cacheDir = new File(Const.IMAGE_CACHE_PATH);
                if(!cacheDir.exists())
                {
                    cacheDir.mkdirs();
                }
//                String urlMd5 = FileMD5.checkHashURL(uri);//暂时不用MD5来验证
                String fileName = uri.substring(uri.lastIndexOf("/"),uri.length());
                cacheFile = new File(Const.IMAGE_CACHE_PATH+"/"+fileName);
                if(cacheFile.exists())
                {
                    return cacheFile;
                }
                cacheFile.createNewFile();
                url = new URL(uri);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                FileUtil.writeToFile(conn.getInputStream(), cacheFile.getAbsolutePath());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(conn != null)
                {
                    conn.disconnect();
                }
            }
        }
        return cacheFile;
    }
    
    public File getImageFromCache(String uri)
    {
        File cacheFile = null;
        if (FileUtil.isSDCardAviliable() && !TextUtils.isEmpty(uri))
        {
            try
            {
                File cacheDir = new File(Const.IMAGE_CACHE_PATH);
                if (!cacheDir.exists())
                {
                    cacheDir.mkdirs();
                }
                String fileName = uri.substring(uri.lastIndexOf("/"),
                        uri.length());
                cacheFile = new File(Const.IMAGE_CACHE_PATH + "/" + fileName);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return cacheFile;
    }
    
    private Bitmap makBitmapFromUrl(String uri,int maxWidth)
    {
        Bitmap bitmap = null;
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        try
        {
            url = new URL(uri);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            is = conn.getInputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null,options);
            is.close();
            
            
            int scale = 1;
            int srcOutWidth = options.outWidth;
            if(srcOutWidth > maxWidth)
            {
                scale = srcOutWidth / maxWidth;
            }
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false; 
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is, null, options); 
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        catch(OutOfMemoryError error)
        {
            error.printStackTrace();
        }
        finally{
            if(is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if(conn != null)
            {
                conn.disconnect();
            }
        }
        return bitmap;
    }

    private Bitmap makeBitmap(String path, int maxWidth)
    {
        InputStream is = null;
        Bitmap bitmap = null;  
        try
        {
            is = new FileInputStream(path);
            if(maxWidth == 0)
            {
//                maxWidth = ClientManager.getInstance().getScreenWidth();
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null,options);
            is.close();
            
                int scale = 1;
                int srcOutWidth = options.outWidth;
//                while(srcOutWidth >= maxWidth)
//                {
//                    srcOutWidth = srcOutWidth/2;
//                    scale ++;
//                }
                if(srcOutWidth > maxWidth)
                {
                    scale = srcOutWidth / maxWidth;
                }
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false; 
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                is = new FileInputStream(path);
                bitmap = BitmapFactory.decodeStream(is, null, options); 
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        catch(OutOfMemoryError error)
        {
            error.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return bitmap;  
    }
    
    public Bitmap creatThumnials(int thumW,int thumH,Bitmap src)
    {
        if (thumH < 0 || thumW < 0 || src == null)
        {
            return null;
        }
        int srcW = src.getWidth();
        int srcH = src.getHeight();
        if (srcW <= thumW && srcH <= thumH)
        {
            return src;
        }
        if(srcW < thumW)
        {
            thumW = srcW;
        }
        if(srcH < thumH)
        {
            thumH = srcH;
        }
        int startX = 0;
        int startY = 0;
        if (srcW > thumW )
        {
            startX = (srcW - thumW) / 2;
        }
        if (srcH > thumH )
        {
            startY = (srcH - thumH) / 2;
        }
        return Bitmap.createBitmap(src, startX, startY, thumW, thumH,
                new Matrix(), false);
    }
}
