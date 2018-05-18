package com.cd.dnf.credit.util;

/**
 * Created by jack on 2018/2/2.
 * 通知类型
 */

public class CreditNotifyType {
    public static String NOTIFY_BORROW_ORDER_APPLY_OK="notify_borrow_order_apply_ok";//贷款订单提交成功
    public static String NOTIFY_BIND_CARD_CHANGE="notify_bind_card_change";//通知绑定的卡进行了添加或者修改
    public static String NOTIFY_REGISTER_OK="notify_register_ok";//注册成功 直接登陆成功
    public static String NOTIFY_LOGIN_OK="notify_login_ok";//登陆成功 直接进行了
    public static String NOTIFY_LOGIN_OUT="notify_login_out";//退出登陆
    public static String NOTIFY_JPUSH_ORDER_STATUS_CHANGE="notify_jpush_order_status_change";//推送订单状态改变
    public static String NOTIFY_JPUSH_CHANGE="notify_jpush_change";//收到推送信息
}
