package com.extensivepro.mxl.app.bean;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

public class PaymentConfig extends BaseObject
{
    public enum PaymentConfigType
    {
        mobile, online, deposit, unknow;

        public static PaymentConfigType getConfigType(String paymentType)
        {
            PaymentConfigType type = PaymentConfigType.unknow;
            if(TextUtils.isEmpty(paymentType))
            {
                return type;
            }
            if (PaymentConfigType.online.toString().equals(paymentType))
            {
                type = PaymentConfigType.online;
            }
            else if (PaymentConfigType.deposit.toString().equals(paymentType))
            {
                type = PaymentConfigType.deposit;
            }
            else if (PaymentConfigType.mobile.toString().equals(paymentType))
            {
                type = PaymentConfigType.mobile;
            }
            return type;
        }
    }
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;

    @Expose
    private String id;

    @Expose
    private String paymentProductId;

    @Expose
    private String name;

    @Expose
    private String paymentConfigType;

    private Date modifyDate;

    private Date createDate;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPaymentProductId()
    {
        return paymentProductId;
    }

    public void setPaymentProductId(String paymentProductId)
    {
        this.paymentProductId = paymentProductId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public PaymentConfigType getPaymentConfigType()
    {
        PaymentConfigType type = PaymentConfigType.unknow;
        if (PaymentConfigType.online.toString().equals(paymentConfigType))
        {
            type = PaymentConfigType.online;
        }
        else if (PaymentConfigType.deposit.toString().equals(paymentConfigType))
        {
            type = PaymentConfigType.deposit;
        }
        else if (PaymentConfigType.mobile.toString().equals(paymentConfigType))
        {
            type = PaymentConfigType.mobile;
        }
        return type;
    }

    public void setPaymentConfigType(String paymentConfigType)
    {
        this.paymentConfigType = paymentConfigType;
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
        return "PaymentConfig [id=" + id + ", paymentProductId="
                + paymentProductId + ", name=" + name + ", paymentConfigType="
                + paymentConfigType + ", modifyDate=" + modifyDate
                + ", createDate=" + createDate + "]";
    }
    
    
}
