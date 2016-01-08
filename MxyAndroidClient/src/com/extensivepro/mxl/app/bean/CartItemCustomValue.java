package com.extensivepro.mxl.app.bean;

import java.util.List;

import com.google.gson.annotations.Expose;

public class CartItemCustomValue extends BaseObject
{

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;

    @Expose
    private List<String> images;

    @Expose
    private String specificationId;

    @Expose
    private Templates template;

    public List<String> getImages()
    {
        return images;
    }

    public void setImages(List<String> images)
    {
        this.images = images;
    }

    public String getSpecificationId()
    {
        return specificationId;
    }

    public void setSpecificationId(String specificationId)
    {
        this.specificationId = specificationId;
    }

    public Templates getTemplate()
    {
        return template;
    }

    public void setTemplate(Templates template)
    {
        this.template = template;
    }

    @Override
    public String toString()
    {
        return "CartItemCustomValue [images=" + images + ", specificationId="
                + specificationId + ", template=" + template + "]";
    }

    
}
