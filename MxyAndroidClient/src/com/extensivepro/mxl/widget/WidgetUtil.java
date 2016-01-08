package com.extensivepro.mxl.widget;

import android.content.Context;

import com.extensivepro.mxl.app.client.ClientManager;

public class WidgetUtil
{
//    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        if(listView == null)
//        {
//            return;
//        }
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            return;
//        }
//        int totalHeight = 0;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//           View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//        if(listAdapter.getCount() > 0)
//        {
//            totalHeight += (totalHeight/listAdapter.getCount())/2;
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight
//                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        listView.setLayoutParams(params);
//    }
    
    public static int getDispFullImageHeight(Context context,int bmpHeight)
    {
        return (ClientManager.getInstance().getScreenHeight()*bmpHeight)/960;
    }
    
}   
