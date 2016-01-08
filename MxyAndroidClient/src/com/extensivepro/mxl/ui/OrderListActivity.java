package com.extensivepro.mxl.ui;

import java.net.URLEncoder;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.extensivepro.alipay.AlixId;
import com.extensivepro.alipay.AlixUtil;
import com.extensivepro.alipay.BaseHelper;
import com.extensivepro.alipay.MobileSecurePayHelper;
import com.extensivepro.alipay.MobileSecurePayer;
import com.extensivepro.alipay.ResultChecker;
import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.Order;
import com.extensivepro.mxl.app.bean.PaymentParameter;
import com.extensivepro.mxl.app.cart.CartManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.DialogHelper;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.OrderListAdapter;
import com.extensivepro.mxl.widget.TitleBar;

/** 
 * @Description 
 * @author Admin
 * @date 2013-5-14 下午4:21:46 
 * @version V1.3.1
 */

public class OrderListActivity extends BaseActivity
{
    private static final String TAG = OrderListActivity.class.getSimpleName();
    
    private TitleBar mTitleBar;
    
    private boolean mIsUnpaid;
    
    private OrderListAdapter mAdapter;
    private ListView mOrderListView;
    
    private StateReceiver mStateReceiver;
    
    private List<Order> mOrders;
    
    private ProgressBar mLoadProgressBar;
    
    private View mEmptyOrderView;
    
    private View mBodyContanier;
    
    private class StateReceiver extends BroadcastReceiver
    {
        @SuppressWarnings("unchecked")
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.d(TAG, "StateReceiver.onReceive()[action:"+action+"]");
            if(action!=null && action.equals(Const.ACTION_LIST_UNPAID_ORDER_SUCCESS))
            {
                if(mIsUnpaid)
                {
                    mOrderListView.setVisibility(View.VISIBLE);
                    mLoadProgressBar.setVisibility(View.GONE);
                    Object obj = intent.getSerializableExtra(Const.EXTRA_LIST_UNPAY_ORDERS);
                    if(obj instanceof List<?>)
                    {
                        mOrders = (List<Order>) obj;
                    }
                    mAdapter.notifyDateSetChanged(mOrders);
//                    setListViewHeightBasedOnChildren(mOrderListView);
                    showViewByCount();
                }
                return;
            }
            if(action!=null && action.equals(Const.ACTION_LIST_HISTORY_ORDER_SUCCESS))
            {
                if(!mIsUnpaid)
                {
                    mOrderListView.setVisibility(View.VISIBLE);
                    mLoadProgressBar.setVisibility(View.GONE);
                    Object obj = intent.getSerializableExtra(Const.EXTRA_LIST_HISTORY_ORDERS);
                    if(obj instanceof List<?>)
                    {
                        mOrders = (List<Order>) obj;
                    }
                    mAdapter.notifyDateSetChanged(mOrders);
//                    setListViewHeightBasedOnChildren(mOrderListView);
                    showViewByCount();
                }
                return;
            }
            if(action!=null && action.equals(Const.ACTION_START_ALIX_PAY_ORDER))
            {
                Object obj = intent.getSerializableExtra(Const.EXTRA_OBJ_PAYMENT_PARAM);
                if(obj instanceof PaymentParameter)
                {
                    pay(OrderListActivity.this, (PaymentParameter)obj, mHandler);
                }
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Logger.d(TAG, "onCreate()[access]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list);
        mEmptyOrderView = findViewById(R.id.empty_order);
        mBodyContanier = findViewById(R.id.m_body_contanier);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mLoadProgressBar = (ProgressBar) findViewById(R.id.load_progress);
        mOrderListView = (ListView) findViewById(R.id.orders_list);
        int type = getIntent().getIntExtra(Const.EXTRA_START_ORDER_LIST_KEY,
                Const.EXTRA_START_ORDER_LIST_VALUE_UNPAY);
        mIsUnpaid = type == Const.EXTRA_START_ORDER_LIST_VALUE_UNPAY;
        mAdapter = new OrderListAdapter(this, mOrders,
                mIsUnpaid);
        mOrderListView.setAdapter(mAdapter);
        refreshDataByType(getIntent());
        regReceiver();
        
    }

    private void refreshDataByType(Intent intent)
    {
        if (intent != null)
        {
            int type = intent.getIntExtra(
                    Const.EXTRA_START_ORDER_LIST_KEY, -1);
            if (type == Const.EXTRA_START_ORDER_LIST_VALUE_UNPAY
                    || type == Const.EXTRA_START_ORDER_LIST_VALUE_HISTORY)
            {
                boolean newType = type == Const.EXTRA_START_ORDER_LIST_VALUE_UNPAY;
                mIsUnpaid = newType;
                mAdapter.notifyDateSetChanged(null);
                mAdapter.notifyListType(mIsUnpaid);
                CartManager.getInstance().listOrders(mIsUnpaid);
                mOrderListView.setVisibility(View.GONE);
                mLoadProgressBar.setVisibility(View.VISIBLE);
                switch (type)
                {
                    case Const.EXTRA_START_ORDER_LIST_VALUE_HISTORY:
                        mTitleBar.setTitle(R.string.order_history_txt);
                        break;
                    case Const.EXTRA_START_ORDER_LIST_VALUE_UNPAY:
                        mTitleBar.setTitle(R.string.unpay_orders_txt);
                        break;
                }
            }
        }
    }
    
    @Override
    protected void onDestroy()
    {
        Logger.d(TAG, "onDestroy()[access]");
        super.onDestroy();
        mAdapter.clearCache();
        unregReceiver();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Logger.d(TAG, "onNewIntent()[access]");
        super.onNewIntent(intent);
    }
    
    private void showViewByCount()
    {
        if(mOrderListView.getCount() == 0)
        {
            mBodyContanier.setVisibility(View.GONE);
            mEmptyOrderView.setVisibility(View.VISIBLE);
        }
        else 
        {
           mBodyContanier.setVisibility(View.VISIBLE);
           mEmptyOrderView.setVisibility(View.GONE);
        }
    }

    
    private void regReceiver()
    {
        if(mStateReceiver == null)
        {
            mStateReceiver = new StateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_LIST_HISTORY_ORDER_SUCCESS);
            filter.addAction(Const.ACTION_LIST_HISTORY_ORDER_FAILED);
            filter.addAction(Const.ACTION_LIST_UNPAID_ORDER_SUCCESS);
            filter.addAction(Const.ACTION_LIST_UNPAID_ORDER_FAILED);
            filter.addAction(Const.ACTION_START_ALIX_PAY_ORDER);
            filter.addAction(Const.ACTION_LOGIN_FAILED);
            registerReceiver(mStateReceiver, filter);
        }
    }
    
    private void unregReceiver()
    {
        if(mStateReceiver != null)
        {
            unregisterReceiver(mStateReceiver);
            mStateReceiver = null;
        }
    }
    
    // 这里接收支付结果，支付宝手机端同步通知
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg)
        {
            try
            {
                String strRet = (String) msg.obj;

                Log.e(TAG, strRet); // strRet范例：resultStatus={9000};memo={};result={partner="2088201564809153"&seller="2088201564809153"&out_trade_no="050917083121576"&subject="123456"&body="2010新款NIKE 耐克902第三代板鞋 耐克男女鞋 386201 白红"&total_fee="0.01"&notify_url="http://notify.java.jpxx.org/index.jsp"&success="true"&sign_type="RSA"&sign="d9pdkfy75G997NiPS1yZoYNCmtRbdOP0usZIMmKCCMVqbSG1P44ohvqMYRztrB6ErgEecIiPj9UldV5nSy9CrBVjV54rBGoT6VSUF/ufjJeCSuL510JwaRpHtRPeURS1LXnSrbwtdkDOktXubQKnIMg2W0PreT1mRXDSaeEECzc="}
                switch (msg.what)
                {
                    case AlixId.RQF_PAY:
                    {
                        //
                        closeProgress();

                        Logger.d(TAG, strRet);

                        // 处理交易结果
                        try
                        {
                            // 获取交易状态码，具体状态代码请参看文档
                            String tradeStatus = "resultStatus={";
                            int imemoStart = strRet.indexOf("resultStatus=");
                            imemoStart += tradeStatus.length();
                            int imemoEnd = strRet.indexOf("};memo=");
                            tradeStatus = strRet
                                    .substring(imemoStart, imemoEnd);

                            // 先验签通知
                            ResultChecker resultChecker = new ResultChecker(
                                    strRet);
                            int retVal = resultChecker.checkSign();
                            // 验签失败
                            if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED)
                            {
                                DialogHelper.showDialog(
                                        OrderListActivity.this,
                                        getResources().getString(
                                                R.string.prompt),
                                        getResources().getString(
                                                R.string.check_sign_failed),
                                        android.R.drawable.ic_dialog_alert);
                            }
                            else
                            {// 验签成功。验签成功后再判断交易状态码
                                if (tradeStatus.equals("9000"))// 判断交易状态码，只有9000表示交易成功
                                    DialogHelper
                                            .createDialog(
                                                    OrderListActivity.this,
                                                    getResources().getString(
                                                            R.string.prompt),
                                                    getResources()
                                                            .getString(
                                                                    R.string.prompt_pay_success)
                                                            + tradeStatus,
                                                    R.drawable.infoicon).show();
                                else
                                    DialogHelper.createDialog(
                                            OrderListActivity.this,
                                            getResources().getString(
                                                    R.string.prompt),
                                            getResources().getString(
                                                    R.string.prompt_pay_failed)
                                                    + tradeStatus,
                                            R.drawable.infoicon).show();
                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            DialogHelper.showDialog(OrderListActivity.this,
                                    getResources().getString(R.string.prompt),
                                    strRet, R.drawable.infoicon);
                        }
                    }
                        break;
                }

                super.handleMessage(msg);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };
    
    private ProgressDialog  mProgress = null;
    
    private void pay(Activity context,PaymentParameter paymentParameter,Handler handler)
    {
        if(paymentParameter == null || handler == null )
        {
            Logger.e(TAG, "pay()[paymentParamter or handler can not be null");
            return;
        }
        


        //
        // check to see if the MobileSecurePay is already installed.
        // 检测安全支付服务是否安装
        MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(context);
        boolean isMobile_spExist = mspHelper.detectMobile_sp();
        if (!isMobile_spExist)
            return;

        // check some info.
        // 检测配置信息
        if (!AlixUtil.checkInfo())
        {
            DialogHelper.createDialog(this, getString(R.string.prompt),
                    getString(R.string.miss_partner_seller),
                    R.drawable.infoicon).show();
            return;
        }

        // start pay for this order.
        // 根据订单信息开始进行支付
        try {
            // prepare the order info.
            // 准备订单信息
            String orderInfo = AlixUtil.getOrderInfo(paymentParameter);
            // 这里根据签名方式对订单信息进行签名
            String signType = AlixUtil.getSignType();
            String strsign = AlixUtil.sign(signType, orderInfo);
            Logger.v(TAG,"sign()[strsign:"+strsign+"]");
            // 对签名进行编码
            strsign = URLEncoder.encode(strsign);
            // 组装好参数
            String info = orderInfo + "&sign=" + "\"" + strsign + "\"" + "&"
                    + AlixUtil.getSignType();
            Logger.v(TAG,"sign()[info:"+info+"]");
            // start the pay.
            // 调用pay方法进行支付
            MobileSecurePayer msp = new MobileSecurePayer();
            boolean bRet = msp.pay(info, handler, AlixId.RQF_PAY, context);

            if (bRet)
            {
                // show the progress bar to indicate that we have started
                // paying.
                // 显示“正在支付”进度条
                closeProgress();
                mProgress = DialogHelper.showProgress(context, null,
                        getString(R.string.paying_progress), false, true);
            } else
                ;
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, R.string.remote_call_failed,
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    private void closeProgress() {
        try {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
