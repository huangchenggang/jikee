package com.extensivepro.mxl.app.share;

import java.util.Comparator;

import com.extensivepro.mxl.app.bean.ShareItem;

public class CompareByDate implements Comparator<ShareItem>
{

    @Override
    public int compare(ShareItem o1, ShareItem o2)
    {
        int num=0;
        ShareItem s1=(ShareItem)o1;
        ShareItem s2=(ShareItem)o2;
        long long1 =s1.getCreateDate().getTime();
        long long2 =s2.getCreateDate().getTime();
        
        if(long1>long2){
            num=-1;
        }else{
            num=1;
        }
        return num;
    }

}
