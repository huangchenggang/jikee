package com.extensivepro.mxl.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.extensivepro.mxl.app.bean.DepositCard;
import com.extensivepro.mxl.app.bean.Area;
import com.extensivepro.mxl.app.bean.Carousel;
import com.extensivepro.mxl.app.bean.CartItem;
import com.extensivepro.mxl.app.bean.Goods;
import com.extensivepro.mxl.app.bean.GoodsCategory;
import com.extensivepro.mxl.app.bean.ImageStore;
import com.extensivepro.mxl.app.bean.Order;
import com.extensivepro.mxl.app.bean.PaymentConfig;
import com.extensivepro.mxl.app.bean.Receiver;
import com.extensivepro.mxl.app.bean.ShareItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonUtil
{
    private static final String TAG = JsonUtil.class.getSimpleName();

    @SuppressWarnings("unchecked")
    public static List<?> jsonToList(String json,Class<?> clazz,String jsonListName)
    {
        List<Object> results = new ArrayList<Object>();
        if (json == null)
            return results;
        json = getJsonListStr(json, jsonListName);
        if (!json.startsWith("["))
        {
            json = "[" + json;
        }
        if (!json.endsWith("]"))
        {
            json = json + "]";
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = gsonBuilder.create();
        try
        {
            TypeToken<?> token = getToken(clazz);
            if(token == null)
            {
                results = (List<Object>) gson.fromJson(json, clazz);
            }
            else 
            {
                results = gson.fromJson(json, token.getType());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Logger.d(TAG, "jsonToList()[results:" + results + "]");
        return results;
    }
    
    public static Object jsonToObject(String json, Class<?> clazz,
            String jsonObjName)
    {
        Gson gson = null;
        Object obj = null;
        try
        {
            String result = json;
            if (!TextUtils.isEmpty(jsonObjName))
            {
                JSONObject jsonObj = new JSONObject(json);
                result = jsonObj.getString(jsonObjName);
            }
            GsonBuilder builder = new GsonBuilder();
            builder.excludeFieldsWithoutExposeAnnotation();
            gson = builder.create();
            obj = gson.fromJson(result, clazz);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return obj;

    }
    
    private static String getJsonListStr(String json,String listName)
    {
        String result = "";
        if(TextUtils.isEmpty(json))
        {
            return result;
        }
        try
        {
            if(TextUtils.isEmpty(listName))
            {
                result = json;
            }
            else 
            {
                JSONObject jsonObject = new JSONObject(json);
                result = jsonObject.getJSONArray(listName).toString();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    private static  TypeToken<?> getToken(Class<?> clazz)
    {
        if (clazz.getName().equals(Carousel.class.getName()))
        {
            return new TypeToken<List<Carousel>>() {
            };
        }
        if (clazz.getName().equals(GoodsCategory.class.getName()))
        {
            return new TypeToken<List<GoodsCategory>>() {
            };
        }
        if (clazz.getName().equals(Goods.class.getName()))
        {
            return new TypeToken<List<Goods>>() {
            };
        }
        if (clazz.getName().equals(ImageStore.class.getName()))
        {
            return new TypeToken<List<ImageStore>>() {
            };
        }
        if (clazz.getName().equals(Area.class.getName()))
        {
            return new TypeToken<List<Area>>() {
            };
        }
        if (clazz.getName().equals(Receiver.class.getName()))
        {
            return new TypeToken<List<Receiver>>() {
            };
        }
        if (clazz.getName().equals(CartItem.class.getName()))
        {
            return new TypeToken<List<CartItem>>() {
            };
        }
        if (clazz.getName().equals(PaymentConfig.class.getName()))
        {
            return new TypeToken<List<PaymentConfig>>() {
            };
        }
        if (clazz.getName().equals(Order.class.getName()))
        {
            return new TypeToken<List<Order>>() {
            };
        }
        if (clazz.getName().equals(ShareItem.class.getName()))
        {
            return new TypeToken<List<ShareItem>>() {
            };
        }
        if (clazz.getName().equals(DepositCard.class.getName()))
        {
            return new TypeToken<List<DepositCard>>() {
            };
        }
        return null;
    }
    
    
    public static String beanToJson(Object object)
    {
        if(object == null)
        {
            return "{}";
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }
}
