package com.cd.dnf.credit.interfaces;

import com.cd.dnf.credit.bean.KaptchaBean;

/**
 * Created by wuwenliang on 2018/1/24.
 */

public interface KaptchaInterface {
    public void inputKaptcha(String kaptcha,KaptchaBean kaptchaBean);//输入的图片码
    public void refreshKaptcha();//刷新图片
}
