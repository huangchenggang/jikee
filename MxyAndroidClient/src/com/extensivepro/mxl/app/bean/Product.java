package com.extensivepro.mxl.app.bean;

import java.util.List;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

public class Product extends BaseObject
{
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;

    @Expose
    private double marketPrice;

    @Expose
    private double weight;

    @Expose
    private Goods goods;

    @Expose
    private boolean isMarketable;

    @Expose
    private Date modifyDate;

    private String storePlace;

    private int freezeStore;

    @Expose
    private String id;

    @Expose
    private String productSn;

    @Expose
    private double price;

    @Expose
    private boolean isDefault;

    @Expose
    private String name;

    @Expose
    private List<SpecificationValueStore> specificationValueStore;

    @Expose
    private Date createDate;
    
    public double getMarketPrice()
    {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice)
    {
        this.marketPrice = marketPrice;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    public Goods getGoods()
    {
        return goods;
    }

    public void setGoods(Goods goods)
    {
        this.goods = goods;
    }

    public boolean isMarketable()
    {
        return isMarketable;
    }

    public void setMarketable(boolean isMarketable)
    {
        this.isMarketable = isMarketable;
    }

    public Date getModifyDate()
    {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate)
    {
        this.modifyDate = modifyDate;
    }

    public String getStorePlace()
    {
        return storePlace;
    }

    public void setStorePlace(String storePlace)
    {
        this.storePlace = storePlace;
    }

    public int getFreezeStore()
    {
        return freezeStore;
    }

    public void setFreezeStore(int freezeStore)
    {
        this.freezeStore = freezeStore;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getProductSn()
    {
        return productSn;
    }

    public void setProductSn(String productSn)
    {
        this.productSn = productSn;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public void setDefault(boolean isDefault)
    {
        this.isDefault = isDefault;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<SpecificationValueStore> getSpecificationValueStore()
    {
        return specificationValueStore;
    }

    public void setSpecificationValueStore(
            List<SpecificationValueStore> specificationValueStore)
    {
        this.specificationValueStore = specificationValueStore;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    public String getGoodsDesc()
    {
        if(specificationValueStore == null || specificationValueStore.isEmpty())
        {
            return "";
        }
        String desc = "";
        for(SpecificationValueStore valueStore:specificationValueStore)
        {
            if (!TextUtils.isEmpty(valueStore.getName())
                    && valueStore.getTypeEnum() != SpecificationValueStore.SpecificationValueStoreType.custom
                    && valueStore.getTypeEnum() != SpecificationValueStore.SpecificationValueStoreType.picture)
            {
                desc += valueStore.getName()+" ";
            }
        }
        return desc;
    }

    @Override
    public String toString()
    {
        return "Product [marketPrice=" + marketPrice + ", weight=" + weight
                + ", goods=" + goods + ", isMarketable=" + isMarketable
                + ", modifyDate=" + modifyDate + ", storePlace=" + storePlace
                + ", freezeStore=" + freezeStore + ", id=" + id
                + ", productSn=" + productSn + ", price=" + price
                + ", isDefault=" + isDefault + ", name=" + name
                + ", specificationValueStore=" + specificationValueStore
                + ", createDate=" + createDate + "]";
    }
    
    

    
    
}
