package com.extensivepro.mxl.app.bean;

import java.util.List;

import com.google.gson.annotations.Expose;

public class JsonData
{
    @Expose
    private String name;
    @Expose
    private List list;
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public List getList()
    {
        return list;
    }
    public void setList(List list)
    {
        this.list = list;
    }
    @Override
    public String toString()
    {
        return "JsonData [name=" + name + ", list=" + list + "]";
    }

    
    
}
