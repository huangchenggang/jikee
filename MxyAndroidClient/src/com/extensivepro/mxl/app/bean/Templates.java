package com.extensivepro.mxl.app.bean;

import com.google.gson.annotations.Expose;

public class Templates extends BaseObject
{

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;

    @Expose
    private String name;

    @Expose
    private String image;

    @Expose
    private int orderList;
    
    @Expose
    private int  resId;
    
    public int getResId()
    {
        return resId;
    }

    public void setResId(int resId)
    {
        this.resId = resId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public int getOrderList()
    {
        return orderList;
    }

    public void setOrderList(int orderList)
    {
        this.orderList = orderList;
    }
}
