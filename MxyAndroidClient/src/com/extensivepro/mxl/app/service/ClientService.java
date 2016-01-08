package com.extensivepro.mxl.app.service;

import java.io.InputStream;

import com.extensivepro.mxl.app.BaseService;
import com.extensivepro.mxl.app.EventDispacher;
import com.extensivepro.mxl.app.MxlApplication;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.app.client.IBusinessCallback;
import com.extensivepro.mxl.product.ProductManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.SharedPreferenceUtil;
/**
 * 
 * @Description 
 * @author damon
 * @date Apr 20, 2013 10:34:32 AM 
 * @version V1.3.1
 */
public class ClientService extends BaseService implements IBusinessCallback
{

    @Override
    public void onCreate()
    {
        super.onCreate();
        ClientManager.getInstance().regCallback(this);
        ClientManager.getInstance().setAppContext(this);
        ProductManager.getInstance().loadFreeSale();
        /**
         * 首页要自动加载的内容需要放在这里执行，否则Callback没有注册，无法回调
         */
        if(!SharedPreferenceUtil.isLoadCarouselSuccess())
        {
            ProductManager.getInstance().loadCarousel();
        }
        /**
         * 将加载商品分类提前
         */
        boolean needLoadCategoryFromLocal = SharedPreferenceUtil
                .isNeedLoadGoodsCategoryFromLocal()
                && !SharedPreferenceUtil
                        .isLoadGoodsCategoryFromNetworkSuccess();
        ProductManager.getInstance().loadGoodsCategory(needLoadCategoryFromLocal);
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ClientManager.getInstance().unregCallback();
        ClientManager.getInstance().setAppContext(null);
    }
    
    @Override
    public void notifyMgr(InputStream stream, int reqCode)
    {
        EventDispacher.dispatchEvent(stream, reqCode, Const.CONN_REQUEST_OK);
    }

    @Override
    public void notifyError(int reqCode, int errorCode)
    {
        EventDispacher.dispatchEvent(null, reqCode, errorCode);
    }
}
