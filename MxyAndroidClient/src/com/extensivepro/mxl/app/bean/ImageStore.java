package com.extensivepro.mxl.app.bean;

import java.io.Serializable;

import com.extensivepro.mxl.util.Const;
import com.google.gson.annotations.Expose;

public class ImageStore implements Serializable
{
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;

    //    "id":"47a3e6fd5c0d473d9a18e9f7e0a71f66",
//    "description":"",
//    "path":"\/upload\/image\/201305",
//    "sourceImageFormatName":"png",
//    "orderList":1
    @Expose        
    private String path="";

    @Expose        
    private String id="";

    @Expose        
    private String sourceImageFormatName="";

    private int orderList;

    @Expose        
    private String description;

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getSourceImageFormatName()
    {
        return sourceImageFormatName;
    }

    public void setSourceImageFormatName(String sourceImageFormatName)
    {
        this.sourceImageFormatName = sourceImageFormatName;
    }

    public int getOrderList()
    {
        return orderList;
    }

    public void setOrderList(int orderList)
    {
        this.orderList = orderList;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String toString()
    {
        return "ImageStore [path=" + path + ", id=" + id
                + ", sourceImageFormatName=" + sourceImageFormatName
                + ", orderList=" + orderList + ", description=" + description
                + "]";
    }
    
    public String generateUrl()
    {
        return Const.BASE_URI+path+"/source/"+id+"."+sourceImageFormatName;
    }
    

}
