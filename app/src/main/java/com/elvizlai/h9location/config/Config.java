package com.elvizlai.h9location.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.elvizlai.h9location.entity.WriteSiteNote;
import com.elvizlai.h9location.util.ApplictionUtil;
import com.elvizlai.h9location.util.CryptUtil;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;


/**
 * Created by elvizlai on 14-4-13.
 */
public class Config {

    private static Config config = new Config();
    private SharedPreferences usrPreference;
    private SharedPreferences sysPreference;

    private Config() {
        usrPreference = ApplictionUtil.getContext().getSharedPreferences("UserInfos", Context.MODE_PRIVATE);
        sysPreference = ApplictionUtil.getContext().getSharedPreferences("SystemInfos", Context.MODE_PRIVATE);
    }

    public static Config getInstance() {
        if (config == null)
            config = new Config();
        return config;
    }

    public void storeWriteSiteNote(WriteSiteNote writeSiteNote) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(writeSiteNote);
            // 将字节流编码成base64的字符窜
            String writeSiteNote_Base64 = new String(Base64.encodeBase64(baos.toByteArray()));
            usrPreference.edit().putString("writeSiteNote", writeSiteNote_Base64).apply();
        } catch (IOException e) {
            // TODO Auto-generated
            e.printStackTrace();
        }
    }

    public WriteSiteNote restoreWriteSiteNote() {
        WriteSiteNote writeSiteNote = null;
        String xx = usrPreference.getString("writeSiteNote", "");
        byte[] base64 = Base64.decodeBase64(xx.getBytes());
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            ObjectInputStream bis = new ObjectInputStream(bais);
            try {
                writeSiteNote = (WriteSiteNote) bis.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writeSiteNote;
    }


    public String getUserId() {
        return usrPreference.getString("UserId", "");
    }

    public void setUserId(String userId) {
        usrPreference.edit().putString("UserId", userId).apply();
    }

    public int getIsVisited() {
        return usrPreference.getInt("IsVisited", 0);
    }

    public void setIsVisited(int isVisited) {
        usrPreference.edit().putInt("IsVisited", isVisited).apply();
    }

    public String getServiceUrl() {
        String defaultServiceUrl = "192.168.1.94/c6/JHSoft.WCF/POSTServiceForAndroid.svc/";
        return sysPreference.getString("ServiceUrl", defaultServiceUrl);
    }

    public void setServiceUrl(String url) {
        sysPreference.edit().putString("ServiceUrl", url).apply();
    }

    public String getAccount() {
        return usrPreference.getString("Account", "");
    }

    public void setAccount(String account) {
        usrPreference.edit().putString("Account", account).apply();
    }

    public String getPassword() {
        return usrPreference.getString("Password", "");
    }

    public void setPassword(String psw) {
        usrPreference.edit().putString("Password", psw).apply();
    }

//    public void setSign() {
//        usrPreference.edit().putString("sign", account).apply();
//    }

    public String getSign() {
        return getAccount() + "$" + CryptUtil.encryptSHA(CryptUtil.decryptPsw(getPassword()));
    }


    public boolean getIsRemPsw() {
        return usrPreference.getBoolean("isRemPsw", false);
    }

    public void setIsRemPsw(boolean isRemPsw) {
        usrPreference.edit().putBoolean("isRemPsw", isRemPsw).apply();
    }

    public boolean getIsAutoLogin() {
        return usrPreference.getBoolean("isAutoLogin", false);
    }

    public void setIsAutoLogin(boolean isAutoLogin) {
        usrPreference.edit().putBoolean("isAutoLogin", isAutoLogin).apply();
    }

    public boolean getIsFirstInstall() {
        return usrPreference.getBoolean("isFirstInstall", true);
    }

    public void setIsFirstInstall(boolean isFirstInstall) {
        usrPreference.edit().putBoolean("isFirstInstall", isFirstInstall).apply();
    }


}
