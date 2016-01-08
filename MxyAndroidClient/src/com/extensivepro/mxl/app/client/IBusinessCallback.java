package com.extensivepro.mxl.app.client;

import java.io.InputStream;

/**
 * 
 * @Description 
 * @author damon
 * @date Apr 20, 2013 10:34:15 AM 
 * @version V1.3.1
 */
public interface IBusinessCallback
{
    void notifyMgr(InputStream stream, int reqCode);
    void notifyError(int reqCode,int errorCode);
}
