package com.extensivepro.mxl.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.extensivepro.mxl.R;

public class DialogHelper
{
    public static Dialog createDialog(Context context,String title,String msg,int drawableId)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        if(drawableId !=-1)
        {
            builder.setIcon(drawableId);
        }
        return builder.create();
    }
    
    public static ProgressDialog createProgressDialog(Context context,int titleResId)
    {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(titleResId);
        return progressDialog;
    }
    
    
    /**
     * 显示dialog
     * 
     * @param context
     *            环境
     * @param strTitle
     *            标题
     * @param strText
     *            内容
     * @param icon
     *            图标
     */
    public static void showDialog(Activity context, String strTitle,
            String strText, int icon) {
        AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
        tDialog.setIcon(icon);
        tDialog.setTitle(strTitle);
        tDialog.setMessage(strText);
        tDialog.setPositiveButton(R.string.Ensure, null);
        tDialog.show();
    }


    //
    // show the progress bar.
    /**
     * 显示进度条
     * 
     * @param context
     *            环境
     * @param title
     *            标题
     * @param message
     *            信息
     * @param indeterminate
     *            确定性
     * @param cancelable
     *            可撤销
     * @return
     */
    public static ProgressDialog showProgress(Context context,
            CharSequence title, CharSequence message, boolean indeterminate,
            boolean cancelable) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(false);
        // dialog.setDefaultButton(false);
//      dialog.setOnCancelListener(new AlixDemo.AlixOnCancelListener(
//              (Activity) context));

        dialog.show();
        return dialog;
    }
}
