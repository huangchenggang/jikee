package com.extensivepro.mxl.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.CartItem;
import com.extensivepro.mxl.app.bean.PaymentConfig;
import com.extensivepro.mxl.app.bean.Receiver;
import com.extensivepro.mxl.app.cart.CartManager;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.ImageDownloader;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.ShoppingTrolleryListAdapter;
import com.extensivepro.mxl.widget.TitleBar;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 16, 2013 11:18:40 AM
 * @version V1.3.1
 */
public class ShoppingTrolleyActivity extends BaseActivity implements
        OnClickListener
{
    private static final String TAG = ShoppingTrolleyActivity.class
            .getSimpleName();

    private static final int DELIVER_POS = -1;

    private TitleBar mTitleBar;

    private StateReceiver mStateReceiver;

    private ListView mlistView;

    private ShoppingTrolleryListAdapter mAdapter;

    private View mShoppingTrolleryBasicInfo;

    private View mEmptyCartView;

    private View mCartBodyContianer;

    private List<PaymentConfig> mPaymentConfigs;

    private int changeReceiverPos;

    private int defaultReceiverPos;

    private Receiver selectReceiver;

    private Receiver defaultReceiver;

    private List<Receiver> mReceivers;

    private List<CartItem> mCartItems;

    private boolean mNeedReload = false;

    private String from = "";
    
    private class StateReceiver extends BroadcastReceiver
    {
        @SuppressWarnings("unchecked")
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.d(TAG, "StateReceiver.onReceive()[action:" + action + "]");
            if (action != null
                    && action.equals(Const.ACTION_LIST_ALL_CART_ITEM_SUCCSEE)
                    && AccountManager.getInstance().hasLogined())
            {
                Object obj = intent
                        .getSerializableExtra(Const.EXTRA_LIST_CART_ITEMS);
                if (obj instanceof List<?>)
                {
                    mCartItems = (List<CartItem>) obj;
                    showShoppingTrolleryBasicInfos();
                    mAdapter.notifyDataChanged(mCartItems);
                }
//                if(from.equals("DeliverAddressActivity")){
//                    return;
//                }
                    setListViewHeightBasedOnChildren(mlistView);
                    showViewByCount();
                    
                if (getParent() instanceof HomeActivity)
                {
                    ((HomeActivity) getParent())
                            .refreshShoppingTrolleryCount(mAdapter
                                    .getCartItemTotalCount());
                }
            }
            else if (action != null
                    && action.equals(Const.ACTION_REFRESH_CART_ITEM))
            {
                int changedIndex = intent.getIntExtra(
                        Const.EXTRA_CART_ITEM_CHANGED_INDEX, -1);
                // if (changedIndex != -1
                // && changedIndex < CartManager.getInstance()
                // .getCurCartItems().size())
                // {
                // mAdapter.notifyDataChanged(changedIndex, CartManager
                // .getInstance().getCurCartItems().get(changedIndex));
                // }
            }
            else if (action != null
                    && action.equals(Const.ACTION_GET_ALL_PAYMENT_CONFIG))
            {
                mPaymentConfigs = CartManager.getInstance().getPaymentConfigs();
            }
            else if (action != null
                    && action.equals(Const.ACTION_SAVE_ORDER_SUCCESS))
            {
                mPaymentConfigs = CartManager.getInstance().getPaymentConfigs();
                if (getParent() instanceof HomeActivity)
                {
                    ((HomeActivity) getParent()).setCurrentTab(2);
                    intent.setClass(ShoppingTrolleyActivity.this,
                            OrderListActivity.class);
                    intent.putExtra(Const.EXTRA_START_ORDER_LIST_KEY,
                            Const.EXTRA_START_ORDER_LIST_VALUE_UNPAY);
                    ((HomeActivity) getParent())
                            .startActivityPushHistoryWithGuideBar(
                                    OrderListActivity.class, intent,
                                    new Class[] { AccountActivity.class,
                                            OrderStateActivity.class });
                }
            }
            else if (action != null
                    && action.equals(Const.ACTION_LOGOUT_SUCCESS))
            {
                mNeedReload = true;
                mCartItems = null;
                showShoppingTrolleryBasicInfos();
                mAdapter.notifyDataChanged(mCartItems);
                showViewByCount();
                if (getParent() instanceof HomeActivity)
                {
                    ((HomeActivity) getParent())
                            .refreshShoppingTrolleryCount(mAdapter
                                    .getCartItemTotalCount());
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Logger.d(TAG, "onCreate()[access]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_trolley);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle(R.string.trollery);
        mTitleBar.setBackBtnVisibility(View.GONE);
        mlistView = (ListView) findViewById(R.id.shopping_trolley_listView);
        mShoppingTrolleryBasicInfo = findViewById(R.id.shopping_trollery_basic_info);
        mEmptyCartView = findViewById(R.id.empty_shopping_trollery);
        mCartBodyContianer = findViewById(R.id.cart_body_contianer);
        mAdapter = new ShoppingTrolleryListAdapter(this, null);

        mlistView.setAdapter(mAdapter);

        setListViewHeightBasedOnChildren(mlistView);

        findViewById(R.id.prompt_payment).setOnClickListener(this);
        findViewById(R.id.change_delivery_address).setOnClickListener(this);
        findViewById(R.id.trolley_help).setOnClickListener(this);
        findViewById(R.id.call_the_yly).setOnClickListener(this);
        AccountManager.getInstance().getAllDeliverAddress();
        mReceivers = AccountManager.getInstance().getCurrentAccount()
                .getReceivers();
        regReceiver();
        showViewByCount();
        CartManager.getInstance().getAllCartItems();
        if (CartManager.getInstance().getPaymentConfigs() == null
                || CartManager.getInstance().getPaymentConfigs().isEmpty())
        {
            CartManager.getInstance().loadPaymentConfig();
        }
        else
        {
            mPaymentConfigs = CartManager.getInstance().getPaymentConfigs();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mNeedReload)
        {
            CartManager.getInstance().getAllCartItems();
            mNeedReload = false;
        }
    }

    private void setListViewHeightBasedOnChildren(ListView listView)
    {
        if (listView == null)
        {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        if (listAdapter.getCount() > 0)
        {
            totalHeight += (totalHeight / listAdapter.getCount()) / 2;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1))
                + 30;
        listView.setLayoutParams(params);
    }

    private void showViewByCount()
    {
        if (mlistView.getAdapter().getCount() == 0)
        {
            mCartBodyContianer.setVisibility(View.GONE);
            mEmptyCartView.setVisibility(View.VISIBLE);
        }
        else
        {
            mCartBodyContianer.setVisibility(View.VISIBLE);
            mEmptyCartView.setVisibility(View.GONE);
        }
    }

    private synchronized void showShoppingTrolleryBasicInfos()
    {
        mShoppingTrolleryBasicInfo.setVisibility(View.VISIBLE);
        if (AccountManager.getInstance().hasLogined() && mCartItems != null)
        {
            AccountManager.getInstance().getAllDeliverAddress();
            mReceivers = AccountManager.getInstance().getCurrentAccount()
                    .getReceivers();
            defaultReceiver = AccountManager.getInstance().getCurrentAccount()
                    .getDefaultReceiver();
            if (selectReceiver != null)
            {
                ((TextView) findViewById(R.id.goods_receiver_name))
                        .setText(getResources().getString(
                                R.string.goods_receiver,
                                selectReceiver.getName()));
                ((TextView) findViewById(R.id.goods_delivery_address_main))
                        .setText(selectReceiver.getAreaStore().getDisplayName());
                ((TextView) findViewById(R.id.goods_delivery_address_detial))
                        .setText(selectReceiver.getAddress());
                ((TextView) findViewById(R.id.telphone_number))
                        .setText(getResources().getString(
                                R.string.telphone_number,
                                selectReceiver.getMobile()));
                ((TextView) findViewById(R.id.zip_code)).setText(getResources()
                        .getString(R.string.zip_code,
                                selectReceiver.getZipCode()));
            }
            else if (defaultReceiver != null)
            {
                ((TextView) findViewById(R.id.goods_receiver_name))
                        .setText(getResources().getString(
                                R.string.goods_receiver,
                                defaultReceiver.getName()));
                ((TextView) findViewById(R.id.goods_delivery_address_main))
                        .setText(defaultReceiver.getAreaStore()
                                .getDisplayName());
                ((TextView) findViewById(R.id.goods_delivery_address_detial))
                        .setText(defaultReceiver.getAddress());
                ((TextView) findViewById(R.id.telphone_number))
                        .setText(getResources().getString(
                                R.string.telphone_number,
                                defaultReceiver.getMobile()));
                ((TextView) findViewById(R.id.zip_code)).setText(getResources()
                        .getString(R.string.zip_code,
                                defaultReceiver.getZipCode()));
            }
            else
            {
                ((TextView) findViewById(R.id.goods_receiver_name))
                        .setText(R.string.goods_receiver_null);
                ((TextView) findViewById(R.id.goods_delivery_address_main))
                        .setText("");
                ((TextView) findViewById(R.id.goods_delivery_address_detial))
                        .setText("");
                ((TextView) findViewById(R.id.telphone_number))
                        .setText(R.string.telphone_number_null);
                ((TextView) findViewById(R.id.zip_code))
                        .setText(R.string.zip_code_null);
            }

            double totalPrice = 0;
            double expressCost = 10;
            for (int i = 0; i < mCartItems.size(); i++)
            {
                CartItem item = mCartItems.get(i);
                if (item != null && item.getProduct() != null)
                {
                    totalPrice += item.getProduct().getPrice();
                }
            }
            ((TextView) findViewById(R.id.goods_fee)).setText(getResources()
                    .getString(R.string.goods_fee, totalPrice));
            ((TextView) findViewById(R.id.express_fee)).setText(getResources()
                    .getString(R.string.express_fee, expressCost));
            ((TextView) findViewById(R.id.actual_pay)).setText(getResources()
                    .getString(R.string.pay_yuan, totalPrice + expressCost));
        }
    }

    @Override
    protected void onDestroy()
    {
        Logger.d(TAG, "onDestroy()[access]");
        super.onDestroy();
        unregReceiver();
        mAdapter.clearCache();
        Log.d("zhh-->", "ondesdroy-->");
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Logger.d(TAG, "onNewIntent()[access]");
        super.onNewIntent(intent);
        CartManager.getInstance().getAllCartItems();
        AccountManager.getInstance().getAllDeliverAddress();
        mReceivers = AccountManager.getInstance().getCurrentAccount()
                .getReceivers();
//        from = intent.getStringExtra("From");
//        Log.d("zhh-->", "from-->"+from);
        for (int i = 0; i < mReceivers.size(); i++)
        {
            if (mReceivers.get(i).isDefault())
            {
                defaultReceiverPos = i;
            }
            else
            {
                defaultReceiverPos = DELIVER_POS;
            }
        }
        changeReceiverPos = intent.getIntExtra(
                Const.EXTRA_SELECTED_DELIVER_ADDRESS_POS, defaultReceiverPos);
        if (changeReceiverPos != DELIVER_POS && mReceivers.size() != 0)
        {
            selectReceiver = mReceivers.get(changeReceiverPos);
        }
        else
        {
            selectReceiver = null;
        }
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.prompt_payment:

                showChoosePaymentDialog();
                break;
            case R.id.change_delivery_address:

                if (mReceivers.size() != 0)
                {
                    if (getParent() instanceof HomeActivity)
                    {
                        Intent intent = new Intent(this,
                                DeliverAddressActivity.class);
                        intent.putExtra(Const.EXTRA_CHANGE_DELIVER_ADDRESS,
                                true);
                        ((HomeActivity) getParent())
                                .setCurrentTab(HomeActivity.CUR_TAB_ID_HOME_ACCOUNT);
                        ((HomeActivity) getParent()).startActivityWithGuideBar(
                                DeliverAddressActivity.class, intent);
                        mShoppingTrolleryBasicInfo.destroyDrawingCache();
                    }
                    else
                    {
                        Logger.e(TAG, "startNextActivity()[failed]");
                    }
                }
                else
                {
                    Toast.makeText(ShoppingTrolleyActivity.this,
                            R.string.null_deliver_address, Toast.LENGTH_LONG)
                            .show();
                }

                break;
            case R.id.trolley_help:
                if (getParent() instanceof HomeActivity)
                {
                    ((HomeActivity) getParent()).setCurrentTab(2);
                    ((HomeActivity) getParent())
                            .startActivityPushHistoryWithGuideBar(
                                    HelpActivity.class, null,
                                    new Class[] { HelpActivity.class,
                                            OrderStateActivity.class });
                }
                break;
            case R.id.call_the_yly:
                AlertDialog.Builder builder = new Builder(ShoppingTrolleyActivity.this);
                builder.setMessage(R.string.phone_message);
                builder.setTitle(R.string.call_the_yly);
                builder.setPositiveButton(R.string.phone_sure_bottom,new  DialogInterface.OnClickListener(){
                   @Override
                   public void onClick(DialogInterface dialog, int which)
                   {
                       // TODO Auto-generated method stub
                       Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+getString(R.string.phone_message)));
                       startActivity(intent);
                   }   
                });
                builder.setNegativeButton(R.string.phone_cancel_bottom,new DialogInterface.OnClickListener(){
                   @Override
                   public void onClick(DialogInterface dialog, int which)
                   {
                       // TODO Auto-generated method stub
                       dialog.dismiss();                        
                   }
                });
                builder.create().show();
                break;
            default:
                break;
        }

    }

    private synchronized void showChoosePaymentDialog()
    {
        if (mPaymentConfigs == null || mPaymentConfigs.isEmpty())
        {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.please_choose_payment_way);
        CharSequence items[] = new CharSequence[mPaymentConfigs.size()];
        for (int i = 0; i < mPaymentConfigs.size(); i++)
        {
            items[i] = mPaymentConfigs.get(i).getName();
        }
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String paymentConfigId = mPaymentConfigs.get(which).getId();
                if (selectReceiver != null)
                {
                    CartManager.getInstance().saveOrder(selectReceiver,
                            paymentConfigId, "");
                }
                else if (defaultReceiver != null)
                {
                    CartManager.getInstance().saveOrder(
                            AccountManager.getInstance().getCurrentAccount()
                                    .getDefaultReceiver(), paymentConfigId, "");
                    Logger.d(TAG, "showChoosePaymentDialog.onClick()[which:"
                            + which + "]");
                }
                else
                {
                    Toast.makeText(ShoppingTrolleyActivity.this,
                            R.string.empty_trolley_receiver, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        builder.create().show();

    }

    private void regReceiver()
    {
        if (mStateReceiver == null)
        {
            mStateReceiver = new StateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_LIST_ALL_CART_ITEM_FAILED);
            filter.addAction(Const.ACTION_LIST_ALL_CART_ITEM_SUCCSEE);
            filter.addAction(Const.ACTION_REFRESH_CART_ITEM);
            filter.addAction(Const.ACTION_GET_ALL_PAYMENT_CONFIG);
            filter.addAction(Const.ACTION_SAVE_ORDER_SUCCESS);
            filter.addAction(Const.ACTION_SAVE_ORDER_FAILED);
            filter.addAction(Const.ACTION_LOGOUT_SUCCESS);
            registerReceiver(mStateReceiver, filter);
        }
    }

    private void unregReceiver()
    {
        if (mStateReceiver != null)
        {
            unregisterReceiver(mStateReceiver);
            mStateReceiver = null;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d("zhh-->", "onpause");
    }

}
