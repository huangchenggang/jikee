package com.extensivepro.mxl.app.bean;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.method.MetaKeyKeyListener;
import android.widget.TextView;

import com.extensivepro.mxl.R;
import com.extensivepro.mxl.R.drawable;
import com.extensivepro.mxl.app.provider.MxlTables.TGoods;
import com.extensivepro.mxl.util.JsonUtil;
import com.google.gson.annotations.Expose;

public class Goods extends BaseObject
{

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;

    @Expose
    private List<ImageStore> goodsImageStore;

    @Expose
    private GoodsCategory goodsCategory;// Category ID

    @Expose
    private String name;

    @Expose
    private String id;
    
    @Expose
    private boolean isSaleOnlyOne;//是否是免费赠送
    
    public List<SpecificationSet> getSpecificationSet()
    {
        return specificationSet;
    }

    public void setSpecificationSet(List<SpecificationSet> specificationSet)
    {
        this.specificationSet = specificationSet;
    }

    @Expose
    private List<SpecificationSet> specificationSet;

    @Expose
    private Date modifyDate;

    @Expose
    private double weight;

    @Expose
    private List<Product> productSet;

    @Expose
    private double price;

    @Expose
    private String metaDescription;

    private ContentValues values;

    @Override
    public ContentValues toContentValues()
    {
        if (values == null)
        {
            values = new ContentValues();
        }
        values.clear();
        values.put(TGoods.GOODS_ID, id);
        values.put(TGoods.CATEGORY_ID, goodsCategory == null ? ""
                : goodsCategory.getId());
        values.put(TGoods.GOODS_NAME, name);
        values.put(
                TGoods.GOODS_IMAGE,
                goodsImageStore == null ? "" : JsonUtil
                        .beanToJson(goodsImageStore));
        values.put(TGoods.PRICE, price);
        values.put(TGoods.META_DESCRIPTION, metaDescription);
        values.put(TGoods.IS_SALE_ONLY_ONE, isSaleOnlyOne?1:0);
        return values;
    }

    public Goods()
    {
        super();
    }

    @SuppressWarnings("unchecked")
    public Goods(Cursor c)
    {
        if (c != null && c.getCount() > 0)
        {
            id = c.getString(c.getColumnIndex(TGoods.GOODS_ID));
            goodsCategory = new GoodsCategory();
            goodsCategory.setId(c.getString(c
                    .getColumnIndex(TGoods.CATEGORY_ID)));
            name = c.getString(c.getColumnIndex(TGoods.GOODS_NAME));
            String imageStoreJson = c.getString(c
                    .getColumnIndex(TGoods.GOODS_IMAGE));
            goodsImageStore = (List<ImageStore>) JsonUtil.jsonToList(
                    imageStoreJson, ImageStore.class, null);
            metaDescription = c.getString(c
                    .getColumnIndex(TGoods.META_DESCRIPTION));
            price = c.getDouble(c.getColumnIndex(TGoods.PRICE));
            weight = c.getDouble(c.getColumnIndex(TGoods.WEIGHT));
            isSaleOnlyOne = c.getInt(c.getColumnIndex(TGoods.IS_SALE_ONLY_ONE)) == 1;
        }
    }

    public List<ImageStore> getGoodsImageStore()
    {
        return goodsImageStore;
    }

    public void setGoodsImageStore(List<ImageStore> goodsImageStore)
    {
        this.goodsImageStore = goodsImageStore;
    }

    public GoodsCategory getGoodsCategory()
    {
        return goodsCategory;
    }

    public void setGoodsCategory(GoodsCategory goodsCategory)
    {
        this.goodsCategory = goodsCategory;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Date getModifyDate()
    {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate)
    {
        this.modifyDate = modifyDate;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    public List<Product> getProductSet()
    {
        return productSet;
    }

    public void setProductSet(List<Product> productSet)
    {
        this.productSet = productSet;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public String getMetaDescription()
    {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription)
    {
        this.metaDescription = metaDescription;
    }

    public boolean isSaleOnlyOne()
    {
        return isSaleOnlyOne;
    }

    public void setSaleOnlyOne(boolean isSaleOnlyOne)
    {
        this.isSaleOnlyOne = isSaleOnlyOne;
    }

    @Override
    public String toString()
    {
        return "Goods [goodsImageStore=" + goodsImageStore + ", goodsCategory="
                + goodsCategory + ", name=" + name + ", id=" + id
                + ", isSaleOnlyOne=" + isSaleOnlyOne + ", specificationSet="
                + specificationSet + ", modifyDate=" + modifyDate + ", weight="
                + weight + ", productSet=" + productSet + ", price=" + price
                + ", metaDescription=" + metaDescription + "]";
    }

    
}
