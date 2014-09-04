package com.elvizlai.h9location.entity;

import java.io.Serializable;
import java.util.List;

public class WriteSiteNote implements Serializable
{

    private String companyName;
    private int isSign;
    private boolean isSysn2Diary;
    private String sign;
    private String siteAddress;
    private double siteGPS[];
    private String siteNoteContent;
    private String siteNoteId;
    private List sitePicPath;
    private String userId;


    public String getCompanyName()
    {
        return companyName;
    }

    public int getIsSign()
    {
        return isSign;
    }

    public String getSign()
    {
        return sign;
    }

    public String getSiteAddress()
    {
        return siteAddress;
    }

    public double[] getSiteGPS()
    {
        return siteGPS;
    }

    public String getSiteNoteContent()
    {
        return siteNoteContent;
    }

    public String getSiteNoteId()
    {
        return siteNoteId;
    }

    public List getSitePicPath()
    {
        return sitePicPath;
    }

    public String getUserId()
    {
        return userId;
    }

    public boolean isSysn2Diary()
    {
        return isSysn2Diary;
    }

    public void setCompanyName(String s)
    {
        companyName = s;
    }

    public void setIsSign(int i)
    {
        isSign = i;
    }

    public void setSign(String s)
    {
        sign = s;
    }

    public void setSiteAddress(String s)
    {
        siteAddress = s;
    }

    public void setSiteGPS(double ad[])
    {
        siteGPS = ad;
    }

    public void setSiteNoteContent(String s)
    {
        siteNoteContent = s;
    }

    public void setSiteNoteId(String s)
    {
        siteNoteId = s;
    }

    public void setSitePicPath(List list)
    {
        sitePicPath = list;
    }

    public void setSysn2Diary(boolean flag)
    {
        isSysn2Diary = flag;
    }

    public void setUserId(String s)
    {
        userId = s;
    }
}
