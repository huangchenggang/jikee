package com.extensivepro.mxl.ui;

import java.net.URLEncoder;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.extensivepro.alipay.AlixId;
import com.extensivepro.alipay.AlixUtil;
import com.extensivepro.alipay.MobileSecurePayHelper;
import com.extensivepro.alipay.MobileSecurePayer;
import com.extensivepro.alipay.ResultChecker;
import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.DepositCard;
import com.extensivepro.mxl.app.bean.Member;
import com.extensivepro.mxl.app.bean.PaymentParameter;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.DialogHelper;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.AccountRechargeListAdapter;
import com.extensivepro.mxl.widget.TitleBar;
import com.extensivepro.mxl.widget.WidgetUtil;

/**
 * @Description
 * @author Admin
 * @date 2013-5-10 下午4:50:52
 * @version V1.3.1
 */

public class AccountRechargeActivity extends BaseActivity implements OnItemClickListener
{
    private static final String TAG = AccountRechargeActivity.class
            .getSimpleName();

    private TitleBar mTitleBar;

    private ListView mRechargeList;
    
    private List<DepositCard> mContents;

    private AccountRechargeListAdapter mAdapter;
    
    private StateReceiver mStateReceiver;

    private class StateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.d(TAG, "StateReceiver.onReceive()[action:" + action + "]");
            if (action != null
                    && action.equals(Const.ACTION_LIST_DEPOSIT_CARD_SUCCESS))
            {
                mContents = AccountManager.getInstance().getCurDepositCards();
                mAdapter.notifyDataSetChanged(mContents);
                setListViewHeightBasedOnChildren(mRechargeList);
            }
            else if (action != null
                    && action.equals(Const.ACTION_LIST_DEPOSIT_CARD_FAILED))
            {
            }
            else if (action != null
                    && action.equals(Const.ACTION_START_ALIX_PAY_DEPOSIT_CARD))
            {
                Object obj = intent.getSerializableExtra(Const.EXTRA_OBJ_PAYMENT_PARAM);
                if(obj instanceof PaymentParameter)
                {
                    pay(AccountRechargeActivity.this, (PaymentParameter)obj, mHandler);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_recharge);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle(R.string.account_recharge_txt);
        mRechargeList = (ListView) findViewById(R.id.account_recharge_list);
        mAdapter = new AccountRechargeListAdapter(this, mContents);
        mRechargeList.setAdapter(mAdapter);
        mRechargeList.setOnItemClickListener(this);
        setListViewHeightBasedOnChildren(mRechargeList);
        regReceiver();
        
        Member member = AccountManager.getInstance().getCurrentAccount();
        if (member != null)
        {
            ((TextView) findViewById(R.id.cur_account)).setText(getResources()
                    .getString(R.string.cur_account_txt, member.getUsername()));
            ((TextView) findViewById(R.id.available_balance))
                    .setText(getResources()
                            .getString(R.string.available_balance_txt,
                                    member.getDeposit()));
        }
        AccountManager.getInstance().loadAllDepositCard();
    }

    
    private void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null)
        {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
           View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        if(listAdapter.getCount() > 0)
        {
            totalHeight += (totalHeight/listAdapter.getCount())/2;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregReceiver();
        mAdapter.clearCache();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
    }
    
    private void regReceiver()
    {
        if(mStateReceiver == null)
        {
            mStateReceiver = new StateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_LIST_DEPOSIT_CARD_SUCCESS);
            filter.addAction(Const.ACTION_LIST_DEPOSIT_CARD_FAILED);
            filter.addAction(Const.ACTION_START_ALIX_PAY_DEPOSIT_CARD);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        Logger.d(TAG, "onItemClick()[access]");
        showPayDepositCardDialog(position);
    }
    
    private void  showPayDepositCardDialog(int position)
    {
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_pay_deposit_card);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setNeutralButton(R.string.confirm, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                DepositCard depositCard = (DepositCard) mAdapter.getItem(pos);
                AccountManager.getInstance().payDepositCard(depositCard);
            }
        });
        builder.create().show();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
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
                                        AccountRechargeActivity.this,
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
                                                    AccountRechargeActivity.this,
                                                    getResources().getString(
                                                            R.string.prompt),
                                                    getResources()
                                                            .getString(
                                                                    R.string.prompt_pay_success)
                                                            + tradeStatus,
                                                    R.drawable.infoicon).show();
                                else
                                    DialogHelper.createDialog(
                                            AccountRechargeActivity.this,
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
                            DialogHelper.createDialog(AccountRechargeActivity.this,
                                    getResources().getString(R.string.prompt),
                                    strRet, R.drawable.infoicon).show();
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
