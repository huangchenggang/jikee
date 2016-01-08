package com.extensivepro.mxl.app;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.util.SparseArray;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 20, 2013 10:34:46 AM
 * @version V1.3.1
 */
@SuppressLint("UseSparseArrays")
public class EventDispacher
{
    private static SparseArray<ICommonCallback> mCallbacks = new SparseArray<ICommonCallback>(
            0);

    private static SparseArray<Integer> mReqCodes = new SparseArray<Integer>();

    public class EventCodeBusiness
    {
        public static final int EVENT_CODE_LOGIN = 1000;

        public static final int EVENT_CODE_LOGOUT = 1001;

        /**
         * 获取焦点图
         */
        public static final int EVENT_CODE_LOAD_CAROUSEL = 1002;

        /**
         * 获取商品分类
         */
        public static final int EVENT_CODE_LOAD_GOODS_CATEGOAY = 1003;

        /**
         * 获取商品列表
         */
        public static final int EVENT_CODE_LOAD_GOODS_BY_CATEGORYID = 100４;

        /**
         * 获取商品详细
         */
        public static final int EVENT_CODE_LOAD_DEATAIL_BY_ID = 1005;
        /**
         * 注册
         */
        public static final int EVENT_CODE_REGISTER = 1006;
        /**
         * 添加收货地址
         */
        public static final int EVENT_CODE_ADD_RECEIVER_ADDR = 1007;
        
        /**
         * 获取所有地区码
         */
        public static final int EVENT_CODE_GET_ALL_AREA = 1008;
        /**
         * 获取产品模板组
         */
        public static final int EVENT_CODE_LOAD_TEMPLATEGROUP_BY_ID = 1009;
        
        /**
         * 获取当前账户所有的收货地址
         */
        public static final int EVENT_CODE_GET_ALL_RECEIVER_ADDR = 1010;
        /**
         * 添加购物车项
         */
        public static final int EVENT_CODE_ADD_CART_ITEM = 1011;
        /**
         * 查看所有购物车项
         */
        public static final int EVENT_CODE_LIST_ALL_CART_ITEMS = 1012;
        /**
         * 查看所有购物车项
         */
        public static final int EVENT_CODE_GET_PAYMENT_CONFIG = 1013;
        /**
         * 保存订单项
         */
        public static final int EVENT_CODE_SAVE_ORDER = 1014;
        /**
         * 显示未付款订单项
         */
        public static final int EVENT_CODE_LIST_UNPAID_ORDER = 1015;
        /**
         * 显示历史订单项
         */
        public static final int EVENT_CODE_LIST_HISTORY_ORDER = 1016;
        
        /**
         * 支付订单
         */
        public static final int EVENT_CODE_PAY_ORDER = 1017;
        /**
         * 放弃订单
         */
        public static final int EVENT_CODE_INVALID_ORDER = 1018;
        /**
         * 添加收货地址
         */
        public static final int EVENT_CODE_DEL_RECEIVER_ADDR = 1019;
        /**
         * 修改收货地址
         */
        public static final int EVENT_CODE_EDIT_RECEIVER_ADDR = 1020;
        
        /**
         * 取得上架的充值卡列表.
         */
        public static final int EVENT_CODE_LIST_DEPOSIT_CARD = 1021;
        /**
         * 充值卡充值.
         */
        public static final int EVENT_CODE_PAY_DEPOSIT_CARD = 1022;
        
        /**
         * 获取分享信息
         */
        public static final int EVENT_CODE_GET_SHARE = 1023;
        /**
         * 检查当前免费赠送是否已经领取
         */
        public static final int EVENT_CODE_CHECK_FREE_SALE = 1024;
        /**
         * 获取免费赠送内容
         */
        public static final int EVENT_CODE_LOAD_FREE_SALE = 1025;

        /**
         * 提交分享在线留言
         */
        public static final int EVENT_CODE_SAVE_MESSAGE = 1026;
        
        /**
         * 编辑购物车项
         */
        public static final int EVENT_CODE_EDIT_CART_ITEM = 1027;
        /**
         * 删除购物车项
         */
        public static final int EVENT_CODE_DEL_CART_ITEM = 1028;
        /**
         * 设置默认收货地址
         */
        public static final int EVENT_CODE_SET_DEFAULT_RECEIVER_ADDR = 1029;
        /**
         * 喜欢在线留言
         */
        public static final int EVENT_CODE_GOOD_MESSAGE = 1030;
        
        public static final int EVENT_CODE_GET_NOTIFY_MESSAGE = 1031;
        
        public static final int EVENT_CODE_DEL_NOTIFY_MESSAGE = 1032;
        
    }

    public class MoudelCode
    {
        public static final int MOUDEL_CODE_LOGIN = 2000;

        public static final int MOUDEL_CODE_TROLLERY = 2001;

        public static final int MOUDEL_CODE_SHARE = 2002;

        public static final int MOUDEL_CODE_PRODUCT = 2003;

        public static final int MOUDEL_CODE_PRODUCT_DEATAIL = 2004;
    }

    public static void regReqCode(int reqCode, int moudelCode)
    {
        mReqCodes.put(reqCode, moudelCode);
    }

    public static void regCallback(int moudelCode, ICommonCallback callback)
    {
        mCallbacks.put(moudelCode, callback);
    }

    public static void unregCallback(int moudelCode)
    {
        if (mCallbacks.indexOfKey(moudelCode) != -1)
        {
            mCallbacks.remove(moudelCode);
        }
    }
    
    public static void dispatchEvent(InputStream stream, int reqCode, int errorCode)
    {
        if (mReqCodes.indexOfKey(reqCode) != -1)
        {
            int moudelCode = mReqCodes.get(reqCode);
            mReqCodes.remove(reqCode);
            if (mCallbacks.indexOfKey(moudelCode) != -1 && mCallbacks.get(moudelCode) != null)
            {
                mCallbacks.get(moudelCode).onResp(reqCode, errorCode, stream);
            }
        }
    }
    
    public static void clearCallback()
    {
        mCallbacks.clear();
    }
}
