package com.extensivepro.mxl.app.bean;

import android.content.ContentValues;

import com.extensivepro.mxl.app.provider.MxlTables.TCarousel;
import com.google.gson.annotations.Expose;

public class Carousel extends BaseObject
{

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;

    // "id":"2",
    // "title":"test",
    // "imageSrc":"banner1",
    // "actionParams":"",
    // "actionType":"SALES",
    // "modifyDate":
    // "createDate":
    @Expose
    private String id;

//    @Expose
    private String title;

    @Expose
    private String imageSrc;

//    @Expose
    private String actionParams;

//    @Expose
    private String actionType;

    private long modifyDate;

    private long createDate;
    
    private ContentValues values;

    public Carousel()
    {
        super();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getImageSrc()
    {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc)
    {
        this.imageSrc = imageSrc;
    }

    public String getActionParams()
    {
        return actionParams;
    }

    public void setActionParams(String actionParams)
    {
        this.actionParams = actionParams;
    }

    public String getActionType()
    {
        return actionType;
    }

    public void setActionType(String actionType)
    {
        this.actionType = actionType;
    }

    public long getModifyDate()
    {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate)
    {
        this.modifyDate = modifyDate;
    }

    public long getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(long createDate)
    {
        this.createDate = createDate;
    }

    @Override
    public String toString()
    {
        return "Carousel [id=" + id + ", title=" + title + ", imageSrc="
                + imageSrc + ", actionParams=" + actionParams + ", actionType="
                + actionType + ", modifyDate=" + modifyDate + ", createDate="
                + createDate + "]";
    }

    @Override
    public ContentValues toContentValues()
    {
        if(values == null)
        {
            values = new ContentValues();
        }
        values.clear();
        values.put(TCarousel.ID, id);
        values.put(TCarousel.TITLE, title);
        values.put(TCarousel.IMAGE_SRC, imageSrc);
        values.put(TCarousel.ACTION_PARAMS, actionParams);
        values.put(TCarousel.ACTION_TYPE, actionType);
        values.put(TCarousel.MODIFY_DATE, modifyDate);
        values.put(TCarousel.CREATE_DATE, createDate);
        return values;
    }
    
}
