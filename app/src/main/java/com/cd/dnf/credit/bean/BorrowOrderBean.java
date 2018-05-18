package com.cd.dnf.credit.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by jack on 2018/1/30.
 * 提交订单
 */

public class BorrowOrderBean extends DataSupport implements Parcelable{
    private long id;//
    private String planId;//借款的id  根据第一个界面来 会有4个 先写死
    private String deviceId;//设备id
    private String userName;//用户全名
    private int education=-1;//教育 传的是0 1 2 3 4 5
    private int work=-1;//工作
    private String idCardNumber;//身份证号
    private int gender=-1;//性别 传0 1
    private String province="";//省
    private String city="";//市
    private String area="";//区
    private String addressDetail;//地址详情
    private String company;//公司名称
    private String companyPhone;//公司电话
    private String companyAddress;//公司地址
    private int salary=-1;//月收入
    private String salaryDay;//发薪日
    private int relationship1=-1;//关系1
    private String contactsName1;//第一联系人
    private String contactsPhone1;//第一联系人电话
    private int relationship2=-1;//关系2
    private String contactsName2;//第二联系人
    private String contactsPhone2;//第二联系人电话
    private String idCardPhotoFront;//身份证正面照
    private String peopleIdCardPhoto;//手持身份证照片
    private String workCardPhoto;//工牌照片
    private String payrollPhoto;//工资条照片
    private String bankName;//银行名称
    private String bankAccount;//银行卡号
    private String bankCode;//银行编码
    private String bankUserName;//完整的账户持有人的名字
    private String dutyParagraph;//税号
    private int goal=-1;//贷款目的
    private String userId;//用户id 用于本地存储
    private String imeis;//imeis序列号
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getEducation() {
        return education;
    }

    public void setEducation(int education) {
        this.education = education;
    }

    public int getWork() {
        return work;
    }

    public void setWork(int work) {
        this.work = work;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getSalaryDay() {
        return salaryDay;
    }

    public void setSalaryDay(String salaryDay) {
        this.salaryDay = salaryDay;
    }

    public int getRelationship1() {
        return relationship1;
    }

    public void setRelationship1(int relationship1) {
        this.relationship1 = relationship1;
    }

    public String getContactsName1() {
        return contactsName1;
    }

    public void setContactsName1(String contactsName1) {
        this.contactsName1 = contactsName1;
    }

    public String getContactsPhone1() {
        return contactsPhone1;
    }

    public void setContactsPhone1(String contactsPhone1) {
        this.contactsPhone1 = contactsPhone1;
    }

    public int getRelationship2() {
        return relationship2;
    }

    public void setRelationship2(int relationship2) {
        this.relationship2 = relationship2;
    }

    public String getContactsName2() {
        return contactsName2;
    }

    public void setContactsName2(String contactsName2) {
        this.contactsName2 = contactsName2;
    }

    public String getContactsPhone2() {
        return contactsPhone2;
    }

    public void setContactsPhone2(String contactsPhone2) {
        this.contactsPhone2 = contactsPhone2;
    }

    public String getIdCardPhotoFront() {
        return idCardPhotoFront;
    }

    public void setIdCardPhotoFront(String idCardPhotoFront) {
        this.idCardPhotoFront = idCardPhotoFront;
    }

    public String getPeopleIdCardPhoto() {
        return peopleIdCardPhoto;
    }

    public void setPeopleIdCardPhoto(String peopleIdCardPhoto) {
        this.peopleIdCardPhoto = peopleIdCardPhoto;
    }

    public String getWorkCardPhoto() {
        return workCardPhoto;
    }

    public void setWorkCardPhoto(String workCardPhoto) {
        this.workCardPhoto = workCardPhoto;
    }

    public String getPayrollPhoto() {
        return payrollPhoto;
    }

    public void setPayrollPhoto(String payrollPhoto) {
        this.payrollPhoto = payrollPhoto;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankUserName() {
        return bankUserName;
    }

    public void setBankUserName(String bankUserName) {
        this.bankUserName = bankUserName;
    }

    public String getDutyParagraph() {
        return dutyParagraph;
    }

    public void setDutyParagraph(String dutyParagraph) {
        this.dutyParagraph = dutyParagraph;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImeis() {
        return imeis;
    }

    public void setImeis(String imeis) {
        this.imeis = imeis;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.planId);
        dest.writeString(this.deviceId);
        dest.writeString(this.userName);
        dest.writeInt(this.education);
        dest.writeInt(this.work);
        dest.writeString(this.idCardNumber);
        dest.writeInt(this.gender);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.area);
        dest.writeString(this.addressDetail);
        dest.writeString(this.company);
        dest.writeString(this.companyPhone);
        dest.writeString(this.companyAddress);
        dest.writeInt(this.salary);
        dest.writeString(this.salaryDay);
        dest.writeInt(this.relationship1);
        dest.writeString(this.contactsName1);
        dest.writeString(this.contactsPhone1);
        dest.writeInt(this.relationship2);
        dest.writeString(this.contactsName2);
        dest.writeString(this.contactsPhone2);
        dest.writeString(this.idCardPhotoFront);
        dest.writeString(this.peopleIdCardPhoto);
        dest.writeString(this.workCardPhoto);
        dest.writeString(this.payrollPhoto);
        dest.writeString(this.bankName);
        dest.writeString(this.bankAccount);
        dest.writeString(this.bankCode);
        dest.writeString(this.bankUserName);
        dest.writeString(this.dutyParagraph);
        dest.writeInt(this.goal);
        dest.writeString(this.userId);
        dest.writeString(this.imeis);
    }

    public BorrowOrderBean() {
    }

    protected BorrowOrderBean(Parcel in) {
        this.id = in.readLong();
        this.planId = in.readString();
        this.deviceId = in.readString();
        this.userName = in.readString();
        this.education = in.readInt();
        this.work = in.readInt();
        this.idCardNumber = in.readString();
        this.gender = in.readInt();
        this.province = in.readString();
        this.city = in.readString();
        this.area = in.readString();
        this.addressDetail = in.readString();
        this.company = in.readString();
        this.companyPhone = in.readString();
        this.companyAddress = in.readString();
        this.salary = in.readInt();
        this.salaryDay = in.readString();
        this.relationship1 = in.readInt();
        this.contactsName1 = in.readString();
        this.contactsPhone1 = in.readString();
        this.relationship2 = in.readInt();
        this.contactsName2 = in.readString();
        this.contactsPhone2 = in.readString();
        this.idCardPhotoFront = in.readString();
        this.peopleIdCardPhoto = in.readString();
        this.workCardPhoto = in.readString();
        this.payrollPhoto = in.readString();
        this.bankName = in.readString();
        this.bankAccount = in.readString();
        this.bankCode = in.readString();
        this.bankUserName = in.readString();
        this.dutyParagraph = in.readString();
        this.goal = in.readInt();
        this.userId = in.readString();
        this.imeis = in.readString();
    }

    public static final Creator<BorrowOrderBean> CREATOR = new Creator<BorrowOrderBean>() {
        @Override
        public BorrowOrderBean createFromParcel(Parcel source) {
            return new BorrowOrderBean(source);
        }

        @Override
        public BorrowOrderBean[] newArray(int size) {
            return new BorrowOrderBean[size];
        }
    };
}
