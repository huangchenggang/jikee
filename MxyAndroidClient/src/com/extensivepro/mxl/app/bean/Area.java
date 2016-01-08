package com.extensivepro.mxl.app.bean;

import java.util.List;

import com.google.gson.annotations.Expose;

public class Area extends BaseObject
{
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;

    @Expose
    private String id;

    @Expose
    private String name;

    @Expose
    private String path;

    @Expose
    private List<Area> children;

    @Expose
    private int grade;

    @Expose
    private String displayName;

    @Expose
    private Object orderList;

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

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public List<Area> getChildren()
    {
        return children;
    }

    public void setChildren(List<Area> children)
    {
        this.children = children;
    }

    public int getGrade()
    {
        return grade;
    }

    public void setGrade(int grade)
    {
        this.grade = grade;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public Object getOrderList()
    {
        return orderList;
    }

    public void setOrderList(Object orderList)
    {
        this.orderList = orderList;
    }

    @Override
    public String toString()
    {
        return "Area [id=" + id + ", name=" + name + ", path=" + path
                + ", children=" + children + ", grade=" + grade
                + ", displayName=" + displayName + ", orderList=" + orderList
                + "]";
    }
}
