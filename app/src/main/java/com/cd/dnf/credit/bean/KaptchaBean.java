package com.cd.dnf.credit.bean;

/**
 * Created by jack on 2018/1/23.
 */

public class KaptchaBean {
    private String id;//图片Id
    private String codeImage;//图片Base64的值
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodeImage() {
        return codeImage;
    }

    public void setCodeImage(String codeImage) {
        this.codeImage = codeImage;
    }
}
