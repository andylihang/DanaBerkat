package com.cd.dnf.credit.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by jack on 2018/2/8.
 * 推送通知Bean
 */

public class NotificationBean{
    //推送的类型 1表示的是 用户订单状态进行了改变
    private String type;
    private String userId;//用户Id
    private String data;//数据
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
