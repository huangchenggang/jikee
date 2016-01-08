package com.extensivepro.mxl.app.bean;

import com.google.gson.annotations.Expose;

public class PaymentParameter extends BaseObject
{
    
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;
    @Expose
    private String body;
    @Expose
    private String subject;
    @Expose
    private String sign_type;
    @Expose
    private String notify_url;
    @Expose
    private String defaultbank;
    @Expose
    private String out_trade_no;
    @Expose
    private String return_url;
    @Expose
    private String sign;
    @Expose
    private String extra_common_param;
    @Expose
    private double total_fee;
    @Expose
    private String service;
    @Expose
    private String paymethod;
    @Expose
    private String seller_id;
    @Expose
    private String partner;
    @Expose
    private String payment_type;
    @Expose
    private String show_url;
    public String getBody()
    {
        return body;
    }
    public void setBody(String body)
    {
        this.body = body;
    }
    public String getSubject()
    {
        return subject;
    }
    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    public String getSign_type()
    {
        return sign_type;
    }
    public void setSign_type(String sign_type)
    {
        this.sign_type = sign_type;
    }
    public String getNotify_url()
    {
        return notify_url;
    }
    public void setNotify_url(String notify_url)
    {
        this.notify_url = notify_url;
    }
    public String getDefaultbank()
    {
        return defaultbank;
    }
    public void setDefaultbank(String defaultbank)
    {
        this.defaultbank = defaultbank;
    }
    public String getOut_trade_no()
    {
        return out_trade_no;
    }
    public void setOut_trade_no(String out_trade_no)
    {
        this.out_trade_no = out_trade_no;
    }
    public String getReturn_url()
    {
        return return_url;
    }
    public void setReturn_url(String return_url)
    {
        this.return_url = return_url;
    }
    public String getSign()
    {
        return sign;
    }
    public void setSign(String sign)
    {
        this.sign = sign;
    }
    public String getExtra_common_param()
    {
        return extra_common_param;
    }
    public void setExtra_common_param(String extra_common_param)
    {
        this.extra_common_param = extra_common_param;
    }
    public double getTotal_fee()
    {
        return total_fee;
    }
    public void setTotal_fee(double total_fee)
    {
        this.total_fee = total_fee;
    }
    public String getService()
    {
        return service;
    }
    public void setService(String service)
    {
        this.service = service;
    }
    public String getPaymethod()
    {
        return paymethod;
    }
    public void setPaymethod(String paymethod)
    {
        this.paymethod = paymethod;
    }
    public String getSeller_id()
    {
        return seller_id;
    }
    public void setSeller_id(String seller_id)
    {
        this.seller_id = seller_id;
    }
    public String getPartner()
    {
        return partner;
    }
    public void setPartner(String partner)
    {
        this.partner = partner;
    }
    public String getPayment_type()
    {
        return payment_type;
    }
    public void setPayment_type(String payment_type)
    {
        this.payment_type = payment_type;
    }
    public String getShow_url()
    {
        return show_url;
    }
    public void setShow_url(String show_url)
    {
        this.show_url = show_url;
    }
    @Override
    public String toString()
    {
        return "PaymentParameter [body=" + body + ", subject=" + subject
                + ", sign_type=" + sign_type + ", notify_url=" + notify_url
                + ", defaultbank=" + defaultbank + ", out_trade_no="
                + out_trade_no + ", return_url=" + return_url + ", sign="
                + sign + ", extra_common_param=" + extra_common_param
                + ", total_fee=" + total_fee + ", service=" + service
                + ", paymethod=" + paymethod + ", seller_id=" + seller_id
                + ", partner=" + partner + ", payment_type=" + payment_type
                + ", show_url=" + show_url + "]";
    }
    
}
