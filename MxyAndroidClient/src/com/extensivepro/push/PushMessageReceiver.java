package com.extensivepro.push;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baidu.android.common.logging.Log;
import com.baidu.android.pushservice.PushConstants;
import com.extensivepro.mxl.R;
import com.extensivepro.mxl.ui.HomeActivity;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.HttpUtil;
import com.extensivepro.mxl.util.Logger;

public class PushMessageReceiver extends BroadcastReceiver
{
    public static final String TAG = PushMessageReceiver.class.getSimpleName();

    String appid = "";

    String channelid = "";

    String userid = "";

    private String str;

    /**
     * 
     * 
     * @param context Context
     * @param intent 接收的intent
     */
    @Override
    public void onReceive(final Context context, Intent intent)
    {

        Log.d(TAG, ">>> Receive intent: \r\n" + intent);

        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE))
        {
            //获取消息内容
            String message = intent.getExtras().getString(
                    PushConstants.EXTRA_PUSH_MESSAGE_STRING);

            //用户自定义内容读取方式，CUSTOM_KEY值和管理界面发送时填写的key对应
            //String customContentString = intent.getExtras().getString(CUSTOM_KEY);
            Logger.i(TAG, "onMessage: " + message);

            //用户在此自定义处理消息,以下代码为demo界面展示用
            Intent responseIntent = new Intent(context, HomeActivity.class);
            NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification(R.drawable.ic_launcher, message, System.currentTimeMillis());
            notification.flags = Notification.FLAG_AUTO_CANCEL; 
            long[] vibrate = {0,100,200,300};
            notification.vibrate = vibrate;
            notification.defaults |= Notification.DEFAULT_LIGHTS;
            notification.defaults |= Notification.DEFAULT_SOUND;
            
            
            responseIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);  
            PendingIntent contentIntent = PendingIntent.getActivity(
                    context, 
                    R.string.app_name, 
                    responseIntent, 
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(
                    context,
                    context.getResources().getString(R.string.app_new_push_message), 
                    message, 
                    contentIntent);
            notifyMgr.notify(R.string.app_name, notification);
//            responseIntent = new Intent(PushDemoActivity.ACTION_MESSAGE);
//            responseIntent.putExtra(PushDemoActivity.EXTRA_MESSAGE, message);
//            responseIntent.setClass(context, LogoActivity.class);
//            responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(responseIntent);
            
        }
        else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE))
        {
            // 处理绑定等方法的返回数据
            // PushManager.startWork()的返回值通过PushConstants.METHOD_BIND得到

            // 获取方法
            final String method = intent
                    .getStringExtra(PushConstants.EXTRA_METHOD);
            // 方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
            // 绑定失败的原因有多种，如网络原因，或access token过期。
            // 请不要在出错时进行简单的startWork调用，这有可能导致死循环。
            // 可以通过限制重试次数，或者在其他时机重新调用来解决。
            final int errorCode = intent
                    .getIntExtra(PushConstants.EXTRA_ERROR_CODE,
                            PushConstants.ERROR_SUCCESS);
            // 返回内容
            final String content = new String(
                    intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));

            try
            {
                JSONObject jsonContent = new JSONObject(content);
                JSONObject params = jsonContent
                        .getJSONObject("response_params");
                appid = params.getString("appid");
                channelid = params.getString("channel_id");
                userid = params.getString("user_id");
            }
            catch (JSONException e)
            {
                Log.e(Utils.TAG, "Parse bind json infos error: " + e);
            }

            new Thread() {
                public void run()
                {
                    try
                    {
                        HttpUtil.questDataAsString(Const.PushMobuel.URI_PUSH+"?driverToKen="
                                + userid + "&terminalType=and");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                };
            }.start();

        }// 可选。通知用户点击事件处理
        else if (intent.getAction().equals(
                PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK))
        {

        }
    }
}
