package com.cd.dnf.credit.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.bean.BorrowOrderBean;


public class AppPreferences {
    public static final String USER_JSON = "userjson";
    public static final String USER_TOKEN = "token";
    public static final String USER_ID="userId";
    public static final String USER_PHONE="userPhone";
    public static final String USER_BORROW_ORDER = "borrowOrder";
    public static final String CUSTOM_CALL="customCall";//客服电话
    private Context context;

    public AppPreferences(Context context) {
        this.context = context;
    }


    public void save(String name, String value) {
        SharedPreferences sp = getSettingsPreference();
        Editor editor = sp.edit();
        editor.putString(name, value);
        editor.commit();
    }

/*    public BorrowOrderBean getBorrowOrder() {
        SharedPreferences sp = getSettingsPreference();
        String borrowOrder = sp.getString(USER_BORROW_ORDER, "");
        if (!TextUtils.isEmpty(borrowOrder)) {
            BorrowOrderBean orderBean = JSON.parseObject(borrowOrder, BorrowOrderBean.class);
            return orderBean;
        }
        return new BorrowOrderBean();
    }*/

    public boolean getBoolean(String name) {
        SharedPreferences sp = getSettingsPreference();
        return sp.getBoolean(name, true);
    }


    public String get(String name) {
        SharedPreferences sp = getSettingsPreference();
        return sp.getString(name, null);
    }

    public String get(String name, String defaultValue) {
        SharedPreferences sp = getSettingsPreference();
        if(sp!=null){
            return sp.getString(name, defaultValue);
        }
        return "";

    }


    private SharedPreferences getSettingsPreference() {
        SharedPreferences sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sp;
    }


}
