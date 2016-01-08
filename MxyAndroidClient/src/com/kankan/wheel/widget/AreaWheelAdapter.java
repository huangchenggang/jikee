/*
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kankan.wheel.widget;

import java.util.List;

import com.extensivepro.mxl.app.bean.Area;

/**
 * Numeric Wheel adapter.
 */
public class AreaWheelAdapter implements WheelAdapter
{

    /** The default min value */
    private List<Area> mContents;

    /**
     * 构造方法
     * 
     * @param strContents
     */
    public AreaWheelAdapter(List<Area> contents)
    {
        mContents = contents;
    }

    public List<Area> getContents()
    {
        return mContents;
    }

    public void setContents(List<Area> contents)
    {
        mContents = contents;
    }

    public String getItem(int index)
    {
        if (index >= 0 && index < getItemsCount())
        {
            return mContents.get(index).getName();
        }
        return null;
    }

    public int getItemsCount()
    {
        return mContents != null ? mContents.size() : 0;
    }

    /**
     * 设置最大的宽度
     */
    public int getMaximumLength()
    {
        int maxLen = 5;
        return maxLen;
    }
}
