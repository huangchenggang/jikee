package com.extensivepro.mxl.app.bean;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

public class StatusMessage extends BaseObject
{
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;

    public enum Status
    {
        success,failed,unknow,ready;
        
        public static Status getStatus(String str)
        {
            Status type = Status.unknow;
            if(TextUtils.isEmpty(str))
            {
                return type;
            }
            if (Status.success.toString().equals(str))
            {
                type = Status.success;
            }
            else if (Status.failed.toString().equals(str))
            {
                type = Status.failed;
            }
            else if (Status.ready.toString().equals(str))
            {
                type = Status.ready;
            }
            return type;
        }
    }
    
    @Expose
    protected String message;

    @Expose
    protected String status;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public Status getStatus()
    {
        Status result = Status.unknow;
        if (Status.success.toString().equals(status))
        {
            result = Status.success;
        }
        else if (Status.failed.toString().equals(status))
        {
            result = Status.failed;
        }
        return result;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "StatusMessage [message=" + message + ", status=" + status + "]";
    }
    
    
    
}
