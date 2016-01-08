package com.extensivepro.mxl.app.bean;

import java.io.File;

import com.extensivepro.mxl.util.Const;
import com.google.gson.annotations.Expose;

public class DepositCard extends BaseObject
{
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;

    @Expose
    private String id;

    @Expose
    private double price;

    @Expose
    private String memo;

    @Expose
    private String imagePath;

    @Expose
    private String name;

    @Expose
    private boolean isMarketable;

    @Expose
    private double faceValue;

    @Expose
    private Date modifyDate;

    @Expose
    private Date createDate;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String memo)
    {
        this.memo = memo;
    }

    public String getImagePath()
    {
        return Const.BASE_URI + File.separator + imagePath;
    }

    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isMarketable()
    {
        return isMarketable;
    }

    public void setMarketable(boolean isMarketable)
    {
        this.isMarketable = isMarketable;
    }

    public double getFaceValue()
    {
        return faceValue;
    }

    public void setFaceValue(double faceValue)
    {
        this.faceValue = faceValue;
    }

    public Date getModifyDate()
    {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate)
    {
        this.modifyDate = modifyDate;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    @Override
    public String toString()
    {
        return "AccountRechargeItem [id=" + id + ", price=" + price + ", memo="
                + memo + ", imagePath=" + imagePath + ", name=" + name
                + ", isMarketable=" + isMarketable + ", faceValue=" + faceValue
                + ", modifyDate=" + modifyDate + ", createDate=" + createDate
                + "]";
    }
    
    
}
