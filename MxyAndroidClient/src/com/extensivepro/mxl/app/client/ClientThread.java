package com.extensivepro.mxl.app.client;

import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.extensivepro.mxl.app.EventDispacher;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.app.mock.MockData;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.DataUtil;
import com.extensivepro.mxl.util.Logger;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 20, 2013 10:34:02 AM
 * @version V1.3.1
 */
public class ClientThread implements Runnable
{
    private static final String TAG = ClientThread.class.getSimpleName();

    private static CookieStore cookieStore;
    
    private int mReqCode;

    private int mReqMoudelCode;

    private String mUrl;
    
    private Command mCommand;
    
    private static final int TIMEOUT_MILLIS = 10000;
    
    public ClientThread(int reqCode, int reqMoudelCode, Command command)
    {
        super();
        this.mReqCode = reqCode;
        this.mReqMoudelCode = reqMoudelCode;
        EventDispacher.regReqCode(mReqCode, mReqMoudelCode);
        mCommand = command;
        constructRequest();
    }

    @Override
    public void run()
    {
        Logger.d(TAG, "run()");
        if(MockData.MOCK_ENABLE)//启用mock数据时从本地读
        {
            doMock();
        }
        else if(!mCommand.isLoadFromLocal())//非启用mock数据且不开启强制从本地读取数据时连接网络读取
        {
            if(DataUtil.isNetworkAvilable())
            {
                Logger.i(TAG, mUrl);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = null;
                HttpUriRequest request = null;
                try
                {
                    if(mCommand.isDoGet())
                    {
                        request = new HttpGet(mUrl);
                    }
                    else 
                    {
                        request = new HttpPost(mUrl);
                        UrlEncodedFormEntity bodyEntity = new UrlEncodedFormEntity(
                                mCommand.getNameValuePairs(), "UTF-8");
                        ((HttpPost) request).setEntity(bodyEntity);
                    }
                    if (cookieStore != null
                            && mReqCode != EventDispacher.EventCodeBusiness.EVENT_CODE_LOGIN)
                    {
                        httpClient.setCookieStore(cookieStore);
                    }
                    //set timeout
                    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), TIMEOUT_MILLIS);
                    HttpConnectionParams.setSoTimeout(httpClient.getParams(), TIMEOUT_MILLIS);
                    response = httpClient.execute(request);
                    if(response != null)
                    {
                        HttpEntity entity = response.getEntity();
                        cookieStore = httpClient.getCookieStore();
                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
                                && entity != null)
                        {
                            //本次若是登录请求且请求OK，且已有retry thread 存在，执行retry thread
                            if(mReqCode == EventDispacher.EventCodeBusiness.EVENT_CODE_LOGIN)
                            {
                                Logger.d(TAG, "run()[do retry]");
                                ClientThread retryThread = ClientManager.getInstance().getCurRetryThread();
                                if(retryThread != null)
                                {
                                    ClientManager.getInstance().resetRetryThread();
                                    retryThread.getCommand().commit(retryThread);
                                }
                            }
                            InputStream is = null;
                            try
                            {
                                is = entity.getContent();
                                onResp(is);
                            }
                            finally
                            {
                                if(is != null)
                                {
                                    is.close();
                                }
                            }
                            }
                        else
                        {
                            int respCode = response.getStatusLine()
                                    .getStatusCode();
                            Logger.e(TAG, "[conn code:" + respCode + "]");
                            if (respCode == HttpStatus.SC_UNAUTHORIZED
                                    && mReqCode != EventDispacher.EventCodeBusiness.EVENT_CODE_LOGIN)
                            {
                                Logger.d(TAG, "run()[save retry thread and do login]");
                                ClientManager.getInstance().saveCurRetryThread(this);
                                AccountManager.getInstance().login(
                                        AccountManager.getInstance()
                                        .getCurrentAccount()
                                        .getUsername(),
                                        AccountManager.getInstance()
                                        .getCurrentAccount()
                                        .getPassword());
                            }
                            else
                            {
                                if(entity != null)
                                {
                                    String errResp = EntityUtils.toString(entity);
                                    Logger.w(TAG, "[errResp:" + errResp + "]");
                                }
                                onConnError(respCode);
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    if(response != null && response.getStatusLine() != null)
                    {
                        int respCode = response.getStatusLine()
                                .getStatusCode();
                        Logger.e(TAG, "[conn code:" + respCode + "]");
                    }
                    else 
                    {
                        onConnError(HttpStatus.SC_NOT_FOUND);
                    }
                    e.printStackTrace();
                }
                finally
                {
//                    httpClient.getConnectionManager().shutdown();
                }
            }
            else 
            {
                Logger.e(TAG, "[net work not avilable]");
                onConnError(Const.CONN_REQUEST_NETWORK_NOT_AVLIALBE);
            }
        }
        else if(mCommand.isLoadFromLocal())//未启用mock数据但强制要求从本地读取时，从mock数据读取
        {
            doMock();
        }

    }
    
    private void doMock()
    {
        String mockAssetFilePath = "";
        switch (mReqCode)
        {
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOGIN:
                mockAssetFilePath = "loginresp.json";
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_CAROUSEL:
                mockAssetFilePath = "CarouselAction.json";
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_GOODS_CATEGOAY:
                mockAssetFilePath = "GoodsCategory.json";
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_GOODS_BY_CATEGORYID:
                mockAssetFilePath = "FindGoodsByCategoryIdAction.json";
                break;
            default:
                break;
        }
        onResp(MockData.getMockInputStream(mockAssetFilePath));
    }

    private void onResp(InputStream is)
    {
        Logger.d(TAG, "onResp()[mReqCode:" + mReqCode + ",mReqMoudelCode:"
                + mReqMoudelCode + "]");
        ClientManager.getInstance().notifyResp(is, mReqCode);
    }
    
    private void onConnError(int errorCode)
    {
        Logger.d(TAG, "onConnError()[errorCode:" + errorCode + "]");
        ClientManager.getInstance().notifyError(mReqCode,errorCode);
    }
    
    private void constructRequest()
    {
        switch (mReqCode)
        {
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOGIN:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_LOGIN);
                Log.d("zhh", "登录url--->"+mUrl);
                mCommand.addRequestHead("WWW-Authenticate", "Authorization");
//                mUrl = Const.BASE_URI+"/api/login.action?member.username=mycsoft@qq.com&member.password=mycmyc";
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_CAROUSEL:
                mUrl = mCommand.appendAttribute(Const.ProductModuel.URI_LOAD_CAROUSEL);
                Log.d("zhh", "获取焦点图url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_GOODS_CATEGOAY:
                mUrl = mCommand.appendAttribute(Const.ProductModuel.URI_LOAD_GOODS_CAGEGORY);
                Log.d("zhh", "获取商品分类url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_GOODS_BY_CATEGORYID:
                mUrl = mCommand.appendAttribute(Const.ProductModuel.URI_LOAD_GOODS_BY_CATEGORYID_STRING);
                Log.d("zhh", "获取商品列表url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_DEATAIL_BY_ID:
                mUrl = mCommand.appendAttribute(Const.ProductDetailModuel.URI_LOAD_ProductDetail_BY_id_STRING);
                Log.d("zhh", "获取商品详细url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_REGISTER:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_REGISTER);
                Log.d("zhh", "注册url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_ADD_RECEIVER_ADDR:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_ADD_RECEIVER_ADDR);
                Log.d("zhh", "添加1收货地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_DEL_RECEIVER_ADDR:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_DEL_RECEIVER_ADDR);
                Log.d("zhh", "添加2收获地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_EDIT_RECEIVER_ADDR:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_EDIT_RECEIVER_ADDR);
                Log.d("zhh", "修改收获地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_SET_DEFAULT_RECEIVER_ADDR:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_SET_DEFAULT_RECEIVER_ADDR);
                Log.d("zhh", "设置默认地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_GET_ALL_AREA:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_GET_ALL_AREA);
                Log.d("zhh", "获取所有地区码url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_TEMPLATEGROUP_BY_ID:
                mUrl = mCommand.appendAttribute(Const.TemplateGroupModuel.URI_LOAD_TemplateGroup_BY_id_STRING);
                Log.d("zhh", "获取产品模板组url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_GET_ALL_RECEIVER_ADDR:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_GET_ALL_RECEIVER_ADDR);
                Log.d("zhh", "获取当前账户所有的收货地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_ADD_CART_ITEM:
                mUrl = mCommand.appendAttribute(Const.CartMoudel.URI_ADD_CART_ITEM);
                Log.d("zhh", "添加购物车项url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_ALL_CART_ITEMS:
                mUrl = mCommand.appendAttribute(Const.CartMoudel.URI_LIST_ALL_CART_ITEMS);
                Log.d("zhh", "查看所有购物车项url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_GET_PAYMENT_CONFIG:
                mUrl = mCommand.appendAttribute(Const.CartMoudel.URI_GET_PAYMENT_CONFIG);
                Log.d("zhh", "支付配置url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_SAVE_ORDER:
                mUrl = mCommand.appendAttribute(Const.CartMoudel.URI_SAVE_ORDER);
                Log.d("zhh", "保存订单项url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_UNPAID_ORDER:
                mUrl = mCommand.appendAttribute(Const.CartMoudel.URI_LIST_UNPAID_ORDER);
                Log.d("zhh", "显示未付款订单项url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_HISTORY_ORDER:
                mUrl = mCommand.appendAttribute(Const.CartMoudel.URI_LIST_HISTORY_ORDER);
                Log.d("zhh", "显示历史订单项url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_PAY_ORDER:
                mUrl = mCommand.appendAttribute(Const.CartMoudel.URI_PAY_ORDER);
                Log.d("zhh", "支付订单url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_INVALID_ORDER:
                mUrl = mCommand.appendAttribute(Const.CartMoudel.URI_LIST_INVALID_ORDER);
                Log.d("zhh", "放弃订单url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_DEPOSIT_CARD:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_LIST_DEPOSIT_CARD);
                Log.d("zhh", "取得上架的充值卡列表url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_PAY_DEPOSIT_CARD:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_PAY_DEPOSIT_CARD);
                Log.d("zhh", "充值卡充值url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_GET_SHARE:
                mUrl = mCommand.appendAttribute(Const.ShareItemModuel.URI_LOAD_SHAREINFOS);
                Log.d("zhh", "设置默认地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_CHECK_FREE_SALE:
                mUrl = mCommand.appendAttribute(Const.ProductModuel.URI_CHECK_FREE_SALE);
                Log.d("zhh", "设置默认地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_FREE_SALE:
                mUrl = mCommand.appendAttribute(Const.ProductModuel.URI_LOAD_FREE_SALE);
                Log.d("zhh", "设置默认地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_SAVE_MESSAGE:
                mUrl = mCommand.appendAttribute(Const.ShareItemModuel.URI_SAVE_MESSAGE); 
                Log.d("zhh", "设置默认地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOGOUT:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_LOGOUT);
                Log.d("zhh", "设置默认地址url--->"+mUrl);
            case EventDispacher.EventCodeBusiness.EVENT_CODE_EDIT_CART_ITEM:
                mUrl = mCommand.appendAttribute(Const.CartMoudel.URI_EDIT_CART_ITEM);
                Log.d("zhh", "设置默认地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_DEL_CART_ITEM:
                mUrl = mCommand.appendAttribute(Const.CartMoudel.URI_DEL_CART_ITEM);
                Log.d("zhh", "设置默认地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_GOOD_MESSAGE:
                mUrl = mCommand.appendAttribute(Const.ShareItemModuel.URI_GOOD_MESSAGE);
                Log.d("zhh", "设置默认地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_GET_NOTIFY_MESSAGE:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_GET_NOTIFY_MSGS);
                Log.d("zhh", "设置默认地址url--->"+mUrl);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_DEL_NOTIFY_MESSAGE:
                mUrl = mCommand.appendAttribute(Const.AccountModuel.URI_DEL_NOTIFY_MSGS);
                Log.d("zhh", "设置默认地址url--->"+mUrl);
                break;
                
            default:
                break;
        }
    }

    
    public Command getCommand()
    {
        return mCommand;
    }
}
