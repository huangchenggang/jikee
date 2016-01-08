package com.extensivepro.mxl.app.login;

import java.io.InputStream;
import java.util.List;

import android.content.Intent;

import com.extensivepro.mxl.app.EventAnnotation;
import com.extensivepro.mxl.app.EventDispacher;
import com.extensivepro.mxl.app.bean.Area;
import com.extensivepro.mxl.app.bean.DepositCard;
import com.extensivepro.mxl.app.bean.Member;
import com.extensivepro.mxl.app.bean.PaymentConfig.PaymentConfigType;
import com.extensivepro.mxl.app.bean.PaymentMessage;
import com.extensivepro.mxl.app.bean.Receiver;
import com.extensivepro.mxl.app.bean.StatusMessage.Status;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.app.client.ClientThread;
import com.extensivepro.mxl.app.client.Command;
import com.extensivepro.mxl.product.ProductManager;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.DataUtil;
import com.extensivepro.mxl.util.JsonUtil;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.util.SharedPreferenceUtil;
/**
 * 
 * @Description 
 * @author damon
 * @date Apr 20, 2013 10:34:24 AM 
 * @version V1.3.1
 */
public class AccountManager implements IAccountCallback
{
    private static final String TAG = AccountManager.class.getSimpleName();
    private static AccountManager mInstance;
    
    private boolean mHasLogined;
    
    private static final int MOUDEL_CODE = EventDispacher.MoudelCode.MOUDEL_CODE_LOGIN;
    
    private Member mCurAccount;
    
    private List<DepositCard> mCurDepositCards;
    
    private boolean mRememberCurAccount;
    
    private AccountManager()
    {
        EventDispacher.regCallback(MOUDEL_CODE, this);
    }
    
    public static AccountManager getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new AccountManager();
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
                case EventDispacher.EventCodeBusiness.EVENT_CODE_LOGIN:
                    onLoginFailed(reasonCode);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_REGISTER:
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_ADD_RECEIVER_ADDR:
                    Intent intent = new Intent(Const.ACTION_ADD_DELIVER_ADDR_FAILED);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_LOGOUT:
                    intent = new Intent(Const.ACTION_LOGOUT_FAILED);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
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
                case EventDispacher.EventCodeBusiness.EVENT_CODE_LOGIN:
                    onLoginSuccess(reasonCode,resp);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_REGISTER:
                    onRegisterSuccess(reasonCode,resp);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_GET_ALL_AREA:
                    onGetAllAreaSuccess(reasonCode, resp);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_ADD_RECEIVER_ADDR:
                    intent = new Intent(Const.ACTION_ADD_DELIVER_ADDR_SUCCESS);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_GET_ALL_RECEIVER_ADDR:
                    List<Receiver> receivers = (List<Receiver>) JsonUtil.jsonToList(resp, Receiver.class, Const.JSON_LIST_NAME);
                    if(mCurAccount != null)
                    {
                        mCurAccount.setReceivers(receivers);
                        
//                        if(receivers != null && !receivers.isEmpty()&& mCurAccount.getDefaultReceiver() == null)
//                        {
//                            mCurAccount.setDefaultReceiver(receivers.get(0));
//                        }
                    }
                    intent = new Intent(Const.ACTION_GET_ALL_DELIVER_ADDR_SUCCESS);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_DEPOSIT_CARD:
                    mCurDepositCards = (List<DepositCard>) JsonUtil.jsonToList(resp, DepositCard.class, Const.JSON_LIST_NAME);
                    intent = new Intent(Const.ACTION_LIST_DEPOSIT_CARD_SUCCESS);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_EDIT_RECEIVER_ADDR:
                    intent = new Intent(Const.ACTION_EDIT_DELIVER_ADDR_SUCCESS);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_SET_DEFAULT_RECEIVER_ADDR:
                    intent = new Intent(Const.ACTION_DEL_DELIVER_ADDR_SUCCESS);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_DEL_RECEIVER_ADDR:
                    intent = new Intent(Const.ACTION_DEL_DELIVER_ADDR_SUCCESS);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_LOGOUT:
                    mCurAccount = null;
                    mHasLogined = false;
                    intent = new Intent(Const.ACTION_LOGOUT_SUCCESS);
                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_PAY_DEPOSIT_CARD:
                    PaymentMessage paymentMessage = (PaymentMessage) JsonUtil
                            .jsonToObject(resp, PaymentMessage.class, null);
                    if (paymentMessage != null
                            && paymentMessage.getStatus() == Status.success
                            && paymentMessage.getPaymentType() == PaymentConfigType.mobile)
                    {
                        // TODO alix pay here
                        Logger.d(TAG, "onResp()[paymentMsg:" + paymentMessage
                                + "]");
                        intent = new Intent(
                                Const.ACTION_START_ALIX_PAY_DEPOSIT_CARD);
                        intent.putExtra(Const.EXTRA_OBJ_PAYMENT_PARAM,
                                paymentMessage.getPaymentParams());
                        ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    }
                    break;
//                case EventDispacher.EventCodeBusiness.EVENT_CODE_GET_NOTIFY_MESSAGE:
//                    intent = new Intent(Const.ACTION_LOGOUT_SUCCESS);
//                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
//                    break;
            }
        }
    }

    @Override
    public void onLoginSuccess(int reasonCode, String respStr)
    {
        Logger.d(TAG, "login()[reasonCode:" + reasonCode+"]");
        mHasLogined = true;
        Object obj = JsonUtil.jsonToObject(respStr, Member.class, Const.JSON_OBJ_NAME_MEMBER);
        if(obj instanceof Member)
        {
            Member member = (Member) obj;
            mCurAccount.update(member);
        }
        if(!mRememberCurAccount)
        {
            SharedPreferenceUtil.setLastestUserName(mCurAccount.getUsername());
        }
        ProductManager.getInstance().checkFreeSale();
        Intent intent = new Intent(Const.ACTION_LOGIN_SUCCESS);
        ClientManager.getInstance().getAppContext().sendBroadcast(intent);
    }

    @Override
    public void onLoginFailed(int reasonCode)
    {
        Logger.d(TAG, "onLoginFailed()[reasonCode:" + reasonCode+"]");
        mHasLogined = false;
        Intent intent = new Intent(Const.ACTION_LOGIN_FAILED);
        ClientManager.getInstance().getAppContext().sendBroadcast(intent);
    }
    
    @EventAnnotation(eventCode = EventDispacher.EventCodeBusiness.EVENT_CODE_LOGIN)
    public void login(String username,String passwd)
    {
//        username = "qukers@126.com";
//        passwd = "123456";
        Logger.d(TAG, "login()[username:" + username + ",passwd" + passwd + "]");
        mCurAccount = new Member();
        mCurAccount.setUsername(username);
        mCurAccount.setPassword(passwd);
        Command command = new Command();
        command.addAttribute(Const.AccountModuel.PARAM_USERNAME, username);
        command.addAttribute(Const.AccountModuel.PARAM_PASSWD, passwd);
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_LOGIN, MOUDEL_CODE,
                command);
        command.commit(thread);
    }
    
    public void getNotifyMsgs()
    {
        Logger.d(TAG, "getNotifyMsgs()");
        Command command = new Command();
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_GET_NOTIFY_MESSAGE,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    public void register(Member member)
    {
        if(member!=null)
        {
            Logger.d(TAG, "register()[:"+member+"]");
            mCurAccount = member;
            Command command = new Command();
            command.addAttribute(Const.AccountModuel.PARAM_USERNAME, member.getUsername());
            command.addAttribute(Const.AccountModuel.PARAM_EMAIL, member.getUsername());
            command.addAttribute(Const.AccountModuel.PARAM_PASSWD, member.getPassword());
            command.addAttribute(Const.AccountModuel.PARAM_NAME, member.getName());
            ClientThread thread = new ClientThread(
                    EventDispacher.EventCodeBusiness.EVENT_CODE_REGISTER, MOUDEL_CODE,
                    command);
            command.commit(thread);
        }
        else 
        {
            Logger.e(TAG, "register()[memeber is null]");
        }
    }
    
    public boolean hasLogined()
    {
        return mHasLogined;
    }

    @Override
    public void onRegisterSuccess(int reasonCode, String respStr)
    {
        if(mCurAccount != null)
        {
            login(mCurAccount.getUsername(), mCurAccount.getPassword());
        }
    }

    @Override
    public void onRegisterFailed(int reasonCode)
    {
        // TODO Auto-generated method stub
        
    }
    
    public Member getCurrentAccount()
    {
        return mCurAccount;
    }
    
    public void addDeliverAddress(Receiver receiver)
    {
        if(receiver == null)
        {
            Logger.e(TAG, "register()[receiver is null]");
            return;
        }
        Logger.d(TAG, "addDeliverAddress()[receiver:"+receiver+"]");
        Command command = new Command();
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_AREAID,
                receiver.getAreaStore().getId());
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_NAME,
                receiver.getName());
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_ADDRESS,
                receiver.getAddress());
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_ZIPCODE,
                receiver.getZipCode());
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_MOBILE,
                receiver.getMobile());
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_ADD_RECEIVER_ADDR, MOUDEL_CODE,
                command);
        command.commit(thread);
    }
    
    public void delDeliverAddress(Receiver receiver){
        Logger.d(TAG, "delDeliverAddress()[receiver:"+receiver+"]");
        Command command = new Command();
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_ID, 
                receiver.getId());
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_DEL_RECEIVER_ADDR, MOUDEL_CODE,
                command);
        command.commit(thread);
    }
    
    public void setDefaultDeliverAddress(Receiver receiver){
        Logger.d(TAG, "setDefaultDeliverAddress()[receiver:"+receiver+"]");
        Command command = new Command();
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_ID, 
                receiver.getId());
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_SET_DEFAULT_RECEIVER_ADDR, MOUDEL_CODE,
                command);
        command.commit(thread);
    }
    
    public void editDeliverAddress(Receiver receiver){
        Command command = new Command();
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_ID,
                receiver.getId());
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_AREAID,
                receiver.getAreaStore().getId());
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_NAME,
                receiver.getName());
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_ADDRESS,
                receiver.getAddress());
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_ZIPCODE,
                receiver.getZipCode());
        command.addAttribute(Const.AccountModuel.PARAM_RECEIVER_MOBILE,
                receiver.getMobile());
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_EDIT_RECEIVER_ADDR,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    public void getAllArea()
    {
        Logger.d(TAG, "getAllArea()[access]");
        Command command = new Command();
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_GET_ALL_AREA, MOUDEL_CODE,
                command);
        command.commit(thread);
    }
    
    private void onGetAllAreaSuccess(int reasonCode, String respStr)
    {
        Logger.d(TAG, "onGetAllAreaSuccess()[reasonCode:"+reasonCode+"]");
        @SuppressWarnings("unchecked")
        List<Area> areas = (List<Area>) JsonUtil.jsonToList(respStr, Area.class, Const.JSON_LIST_NAME);
        if(areas != null && areas.size() >0 && areas.get(0) != null)
        {
            Intent intent = new Intent(Const.ACTION_GEA_ALL_AREA_SUCCESS);
            intent.putExtra(Const.EXTRA_AREA_OBJ, areas.get(0));
            ClientManager.getInstance().getAppContext().sendBroadcast(intent);
        }
    }
    
    public void getAllDeliverAddress()
    {
        Logger.d(TAG, "getAllDeliverAddress()[access]");
        Command command = new Command();
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_GET_ALL_RECEIVER_ADDR, MOUDEL_CODE,
                command);
        command.commit(thread);
    }
    
    public void loadAllDepositCard()
    {
        Logger.d(TAG, "listAllDepositCard()[access]");
        Command command = new Command();
        command.setDoGet(true);
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_LIST_DEPOSIT_CARD,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    public List<DepositCard> getCurDepositCards()
    {
        return mCurDepositCards;
    }

    public void payDepositCard(DepositCard depositCard)
    {
        Logger.d(TAG, "payDepositCard()[access]");
        if(depositCard == null)
        {
            Logger.e(TAG, "payDepositCard()[depositCard is null]");
            return;
        }
        Command command = new Command();
        command.addAttribute(Const.AccountModuel.PARAM_DEPOSIT_CARD_ID, depositCard.getId());
        command.addAttribute(Const.AccountModuel.PARAM_DEPOSIT_CARD_COUNT, String.valueOf(1));
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_PAY_DEPOSIT_CARD,
                MOUDEL_CODE, command);
        command.commit(thread);
    }
    
    public void clearCache()
    {
        mCurAccount = null;
        mCurDepositCards = null;
        mHasLogined = false;
    }
    
    public void logout()
    {
        Logger.d(TAG, "logout()[access]");
        Command command = new Command();
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_LOGOUT,
                MOUDEL_CODE, command);
        command.commit(thread);
    }

    public boolean isRememberCurAccount()
    {
        return mRememberCurAccount;
    }

    public void setRememberCurAccount(boolean rememberCurAccount)
    {
        this.mRememberCurAccount = rememberCurAccount;
    }
    
    
    
}
