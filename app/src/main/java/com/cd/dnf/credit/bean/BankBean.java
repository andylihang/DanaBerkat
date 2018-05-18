package com.cd.dnf.credit.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by jack on 2018/1/31.
 */

public class BankBean implements Parcelable, Comparable<BankBean> {
    private String bankCode;//银行code
    private String bankName;//银行名称
    private String firstLetter;//首字母

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

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bankCode);
        dest.writeString(this.bankName);
        dest.writeString(this.firstLetter);
    }

    public BankBean() {
    }

    protected BankBean(Parcel in) {
        this.bankCode = in.readString();
        this.bankName = in.readString();
        this.firstLetter = in.readString();
    }

    public static final Creator<BankBean> CREATOR = new Creator<BankBean>() {
        @Override
        public BankBean createFromParcel(Parcel source) {
            return new BankBean(source);
        }

        @Override
        public BankBean[] newArray(int size) {
            return new BankBean[size];
        }
    };

    @Override
    public int compareTo(@NonNull BankBean bankBean) {
        //自定义比较方法，如果认为此实体本身大则返回1，否则返回-1
        if (this.firstLetter.compareTo(bankBean.getFirstLetter())>0) {
            return 1;
        }
        return -1;
    }
}
