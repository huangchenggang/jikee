package com.extensivepro.mxl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.app.BaseActivity;
import com.extensivepro.mxl.widget.TitleBar;

/** 
 * @Description 
 * @author Admin
 * @date 2013-5-11 上午11:42:12 
 * @version V1.3.1
 */

public class SoftwareLicenseAgereementActivity extends BaseActivity implements OnClickListener
{

    private TitleBar mTitleBar;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.software_license_agreement);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle(R.string.software_license_agreement_txt);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.loadUrl("file:///android_asset/agreement.html");
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
