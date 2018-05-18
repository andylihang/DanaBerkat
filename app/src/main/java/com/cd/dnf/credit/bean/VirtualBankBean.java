package com.cd.dnf.credit.bean;

/**
 * Created by jack on 2018/2/3.
 */

public class VirtualBankBean {
    private String bankCode;//银行code
    private String bankName;//银行名称
    private String cardNumber;//银行卡号
    private String expirationDate;//过期时间
    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
