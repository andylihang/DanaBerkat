package com.cd.dnf.credit.bean;

/**
 * Created by jack on 2018/2/2.
 */

public class CreditNotification {
    private String notifyType;//通知类型
    private Object notifyData;//通知的数据
    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public Object getNotifyData() {
        return notifyData;
    }

    public void setNotifyData(Object notifyData) {
        this.notifyData = notifyData;
    }
}
