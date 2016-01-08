package com.extensivepro.mxl.app.bean;

import java.io.Serializable;


import android.content.ContentValues;

import com.google.gson.GsonBuilder;

public abstract class BaseObject implements Serializable
{
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;
    
    public ContentValues toContentValues()
    {
        return null;
    }
}
