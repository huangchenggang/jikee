package com.extensivepro.mxl.app.bean;

import java.io.File;

import com.extensivepro.mxl.util.Const;
import com.google.gson.annotations.Expose;

public class FreeSale extends BaseObject
{
    @Expose
    private String imageSrc;

    @Expose
    private String saleId;

    @Expose
    private String buttonText;

    public String getImageSrc()
    {
        return Const.BASE_URI+imageSrc;
    }

    public void setImageSrc(String imageSrc)
    {
        this.imageSrc = imageSrc;
    }

    public String getSaleId()
    {
        return saleId;
    }

    public void setSaleId(String saleId)
    {
        this.saleId = saleId;
    }

    public String getButtonText()
    {
        return buttonText;
    }

    public void setButtonText(String buttonText)
    {
        this.buttonText = buttonText;
    }

    @Override
    public String toString()
    {
        return "FreeSale [imageSrc=" + imageSrc + ", saleId=" + saleId
                + ", buttonText=" + buttonText + "]";
    }
    
    
}
