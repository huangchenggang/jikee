package com.extensivepro.mxl.app.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.extensivepro.mxl.util.Logger;

public class MxlDatabaseHelper extends SQLiteOpenHelper
{
    private static final String TAG = MxlDatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "Mxl.db";
    private static final int DATABASE_VERSION = 1;
    
    private static MxlDatabaseHelper mInstance;
    
    private MxlDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /* package */ static synchronized MxlDatabaseHelper getInstance(Context context)
    {
        if(mInstance == null)
        {
            mInstance = new MxlDatabaseHelper(context);
        }
        return mInstance;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Logger.d(TAG, "onCreate()[access]");
        MxlTables.TCarousel.createTable(db);
        MxlTables.TGoodsCategory.createTable(db);
        MxlTables.TGoods.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Logger.d(TAG, "onUpgrade()[oldVersion:" + oldVersion + ",newVersion:"
                + newVersion + "]");
        // TODO Auto-generated method stub
    }

}
