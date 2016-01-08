package com.extensivepro.mxl.app.share;

import java.util.Comparator;

import com.extensivepro.mxl.app.bean.ShareItem;

public class CompareByLike implements Comparator<ShareItem>
{

    @Override
    public int compare(ShareItem o1, ShareItem o2)
    {
        ShareItem s1=(ShareItem)o1;
        ShareItem s2=(ShareItem)o2;
        Integer i1 =Integer.valueOf(s1.getGood());
        Integer i2 =Integer.valueOf(s2.getGood());
        int num=i2.compareTo(i1);
        return num;
    }

}
