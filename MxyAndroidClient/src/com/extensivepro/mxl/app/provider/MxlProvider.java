package com.extensivepro.mxl.app.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.extensivepro.mxl.app.provider.MxlTables.TCarousel;
import com.extensivepro.mxl.app.provider.MxlTables.TGoods;
import com.extensivepro.mxl.app.provider.MxlTables.TGoodsCategory;
import com.extensivepro.mxl.util.Logger;

public class MxlProvider extends ContentProvider
{
    private static final String TAG = MxlProvider.class.getSimpleName();

    private MxlDatabaseHelper mDBHelper;

    private static final UriMatcher mUriMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    /**
     * TCarousel
     */
    private static final int CAROUSEL_ALL = 1;

    private static final int CAROUSEL_ID = 2;
    
    /**
     * TGoodsCategory
     */
    private static final int GOODS_CATEGORY_ALL = 3;
    
    private static final int GOODS_CATEGORY_ID = 4;
    /**
     * TGoods
     */
    private static final int GOODS_ALL = 5;
    
    private static final int GOODS_ID = 6;
    
    
    
    private static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mxlprovider.mxl";

    private static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.mxlprovider.mxl";
    
    private boolean mNeedNotify;
    
    
    /**
     *  TCarousel projection map
     */
    private static final HashMap<String, String> sCarouselProjectionMap;
    static {
        sCarouselProjectionMap = new HashMap<String, String>();
        sCarouselProjectionMap.put(TCarousel._ID, TCarousel._ID);
        sCarouselProjectionMap.put(TCarousel.ID, TCarousel.ID);
        sCarouselProjectionMap.put(TCarousel.TITLE, TCarousel.TITLE);
        sCarouselProjectionMap.put(TCarousel.IMAGE_SRC, TCarousel.IMAGE_SRC);
        sCarouselProjectionMap.put(TCarousel.ACTION_PARAMS, TCarousel.ACTION_PARAMS);
        sCarouselProjectionMap.put(TCarousel.ACTION_TYPE, TCarousel.ACTION_TYPE);
        sCarouselProjectionMap.put(TCarousel.MODIFY_DATE, TCarousel.MODIFY_DATE);
        sCarouselProjectionMap.put(TCarousel.CREATE_DATE, TCarousel.CREATE_DATE);
    }
    
    /**
     *  TCarousel projection map
     */
    private static final HashMap<String, String> sGoodsCategoryProjectionMap;
    static {
        sGoodsCategoryProjectionMap = new HashMap<String, String>();
        sGoodsCategoryProjectionMap.put(TGoodsCategory._ID, TGoodsCategory._ID);
        sGoodsCategoryProjectionMap.put(TGoodsCategory.CATEGORY_NAME, TGoodsCategory.CATEGORY_NAME);
        sGoodsCategoryProjectionMap.put(TGoodsCategory.CATEGORY_ID, TGoodsCategory.CATEGORY_ID);
        sGoodsCategoryProjectionMap.put(TGoodsCategory.PATH, TGoodsCategory.PATH);
        sGoodsCategoryProjectionMap.put(TGoodsCategory.META_DESCRIPTION, TGoodsCategory.META_DESCRIPTION);
        sGoodsCategoryProjectionMap.put(TGoodsCategory.DISPLAY_IMAGE, TGoodsCategory.DISPLAY_IMAGE);
    }
    
    /**
     *  TGoods projection map
     */
    private static final HashMap<String, String> sGoodsProjectionMap;
    static {
        sGoodsProjectionMap = new HashMap<String, String>();
        sGoodsProjectionMap.put(TGoods._ID, TGoods._ID);
        sGoodsProjectionMap.put(TGoods.GOODS_ID, TGoods.GOODS_ID);
        sGoodsProjectionMap.put(TGoods.CATEGORY_ID, TGoods.CATEGORY_ID);
        sGoodsProjectionMap.put(TGoods.GOODS_NAME, TGoods.GOODS_NAME);
        sGoodsProjectionMap.put(TGoods.GOODS_ENGLISH_NAME, TGoods.GOODS_ENGLISH_NAME);
        sGoodsProjectionMap.put(TGoods.GOODS_IMAGE, TGoods.GOODS_IMAGE);
        sGoodsProjectionMap.put(TGoods.META_KEY_WORDS, TGoods.META_KEY_WORDS);
        sGoodsProjectionMap.put(TGoods.META_DESCRIPTION, TGoods.META_DESCRIPTION);
        sGoodsProjectionMap.put(TGoods.PRICE, TGoods.PRICE);
        sGoodsProjectionMap.put(TGoods.WEIGHT, TGoods.WEIGHT);
        sGoodsProjectionMap.put(TGoods.SEND_TIME, TGoods.SEND_TIME);
        sGoodsProjectionMap.put(TGoods.PRODUCT_SET, TGoods.PRODUCT_SET);
        sGoodsProjectionMap.put(TGoods.IS_SALE_ONLY_ONE, TGoods.IS_SALE_ONLY_ONE);
    }
    

    /**
     * initial URI matcher
     */
    static
    {
        /**
         * TCarousel
         */
        mUriMatcher.addURI(MxlTables.AUTHORITY, MxlTables.TCarousel.TABLE_NAME,
                CAROUSEL_ALL);
        mUriMatcher.addURI(MxlTables.AUTHORITY, MxlTables.TCarousel.TABLE_NAME
                + "/#", CAROUSEL_ID);
        /**
         * TGoodsCategory
         */
        mUriMatcher.addURI(MxlTables.AUTHORITY, MxlTables.TGoodsCategory.TABLE_NAME,
                GOODS_CATEGORY_ALL);
        mUriMatcher.addURI(MxlTables.AUTHORITY, MxlTables.TGoodsCategory.TABLE_NAME
                + "/#", GOODS_CATEGORY_ID);
        /**
         * TGoods
         */
        mUriMatcher.addURI(MxlTables.AUTHORITY, MxlTables.TGoods.TABLE_NAME,
                GOODS_ALL);
        mUriMatcher.addURI(MxlTables.AUTHORITY, MxlTables.TGoodsCategory.TABLE_NAME
                + "/#", GOODS_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int count;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri))
        {
            case GOODS_CATEGORY_ALL:
                count = db.delete(MxlTables.TGoodsCategory.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case GOODS_ALL:
                count = db.delete(MxlTables.TGoods.TABLE_NAME,
                        selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("unknow xly uri:" + uri);
        }
        if(count > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public String getType(Uri uri)
    {
        switch (mUriMatcher.match(uri))
        {
            case CAROUSEL_ALL:
            case GOODS_CATEGORY_ALL:
            case GOODS_ALL:
                return CONTENT_TYPE;
            case CAROUSEL_ID:
            case GOODS_CATEGORY_ID:
            case GOODS_ID:
                return CONTENT_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("unknow xly uri:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        Logger.d(TAG, "insert()[uri:" + uri+"]");
        int match = mUriMatcher.match(uri);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        // int index = MxlTables.TCarousel._ID;
        Cursor c = null;
        switch (match)
        {
            case CAROUSEL_ALL:
                String id = (String) values.get(MxlTables.TCarousel.ID);
                c = query(TCarousel.CONTENT_URI, null, TCarousel.ID
                        + "='" + id + "'", null, null);
                if (c.getCount() == 0)
                {
                    long rowId = db.insert(MxlTables.TCarousel.TABLE_NAME,
                            null, values);
                    if (rowId > 0)
                    {
                        uri = ContentUris.withAppendedId(
                                MxlTables.TCarousel.CONTENT_URI, rowId);
                    }
                }
                break;
            case GOODS_CATEGORY_ALL:
                String categoryId = (String) values.get(MxlTables.TGoodsCategory.CATEGORY_ID);
                c = query(TGoodsCategory.CONTENT_URI, null, TGoodsCategory.CATEGORY_ID
                        + "='" + categoryId + "'", null, null);
                if (c.getCount() == 0)
                {
                    long rowId = db.insert(MxlTables.TGoodsCategory.TABLE_NAME,
                            null, values);
                    if (rowId > 0)
                    {
                        uri = ContentUris.withAppendedId(
                                MxlTables.TGoodsCategory.CONTENT_URI, rowId);
                    }
                }
                break;
            case GOODS_ALL:
                String goodsId = (String) values.get(MxlTables.TGoods.GOODS_ID);
                c = query(TGoods.CONTENT_URI, null, TGoods.GOODS_ID
                        + "='" + goodsId + "'", null, null);
                if (c.getCount() == 0)
                {
                    long rowId = db.insert(MxlTables.TGoods.TABLE_NAME,
                            null, values);
                    if (rowId > 0)
                    {
                        uri = ContentUris.withAppendedId(
                                MxlTables.TGoods.CONTENT_URI, rowId);
                    }
                }
                break;
            default:
                break;
        }
        if (mNeedNotify && uri != null)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return uri;
    }

    @Override
    public boolean onCreate()
    {
        mDBHelper = MxlDatabaseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder)
    {
        Logger.d(TAG, "query()[uri:" + uri + ",projection:" + projection
                + ",selection:" + selection + ",selectionArgs:" + selectionArgs
                + ",sortOrder" + sortOrder + "]");
        int match = mUriMatcher.match(uri);
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final SelectionBuilder selectionBuilder = new SelectionBuilder(selection);
        // int index = MxlTables.TCarousel._ID;
        switch (match)
        {
            case CAROUSEL_ALL:
                qb.setTables(TCarousel.TABLE_NAME);
                qb.setProjectionMap(sCarouselProjectionMap);
                break;
            case CAROUSEL_ID:
                break;
            case GOODS_CATEGORY_ALL:
                qb.setTables(TGoodsCategory.TABLE_NAME);
                qb.setProjectionMap(sGoodsCategoryProjectionMap);
                break;
            case GOODS_ALL:
                qb.setTables(TGoods.TABLE_NAME);
                qb.setProjectionMap(sGoodsProjectionMap);
                break;
        }
        final Cursor c = qb.query(db, projection, selectionBuilder.build(), selectionArgs, null,
                null, sortOrder, null);
        if (c != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        int numValues = 0;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        try
        {
            mNeedNotify = false;
            numValues = values.length;
            for(int i=0;i<numValues;i++)
            {
                insert(uri, values[i]);
            }
            db.setTransactionSuccessful();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
            db.endTransaction();
            mNeedNotify = true;
        }
        if (mNeedNotify && uri != null)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numValues;
    }

}
