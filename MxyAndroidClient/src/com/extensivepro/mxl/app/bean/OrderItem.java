package com.extensivepro.mxl.app.bean;

import com.extensivepro.mxl.util.Const;
import com.google.gson.annotations.Expose;

public class OrderItem extends BaseObject
{

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;

    @Expose
    private String id;

    @Expose
    private Product product;

    @Expose
    private String productSn;

    @Expose
    private double productQuantity;

    @Expose
    private double deliveryQuantity;

    @Expose
    private Date createDate;

    @Expose
    private Date modifyDate;

    @Expose
    private String productName;

    @Expose
    private double productPrice;

    @Expose
    private Object customValues;
    
    @Expose
    private String goodsName;

    @Expose
    private String goodsCategoryName;
    
    @Expose
    private String productImage;
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public String getProductSn()
    {
        return productSn;
    }

    public void setProductSn(String productSn)
    {
        this.productSn = productSn;
    }

    public double getProductQuantity()
    {
        return productQuantity;
    }

    public void setProductQuantity(double productQuantity)
    {
        this.productQuantity = productQuantity;
    }

    public double getDeliveryQuantity()
    {
        return deliveryQuantity;
    }

    public void setDeliveryQuantity(double deliveryQuantity)
    {
        this.deliveryQuantity = deliveryQuantity;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    public Date getModifyDate()
    {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate)
    {
        this.modifyDate = modifyDate;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public double getProductPrice()
    {
        return productPrice;
    }

    public void setProductPrice(double productPrice)
    {
        this.productPrice = productPrice;
    }

    public Object getCustomValues()
    {
        return customValues;
    }

    public void setCustomValues(Object customValues)
    {
        this.customValues = customValues;
    }

    
    public String getGoodsName()
    {
        return goodsName;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    public String getGoodsCategoryName()
    {
        return goodsCategoryName;
    }

    public void setGoodsCategoryName(String goodsCategoryName)
    {
        this.goodsCategoryName = goodsCategoryName;
    }

    
    
    public String getProductImage()
    {
        return Const.BASE_URI + productImage;
    }

    public void setProductImage(String productImage)
    {
        this.productImage = productImage;
    }

    @Override
    public String toString()
    {
        return "OrderItem [id=" + id + ", product=" + product + ", productSn="
                + productSn + ", productQuantity=" + productQuantity
                + ", deliveryQuantity=" + deliveryQuantity + ", createDate="
                + createDate + ", modifyDate=" + modifyDate + ", productName="
                + productName + ", productPrice=" + productPrice
                + ", customValues=" + customValues + "]";
    }

}
