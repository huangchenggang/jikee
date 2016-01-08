package com.extensivepro.mxl.ui;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.app.bean.Area;
import com.extensivepro.mxl.app.bean.Receiver;
import com.extensivepro.mxl.app.login.AccountManager;
import com.extensivepro.mxl.util.CommonUtil;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.widget.TitleBar;
import com.kankan.wheel.widget.AreaWheelAdapter;
import com.kankan.wheel.widget.OnWheelScrollListener;
import com.kankan.wheel.widget.WheelView;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 16, 2013 11:18:15 AM
 * @version V1.3.1
 */
public class AddNewDeliverAddressActivity extends BaseActivity implements OnClickListener,OnWheelScrollListener,OnFocusChangeListener
{
    private static final String TAG = AddNewDeliverAddressActivity.class.getSimpleName();

    
    private TitleBar mTitleBar;
    
    private ViewGroup mAddressGroup;
    
    private AreaWheelAdapter mProvinceWheelAdapter;
    private AreaWheelAdapter mCityWheelAdapter;
    private AreaWheelAdapter mAreaWheelAdapter;
    private WheelView mProvinceWheel;
    private WheelView mCityWheel;
    private WheelView mAreaWheel;
    
    private StateReceiver mStateReceiver;
    
    private List<Area> mProvinces;
    private List<Area> mCities;
    private List<Area> mAreas;
    
    private EditText mDeliverName;
    private EditText mPhoneNumber;
    private EditText mPostCode;
    private TextView mLocationArea;
    private EditText mDetialAddress;
    
    private Area mSelectArea;
    
    private View mAreaPicker;
    
    private TaskState mTaskState = TaskState.IDEL;
    
    private static final int ANIM_POST_DELAY_MILLS = 0;
    
    private class StateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Logger.d(TAG, "StateReceiver.onReceive()[action:" + action + "]");
            if (action != null
                    && action.equals(Const.ACTION_GEA_ALL_AREA_SUCCESS))
            {
                Object obj = intent.getSerializableExtra(Const.EXTRA_AREA_OBJ);
                if (obj instanceof Area)
                {
                    Area country = (Area) obj;
                    if (country.getChildren() != null)
                    {
                        mProvinces = country.getChildren();
                        mProvinceWheel = initWheel(R.id.province, mProvinces);
                        mCities = mProvinces.get(0).getChildren();
                        if (mCities != null)
                        {
                            mCityWheel = initWheel(R.id.city, mCities);
                            mAreas = mCities.get(0).getChildren();
                            if (mAreas != null)
                            {
                                mAreaWheel = initWheel(R.id.area, mAreas);
                            }
                        }
                    }
                }
                return;
            }
            if (action != null
                    && action.equals(Const.ACTION_ADD_DELIVER_ADDR_SUCCESS))
            {
                mTitleBar.onBackBtnClick();
                return;
            }
            if (action != null
                    && action.equals(Const.ACTION_ADD_DELIVER_ADDR_FAILED))
            {
                Toast.makeText(context, R.string.add_failed_toast_txt, Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        regReceiver();
        Logger.d(TAG, "onCreate()[access]");
        setContentView(R.layout.add_new_deliver_address);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mAddressGroup = (ViewGroup) findViewById(R.id.delivery_address_group);
        mTitleBar.setTitle(R.string.delivery_address_txt);
        mTitleBar.setBackBtnText(R.string.delivery_address_txt);
        mTitleBar.setEditBtnText(R.string.add_deliver_address_txt);
        mTitleBar.setEditBtnVisibility(View.VISIBLE);
        mTitleBar.findViewById(R.id.edit_btn).setOnClickListener(this);
        
        mDeliverName = (EditText) findViewById(R.id.deviler_name_edit);
        mPhoneNumber = (EditText) findViewById(R.id.phone_number_edit);
        mPostCode = (EditText) findViewById(R.id.post_code_edit);
        mLocationArea = (TextView) findViewById(R.id.location_area_text);
        mDetialAddress = (EditText) findViewById(R.id.detial_address_edit);
        mLocationArea.setOnClickListener(this);
        mDeliverName.setOnFocusChangeListener(this);
        mPhoneNumber.setOnFocusChangeListener(this);
        mPostCode.setOnFocusChangeListener(this);
        mDetialAddress.setOnFocusChangeListener(this);
        mAreaPicker = findViewById(R.id.area_picker);
        AccountManager.getInstance().getAllArea();
        checkNum();
        
    }

    @Override
    protected void onResume()
    {
        HomeActivity.keyBackClickCount=3;
        super.onResume();
    }
    
    @Override
    protected void onDestroy()
    {
        Logger.d(TAG, "onDestroy()[access]");
        HomeActivity.keyBackClickCount=0;
        super.onDestroy();
        unregReceiver();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Logger.d(TAG, "onNewIntent()[access]");
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View v)
    {
        Logger.d(TAG, "onClick()[access]");
        switch (v.getId())
        {
            case R.id.edit_btn:
                Logger.d(TAG, "onClick()[edit_btn]");
                if(mAddressGroup.getVisibility() == View.GONE)
                {
                    mAddressGroup.setVisibility(View.VISIBLE);
                    mTitleBar.setBackBtnText(R.string.delivery_address_txt);
                }
                
                if(TextUtils.isEmpty(mDeliverName.getText()))
                {
                    Toast.makeText(this, R.string.deliver_name_none_toast_txt, Toast.LENGTH_LONG).show();
                    return;
                }
                if(mPhoneNumber.getText().length() < Const.PHONE_NUM_CONST){
                    Toast.makeText(getApplicationContext(), R.string.phone_number_error_toast_txt, Toast.LENGTH_SHORT).show();
                    return;      
                } 
                if(!isPhoneNumberValid( mPhoneNumber.getText().toString())){
                    Toast.makeText(getApplicationContext(), R.string.phone_number_form_error_toast_txt, Toast.LENGTH_SHORT).show();
                    return; 
                }
                if(mPostCode.getText().length() < Const.POST_CODE_CONST){
                    Toast.makeText(getApplicationContext(), R.string.post_code_error_toast_txt, Toast.LENGTH_SHORT).show();     
                    return;  
                } 
                if(! checkPostcode( mPostCode.getText().toString())){
                    Toast.makeText(getApplicationContext(), R.string.post_code_form_error_toast_txt, Toast.LENGTH_SHORT).show();
                    return; 
                }
//                if(TextUtils.isEmpty(mPhoneNumber.getText()))
//                {
//                    Toast.makeText(this, R.string.phone_number_none_toast_txt, Toast.LENGTH_LONG).show();
//                    return;
//                }

//                if(TextUtils.isEmpty(mPostCode.getText()))
//                {
//                    Toast.makeText(this, R.string.post_code_none_toast_txt, Toast.LENGTH_LONG).show();
//                    return;
//                }
               
                if(TextUtils.isEmpty(mLocationArea.getText()))
                {
                    Toast.makeText(this, R.string.location_area_none_toast_txt, Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(mDetialAddress.getText()))
                {
                    Toast.makeText(this, R.string.detial_address_none_toast_txt, Toast.LENGTH_LONG).show();
                    return;
                }
                Receiver receiver = new Receiver(mDeliverName.getText()
                        .toString(), mDetialAddress.getText().toString(),
                        mPostCode.getText().toString(), mPhoneNumber.getText()
                                .toString(), mSelectArea, false);
                AccountManager.getInstance().addDeliverAddress(receiver);
//                Receiver receiver = new Receiver(
//                        mSelectArea, mDeliverName
//                        .getText().toString(), mDetialAddress.getText()
//                        .toString(), mPostCode.getText().toString(),
//                        mPhoneNumber.getText().toString(),false);
//                AccountManager.getInstance().addDeliverAddress(receiver);
                break;
            case R.id.location_area_text:
                Logger.d(TAG, "onClick()[detial_address_txt]");
                if(CommonUtil.isInputMethodShow(this))
                {
                    CommonUtil.hideInputMethod(this, mDeliverName);
                }
                showAreaPicker(View.VISIBLE);
                break;
            default:
                break;
        }
    }
    
    private WheelView initWheel(int id,List<Area> areaList) {
        WheelView wheel = (WheelView) findViewById(id);
        switch(id)
        {
            case R.id.province:
                mProvinceWheelAdapter = new AreaWheelAdapter(areaList);
                wheel.setAdapter(mProvinceWheelAdapter);
                break;
            case R.id.city:
                mCityWheelAdapter = new AreaWheelAdapter(areaList);
                wheel.setAdapter(mCityWheelAdapter);
                break;
            case R.id.area:
                mAreaWheelAdapter = new AreaWheelAdapter(areaList);
                wheel.setAdapter(mAreaWheelAdapter);
                break;
        }
        wheel.addScrollingListener(this);
        wheel.setCurrentItem(0);
//        wheel.setCyclic(true);
        wheel.setInterpolator(new AnticipateOvershootInterpolator());
        return wheel;
    }
    
    private void regReceiver()
    {
        if(mStateReceiver == null)
        {
            mStateReceiver = new StateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ACTION_GEA_ALL_AREA_SUCCESS);
            filter.addAction(Const.ACTION_ADD_DELIVER_ADDR_FAILED);
            filter.addAction(Const.ACTION_ADD_DELIVER_ADDR_SUCCESS);
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
    public void onScrollingStarted(WheelView wheel)
    {
        Logger.d(TAG, "onScrollingStarted()[access]");
    }

    @Override
    public void onScrollingFinished(WheelView wheel)
    {
        Logger.d(TAG, "onScrollingFinished()[access]");
        int pos = wheel.getCurrentItem();
        switch(wheel.getId())
        {
            case R.id.province:
                if(pos < mProvinces.size())
                {
                    mCities = mProvinces.get(pos).getChildren();
                    mCityWheel.setAdapter(new AreaWheelAdapter(mCities));
                    mCityWheel.setCurrentItem(0,true);
                    int cityPos = mCityWheel.getCurrentItem();
                    if(cityPos < mCities.size())
                    {
                        mAreas = mCities.get(cityPos).getChildren();
                        mAreaWheel.setAdapter(new AreaWheelAdapter(mAreas));
                        mAreaWheel.setCurrentItem(0,true);
                    }
                }
                if (mAreas != null && !mAreas.isEmpty())
                {
                    mSelectArea = mAreas.get(0);
                }
                else if ((mAreas == null || mAreas.isEmpty())
                        && (mCities != null && !mCities.isEmpty()))
                {
                    mSelectArea = mCities.get(0);
                }
                break;
            case R.id.city:
                if(pos < mCities.size())
                {
                    mAreas = mCities.get(pos).getChildren();
                    mAreaWheel.setAdapter(new AreaWheelAdapter(mAreas));
                    mAreaWheel.setCurrentItem(0,true);
                }
                if (mAreas != null && !mAreas.isEmpty())
                {
                    mSelectArea = mAreas.get(0);
                }
                else if ((mAreas == null || mAreas.isEmpty())
                        && (mCities != null && !mCities.isEmpty()))
                {
                    mSelectArea = mCities.get(mCityWheel.getCurrentItem());
                }
                break;
            case R.id.area:
                if (mAreas != null && !mAreas.isEmpty())
                {
                    mSelectArea = mAreas.get(mAreaWheel.getCurrentItem());
                }
                break;
        }
        if(mSelectArea != null)
        {
            mLocationArea.setText(mSelectArea.getDisplayName());
        }
    }
    private void checkNum(){
        mPostCode.addTextChangedListener(new TextWatcher(){

            @Override
            public void afterTextChanged(Editable s)
            {   
      
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {              
//                try{
//                Integer.parseInt(mPostCode.getText().toString());
//                }catch(Exception e){
//                  
//                    Toast.makeText(getApplicationContext(), R.string.post_code_error_toast_txt, Toast.LENGTH_SHORT).show();
//                }
//                
                
                if(mPostCode.getText().toString().length() >= Const.POST_CODE_CONST){
                    Toast.makeText(getApplicationContext(), R.string.post_code_error_toast_txt, Toast.LENGTH_SHORT).show();                
                }     
  
            }
            
        });
        
        mPhoneNumber.addTextChangedListener(new TextWatcher(){          

            @Override
            public void afterTextChanged(Editable s){
                                   
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {
                // TODO Auto-generated method stub               
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {            
//                try{
//                Integer.parseInt(mPhoneNumber.getText().toString());
//                }catch(Exception e){
//                    Toast.makeText(getApplicationContext(), R.string.phone_number_error_toast_txt, Toast.LENGTH_SHORT).show();
//                }
                
                if(mPhoneNumber.getText().toString().length() >= Const.PHONE_NUM_CONST){
                    Toast.makeText(getApplicationContext(), R.string.phone_number_error_toast_txt, Toast.LENGTH_SHORT).show();                   
                }                  
            }           
        });    
    }
    
    public  boolean isPhoneNumberValid(String phoneNumber)  
    {  
       boolean isValid = false;  
      
       String expression = "^(1(([35][0-9])|(47)|[8][0126789]))\\d{8}$";      
      
       CharSequence inputStr = phoneNumber;  
      
       Pattern pattern = Pattern.compile(expression);  
      
       Matcher matcher = pattern.matcher(inputStr);  
      
    
       if(matcher.matches())  
       {  
       isValid = true;  
       }  
       return isValid;   
     }  
    
    public boolean checkPostcode(String postCode){
        Pattern p=Pattern.compile("[1-9]d{5}(?!d)");
        Matcher m=p.matcher(postCode);
        if (!m.matches()){       
         return false;
        }
        return true;
       }
     
    private void showAreaPicker(int visibility)
    {
        if(mAreaPicker.getVisibility() == visibility)
        {
            return;
        }
        sendMsg(visibility, ANIM_POST_DELAY_MILLS);
    }
    
    private synchronized void sendMsg(int msgWhat, long delayMills) {
        if (mTaskState == TaskState.BUSY) {
            return;
        }
        mTaskState = TaskState.BUSY;
        mHandler.sendEmptyMessageDelayed(msgWhat, delayMills);
    }

    enum TaskState {
        BUSY, IDEL
    }

    private Handler mHandler = new Handler() {
        public synchronized void handleMessage(final android.os.Message msg)
        {
            synchronized (mTitleBar)
            {
                final int visibility = msg.what;
                int animResId = R.anim.push_up_out;
                if (visibility == View.VISIBLE)
                {
                    animResId = R.anim.push_down_in;
                }
                else if (visibility == View.GONE)
                {
                    animResId = R.anim.push_down_out;
                }
                mAreaPicker.clearAnimation();
                Animation animation = AnimationUtils.loadAnimation(
                        AddNewDeliverAddressActivity.this, animResId);
                animation.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        mAreaPicker.setVisibility(visibility);
                        mTaskState = TaskState.IDEL;
                    }
                });
                mAreaPicker.setAnimation(animation);
                mAreaPicker.startAnimation(animation);
            }
        }
    };

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        Logger.d(TAG, "onFocusChange()[hasFocus:"+hasFocus+"]");
        switch(v.getId())
        {
            case R.id.deviler_name_edit:
            case R.id.phone_number_edit:
            case R.id.post_code_edit:
            case R.id.detial_address_edit:
                if(hasFocus)
                {
                    sendMsg(View.GONE, ANIM_POST_DELAY_MILLS);
                }
                break;
        }
    }
    
    @Override
    public void onBackPressed()
    {
        mTitleBar.onBackBtnClick();
    }
}
