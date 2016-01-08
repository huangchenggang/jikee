package com.extensivepro.mxl.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class CommonUtil
{
    public static boolean isInputMethodShow(Context context)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }
    
    public static void hideInputMethod(Context context,EditText editText)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive())
        {
            imm.hideSoftInputFromWindow(editText.getWindowToken() , InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
