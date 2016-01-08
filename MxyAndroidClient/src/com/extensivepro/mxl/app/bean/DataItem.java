package com.extensivepro.mxl.app.bean;

public class DataItem
{

    private String name;

    private int path;
    
    private String gourp;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getPath()
    {
        return path;
    }

    public void setPath(int path)
    {
        this.path = path;
    }

    public String getGourp()
    {
        return gourp;
    }

    public void setGourp(String gourp)
    {
        this.gourp = gourp;
    }

    public DataItem(String name, int path, String gourp)
    {
        super();
        this.name = name;
        this.path = path;
        this.gourp = gourp;
    }

    public DataItem(String name, int path)
    {
        super();
        this.name = name;
        this.path = path;
    }

   
    
   
    
   


}
