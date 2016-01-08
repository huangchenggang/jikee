package com.extensivepro.mxl.app.cart;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.text.TextUtils;

import com.extensivepro.mxl.app.EventDispacher;
import com.extensivepro.mxl.app.ICommonCallback;
import com.extensivepro.mxl.app.MxlApplication;
import com.extensivepro.mxl.app.bean.CartItem;
import com.extensivepro.mxl.app.bean.CartItemCustomValue;
import com.extensivepro.mxl.app.bean.Goods;
import com.extensivepro.mxl.app.bean.Order;
import com.extensivepro.mxl.app.bean.Pager;
import com.extensivepro.mxl.app.bean.PaymentConfig;
import com.extensivepro.mxl.app.bean.PaymentConfig.PaymentConfigType;
import com.extensivepro.mxl.app.bean.PaymentMessage;
import com.extensivepro.mxl.app.bean.Receiver;
import com.extensivepro.mxl.app.bean.StatusMessage.Status;
import com.extensivepro.mxl.app.bean.Templates;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.app.client.ClientThread;
import com.extensivepro.mxl.app.client.Command;
import com.extensivepro.mxl.product.ProductManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.DataUtil;
import com.extensivepro.mxl.util.JsonUtil;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.util.OSSUploadImageTask;
import com.extensivepro.mxl.util.OSSUploadImageTask.IUploadCallback;

public class CartManager implements ICommonCallback
{
    private static final String TAG = CartManager.class.getSimpleName();

    private static CartManager mInstance;

    private static final int MOUDEL_CODE = EventDispacher.MoudelCode.MOUDEL_CODE_TROLLERY;
    
    private List<PaymentConfig> mPaymentConfigs;
    
    public interface ILoadCartItemsCb
    {
        void onLoadSuccess(List<CartItem> cartItems);
        void onLoadFailed(String errMsg);
    }
    
    private boolean mIsUnpaidOrderList;
    
    private CartManager()
    {
        EventDispacher.regCallback(MOUDEL_CODE, this);
    }
    
    public static CartManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new CartManager();
        }
        return mInstance;
    }
    

    @SuppressWarnings("unchecked")
    @Override
    public void onResp(int reqCode, int reasonCode, InputStream stream)
    {
        if(stream == null || reasonCode != Const.CONN_REQUEST_OK)
        {
            switch(reqCode)
            {
                case EventDispacher.EventCodeBusiness.EVENT_CODE_ADD_CART_ITEM:
                    break;
            }
        }
        else 
        {
            String resp = DataUtil.streamToString(stream);
            Logger.d(TAG, "onResp()[reqCode:"+reqCode+",reasonCode:"+",resp:" + resp+"]");
            Intent intent = null;
            switch(reqCode)
            {
                case EventDispacher.EventCodeBusiness.EVENT_CODE_ADD_CART_ITEM:
                    intent = new Intent(Const.ACTION_ADD_CART_ITEM_SUCCSEE);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    getAllCartItems();
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_ALL_CART_ITEMS:
                    List<CartItem> mCartItems = (List<CartItem>) JsonUtil
                            .jsonToList(resp, CartItem.class,
                                    Const.JSON_CART_ITEM_LIST_NAME);
//                    if(mCartItems != null && !mCartItems.isEmpty())
//                    {
//                        for(int i=0;i<mCartItems.size();i++)
//                        {
//                            CartItem item = mCartItems.get(i);
//                            if(item != null && item.getProduct() != null && item.getProduct().getGoods() != null)
//                            {
//                                ProductManager.getInstance()
//                                        .loadProductDeatail(
//                                                item.getProduct().getGoods()
//                                                        .getId());
//                            }
//                        }
//                    }
                    intent = new Intent(Const.ACTION_LIST_ALL_CART_ITEM_SUCCSEE);
                    intent.putExtra(Const.EXTRA_LIST_CART_ITEMS, (Serializable)mCartItems);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_GET_PAYMENT_CONFIG:
                    mPaymentConfigs = (List<PaymentConfig>) JsonUtil
                            .jsonToList(resp, PaymentConfig.class,
                                    Const.JSON_LIST_NAME);
                    intent = new Intent(Const.ACTION_GET_ALL_PAYMENT_CONFIG);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_SAVE_ORDER:
                    intent = new Intent(Const.ACTION_SAVE_ORDER_SUCCESS);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    getAllCartItems();
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_UNPAID_ORDER:
                case EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_HISTORY_ORDER:
                    onListOrdersSuccess(
                            resp,
                            reqCode == EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_UNPAID_ORDER);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_INVALID_ORDER:
                    listOrders(mIsUnpaidOrderList);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_PAY_ORDER:
                    PaymentMessage paymentMessage = (PaymentMessage) JsonUtil
                            .jsonToObject(resp, PaymentMessage.class, null);
                    if(paymentMessage != null && paymentMessage.getStatus() == Status.success)
                    {
                        switch(paymentMessage.getPaymentStatus())
                        {
                            case success:
                                if (paymentMessage.getPaymentType() == PaymentConfigType.deposit)
                                {//刷新订单页面
                                    listOrders(mIsUnpaidOrderList);
                                    break;
                                }
                                break;
                            case ready:
                                if(paymentMessage.getPaymentType() == PaymentConfigType.mobile)
                                {
                                    // TODO alix pay here
                                    Logger.d(TAG, "onResp()[paymentMsg:"
                                            + paymentMessage + "]");
                                    intent = new Intent(
                                            Const.ACTION_START_ALIX_PAY_ORDER);
                                    intent.putExtra(
                                            Const.EXTRA_OBJ_PAYMENT_PARAM,
                                            paymentMessage.getPaymentParams());
                                    ClientManager.getInstance().getAppContext()
                                            .sendBroadcast(intent);
                                }
                                break;
                            case failed:
                                intent = new Intent(Const.ACTION_PAY_ORDER_FAILED);
                                ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_EDIT_CART_ITEM:
                case EventDispacher.EventCodeBusiness.EVENT_CODE_DEL_CART_ITEM:
                    getAllCartItems();
                    break;
            }
        }
    }
    
    /**
     * 
     * @Description 
     * @author damon
     * @param productId 货品id
     * @param quantity 数量
     * @param carItemCustomValue 自定义项(处理后以json格式输出)CartItemCustomValue
     */
    public void addShoppingTrollery(String productId,int quantity,List<CartItemCustomValue> carItemCustomValues)
    {
//        productId = "402880e83e16151d013e16cc8f980007";
//        quantity = 1;
//        carItemCustomValues = testAddCart();
        Logger.d(TAG, "addShoppingTrollery()[id:" + productId + ",quantity:"
                + quantity + ",carItemCustomValue:" + carItemCustomValues + "]");
        
        final Command command = new Command();
        command.addAttribute(Const.CartMoudel.PARAM_ID,
                productId);
        command.addAttribute(Const.CartMoudel.PARAM_QUANTITY,
                String.valueOf(quantity));
        command.addAttribute(Const.CartMoudel.PARAM_CUSTOMVALUES,
                "");
        command.addAttribute(Const.CartMoudel.PARAM_CUSTOMVALUES,
                "{"+JsonUtil.beanToJson(carItemCustomValues)+"}");
        final ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_ADD_CART_ITEM,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    
    public void getAllCartItems()
    {
        Logger.d(TAG, "getAllShoppingTrollerys()");
        Command command = new Command();
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_ALL_CART_ITEMS,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    private List<CartItemCustomValue> testAddCart()
    {
        List<CartItemCustomValue> list = new ArrayList<CartItemCustomValue>();
        CartItemCustomValue cartItemCustomValue1 = new CartItemCustomValue();
        List<String> images1 = new ArrayList<String>();
        images1.add("image11");
        images1.add("image12");
        images1.add("image13");
        cartItemCustomValue1.setImages(images1);
        cartItemCustomValue1.setSpecificationId("402880e83e6dc9ce013e6df8e2d70005");
        Templates template = new Templates();
        template.setImage("template11");
        template.setName("圆形气球");
        cartItemCustomValue1.setTemplate(template);
        list.add(cartItemCustomValue1);
//        CartItemCustomValue cartItemCustomValue2 = new CartItemCustomValue();
//        List<String> images2 = new ArrayList<String>();
//        images1.add("image21");
//        images1.add("image22");
//        images1.add("image23");
//        cartItemCustomValue1.setImages(images1);
//        cartItemCustomValue1.setSpecificationId("");
//        Templates template2 = new Templates();
//        template.setImage("template11");
//        template.setName("fdsaf");
//        cartItemCustomValue1.setTemplate(template2);
//        list.add(cartItemCustomValue1);
        return list;
    }
    
//    public List<CartItem> getCurCartItems()
//    {
//        return mCartItems;
//    }
    
    public void refreshCartItems(Goods goods)
    {
        Logger.d(TAG, "refreshCartItems()");
//        if(mCartItems == null || mCartItems.isEmpty() || goods == null)
//        {
//            return;
//        }
//        for(int i=0;i<mCartItems.size();i++)
//        {
//            if (mCartItems.get(i) != null && mCartItems.get(i).getProduct() != null
//                    && mCartItems.get(i).getProduct().getGoods() != null)
//            {
//                String itemGoodsId = mCartItems.get(i).getProduct().getGoods().getId();
//                String newItemGoodsId = goods.getId();
//                if(!TextUtils.isEmpty(itemGoodsId) && ! TextUtils.isEmpty(newItemGoodsId) && itemGoodsId.equals(newItemGoodsId))
//                {
//                    mCartItems.get(i).getProduct().setGoods(goods);
//                    Intent intent = new Intent(Const.ACTION_REFRESH_CART_ITEM);
//                    intent.putExtra(Const.EXTRA_CART_ITEM_CHANGED_INDEX, i);
//                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
//                }
//            }
//        }
    }
    
    public void loadPaymentConfig()
    {
        Logger.d(TAG, "loadPaymentConfig()");
        Command command = new Command();
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_GET_PAYMENT_CONFIG,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    public List<PaymentConfig> getPaymentConfigs()
    {
        return mPaymentConfigs;
    }
    
    public void saveOrder(Receiver receiver,String paymentConfigId,String orderMemo)
    {
        if(receiver == null || paymentConfigId == null )
        {
            Logger.e(TAG, "saveOrder()[failed,receiver or payment config can't be null]");
            return;
        }
        Logger.d(TAG, "saveOrder()");
        Command command = new Command();
        command.addAttribute(Const.CartMoudel.PARAM_RECEIVER_ID, receiver.getId());
        command.addAttribute(Const.CartMoudel.PARAM_RECEIVER_NAME, receiver.getName());
        command.addAttribute(Const.CartMoudel.PARAM_RECEIVER_ADDRESS, receiver.getAddress());
        command.addAttribute(Const.CartMoudel.PARAM_RECEIVER_ZIPCODE, receiver.getZipCode());
        command.addAttribute(Const.CartMoudel.PARAM_RECEIVER_PHONE, receiver.getPhone());
        command.addAttribute(Const.CartMoudel.PARAM_RECEIVER_MOBILE, receiver.getMobile());
        command.addAttribute(Const.CartMoudel.PARAM_PAYMENTCONFIG_ID, paymentConfigId);
        command.addAttribute(Const.CartMoudel.PARAM_ORDER_MEMO, orderMemo);
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_SAVE_ORDER,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    public void listOrders(boolean isUnpaid)
    {
        Logger.d(TAG, "listOrders()[isUnpaid:"+isUnpaid+"]");
        mIsUnpaidOrderList = isUnpaid;
        Command command = new Command();
        int reqCode = isUnpaid ? EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_UNPAID_ORDER
                : EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_HISTORY_ORDER;
        ClientThread thread = new ClientThread(reqCode, MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    @SuppressWarnings("unchecked")
    private void onListOrdersSuccess(String resp,boolean isUnpaid)
    {
        Pager pager = (Pager) JsonUtil.jsonToObject(resp, Pager.class, Const.JSON_OBJ_NAME_PAGER);
        if(pager.getResult() != null)
        {
            String json = JsonUtil.beanToJson(pager.getResult());
            List<Order> orders = (List<Order>) JsonUtil
                    .jsonToList(json,
                            Order.class, null);
            Logger.d(TAG, "onResp()[orders:"+orders+"]");
            Intent intent = null;
            if(pager.getPageNumber() == 1)
            {
                if(isUnpaid)
                {
//                    mUnPaidOrders.clear();
//                    mUnPaidOrders.addAll(orders);
                    intent = new Intent(Const.ACTION_LIST_UNPAID_ORDER_SUCCESS);
                    intent.putExtra(Const.EXTRA_LIST_UNPAY_ORDERS, (Serializable)orders);
                }
                else
                {
//                    mHistoryOrders.clear();
//                    mHistoryOrders.addAll(orders);
                    intent = new Intent(Const.ACTION_LIST_HISTORY_ORDER_SUCCESS);
                    intent.putExtra(Const.EXTRA_LIST_HISTORY_ORDERS, (Serializable)orders);
                }
            }
            else 
            {
                if(isUnpaid)
                {
//                    mUnPaidOrders.addAll(orders);
                    intent = new Intent(Const.ACTION_LIST_UNPAID_ORDER_SUCCESS);
                    intent.putExtra(Const.EXTRA_LIST_UNPAY_ORDERS, (Serializable)orders);
                }
                else
                {
//                    mHistoryOrders.addAll(orders);
                    intent = new Intent(Const.ACTION_LIST_HISTORY_ORDER_SUCCESS);
                    intent.putExtra(Const.EXTRA_LIST_HISTORY_ORDERS, (Serializable)orders);
                }
            }
            if(intent != null)
            {
                ClientManager.getInstance().getAppContext().sendBroadcast(intent);
            }
        }
    }
    
//    public List<Order> getCurOrderList(boolean isUnPaid)
//    {
//        if(isUnPaid)
//        {
//            return mUnPaidOrders;
//        }
//        else 
//        {
//            return mHistoryOrders;
//        }
//    }
    
    public void payOrder(String orderId)
    {
        if(TextUtils.isEmpty(orderId) )
        {
            Logger.e(TAG, "payOrder()[failed,orderId can't be null]");
            return;
        }
        Logger.d(TAG, "payOrder()");
        Command command = new Command();
        command.addAttribute(Const.CartMoudel.PARAM_ID, orderId);
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_PAY_ORDER,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    /**
     * 放弃订单
     * @Description 
     * @author damon
     * @param orderId
     */
    public void invalidOrder(String orderId)
    {
        if(TextUtils.isEmpty(orderId) )
        {
            Logger.e(TAG, "invalidOrder()[failed,orderId can't be null]");
            return;
        }
        Logger.d(TAG, "invalidOrder()");
        Command command = new Command();
        command.addAttribute(Const.CartMoudel.PARAM_ID, orderId);
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_INVALID_ORDER,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    public void editCartItem(String cartItemId,String count)
    {
        Logger.d(TAG, "editCartItem()[cartItemId:" + cartItemId + ",count:"
                + count + "]");
        Command command = new Command();
        command.addAttribute(Const.CartMoudel.PARAM_ID, cartItemId);
        command.addAttribute(Const.CartMoudel.PARAM_QUANTITY, String.valueOf(count));
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_EDIT_CART_ITEM,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    public void delCartItem(String cartItemId)
    {
        Logger.d(TAG, "deleteCartItem()[cartItemId:" + cartItemId + "]");
        Command command = new Command();
        command.addAttribute(Const.CartMoudel.PARAM_ID, cartItemId);
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_DEL_CART_ITEM,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    public void clearCache()
    {
//        mCartItems = null;
//        mHistoryOrders = null;
//        mPaymentConfigs = null;
//        mUnPaidOrders = null;
    }
}
