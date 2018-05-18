package com.cd.dnf.credit.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by jack on 2018/2/13.
 * 订单的操作后的界面  比如 如果 是从失败回到了操作状态  那么就不需要显示为失败状态  显示到第一个页面
 */

public class BorrowOperateBean extends DataSupport{
    private String userId;//用户Id
    private int orderStatus;//订单的状态
    private String orderId;//订单Id
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
