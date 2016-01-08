package com.extensivepro.mxl.app.bean;

import java.util.List;

import com.google.gson.annotations.Expose;

public class Group extends BaseObject
{

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;

    @Expose
    String id;

    @Expose
    List<Templates> templates;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public List<Templates> getTemplates()
    {
        return templates;
    }

    public void setTemplates(List<Templates> templates)
    {
        this.templates = templates;
    }

}
