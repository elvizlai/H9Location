package com.elvizlai.h9location.entity;

import java.io.Serializable;
import java.util.List;

public class SiteNote implements Serializable
{

    private String endNoteAddress;
    private String endNoteNoteContent;
    private List endNotePicPath;
    private String endNoteThumbPic;
    private String endNoteTime;
    private String isVisited;
    private String noteAddress;
    private String noteId;
    private String noteNoteContent;
    private List notePicPath;
    private String noteThumbPic;
    private String noteTime;
    private String visitCompanyName;


    public String getEndNoteAddress()
    {
        return endNoteAddress;
    }

    public String getEndNoteNoteContent()
    {
        return endNoteNoteContent;
    }

    public List getEndNotePicPath()
    {
        return endNotePicPath;
    }

    public String getEndNoteThumbPic()
    {
        return endNoteThumbPic;
    }

    public String getEndNoteTime()
    {
        return endNoteTime;
    }

    public String getIsVisited()
    {
        return isVisited;
    }

    public String getNoteAddress()
    {
        return noteAddress;
    }

    public String getNoteId()
    {
        return noteId;
    }

    public String getNoteNoteContent()
    {
        return noteNoteContent;
    }

    public List getNotePicPath()
    {
        return notePicPath;
    }

    public String getNoteThumbPic()
    {
        return noteThumbPic;
    }

    public String getNoteTime()
    {
        return noteTime;
    }

    public String getVisitCompanyName()
    {
        return visitCompanyName;
    }

    public void setEndNoteAddress(String s)
    {
        endNoteAddress = s;
    }

    public void setEndNoteNoteContent(String s)
    {
        endNoteNoteContent = s;
    }

    public void setEndNotePicPath(List list)
    {
        endNotePicPath = list;
    }

    public void setEndNoteThumbPic(String s)
    {
        endNoteThumbPic = s;
    }

    public void setEndNoteTime(String s)
    {
        endNoteTime = s;
    }

    public void setIsVisited(String s)
    {
        isVisited = s;
    }

    public void setNoteAddress(String s)
    {
        noteAddress = s;
    }

    public void setNoteId(String s)
    {
        noteId = s;
    }

    public void setNoteNoteContent(String s)
    {
        noteNoteContent = s;
    }

    public void setNotePicPath(List list)
    {
        notePicPath = list;
    }

    public void setNoteThumbPic(String s)
    {
        noteThumbPic = s;
    }

    public void setNoteTime(String s)
    {
        noteTime = s;
    }

    public void setVisitCompanyName(String s)
    {
        visitCompanyName = s;
    }
}
