package com.cd.dnf.credit.bean;

/**
 * Created by jack on 2018/1/30.
 */
/*   private String phoneNumber;//电话号码 对应的address
    private String phoneName;//电话号码对应的联系人名称 person
    private String body;//短信内容
    private String date;//发送时间
    private String smsType;//接收 或发送
    private String read;//已读 未读*/
public class SmsRecordBean {
    private String content;//短信内容
    private String name;//短信号码对应的名称
    private String phone;//电话号码
    private String time;//发送时间
    private int from;//是发送还是接收 1 发送 0接收
    private int isRead;//是否已读
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
}
