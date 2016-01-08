package com.extensivepro.mxl.app;

import java.io.InputStream;

/**
 * 
 * @Description 
 * @author damon
 * @date Apr 20, 2013 10:33:39 AM 
 * @version V1.3.1
 */
public interface ICommonCallback
{
    void onResp(int reqCode,int reasonCode, InputStream stream);
}
