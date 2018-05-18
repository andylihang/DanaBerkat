package com.cd.dnf.credit.bean;

/**
 * Created by jack on 2018/2/23.
 * 消息bean
 */

public class NoticeBean {
    private String content;//内容信息
    private long time;//时间
    private String title;//标题
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
