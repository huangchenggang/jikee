package com.extensivepro.mxl.util;

import android.os.Environment;


/**
 * 
 * @Description To define constants here.
 * @author damon
 * @date Apr 16, 2013 11:36:32 AM 
 * @version V1.3.1
 */
public class Const
{
    public static final String BASE_URI = "http://121.199.2.71:8080";
//  public static final String BASE_URI = "http://192.168.0.25:8080/mxl";
//  public static final String BASE_URI = "http://192.168.0.104:8065/mxl_shop";
    
    public static final int CONN_REQUEST_OK = 200;
    public static final int CONN_REQUEST_NETWORK_NOT_AVLIALBE = 601;
    
    public class AccountModuel
    {
        public static final String URI_LOGIN = BASE_URI+"/api/login.action";
        public static final String URI_REGISTER = BASE_URI+"/api/register.action";
        public static final String URI_ADD_RECEIVER_ADDR = BASE_URI+"/api/receiver!add.action";
        public static final String URI_DEL_RECEIVER_ADDR = BASE_URI+"/api/receiver!delete.action";
        public static final String URI_EDIT_RECEIVER_ADDR = BASE_URI+"/api/receiver!edit.action";
        public static final String URI_SET_DEFAULT_RECEIVER_ADDR = BASE_URI+"/api/receiver!def.action";
        public static final String URI_GET_ALL_AREA = BASE_URI+"/api/area!all.action";
        public static final String URI_GET_ALL_RECEIVER_ADDR = BASE_URI+"/api/receiver!list.action";
        public static final String URI_LOGOUT = BASE_URI+"/api/logout.action";
        
        public static final String URI_GET_NOTIFY_MSGS = BASE_URI+"/api/message!systemMessage.action";
        public static final String URI_DEL_NOTIFY_MSGS = BASE_URI+"/api/message!sysMesisRead.action";
        
        /**
         * 取得上架的充值卡列表.
         */
        public static final String URI_LIST_DEPOSIT_CARD = BASE_URI+"/api/deposit_card!list.action";
        /**
         * 充值卡充值
         */
        public static final String URI_PAY_DEPOSIT_CARD = BASE_URI+"/api/deposit_card!pay.action";
        
        public static final String PARAM_USERNAME = "member.username";
        public static final String PARAM_PASSWD = "member.password";
        public static final String PARAM_NAME = "member.name";
        public static final String PARAM_EMAIL = "member.email";
        public static final String PARAM_RECEIVER_ID = "id";
        public static final String PARAM_RECEIVER_AREAID = "areaId";
        public static final String PARAM_RECEIVER_NAME = "receiver.name";
        public static final String PARAM_RECEIVER_ADDRESS = "receiver.address";
        public static final String PARAM_RECEIVER_ZIPCODE = "receiver.zipCode";
        public static final String PARAM_RECEIVER_MOBILE = "receiver.mobile";
       
        public static final String PARAM_DEPOSIT_CARD_ID = "id";//充值卡id.
        public static final String PARAM_DEPOSIT_CARD_COUNT = "count";//- 数量.
        
    }
    
    public class ProductModuel
    {
        public static final String URI_LOAD_CAROUSEL = BASE_URI+"/api/carousel!index.action";
        public static final String URI_LOAD_GOODS_CAGEGORY = BASE_URI+"/api/goods_category!list.action";
        public static final String URI_LOAD_GOODS_BY_CATEGORYID_STRING = BASE_URI+"/api/goods!findByCategoryId.action";
        /**
         * 校验是否免费赠送已领取
         */
        public static final String URI_CHECK_FREE_SALE = BASE_URI+"/api/home_free_sale!checkGoods.action";
        /**
         * 获取免费赠送信息
         */
        public static final String URI_LOAD_FREE_SALE = BASE_URI+"/api/home_free_sale.action";
        public static final String PARAM_CAGEGORY_ID = "id";
    }
    
    public class ProductDetailModuel
    {
       //http://192.168.0.104:8065/mxl_shop//api/goods!findById.action?id=402880e83e779c62013e77f15df60013
        public static final String URI_LOAD_ProductDetail_BY_id_STRING = BASE_URI+"/api/goods!findById.action";
        public static final String PARAM_ID = "id";
    }
    
    public class TemplateGroupModuel
    {
       //http://192.168.0.104:8065/mxl_shop//api/goods!findById.action?id=402880e83e779c62013e77f15df60013
        public static final String URI_LOAD_TemplateGroup_BY_id_STRING = BASE_URI+"/api/custom_template_group.action";
        public static final String PARAM_ID = "id";
    }
    
    public class CartMoudel
    {
        public static final String URI_NOTIFY_ALIPAY = BASE_URI+"";
        /**
         * 添加购物车项
         */
        public static final String URI_ADD_CART_ITEM = BASE_URI+"/api/cart_item!add.action";
        /**
         * 编辑购物车项
         */
        public static final String URI_EDIT_CART_ITEM = BASE_URI+"/api/cart_item!edit.action";
        
        /**
         * 删除购物车项
         */
        public static final String URI_DEL_CART_ITEM = BASE_URI+"/api/cart_item!delete.action";
        
        /**
         * 获取支付方式
         */
        public static final String URI_GET_PAYMENT_CONFIG = BASE_URI+"/api/payment_config!list.action";
        
        /**
         * 查看所有购物车项
         */
        public static final String URI_LIST_ALL_CART_ITEMS = BASE_URI+"/api/cart_item!list.action";
        public static final String PARAM_ID = "id";
        public static final String PARAM_QUANTITY = "quantity";
        public static final String PARAM_CUSTOMVALUES = "customValues";
       
        /**
         * 生成订单的URI
         */
        public static final String URI_SAVE_ORDER = BASE_URI+"/api/order!save.action";
        
        /**
         * 订单项参数
         */
        public static final String PARAM_RECEIVER_ID = "receiver.id"; //收货人id,不为空时,其它的收货人信息不会处理.否则以其它的收货人信息为准.
        public static final String PARAM_RECEIVER_NAME = "receiver.name"; //- 收货人名称
        public static final String PARAM_RECEIVER_ADDRESS = "receiver.address"; //- 收货人地址
        public static final String PARAM_RECEIVER_ZIPCODE = "receiver.zipCode"; //- 收货人邮编
        public static final String PARAM_RECEIVER_PHONE = "receiver.phone"; //- 收货人电话
        public static final String PARAM_RECEIVER_MOBILE = "receiver.mobile"; //- 收货人手机.
        public static final String PARAM_PAYMENTCONFIG_ID = "paymentConfig.id"; //- 支付方式id.
        public static final String PARAM_ORDER_MEMO = "order.memo"; //- 订单备注.

        /**
         * 支付订单
         */
        public static final String URI_PAY_ORDER = BASE_URI+"/api/order!pay.action";
        
        /**
         * 列出未付款订单项
         */
        public static final String URI_LIST_UNPAID_ORDER = BASE_URI+"/api/order!listForUnpaid.action";
        /**
         * 列出历史订单项
         */
        public static final String URI_LIST_HISTORY_ORDER = BASE_URI+"/api/order!listForOld.action";
        /**
         * 放弃订单项
         */
        public static final String URI_LIST_INVALID_ORDER = BASE_URI+"/api/order!invalid.action";
    }
    public class ShareItemModuel
    {
        public static final String URI_LOAD_SHAREINFOS = BASE_URI+"/api/leave_message!list.action";
        public static final String URI_SAVE_MESSAGE = BASE_URI+"/api/leave_message!save.action";
        
        public static final String URI_GOOD_MESSAGE = BASE_URI+"/api/leave_message!good.action";//喜欢在线留言URI
        
        public static final String PARAM_PAGESIZE = "pageSize";
        public static final String PARAM_LASTTIME = "lastTime";
       
        public static final String PARAM_MSG_CONTACT = "msg.contact";
        public static final String PARAM_MSG_CONTENT = "msg.content";
        public static final String PARAM_MSG_GOOD = "msg.good";//喜欢值.大于0=喜欢,小于0=不喜欢
        public static final String PARAM_MSG_IMAGE = "msg.image";
        public static final String PARAM_MSG_IP = "msg.ip";
        public static final String PARAM_MSG_TITLE = "msg.title";
        public static final String PARAM_MSG_USERNAME = "msg.username";
        public static final String PARAM_MSG_ID = "msg.id";
        public static final String MESSAGE_OBJ = "message_obj";
        
        
        public static final String WEIBO_URL = "http://weibo.com/u/3335328522?topnav=1&wvr=5";
    }
    
    public static final String JSON_LIST_NAME = "list";
    
    public static final String JSON_GOODS_LIST_NAME = "goodsList";
    
    public static final String JSON_CART_ITEM_LIST_NAME = "cartItemList";
    
    public static final String JSON_OBJ_NAME_GOODS = "goods";
    
    public static final String JSON_OBJ_NAME_MEMBER = "member";
    public static final String JSON_OBJ_NAME_GROUP = "group";
    
    public static final String JSON_OBJ_NAME_PAGER = "pager";
    public static final String JSON_OBJ_NAME_SHAREITEM = "shareitem";
    
    public static final String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/MXL";
    public static final String IMAGE_CACHE_PATH = CACHE_PATH+"/image";
    
    /**
     * Extra data define here
     */
    public static final String EXTRA_GOODS_CATEGORY_ID = "extra_goods_category_id";
    
    public static final String EXTRA_GOODS_OBJ = "extra_goods_obj";
    public static final String EXTRA_MEMBER_OBJ = "extra_member_obj";
    public static final String EXTRA_AREA_OBJ = "extra_area_obj";
    public static final String GROUPID_OBJ = "groupid_obj";
    public static final String EXTRA_CART_ITEM_CHANGED_INDEX = "extra_cart_item_changed_index";
    public static final String POSITION = "position";
    public static final String EXTRA_START_ORDER_LIST_KEY = "extra_start_order_list_key";
    public static final int EXTRA_START_ORDER_LIST_VALUE_UNPAY = 1;
    public static final int EXTRA_START_ORDER_LIST_VALUE_HISTORY = 2;
    public static final String EXTRA_OBJ_PAYMENT_PARAM = "extra_obj_payment_param";
    public static final String EXTRA_OBJ_FREE_SALE = "extra_obj_free_sale";
    public static final String EXTRA_MUILT_SELECT_MAX_NUM = "extra_muilt_select_max_num";
    
    
    public static final String EXTRA_HOME_ACTIVITY_KEY = "extra_start_account_activity";
    public static final String EXTRA_HOME_ACTIVITY_VALUE_START_ACCOUNT_ACTIVITY = "extra_home_activity_key_start_account_activity";
    public static final String EXTRA_CHANGE_DELIVER_ADDRESS = "extra_change_deliver_address";
    public static final String EXTRA_SELECTED_DELIVER_ADDRESS_POS = "extra_selected_deliver_address_pos";
    public static final String EXTRA_SELECTED_DELIVER_ADDRESS_ID = "extra_selected_deliver_address_id";
    public static final String EXTRA_LIST_UNPAY_ORDERS = "extra_list_unpay_orders";
    public static final String EXTRA_LIST_HISTORY_ORDERS = "extra_list_history_orders";
    public static final String EXTRA_LIST_PAYMENT_CONFIGS = "extra_list_payment_configs";
    public static final String EXTRA_LIST_CART_ITEMS = "extra_list_cart_items";
    
    public static final String DETAIL_OBJ = "detail_obj";
    public static final String GROUPBACKBEAN_OBJ = "groupbackbean_obj";
    public static final String TEMPLATEGROUP_OBJ = "templategroup_obj";
    public static final String SHAREITEM_OBJ = "shareitem_obj";
    public static final int SELECT_ALBUM = 3001;
    public static final int SELECT_PHOTO = 3002;
    public static final int SELECT_TEMPLATEGROUP = 3003;
    public static final int SELECT_PHOTOS = 3004;
    public static final int BUCKETDETAILACTIVITY_BACK = 3005;
    public static final int TAKE_PIC = 3006;
    /**
     * Actions define here
     */
    public static final String ACTION_LOGIN_SUCCESS = "com.extensivepro.mxl.ACTION_LOGIN_SUCCESS";
    public static final String ACTION_LOGIN_FAILED = "com.extensivepro.mxl.ACTION_LOGIN_FAILED";
    public static final String ACTION_LOAD_PRODUCTDETAIL_SUCCESS = "com.extensivepro.mxl.ACTION_LOAD_PRODUCTDETAIL_SUCCESS";
    public static final String ACTION_LOAD_PRODUCTDETAIL_FAILED = "com.extensivepro.mxl.ACTION_LOAD_PRODUCTDETAIL_FAILED";
    public static final String ACTION_LOAD_GROUP_SUCCESS = "com.extensivepro.mxl.ACTION_LOAD_GROUP_SUCCESS";

    
    public static final String ACTION_GEA_ALL_AREA_SUCCESS = "com.extensivepro.mxl.ACTION_GEA_ALL_AREA_SUCCESS";
    public static final String ACTION_ADD_DELIVER_ADDR_SUCCESS = "com.extensivepro.mxl.ACTION_ADD_DELIVER_ADDR_SUCCESS";
    public static final String ACTION_ADD_DELIVER_ADDR_FAILED = "com.extensivepro.mxl.ACTION_ADD_DELIVER_ADDR_FAILED";
    public static final String ACTION_EDIT_DELIVER_ADDR_SUCCESS = "com.extensivepro.mxl.ACTION_EDIT_DELIVER_ADDR_SUCCESS";
    public static final String ACTION_EDIT_DELIVER_ADDR_FAILED = "com.extensivepro.mxl.ACTION_EDIT_DELIVER_ADDR_FAILED";
    public static final String ACTION_DEL_DELIVER_ADDR_SUCCESS = "com.extensivepro.mxl.ACTION_DEL_DELIVER_ADDR_SUCCESS";
    public static final String ACTION_DEL_DELIVER_ADDR_FAILED = "com.extensivepro.mxl.ACTION_DEL_DELIVER_ADDR_FAILED";
    public static final String ACTION_SET_DEFALUT_DELIVER_ADDR_SUCCESS = "com.extensivepro.mxl.ACTION_SET_DEFALUT_DELIVER_ADDR_SUCCESS";
    public static final String ACTION_SET_DEFALUT_DELIVER_ADDR_FAILED = "com.extensivepro.mxl.ACTION_SET_DEFALUT_DELIVER_ADDR_FAILED";
    public static final String ACTION_GET_ALL_DELIVER_ADDR_SUCCESS = "com.extensivepro.mxl.ACTION_GET_ALL_DELIVER_ADDR_SUCCESS";
    public static final String ACTION_GET_ALL_DELIVER_ADDR_FAILED = "com.extensivepro.mxl.ACTION_GET_ALL_DELIVER_ADDR_FAILED";
    public static final String ACTION_ADD_CART_ITEM_SUCCSEE = "com.extensivepro.mxl.ACTION_ADD_CART_ITEM_SUCCSEE";
    public static final String ACTION_ADD_CART_ITEM_FAILED= "com.extensivepro.mxl.ACTION_ADD_CART_ITEM_FAILED";
    public static final String ACTION_LIST_ALL_CART_ITEM_SUCCSEE = "com.extensivepro.mxl.ACTION_LIST_ALL_CART_ITEM_SUCCSEE";
    public static final String ACTION_LIST_ALL_CART_ITEM_FAILED = "com.extensivepro.mxl.ACTION_LIST_ALL_CART_ITEM_FAILED";
    public static final String ACTION_REFRESH_CART_ITEM = "com.extensivepro.mxl.ACTION_REFRESH_CART_ITEM";
    public static final String ACTION_GET_ALL_PAYMENT_CONFIG = "com.extensivepro.mxl.ACTION_GET_ALL_PAYMENT_CONFIG";
    public static final String ACTION_SAVE_ORDER_SUCCESS = "com.extensivepro.mxl.ACTION_SAVE_ORDER_SUCCESS";
    public static final String ACTION_SAVE_ORDER_FAILED = "com.extensivepro.mxl.ACTION_SAVE_ORDER_FAILED";
    public static final String ACTION_PAY_ORDER_SUCCESS = "com.extensivepro.mxl.ACTION_PAY_ORDER_SUCCESS";
    public static final String ACTION_PAY_ORDER_FAILED = "com.extensivepro.mxl.ACTION_PAY_ORDER_FAILED";
    public static final String ACTION_START_ALIX_PAY_ORDER = "com.extensivepro.mxl.ACTION_START_ALIX_PAY_ORDER";
    public static final String ACTION_LIST_UNPAID_ORDER_SUCCESS = "com.extensivepro.mxl.ACTION_LIST_UNPAID_ORDER_SUCCESS";
    public static final String ACTION_LIST_UNPAID_ORDER_FAILED = "com.extensivepro.mxl.ACTION_LIST_UNPAID_ORDER_FAILED";
    public static final String ACTION_LIST_HISTORY_ORDER_SUCCESS = "com.extensivepro.mxl.ACTION_LIST_HISTORY_ORDER_SUCCESS";
    public static final String ACTION_LIST_HISTORY_ORDER_FAILED = "com.extensivepro.mxl.ACTION_LIST_HISTORY_ORDER_FAILED";
    public static final String ACTION_GETSHARE_SUCCESS = "com.extensivepro.mxl.ACTION_GETSHARE_SUCCESS";
    public static final String ACTION_LIST_DEPOSIT_CARD_SUCCESS = "com.extensivepro.mxl.ACTION_LIST_DEPOSIT_CARD_SUCCESS";
    public static final String ACTION_LIST_DEPOSIT_CARD_FAILED = "com.extensivepro.mxl.ACTION_LIST_DEPOSIT_CARD_FAILED";
    public static final String ACTION_SAVESHARE_SUCCESS = "com.extensivepro.mxl.ACTION_SAVESHARE_SUCCESS";
    public static final String ACTION_SAVESHARE_FAILED = "com.extensivepro.mxl.ACTION_SAVESHARE_FAILED";
    public static final String ACTION_LOAD_FREE_SALE_SUCCESS = "com.extensivepro.mxl.ACTION_LOAD_FREE_SALE_SUCCESS";
    public static final String ACTION_LOAD_FREE_SALE_FAILED = "com.extensivepro.mxl.ACTION_LOAD_FREE_SALE_FAILED";
    public static final String ACTION_LOGOUT_SUCCESS = "com.extensivepro.mxl.ACTION_LOGOUT_SUCCESS";
    public static final String ACTION_LOGOUT_FAILED = "com.extensivepro.mxl.ACTION_LOGOUT_FAILED";
    public static final String ACTION_START_ALIX_PAY_DEPOSIT_CARD = "com.extensivepro.mxl.ACTION_START_ALIX_RECHARGE_ACCOUNT";

    public static final String PRODUCTNAME = "productName";
    public static final String ITEMNAME = "ItemName";
    public static final String ITEMPATH = "ItemPath";
    public static final String ALBUM_IMAGEPATHSFROMPHOTOS = "photos_imagePaths";
 
    
    /**
     * area grades
     */
    public static final int AREA_GRADE_COUNTRY = 0;
    public static final int AREA_GRADE_PROVINCE = 1;
    public static final int AREA_GRADE_CITY = 2;
    public static final int AREA_GRADE_AREA = 3;
    
    public static final int PHONE_NUM_CONST = 11;
    public static final int POST_CODE_CONST = 6;
    
    public static final String PAYMENT_TYPE_ONLINE = "online";
    public static final String PAYMENT_TYPE_DEPOSIT = "deposit";
    
    public class Str_Moduel
    {
        public static final String heart_balloon = "心形气球";
        public static final String star_balloon = "星形气球";
        public static final String round_balloon = "圆形气球";
    }
    
    public class OSSValues
    {
        public static final String ACCESS_ID = "eTC2WNoPY5z83Ai8";
        public static final String ACCESS_KEY = "B0HBeJpmlOmx1FJCd7hxndL10rHBcW";
        public static final String BUCKET_NAME = "youliyin";
        public static final String OSS_ENDPOINT = "http://oss.aliyuncs.com";
        public static final String OSS_FILE_PREFIX = "http://youliyin.oss.aliyuncs.com/";
    }
    
    public class PushMobuel
    {
        public static final String URI_PUSH = BASE_URI+"/api/remote_notification!save.action";
    }
}
