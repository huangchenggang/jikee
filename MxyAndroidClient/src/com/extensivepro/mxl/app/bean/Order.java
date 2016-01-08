package com.extensivepro.mxl.app.bean;

import java.util.List;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

public class Order extends BaseObject
{
    public enum ShippingStatus
    {
        unshipped, unknow;
        public static ShippingStatus getStatus(String status)
        {
            ShippingStatus statusEnum = ShippingStatus.unknow;
            if (TextUtils.isEmpty(status))
            {
                return statusEnum;
            }
            if (ShippingStatus.unshipped.toString().equals(status))
            {
                statusEnum = ShippingStatus.unshipped;
            }
            return statusEnum;
        }
    }
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;

    @Expose
    private double paymentFee;

    @Expose
    private String deliveryTypeName;

    @Expose
    private String shipZipCode;
    
    @Expose
    private List<OrderItem> orderItemSet;
    
    @Expose
    private Date modifyDate;
    
    @Expose
    private PaymentConfig paymentConfig;
    
    @Expose
    private String id;
    
    private String shipMobile;
    
    @Expose
    private double totalProductPrice;
    @Expose
    private double totalAmount;
    
    @Expose
    private String paymentConfigName;
    
    @Expose
    private Date createDate;
    
    
    @Expose
    private String shippingStatus;

    @Expose
    private double deliveryFee;
    
    @Expose
    private Member member;
    
    @Expose
    private int totalProductQuantity;

    @Expose
    private String memo;
    
//    @Expose
    private Object deliveryType;
    
    @Expose
    private double totalProductWeight;
    
    @Expose
    private String paymentStatus;
    
    @Expose
    private String orderSn;
    
    @Expose
    private String orderStatus;

    
    @Expose
    private Area shipAreaStore;
    
    @Expose
    private String shipName;


    @Expose
    private String shipPhone;

    @Expose
    private double paidAmount;

    @Expose
    private String shipAddress;

    public double getPaymentFee()
    {
        return paymentFee;
    }

    public void setPaymentFee(double paymentFee)
    {
        this.paymentFee = paymentFee;
    }

    public String getDeliveryTypeName()
    {
        return deliveryTypeName;
    }

    public void setDeliveryTypeName(String deliveryTypeName)
    {
        this.deliveryTypeName = deliveryTypeName;
    }

    public String getShipZipCode()
    {
        return shipZipCode;
    }

    public void setShipZipCode(String shipZipCode)
    {
        this.shipZipCode = shipZipCode;
    }

    public List<OrderItem> getOrderItemSet()
    {
        return orderItemSet;
    }

    public void setOrderItemSet(List<OrderItem> orderItemSet)
    {
        this.orderItemSet = orderItemSet;
    }

    public Date getModifyDate()
    {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate)
    {
        this.modifyDate = modifyDate;
    }

    public PaymentConfig getPaymentConfig()
    {
        return paymentConfig;
    }

    public void setPaymentConfig(PaymentConfig paymentConfig)
    {
        this.paymentConfig = paymentConfig;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getShipMobile()
    {
        return shipMobile;
    }

    public void setShipMobile(String shipMobile)
    {
        this.shipMobile = shipMobile;
    }

    public double getTotalProductPrice()
    {
        return totalProductPrice;
    }

    public void setTotalProductPrice(double totalProductPrice)
    {
        this.totalProductPrice = totalProductPrice;
    }

    public double getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public String getPaymentConfigName()
    {
        return paymentConfigName;
    }

    public void setPaymentConfigName(String paymentConfigName)
    {
        this.paymentConfigName = paymentConfigName;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    public ShippingStatus getShippingStatus()
    {
        return ShippingStatus.getStatus(shippingStatus);
    }

    public void setShippingStatus(String shippingStatus)
    {
        this.shippingStatus = shippingStatus;
    }

    public double getDeliveryFee()
    {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee)
    {
        this.deliveryFee = deliveryFee;
    }

    public Member getMember()
    {
        return member;
    }

    public void setMember(Member member)
    {
        this.member = member;
    }

    public int getTotalProductQuantity()
    {
        return totalProductQuantity;
    }

    public void setTotalProductQuantity(int totalProductQuantity)
    {
        this.totalProductQuantity = totalProductQuantity;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String memo)
    {
        this.memo = memo;
    }

    public Object getDeliveryType()
    {
        return deliveryType;
    }

    public void setDeliveryType(Object deliveryType)
    {
        this.deliveryType = deliveryType;
    }

    public double getTotalProductWeight()
    {
        return totalProductWeight;
    }

    public void setTotalProductWeight(double totalProductWeight)
    {
        this.totalProductWeight = totalProductWeight;
    }

    public String getPaymentStatus()
    {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus)
    {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderSn()
    {
        return orderSn;
    }

    public void setOrderSn(String orderSn)
    {
        this.orderSn = orderSn;
    }

    public String getOrderStatus()
    {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    public Area getShipAreaStore()
    {
        return shipAreaStore;
    }

    public void setShipAreaStore(Area shipAreaStore)
    {
        this.shipAreaStore = shipAreaStore;
    }

    public String getShipName()
    {
        return shipName;
    }

    public void setShipName(String shipName)
    {
        this.shipName = shipName;
    }

    public String getShipPhone()
    {
        return shipPhone;
    }

    public void setShipPhone(String shipPhone)
    {
        this.shipPhone = shipPhone;
    }

    public double getPaidAmount()
    {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount)
    {
        this.paidAmount = paidAmount;
    }

    public String getShipAddress()
    {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress)
    {
        this.shipAddress = shipAddress;
    }

    @Override
    public String toString()
    {
        return "Order [paymentFee=" + paymentFee + ", deliveryTypeName="
                + deliveryTypeName + ", shipZipCode=" + shipZipCode
                + ", orderItemSet=" + orderItemSet + ", modifyDate="
                + modifyDate + ", paymentConfig=" + paymentConfig + ", id="
                + id + ", shipMobile=" + shipMobile + ", totalProductPrice="
                + totalProductPrice + ", totalAmount=" + totalAmount
                + ", paymentConfigName=" + paymentConfigName + ", createDate="
                + createDate + ", shippingStatus=" + shippingStatus
                + ", deliveryFee=" + deliveryFee + ", member=" + member
                + ", totalProductQuantity=" + totalProductQuantity + ", memo="
                + memo + ", deliveryType=" + deliveryType
                + ", totalProductWeight=" + totalProductWeight
                + ", paymentStatus=" + paymentStatus + ", orderSn=" + orderSn
                + ", orderStatus=" + orderStatus + ", shipAreaStore="
                + shipAreaStore + ", shipName=" + shipName + ", shipPhone="
                + shipPhone + ", paidAmount=" + paidAmount + ", shipAddress="
                + shipAddress + "]";
    }
    
    
    
}
