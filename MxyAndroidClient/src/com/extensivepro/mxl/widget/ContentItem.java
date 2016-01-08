package com.extensivepro.mxl.widget;

import com.google.gson.annotations.Expose;

public class ContentItem
{
    @Expose
    private String title;

    @Expose
    private String content;
    
    private String contentCollapse = "";

    private String more = "";
    
    private boolean isUnread = true;
    
    /**
     * false 表示content收起，true表示content展开
     */
    private boolean collapse;

    @Expose
    private String date;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getContentCollapse()
    {
        return contentCollapse;
    }

    public void setContentCollapse(String contentCollapse)
    {
        this.contentCollapse = contentCollapse;
    }

    
    
    public boolean isCollapse()
    {
        return collapse;
    }

    public void setCollapse(boolean collapse)
    {
        this.collapse = collapse;
    }

    
    
    public String getMore()
    {
        return more;
    }

    public void setMore(String more)
    {
        this.more = more;
    }

    
    
    public boolean isUnread()
    {
        return isUnread;
    }

    public void setUnread(boolean isUnread)
    {
        this.isUnread = isUnread;
    }

    @Override
    public String toString()
    {
        return "ContentItem [title=" + title + ", content=" + content
                + ", contentCollapse=" + contentCollapse + ", more=" + more
                + ", isUnread=" + isUnread + ", collapse=" + collapse
                + ", date=" + date + "]";
    }

}
