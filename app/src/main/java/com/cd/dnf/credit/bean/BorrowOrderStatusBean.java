package com.cd.dnf.credit.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jack on 2018/2/4.
 * 订单状态bean
 */

public class BorrowOrderStatusBean implements Parcelable{
    private String orderId;//订单Id
    private long amount;//贷款金额
    private int status;//状态
    private long applyTime;//申请时间
    private String planId;//
    private String overdueDays;//逾期时间
    private long repaymentTime;//应还款时间
    private BorrowOrderBean applyInfo;//订单详情
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(long applyTime) {
        this.applyTime = applyTime;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(String overdueDays) {
        this.overdueDays = overdueDays;
    }

    public long getRepaymentTime() {
        return repaymentTime;
    }

    public void setRepaymentTime(long repaymentTime) {
        this.repaymentTime = repaymentTime;
    }

    public BorrowOrderBean getApplyInfo() {
        return applyInfo;
    }

    public void setApplyInfo(BorrowOrderBean applyInfo) {
        this.applyInfo = applyInfo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderId);
        dest.writeLong(this.amount);
        dest.writeInt(this.status);
        dest.writeLong(this.applyTime);
        dest.writeString(this.planId);
        dest.writeString(this.overdueDays);
        dest.writeLong(this.repaymentTime);
        dest.writeParcelable(this.applyInfo, flags);
    }

    public BorrowOrderStatusBean() {
    }

    protected BorrowOrderStatusBean(Parcel in) {
        this.orderId = in.readString();
        this.amount = in.readLong();
        this.status = in.readInt();
        this.applyTime = in.readLong();
        this.planId = in.readString();
        this.overdueDays = in.readString();
        this.repaymentTime = in.readLong();
        this.applyInfo = in.readParcelable(BorrowOrderBean.class.getClassLoader());
    }

    public static final Creator<BorrowOrderStatusBean> CREATOR = new Creator<BorrowOrderStatusBean>() {
        @Override
        public BorrowOrderStatusBean createFromParcel(Parcel source) {
            return new BorrowOrderStatusBean(source);
        }

        @Override
        public BorrowOrderStatusBean[] newArray(int size) {
            return new BorrowOrderStatusBean[size];
        }
    };
}
