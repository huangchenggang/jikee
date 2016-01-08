package com.extensivepro.mxl.product;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.extensivepro.mxl.app.EventDispacher;
import com.extensivepro.mxl.app.MxlApplication;
import com.extensivepro.mxl.app.bean.BaseObject;
import com.extensivepro.mxl.app.bean.Carousel;
import com.extensivepro.mxl.app.bean.FreeSale;
import com.extensivepro.mxl.app.bean.Goods;
import com.extensivepro.mxl.app.bean.GoodsCategory;
import com.extensivepro.mxl.app.bean.Group;
import com.extensivepro.mxl.app.bean.StatusMessage;
import com.extensivepro.mxl.app.cart.CartManager;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.app.client.ClientThread;
import com.extensivepro.mxl.app.client.Command;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.app.provider.MxlTables.TCarousel;
import com.extensivepro.mxl.app.provider.MxlTables.TGoods;
import com.extensivepro.mxl.app.provider.MxlTables.TGoodsCategory;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.DataUtil;
import com.extensivepro.mxl.util.JsonUtil;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.util.SharedPreferenceUtil;

public class ProductManager implements IProductCallback
{

    private static final String TAG = ProductManager.class.getSimpleName();

    private static ProductManager mInstance;

    private static final int MOUDEL_CODE = EventDispacher.MoudelCode.MOUDEL_CODE_PRODUCT;
    
    private String mCurSelectGoodsId;
    
    private ProductManager()
    {
        EventDispacher.regCallback(MOUDEL_CODE, this);
    }

    public static ProductManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new ProductManager();
        }
        return mInstance;
    }

    @Override
    public void onResp(int reqCode, int reasonCode, InputStream stream)
    {
        Logger.d(TAG, "onResp()[reqCode:" + reqCode + ",reasonCode:"
                + reasonCode + "]");
        if (stream != null || reasonCode == Const.CONN_REQUEST_OK)
        {
            String resp = DataUtil.streamToString(stream);
            if (!TextUtils.isEmpty(resp))
            {
                Logger.d(TAG, "onAccept()[data length:" + resp.length()
                        + "\nresp:" + resp + "]");
                onLoadSuccess(resp, reqCode, reasonCode);
            }
            else
            {
                onLoadFailed(reasonCode, reqCode);
            }
        }
        else
        {
            onLoadFailed(reasonCode, reqCode);
        }
    }

    private boolean bulkInsert(List<BaseObject> objs, Uri contentUri)
    {
        if (objs == null)
        {
            return false;
        }
        ContentValues[] values = new ContentValues[objs.size()];
        for (int i = 0; i < values.length; i++)
        {
            values[i] = objs.get(i).toContentValues();
        }
        int rowsCount = ClientManager.getInstance().getAppContext().getContentResolver()
                .bulkInsert(contentUri, values);
        if (rowsCount == values.length)
        {
            return true;
        }
        return false;
    }

    public void loadCarousel()
    {
        Logger.d(TAG, "loadCarousel()[access]");
        Command command = new Command();
        command.setLoadFromLocal(true);
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_CAROUSEL,
                MOUDEL_CODE, command);
        command.commit(thread);
    }

    public void loadGoodsCategory(boolean loadFromLocal)
    {
        Logger.d(TAG, "loadGoodsCategory()[access]");
        Command command = new Command();
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_GOODS_CATEGOAY,
                MOUDEL_CODE, command);
        command.setLoadFromLocal(loadFromLocal);
        command.setDoGet(true);
        command.commit(thread);
    }

    public void loadGoods(String categoryId)
    {
        Logger.d(TAG, "loadGoods()[categoryId:" + categoryId + "]");
        Command command = new Command();
        command.setDoGet(true);
        command.addAttribute(Const.ProductModuel.PARAM_CAGEGORY_ID, categoryId);
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_GOODS_BY_CATEGORYID,
                MOUDEL_CODE, command);
        command.commit(thread);
    }

    /**
     * @Description 加载商品详情
     * @author zyw
     * @param id 商品的id
     */
    public void loadProductDeatail(String id)
    {
        Logger.d(TAG, "loadProductDeatail()[id:" + id + "]");
        Command command = new Command();
        command.addAttribute(Const.ProductModuel.PARAM_CAGEGORY_ID, id);
        command.setDoGet(true);
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_DEATAIL_BY_ID,
                MOUDEL_CODE, command);
        command.commit(thread);

    }
    
    /**
     * @Description 加载商品模板组
     * @author zyw
     * @param id 商品模板组的id
     */
    public void loadTemplateGroup(String templateGroupId)
    {
        Logger.d(TAG, "loadTemplateGroup()[id:" + templateGroupId + "]");
        Command command = new Command();
        command.addAttribute(Const.TemplateGroupModuel.PARAM_ID, templateGroupId);
        command.setDoGet(true);
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_TEMPLATEGROUP_BY_ID,
                MOUDEL_CODE, command);
        command.commit(thread);

    }
    

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadSuccess(String respStr, int reqCode, int reasonCode)
    {
        Logger.d(TAG, "onLoadSuccess()[reqCode:" + reqCode + ",reasonCode:"
                + reasonCode + ",respStr:"+respStr+"]");
        switch (reqCode)
        {
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_CAROUSEL:
                List<BaseObject> carousels = (List<BaseObject>) JsonUtil
                        .jsonToList(respStr, Carousel.class,
                                Const.JSON_LIST_NAME);
                if (bulkInsert(carousels, TCarousel.CONTENT_URI))
                {
                    SharedPreferenceUtil.setLoadCarouselSuccess(true);
                }
                Logger.d(TAG, "onLoadSuccess()[carousels:" + carousels + "]");
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_GOODS_CATEGOAY:
                List<BaseObject> goodsCategories = (List<BaseObject>) JsonUtil
                        .jsonToList(respStr, GoodsCategory.class,
                                Const.JSON_LIST_NAME);
                if (SharedPreferenceUtil.isNeedLoadGoodsCategoryFromLocal()
                        && !SharedPreferenceUtil
                                .isLoadGoodsCategoryFromNetworkSuccess())
                {// 在商品分类信息加载完成时判断如果当前默认从本地读取且未从网络侧成功获取过，则开启线程去从网络侧获取，同时标记不需要从本地读取
                    if (DataUtil.isNetworkAvilable())
                    {
                        SharedPreferenceUtil
                                .setNeedLoadGoodsCategoryFromLocal(false);
                        loadGoodsCategory(false);
                    }
                }
                else if (!SharedPreferenceUtil
                        .isNeedLoadGoodsCategoryFromLocal()
                        && !SharedPreferenceUtil
                                .isLoadGoodsCategoryFromNetworkSuccess())
                {// 若本地读取商品分类标记为false,且从网络成功获取为false,则标记其为true,并删除本地的商品分类数据
                    SharedPreferenceUtil
                            .setLoadGoodsCategoryFromNetworkSuccess(true);
                    ClientManager.getInstance().getAppContext().getContentResolver()
                            .delete(TGoodsCategory.CONTENT_URI, null, null);
                }
                bulkInsert(goodsCategories, TGoodsCategory.CONTENT_URI);
                Logger.d(TAG, "onLoadSuccess()[goodsCategories:"
                        + goodsCategories + "]");
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_GOODS_BY_CATEGORYID:
                List<BaseObject> goods = (List<BaseObject>) JsonUtil
                        .jsonToList(respStr, Goods.class,
                                Const.JSON_GOODS_LIST_NAME);
                if(goods != null && !goods.isEmpty())
                {
                    GoodsCategory category = ((Goods) goods.get(0)).getGoodsCategory();
                    if(category != null && category.getId() != null)
                    {
                        ClientManager
                        .getInstance().getAppContext()
                                .getContentResolver()
                                .delete(TGoods.CONTENT_URI,
                                        TGoods.CATEGORY_ID + "='"
                                                + category.getId() + "'", null);
                    }
                    bulkInsert(goods, TGoods.CONTENT_URI);
                }
                Logger.d(TAG, "onLoadSuccess()[goods:" + goods + "]");
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_DEATAIL_BY_ID:
                Goods backBean = (Goods) JsonUtil.jsonToObject(respStr,
                        Goods.class, Const.JSON_OBJ_NAME_GOODS);
                if(AccountManager.getInstance().hasLogined())
                {
                    CartManager.getInstance().refreshCartItems(backBean);
                }
                Logger.d(TAG, "onLoadSuccess()[backBean:" + backBean + "]");
                Intent intent = new Intent(
                        Const.ACTION_LOAD_PRODUCTDETAIL_SUCCESS);
                intent.putExtra(Const.EXTRA_GOODS_OBJ, backBean);
                ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                break;
                
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_TEMPLATEGROUP_BY_ID:
                Group groupBackBean = (Group) JsonUtil.jsonToObject(respStr, Group.class, Const.JSON_OBJ_NAME_GROUP);
                Logger.d(TAG, "onLoadSuccess()[groupBackBean:" + groupBackBean + "]");
                Intent groupintent = new Intent(
                        Const.ACTION_LOAD_GROUP_SUCCESS);
                groupintent.putExtra(Const.GROUPBACKBEAN_OBJ, groupBackBean);
                ClientManager.getInstance().getAppContext().sendBroadcast(groupintent);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_FREE_SALE:
                FreeSale freeSale = (FreeSale) JsonUtil.jsonToObject(respStr, FreeSale.class, null);
                Logger.d(TAG, "onLoadSuccess()[freeSale:"+freeSale+"]");
                Intent freeSaleIntent = new Intent(
                        Const.ACTION_LOAD_FREE_SALE_SUCCESS);
                freeSaleIntent.putExtra(Const.EXTRA_OBJ_FREE_SALE, freeSale);
                ClientManager.getInstance().getAppContext().sendBroadcast(freeSaleIntent);
                break;
            case EventDispacher.EventCodeBusiness.EVENT_CODE_CHECK_FREE_SALE:
                StatusMessage msg = (StatusMessage) JsonUtil.jsonToObject(
                        respStr, StatusMessage.class, null);
                Logger.d(TAG, "onLoadSuccess()[msg:"+msg+"]");
                switch(msg.getStatus())
                {
                    case success:
                        if(AccountManager.getInstance().getCurrentAccount() != null)
                        {
                            AccountManager.getInstance().getCurrentAccount().setHasGotFreeSale(true);
                        }
                        break;
                    case failed:
                        if(AccountManager.getInstance().getCurrentAccount() != null)
                        {
                            AccountManager.getInstance().getCurrentAccount().setHasGotFreeSale(false);
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoadFailed(int reasonCode, int reqCode)
    {
        switch(reqCode)
        {
            case EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_DEATAIL_BY_ID:
                Intent intent = new Intent(
                        Const.ACTION_LOAD_PRODUCTDETAIL_FAILED);
                ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                break;
        }
    }

    /**
     * 根据categoryID取到所有的goods,不包括免费赠送项
     * @Description 
     * @author damon
     * @param categoryId
     * @return
     */
    public List<Goods> getGoodsByCategoryId(String categoryId)
    {
        List<Goods> goodsList = new ArrayList<Goods>();
        if (TextUtils.isEmpty(categoryId))
        {
            return goodsList;
        }
        Cursor c = ClientManager
                .getInstance().getAppContext()
                .getContentResolver()
                .query(TGoods.CONTENT_URI,
                        null,
                        TGoodsCategory.CATEGORY_ID + "=? and "
                                + TGoods.IS_SALE_ONLY_ONE + "=?",
                        new String[] { categoryId, String.valueOf(0) }, null);
        try
        {
            if (c.getCount() > 0)
            {
                while (!c.isLast())
                {
                    c.moveToNext();
                    Goods goods = new Goods(c);
                    goodsList.add(goods);
                }
            }
        }
        finally
        {
            if (!c.isClosed())
            {
                c.close();
            }
        }
        return goodsList;
    }
    
    public String getCurSelectGoodsId()
    {
        return mCurSelectGoodsId;
    }

    public void setCurSelectGoodsId(String selectGoodsId)
    {
        this.mCurSelectGoodsId = selectGoodsId;
    }
    
    

    /**
     * 检查免费赠送是否获取过
     * @Description 
     * @author damon
     */
    public void checkFreeSale()
    {
        Logger.d(TAG, "checkFreeSale()");
        Command command = new Command();
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_CHECK_FREE_SALE,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    /**
     * 获取免费赠送信息
     * @Description 
     * @author damon
     */
    public void loadFreeSale()
    {
        Logger.d(TAG, "loadFreeSale()");
        Command command = new Command();
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_LOAD_FREE_SALE,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    public void clearCache()
    {
        mCurSelectGoodsId = "";
    }
    
    public Cursor findGoodsCategoryByKeywords(String keyword)
    {
        Cursor cursor = ClientManager
                .getInstance().getAppContext()
                .getContentResolver()
                .query(TGoodsCategory.CONTENT_URI,
                        null,
                        "where " + TGoodsCategory.CATEGORY_NAME + " like '"
                                + keyword + "'", null, null);
        return cursor;
    }
}
