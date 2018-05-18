package com.cd.dnf.credit.bean;

/**
 * Created by jack on 2018/1/30.
 */

public class CallRecordBean {
    private String name;//呼叫人姓名
    private String phone;//呼叫人电话
    private String callStartTime;//呼叫开始时间
    private String callTimeLong;//持续时间 通话时长(秒为单位)
    private int isCall;//呼叫类型 呼入 呼出  是否是呼出 1是呼入  0是呼出
    private int status;//0.未接；1.已接 2拒绝 3:未知
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

    public String getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime(String callStartTime) {
        this.callStartTime = callStartTime;
    }

    public String getCallTimeLong() {
        return callTimeLong;
    }

    public void setCallTimeLong(String callTimeLong) {
        this.callTimeLong = callTimeLong;
    }

    public int getIsCall() {
        return isCall;
    }

    public void setIsCall(int isCall) {
        this.isCall = isCall;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
