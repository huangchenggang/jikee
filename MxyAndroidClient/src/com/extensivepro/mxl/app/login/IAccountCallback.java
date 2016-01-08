package com.extensivepro.mxl.app.login;

import com.extensivepro.mxl.app.ICommonCallback;

/**
 * 
 * @Description
 * @author damon
 * @date Apr 20, 2013 10:33:44 AM
 * @version V1.3.1
 */
public interface IAccountCallback extends ICommonCallback
{
    void onLoginSuccess(int reasonCode, String respStr);

    void onLoginFailed(int reasonCode);
    void onRegisterSuccess(int reasonCode, String respStr);
    void onRegisterFailed(int reasonCode);
}
