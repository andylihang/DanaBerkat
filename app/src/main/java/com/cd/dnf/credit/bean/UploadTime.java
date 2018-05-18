package com.cd.dnf.credit.bean;

/**
 * Created by jack on 2018/2/6.
 */

public class UploadTime {
    private long smsLastTime;//短信最后上传时间
    private long callRecordLastTime;//通话记录最后上传时间
    public long getSmsLastTime() {
        return smsLastTime;
    }

    public void setSmsLastTime(long smsLastTime) {
        this.smsLastTime = smsLastTime;
    }

    public long getCallRecordLastTime() {
        return callRecordLastTime;
    }

    public void setCallRecordLastTime(long callRecordLastTime) {
        this.callRecordLastTime = callRecordLastTime;
    }
}
