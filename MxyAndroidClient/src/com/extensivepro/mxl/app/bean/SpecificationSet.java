package com.extensivepro.mxl.app.bean;

import java.util.List;

import com.google.gson.annotations.Expose;

public class SpecificationSet extends BaseObject
{

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;

    @Expose
    private String id;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Expose
    private String name;

    @Expose
    private List<SpecificationValueStore> specificationValueStore;

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

}
