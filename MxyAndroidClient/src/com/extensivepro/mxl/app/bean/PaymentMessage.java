package com.extensivepro.mxl.app.bean;

import com.extensivepro.mxl.app.bean.PaymentConfig.PaymentConfigType;
import com.google.gson.annotations.Expose;

public class PaymentMessage extends StatusMessage
{
    
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;

    @Expose
    private String paymentType;

    @Expose
    private String paymentId;

    @Expose
    private String paymentStatus;
    
    @Expose
    private String paymentUrl;
    
    @Expose
    private PaymentParameter paymentParams;
    
    public PaymentConfigType getPaymentType()
    {
        return PaymentConfigType.getConfigType(paymentType);
    }
    
    public Status getPaymentStatus()
    {
        return Status.getStatus(paymentStatus);
    }

    public String getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }

    public void setPaymentStatus(String paymentStatus)
    {
        this.paymentStatus = paymentStatus;
    }

    
    
    public String getPaymentUrl()
    {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl)
    {
        this.paymentUrl = paymentUrl;
    }

    public PaymentParameter getPaymentParams()
    {
        return paymentParams;
    }

    public void setPaymentParams(PaymentParameter paymentParams)
    {
        this.paymentParams = paymentParams;
    }

    @Override
    public String toString()
    {
        return "PaymentMessage [paymentType=" + paymentType + ", paymentId="
                + paymentId + ", paymentStatus=" + paymentStatus
                + ", paymentUrl=" + paymentUrl + ", paymentParams="
                + paymentParams + ", message=" + message + ", status=" + status
                + "]";
    }

    
}
