package com.extensivepro.mxl.app.provider;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class MxlTables implements BaseColumns
{
    
    public static final String AUTHORITY = "com.extensivepro.mxl.provider";
    public static final String CONTENT = "content://";
//    CREATE TABLE Carousel (_id TEXT PRIMARY KEY,title TEXT,imageSrc TEXT,actionParams TEXT,actionType TEXT,modifyDate INTEGER DEFAULT 0,createDate INGEGER DEFAULT 0)

    
    public static class TCarousel
    {
        public static final String TABLE_NAME = "TCarousel";
        public static final Uri CONTENT_URI = Uri.parse(CONTENT+AUTHORITY+"/"+TABLE_NAME);
        public static final String _ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String IMAGE_SRC = "imageSrc";
        public static final String ACTION_PARAMS = "actionParams";
        public static final String ACTION_TYPE = "actionType";
        public static final String MODIFY_DATE = "modifyDate";
        public static final String CREATE_DATE = "createDate";
        private static final String SQL_CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+
        _ID+" INTEGER PRIMARY KEY,"+
        ID+" TEXT,"+
        TITLE+" TEXT,"+
        IMAGE_SRC+" TEXT,"+
        ACTION_PARAMS+" TEXT,"+
        ACTION_TYPE+" TEXT,"+
        MODIFY_DATE+" INTEGER,"+
        CREATE_DATE+" INTEGER);";
        public static void createTable(SQLiteDatabase db)
        {
            db.execSQL(TCarousel.SQL_CREATE_TABLE);
        }
    }
    
    public static class TGoodsCategory
    {
        public static final String TABLE_NAME = "TGoodsCategory";
        public static final Uri CONTENT_URI = Uri.parse(CONTENT+AUTHORITY+"/"+TABLE_NAME);
        public static final String _ID = BaseColumns._ID;
        public static final String CATEGORY_ID = "categoryId";
        public static final String CATEGORY_NAME = "name";
        public static final String META_KEY_WORDS = "metaKeywords";
        public static final String PATH = "path";
        public static final String META_DESCRIPTION = "metaDescription";
        public static final String DISPLAY_IMAGE = "displayImage";
        
        private static final String SQL_CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+
                _ID+" INTEGER PRIMARY KEY,"+
                CATEGORY_NAME+" TEXT,"+
                META_KEY_WORDS+" TEXT,"+
                CATEGORY_ID+" TEXT,"+
                PATH+" TEXT,"+
                META_DESCRIPTION+" TEXT,"+
                DISPLAY_IMAGE+" TEXT);";
                public static void createTable(SQLiteDatabase db)
                {
                    db.execSQL(TGoodsCategory.SQL_CREATE_TABLE);
                }
    }
    
    public static class TGoods
    {
        public static final String TABLE_NAME = "TGoods";
        public static final Uri CONTENT_URI = Uri.parse(CONTENT+AUTHORITY+"/"+TABLE_NAME);
        public static final String _ID = BaseColumns._ID;
        public static final String GOODS_ID = "goodsId";
        public static final String CATEGORY_ID = "categoryId";
        public static final String GOODS_NAME = "goodsName";
        public static final String GOODS_ENGLISH_NAME = "goodsEnglishName";
        public static final String GOODS_IMAGE = "goodsImage";
        public static final String GOODS_NEXT_IMAGE = "goodsNextImage";
        public static final String META_KEY_WORDS = "metaKeywords";
        public static final String META_DESCRIPTION = "metaDescription";
        public static final String PRICE = "price";
        public static final String WEIGHT = "weight";
        public static final String SEND_TIME = "sendTime";
        public static final String PRODUCT_SET = "productSet";
        public static final String IS_SALE_ONLY_ONE = "isSaleOnlyOne";
        private static final String SQL_CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+
                _ID+" INTEGER PRIMARY KEY,"+
                GOODS_ID+" TEXT,"+
                CATEGORY_ID+" TEXT,"+
                GOODS_NAME+" TEXT,"+
                GOODS_ENGLISH_NAME+" TEXT,"+
                GOODS_IMAGE+" TEXT,"+
                META_KEY_WORDS+" TEXT,"+
                META_DESCRIPTION+" TEXT,"+
                PRICE+" INTEGER,"+
                WEIGHT+" INTEGER,"+
                SEND_TIME+" INTEGER,"+
                IS_SALE_ONLY_ONE+" INTEGER DEFAULT 0,"+
                PRODUCT_SET+" TEXT);";
        public static void createTable(SQLiteDatabase db)
        {
            db.execSQL(TGoods.SQL_CREATE_TABLE);
        }
    }
}

