package com.extensivepro.mxl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.widget.TitleBar;

/** 
 * @Description 
 * @author Admin
 * @date 2013-5-11 上午11:43:20 
 * @version V1.3.1
 */

public class SpecialInstructionActivity extends BaseActivity implements
        OnClickListener
{

    private TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.special_instruction);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle(R.string.special_instruction_txt);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View v)
    {

    }

}
