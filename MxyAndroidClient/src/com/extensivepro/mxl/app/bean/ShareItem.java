package com.extensivepro.mxl.app.bean;

import java.util.List;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

public class ShareItem extends BaseObject
{

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;

    @Expose
    private String contact;

    @Expose
    private String content;

    @Expose
    private int good;

    @Expose
    private String id;

    @Expose
    private String image;

    @Expose
    private String ip;

    @Expose
    private List<ReplyLeaveMessageSet> replyLeaveMessageSet;

    @Expose
    private String title;

    @Expose
    private String username;

    @Expose
    private Date createDate;

    @Expose
    private Date modifyDate;

    private boolean markGood;
    
    public String getContact()
    {
        return contact;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public int getGood()
    {
        return good;
    }

    public void setGood(int good)
    {
        this.good = good;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public List<ReplyLeaveMessageSet> getReplyLeaveMessageSet()
    {
        return replyLeaveMessageSet;
    }

    public void setReplyLeaveMessageSet(
            List<ReplyLeaveMessageSet> replyLeaveMessageSet)
    {
        this.replyLeaveMessageSet = replyLeaveMessageSet;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    public Date getModifyDate()
    {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate)
    {
        this.modifyDate = modifyDate;
    }
    
    

    public boolean isMarkGood()
    {
        return markGood;
    }

    public void setMarkGood(boolean markGood)
    {
        this.markGood = markGood;
    }

    @Override
    public String toString()
    {
        return "ShareItem [contact=" + contact + ", content=" + content
                + ", good=" + good + ", id=" + id + ", image=" + image
                + ", ip=" + ip + ", replyLeaveMessageSet="
                + replyLeaveMessageSet + ", title=" + title + ", username="
                + username + ", createDate=" + createDate + ", modifyDate="
                + modifyDate + "]";
    }

}
