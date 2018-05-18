package com.cd.dnf.credit.api;

/**
 * Created by jack on 2018/1/23.
 */

public class CreditApi {
    public final static String API_SERVER="https://api.pinjamplus.com/api/app/";//正式环境
    //public final static String API_SERVER="http://dnftest.iumiao.com/api/api/app/";//测试环境
  //  public final static String API_SERVER="http://dnf.iumiao.com:2711/app/";//测试接口


    public final static String API_FETCH_PICTURE_CODE=API_SERVER+"login/captcha";//获取图片码
    public final static String API_SEND_SMS_CODE=API_SERVER+"login/verifyCode";//发送短信验证码
    public final static String API_REGISTER=API_SERVER+"login/register";//注册
    public final static String API_LOGIN=API_SERVER+"login/phone";//登陆
    public final static String API_FORGET_PASSWORD=API_SERVER+"user/pass";//忘记密码
    public final static String API_UPLOAD_CONTACT=API_SERVER+"user/addrBookUpload";//上传用户联系人
    public final static String API_UPLOAD_CALL_LOG=API_SERVER+"user/callRecordUpload";//上传通话记录
    public final static String API_UPLOAD_SMS_LOG=API_SERVER+"user/smsUpload";//上传短信记录
    public final static String API_FETCH_BANK=API_SERVER+"pay/xendit/bankList";//获取银行数据
    public final static String API_APPLY_BORROW=API_SERVER+"loan/borrow/apply";//申请借款订单
    public final static String API_HANDLE_MINE_BANK=API_SERVER+"user/bankInfo";//处理用户的银行卡  get方式 获取用户所有 post方式
    public final static String API_REPAYMENT_HISTORY=API_SERVER+"pay/info/repaymentList";//还款历史
    //public final static String API_FETCH_VA_BANK=API_SERVER+"pay/xendit/fetchVA";//获取虚拟三大银行
    public final static String API_FETCH_VA_BANK=API_SERVER+"pay/fetchVA";//获取虚拟银行
    public final static String API_FETCH_BCA_BANK=API_SERVER+"pay/bluePay/fetchBCAVA";//获取动态变更的一个银行
    public final static String API_FETCH_BORROW_ORDER=API_SERVER+"loan/history/order";//获取提交的订单
    public final static String API_FETCH_BORROW_ORDER_DETAIL=API_SERVER+"loan/borrow";//获取主页订单详情
    public final static String API_FETCH_UPDATE_TIME=API_SERVER+"user/info/upTime";//获取上传时间
    public final static String API_FETCH_MINE_MESSAGE=API_SERVER+"userMessage/list";//获取用户消息历史
    public final static String API_FETCH_UNREAD_MESSAGE=API_SERVER+"userMessage/unRead";//读取未读信息

    public final static String API_FETCH_CUSTOM_CALL=API_SERVER+"xloan/baseInfo";//获取客服电话号码
    public final static String API_FETCH_CAN_ORDER=API_SERVER+"loan/borrow/remainingDay";//获取申请订单状态


    public final static String API_CHECK_UPDATE=API_SERVER+"app-version/android";//版本检测 platform 版本号
}
