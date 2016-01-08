package com.extensivepro.mxl.product;

import com.extensivepro.mxl.app.ICommonCallback;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 20, 2013 10:33:44 AM
 * @version V1.3.1
 */
public interface IProductCallback extends ICommonCallback
{
    void onLoadSuccess(String respStr,int reqCode, int reasonCode);

    void onLoadFailed(int reasonCode, int reqCode);
}
