package com.extensivepro.mxl.app.bean;

import java.text.SimpleDateFormat;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;

public class Date extends BaseObject
{

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = 1L;

//    @Expose
    private int nanos;

    @Expose
    private long time;

//    @Expose
    private int minutes;

//    @Expose
    private int seconds;

//    @Expose
    private int hours;

//    @Expose
    private int month;

//    @Expose
    private int timezoneOffset;

//    @Expose
    private int year;

//    @Expose
    private int day;

//    @Expose
    private int date;

    public int getNanos()
    {
        return nanos;
    }

    public void setNanos(int nanos)
    {
        this.nanos = nanos;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public int getMinutes()
    {
        return minutes;
    }

    public void setMinutes(int minutes)
    {
        this.minutes = minutes;
    }

    public int getSeconds()
    {
        return seconds;
    }

    public void setSeconds(int seconds)
    {
        this.seconds = seconds;
    }

    public int getHours()
    {
        return hours;
    }

    public void setHours(int hours)
    {
        this.hours = hours;
    }

    public int getMonth()
    {
        return month;
    }

    public void setMonth(int month)
    {
        this.month = month;
    }

    public int getTimezoneOffset()
    {
        return timezoneOffset;
    }

    public void setTimezoneOffset(int timezoneOffset)
    {
        this.timezoneOffset = timezoneOffset;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public int getDay()
    {
        return day;
    }

    public void setDay(int day)
    {
        this.day = day;
    }

    public int getDate()
    {
        return date;
    }

    public void setDate(int date)
    {
        this.date = date;
    }

    @Override
    public String toString()
    {
        return "Date [nanos=" + nanos + ", time=" + time + ", minutes="
                + minutes + ", seconds=" + seconds + ", hours=" + hours
                + ", month=" + month + ", timezoneOffset=" + timezoneOffset
                + ", year=" + year + ", day=" + day + ", date=" + date + "]";
    }

    @Override
    public ContentValues toContentValues()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getGSM8Date()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new java.util.Date(time));
    }
}
