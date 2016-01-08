/**
 * Copyright (c) 2012 The Wiseserc. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package com.extensivepro.mxl.util;

import com.aliyun.android.oss.asynctask.OSSAsyncTask;
import com.aliyun.android.oss.task.PutObjectTask;

/**
 * 上传一个Object，返回一个String值表示服务器计算的该对象的MD5值 可在onPostExecute方法中进行验证
 * 
 */
public class OSSUploadObjectAsyncTask extends
        OSSAsyncTask<byte[], Integer, String>
{
    public enum UploadObjectType
    {
        DIR("application/x-director"), TXT("text/plain"), IMAGE("image/png");
        /**
         * 请求方法的字符串表示
         */
        private String context;

        /**
         * 创建新枚举实例
         */
        private UploadObjectType(String context)
        {
            this.context = context;
        }

        /**
         * toString
         */
        public String toString()
        {
            return this.context;
        }
    }

    private String objectKey;

    private String type;

    public OSSUploadObjectAsyncTask(String accessKeyId, String accessKeySecret,
            String bucketName, String objectKey, UploadObjectType type)
    {
        super(accessKeyId, accessKeySecret, bucketName);
        this.objectKey = objectKey;
        this.type = type.toString();
    }

    @Override
    protected String doInBackground(byte[]... params)
    {
        if (params == null || params.length == 0)
        {
            return null;
        }
        byte[] bytes = params[0];
        PutObjectTask task = new PutObjectTask(getBucketName(), objectKey,
                type, bytes);
        task.initKey(getAccessKeyId(), getAccessKeySecret());
        return task.getResult();
    }

}
