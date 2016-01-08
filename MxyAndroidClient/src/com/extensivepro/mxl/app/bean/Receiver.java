package com.extensivepro.mxl.app.bean;

import com.google.gson.annotations.Expose;

public class Receiver extends BaseObject
{
    
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;

    @Expose
    private String id;
    
    @Expose
    private String name;

    @Expose
    private String address;

    @Expose
    private String zipCode;

    @Expose
    private String mobile;
    
    @Expose
    private String phone;
    
    @Expose
    private Area areaStore;
    
    @Expose
    private boolean isDefault;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public Area getAreaStore()
    {
        return areaStore;
    }

    public void setAreaStore(Area areaStore)
    {
        this.areaStore = areaStore;
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public void setDefault(boolean isDefault)
    {
        this.isDefault = isDefault;
    }

    
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Receiver(String name, String address, String zipCode, String mobile, Area areaStore, boolean isDefault)
    {
        super();
        this.name = name;
        this.address = address;
        this.zipCode = zipCode;
        this.mobile = mobile;
        this.phone = mobile;
        this.areaStore = areaStore;
        this.isDefault = isDefault;
    }

    @Override
    public String toString()
    {
        return "Receiver [id=" + id + ", name=" + name + ", address=" + address
                + ", zipCode=" + zipCode + ", mobile=" + mobile + ", phone="
                + phone + ", areaStore=" + areaStore + ", isDefault="
                + isDefault + "]";
    }
    
    
}
