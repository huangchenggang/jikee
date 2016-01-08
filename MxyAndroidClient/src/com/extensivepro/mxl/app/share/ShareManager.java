package com.extensivepro.mxl.app.share;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.text.TextUtils;

import com.extensivepro.mxl.app.EventDispacher;
import com.extensivepro.mxl.app.ICommonCallback;
import com.extensivepro.mxl.app.bean.LeaveMessage;
import com.extensivepro.mxl.app.bean.ShareItem;
import com.extensivepro.mxl.app.bean.StatusMessage;
import com.extensivepro.mxl.app.client.ClientManager;
import com.extensivepro.mxl.app.client.ClientThread;
import com.extensivepro.mxl.app.client.Command;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.DataUtil;
import com.extensivepro.mxl.util.JsonUtil;
import com.extensivepro.mxl.util.Logger;
import com.extensivepro.mxl.util.OSSUploadImageTask;
import com.extensivepro.mxl.util.OSSUploadImageTask.IUploadCallback;

public class ShareManager implements ICommonCallback
{

    private static final String TAG = ShareManager.class.getSimpleName();

    private static ShareManager mInstance;

    private static final int MOUDEL_CODE = EventDispacher.MoudelCode.MOUDEL_CODE_SHARE;

//    private List<ShareItem> shareItems;

//    private StatusMessage status;

    private ShareManager()
    {
        EventDispacher.regCallback(MOUDEL_CODE, this);
    }

    public static ShareManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new ShareManager();
        }
        return mInstance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResp(int reqCode, int reasonCode, InputStream stream)
    {
        if (stream == null || reasonCode != Const.CONN_REQUEST_OK)
        {

        }
        else
        {
            Intent intent = null;
            String resp = DataUtil.streamToString(stream);
            Logger.d(TAG, "onResp()[reqCode:" + reqCode + ",reasonCode:"
                    + ",resp:" + resp + "]");
            switch (reqCode)
            {
                case EventDispacher.EventCodeBusiness.EVENT_CODE_GET_SHARE:
                    List<ShareItem> shareItems = (List<ShareItem>) JsonUtil.jsonToList(resp,
                            ShareItem.class, Const.JSON_LIST_NAME);
                    intent = new Intent(Const.ACTION_GETSHARE_SUCCESS);
                    intent.putExtra(Const.GROUPBACKBEAN_OBJ,
                            (Serializable) shareItems);

                    ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_SAVE_MESSAGE:
                    
                    StatusMessage status = (StatusMessage) JsonUtil.jsonToObject(resp, StatusMessage.class,
                            null);
                    if(status!=null ){
                        switch (status.getStatus())
                        {
                            case success:
                                intent = new Intent(Const.ACTION_SAVESHARE_SUCCESS);
                                intent.putExtra(Const.ShareItemModuel.MESSAGE_OBJ, status.getMessage());
                                ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                                break;
                            
                            default:
                                intent = new Intent(Const.ACTION_SAVESHARE_FAILED);
                                intent.putExtra(Const.ShareItemModuel.MESSAGE_OBJ, status.getMessage());
                                ClientManager.getInstance().getAppContext().sendBroadcast(intent);
                                break;
                        }
                    }
                    break;
                case EventDispacher.EventCodeBusiness.EVENT_CODE_GOOD_MESSAGE:
//                    StatusMessage msg = (StatusMessage) JsonUtil.jsonToObject(resp, StatusMessage.class, "");
//                    if(msg.getStatus() == StatusMessage.Status.success)
//                    {
//                        getShareInfos(null, "15");
//                    }
                    break;
            }
        }

    }

    /**
     * @Description 获取分享信息
     * @author Administrator
     */
    public void getShareInfos(String lastTime, String pageSize)
    {
        Command command = new Command();

        command.addAttribute(Const.ShareItemModuel.PARAM_LASTTIME, lastTime);
        command.addAttribute(Const.ShareItemModuel.PARAM_PAGESIZE, pageSize);
        command.setDoGet(true);
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_GET_SHARE,
                MOUDEL_CODE, command);
        command.commit(thread);

    }

    public void postMessage(LeaveMessage msg, List<String> images)
    {
        final Command command = new Command();

        command.addAttribute(Const.ShareItemModuel.PARAM_MSG_CONTACT,
                msg.getContact());
        command.addAttribute(Const.ShareItemModuel.PARAM_MSG_CONTENT,
                msg.getContent());
        command.addAttribute(Const.ShareItemModuel.PARAM_MSG_GOOD,
                msg.getGood());

        command.addAttribute(Const.ShareItemModuel.PARAM_MSG_IP, msg.getIp());
        command.addAttribute(Const.ShareItemModuel.PARAM_MSG_TITLE,
                msg.getTitle());
        command.addAttribute(Const.ShareItemModuel.PARAM_MSG_USERNAME,
                msg.getUsername());
        final ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_SAVE_MESSAGE,
                MOUDEL_CODE, command);

        if (!images.isEmpty())
        {
            new OSSUploadImageTask(new IUploadCallback() {
                @Override
                public void onUploadSuccess(List<String> ossPaths)
                {
                    command.addAttribute(Const.ShareItemModuel.PARAM_MSG_IMAGE,
                            ossPaths.get(0));

                    command.commit(thread);
                }

                @Override
                public void onUploadFailed()
                {
                    Logger.e(TAG, "upload image failed");
                }

                @Override
                public void onProgress(int progress, int progressCount)
                {
                    
                }

                
                
            }, images).startUpload();
        }
        else
        {
            command.commit(thread);
        }

    }
    
    
    /**
     * 
     * @Description 
     * @author damon
     * @param leaveMsgId
     * @param leaveMsgGood 喜欢值.true=喜欢,false=不喜欢
     */
    public void goodMessage(String leaveMsgId,boolean leaveMsgGood)
    {
        Logger.d(TAG, "goodMessage()[leaveMsgId:" + leaveMsgId
                + ",leaveMsgGood:" + leaveMsgGood + "]");
        if(TextUtils.isEmpty(leaveMsgId))
        {
            Logger.e(TAG, "goodMessage()[leaveMsgId is empty]");
            return;
        }
        Command command = new Command();
        command.addAttribute(Const.ShareItemModuel.PARAM_MSG_ID , leaveMsgId);
        command.addAttribute(Const.ShareItemModuel.PARAM_MSG_GOOD,
                String.valueOf(leaveMsgGood ? 1 : -1));
        ClientThread thread = new ClientThread(
                EventDispacher.EventCodeBusiness.EVENT_CODE_GOOD_MESSAGE,
                MOUDEL_CODE, command);
        command.commit(thread);
    }

}
