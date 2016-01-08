package com.extensivepro.mxl.app.bean;

import android.text.TextUtils;

import com.extensivepro.mxl.util.Const;
import com.google.gson.annotations.Expose;

public class CartItem extends BaseObject
{
    
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;

    @Expose
    private String id;

    @Expose
    private Product product;

    @Expose
    private int quantity;

    @Expose
    private Date modifyDate;

    @Expose
    private Date createDate;
    
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

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
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

    
    
    public String getGoodsName()
    {
        return TextUtils.isEmpty(goodsName)?"":goodsName;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    
    
    public String getGoodsCategoryName()
    {
        return TextUtils.isEmpty(goodsCategoryName)?"":goodsCategoryName;
    }

    public void setGoodsCategoryName(String goodsCategoryName)
    {
        this.goodsCategoryName = goodsCategoryName;
    }

    
    
    public String getProductImage()
    {
        return Const.BASE_URI+productImage;
    }

    public void setProductImage(String productImage)
    {
        this.productImage = productImage;
    }

    

    
}
