package com.cd.dnf.credit.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jack on 2018/2/2.
 */

public class BankInfoBean implements Parcelable{
    private String id;
    private String bankCode;//银行code
    private String bankName;//银行名称
    private String cardNumber;//卡号
    private String cardUsername;//开户名
    private String userId;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getCardUsername() {
        return cardUsername;
    }

    public void setCardUsername(String cardUsername) {
        this.cardUsername = cardUsername;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.bankCode);
        dest.writeString(this.bankName);
        dest.writeString(this.cardNumber);
        dest.writeString(this.cardUsername);
        dest.writeString(this.userId);
    }

    public BankInfoBean() {
    }

    protected BankInfoBean(Parcel in) {
        this.id = in.readString();
        this.bankCode = in.readString();
        this.bankName = in.readString();
        this.cardNumber = in.readString();
        this.cardUsername = in.readString();
        this.userId = in.readString();
    }

    public static final Creator<BankInfoBean> CREATOR = new Creator<BankInfoBean>() {
        @Override
        public BankInfoBean createFromParcel(Parcel source) {
            return new BankInfoBean(source);
        }

        @Override
        public BankInfoBean[] newArray(int size) {
            return new BankInfoBean[size];
        }
    };
}
