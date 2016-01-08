package com.extensivepro.mxl.app.bean;

import android.text.TextUtils;

import com.extensivepro.mxl.app.bean.PaymentConfig.PaymentConfigType;
import com.google.gson.annotations.Expose;

public class SpecificationValueStore extends BaseObject
{

    public enum SpecificationValueStoreType
    {
        custom, picture, unknow;

        public static SpecificationValueStoreType getSpecificationValueStoreType(
                String paymentType)
        {
            SpecificationValueStoreType type = SpecificationValueStoreType.unknow;
            if (TextUtils.isEmpty(paymentType))
            {
                return type;
            }
            if (SpecificationValueStoreType.custom.toString().equals(
                    paymentType))
            {
                type = SpecificationValueStoreType.custom;
            }
            else if (SpecificationValueStoreType.picture.toString().equals(
                    paymentType))
            {
                type = SpecificationValueStoreType.picture;
            }
            return type;
        }
    }
    
    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;
    @Expose
    private String id;

    @Expose
    private String imageCount;

    @Expose
    private String name;

    @Expose
    private String templateGroupId;

    @Expose
    private String type;

    public String getId()
    {
        return id;
    }

    public String getImageCount()
    {
        return imageCount;
    }

    public String getName()
    {
        return name;
    }

    public String getTemplateGroupId()
    {
        return templateGroupId;
    }

    public String getType()
    {
        return type;
    }

    public SpecificationValueStoreType getTypeEnum()
    {
        return SpecificationValueStoreType.getSpecificationValueStoreType(type);
    }
    
    public void setId(String id)
    {
        this.id = id;
    }

    public void setImageCount(String imageCount)
    {
        this.imageCount = imageCount;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setTemplateGroupId(String templateGroupId)
    {
        this.templateGroupId = templateGroupId;
    }

    public void setType(String type)
    {
        this.type = type;
    }

}
