package com.extensivepro.mxl.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;
import android.text.TextUtils;

public class FileUtil
{
    public static void createCacheDir()
    {
        File dir = new File(Const.CACHE_PATH);
        if(!dir.exists())
        {
            dir.mkdirs();
        }
    }
    
    public static void writeToFile(InputStream is,String path)
    {
        FileOutputStream fos = null;
        try{
            byte buffer[] = new byte[1024*2];
            fos = new FileOutputStream(path);
            int tempLenth = -1;
            tempLenth = is.read(buffer);
            while(tempLenth != -1)
            {
                fos.write(buffer,0,tempLenth);
                tempLenth = is.read(buffer);
            }
            fos.flush();
        }catch(Exception e)
        {   
            e.printStackTrace();
        }
        finally{
            if(fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static boolean isSDCardAviliable()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    
    public static byte[] readBytes(String fileAbsPath)
    {
        if(TextUtils.isEmpty(fileAbsPath))
        {
            return null;
        }
        File file = new File(fileAbsPath);
        if(!file.exists())
        {
            return null;
        }
        FileInputStream fis = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try
        {
            fis =  new FileInputStream(file);
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = fis.read(b)) != -1) {
                buffer.write(b, 0, len);
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
            try
            {
                fis.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return buffer.toByteArray();
    }
    
}
