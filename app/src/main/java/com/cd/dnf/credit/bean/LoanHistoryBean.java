package com.cd.dnf.credit.bean;

/**
 * Created by jack on 2018/2/2.
 * 还款历史
 */

public class LoanHistoryBean {
    private long amount;//金额
    private String repayTime;//还款时间
    private String endTime;//到期时间
    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getRepayTime() {
        return repayTime;
    }

    public void setRepayTime(String repayTime) {
        this.repayTime = repayTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
