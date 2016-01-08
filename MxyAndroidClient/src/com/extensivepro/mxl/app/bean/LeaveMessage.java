package com.extensivepro.mxl.app.bean;

import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class LeaveMessage extends BaseObject
{

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;

    @Expose
    private String contact;

    @Expose
    private String content;

    private LeaveMessage forLeaveMessage;

    @Expose
    private String good;

    @Expose
    private String image;

    @Expose
    private String ip;

    @Expose
    private Set<LeaveMessage> replyLeaveMessageSet;

    @Expose
    private String title;

    @Expose
    private String username;

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

    public LeaveMessage getForLeaveMessage()
    {
        return forLeaveMessage;
    }

    public void setForLeaveMessage(LeaveMessage forLeaveMessage)
    {
        this.forLeaveMessage = forLeaveMessage;
    }


    public String getGood()
    {
        return good;
    }

    public void setGood(String good)
    {
        this.good = good;
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

    public Set<LeaveMessage> getReplyLeaveMessageSet()
    {
        return replyLeaveMessageSet;
    }

    public void setReplyLeaveMessageSet(Set<LeaveMessage> replyLeaveMessageSet)
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

    @Override
    public String toString()
    {
        return "LeaveMessage [contact=" + contact + ", content=" + content
                + ", forLeaveMessage=" + forLeaveMessage + ", good=" + good
                + ", image=" + image + ", ip=" + ip + ", replyLeaveMessageSet="
                + replyLeaveMessageSet + ", title=" + title + ", username="
                + username + "]";
    }


}
