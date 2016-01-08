package com.extensivepro.mxl.app.bean;

import com.google.gson.annotations.Expose;

public class Pager
{
    @Expose
    private int pageCount;

    @Expose
    private Object result;

    @Expose
    private String order;

    @Expose
    private String orderBy;

    @Expose
    private String totalCount;

    @Expose
    private String pageSize;

    @Expose
    private int pageNumber;

    public int getPageCount()
    {
        return pageCount;
    }

    public void setPageCount(int pageCount)
    {
        this.pageCount = pageCount;
    }

    public Object getResult()
    {
        return result;
    }

    public void setResult(Object result)
    {
        this.result = result;
    }

    public String getOrder()
    {
        return order;
    }

    public void setOrder(String order)
    {
        this.order = order;
    }

    public String getOrderBy()
    {
        return orderBy;
    }

    public void setOrderBy(String orderBy)
    {
        this.orderBy = orderBy;
    }

    public String getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(String totalCount)
    {
        this.totalCount = totalCount;
    }

    public String getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(String pageSize)
    {
        this.pageSize = pageSize;
    }

    public int getPageNumber()
    {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber)
    {
        this.pageNumber = pageNumber;
    }

    @Override
    public String toString()
    {
        return "Pager [pageCount=" + pageCount + ", result=" + result
                + ", order=" + order + ", orderBy=" + orderBy + ", totalCount="
                + totalCount + ", pageSize=" + pageSize + ", pageNumber="
                + pageNumber + "]";
    }
    
    
}
