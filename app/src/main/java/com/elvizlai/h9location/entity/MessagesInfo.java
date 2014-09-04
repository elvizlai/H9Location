package com.elvizlai.h9location.entity;

/**
 * Created by Elvizlai on 14-8-19.
 */
public class MessagesInfo {
    private String mes;
    private int success = 1;

    public String getMes()
    {
        return this.mes;
    }

    public int getSuccess()
    {
        return this.success;
    }

    public void setMes(String paramString)
    {
        this.mes = paramString;
    }

    public void setSuccess(int paramInt)
    {
        this.success = paramInt;
    }
}
