package com.extensivepro.mxl.app.bean;


import java.io.File;

import android.content.ContentValues;
import android.database.Cursor;

import com.extensivepro.mxl.app.mock.MockData;
import com.extensivepro.mxl.app.provider.MxlTables.TGoodsCategory;
import com.extensivepro.mxl.util.Const;
import com.extensivepro.mxl.util.SharedPreferenceUtil;
import com.google.gson.annotations.Expose;

public class GoodsCategory extends BaseObject
{

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;

//    
//    "id":"1",
//    "name":"Photo Balloon",
//    "metaKeywords":"Photo Balloon",
//    "path":"goodsCategory/product_category_0001.png",
//    "metaDescription":"将你的照的照片印制在气球上",
//    "displayImage":"goods/body_showpic_01.png"
    
    @Expose
    private String id;//this to instead categoryId
    
    @Expose
    private String name;

    @Expose
    private String metaKeywords;

    @Expose
    private String iconPath;

    @Expose
    private String metaDescription;

    @Expose
    private String descImagePath;
    
    private ContentValues values;

    public GoodsCategory()
    {
        super();
    }

    @Override
    public ContentValues toContentValues()
    {
        if (values == null)
        {
            values = new ContentValues();
        }
        values.clear();
        values.put(TGoodsCategory.CATEGORY_NAME, name);
        values.put(TGoodsCategory.META_KEY_WORDS, metaKeywords);
        values.put(TGoodsCategory.CATEGORY_ID, id);
        values.put(TGoodsCategory.PATH, SharedPreferenceUtil
                .isLoadGoodsCategoryFromNetworkSuccess() ? getIconPath()
                : iconPath);
        values.put(TGoodsCategory.META_DESCRIPTION, metaDescription);
        values.put(TGoodsCategory.DISPLAY_IMAGE, SharedPreferenceUtil
                .isLoadGoodsCategoryFromNetworkSuccess() ? getDescImagePath()
                : descImagePath);
        return values;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMetaKeywords()
    {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords)
    {
        this.metaKeywords = metaKeywords;
    }

    public String getIconPath()
    {
        return Const.BASE_URI+File.separator+iconPath;
    }

    public void setIconPath(String path)
    {
        this.iconPath = path;
    }

    public String getMetaDescription()
    {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription)
    {
        this.metaDescription = metaDescription;
    }

    public String getDescImagePath()
    {
        return Const.BASE_URI+File.separator+descImagePath;
    }

    public void setDescImagePath(String descImagePath)
    {
        this.descImagePath = descImagePath;
    }

    public ContentValues getValues()
    {
        return values;
    }

    public void setValues(ContentValues values)
    {
        this.values = values;
    }

    @Override
    public String toString()
    {
        return "GoodsCategory [id=" + id + ", name=" + name + ", metaKeywords="
                + metaKeywords + ", iconPath=" + iconPath + ", metaDescription="
                + metaDescription + ", descImagePath=" + descImagePath + "]";
    }
    
}
