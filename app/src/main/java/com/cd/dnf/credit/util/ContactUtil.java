package com.cd.dnf.credit.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.CallLog.Calls;
import android.provider.Telephony;
import android.text.TextUtils;

import com.cd.dnf.credit.bean.CallRecordBean;
import com.cd.dnf.credit.bean.ContactBean;
import com.cd.dnf.credit.bean.SmsRecordBean;
import com.cd.dnf.credit.bean.UploadTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 2018/1/30.
 */

public class ContactUtil {
    //获取联系人列表
    public static List<ContactBean> fetchContact(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        //获取所有人的id
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, new String[]{"_id"}, null, null, null);
        List<ContactBean> contactSource = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    int contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);//获取 id 所在列的索引
                    String contactId = cursor.getString(contactIdIndex);//联系人id
                    //ContactBean contactBean = getContactInfo(contentResolver, contactId);
                    List<ContactBean> contact = getContactInfo(contentResolver, contactId);
                    contactSource.addAll(contact);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return contactSource;
    }

    private static List<ContactBean> getContactInfo(final ContentResolver contentResolver, String contactId) {
        List<ContactBean> contactSource = new ArrayList<>();
        Cursor contactsCursor = null;
        try {
            contactsCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,//注意这个uri
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,//contactId 是上面提到过的联系人id
                    null, null);
            String name = "";
            String phoneResult = "";
            if (contactsCursor != null && contactsCursor.moveToFirst()) {
                int nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);//获取 名字 所在列的索引
                name = contactsCursor.getString(nameIndex);//联系人名字
                // 遍历所有的电话号码
                for (; !contactsCursor.isAfterLast(); contactsCursor.moveToNext()) {
                    int index = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    //int typeindex = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    //int phone_type = contactsCursor.getInt(typeindex);
   /*             System.out.println("---wuwenliangJJPMMMBBGHH::phone_type is is is::"+phone_type);
                String phoneNumber = contactsCursor.getString(index);
                switch (phone_type) {
                    case 2:
                        phoneResult = phoneNumber;
                        break;
                }*/
                    phoneResult = contactsCursor.getString(index);
                    //allPhoneNum.add(phoneNumber);
                    ContactBean contactBean = new ContactBean();
                    contactBean.setPhone(phoneResult);
                    contactBean.setName(name);
                    contactSource.add(contactBean);
                }
                if (contactsCursor != null && !contactsCursor.isClosed()) {
                    contactsCursor.close();
                }
            }
        } catch (Exception e) {

        } finally {
            if (contactsCursor != null && !contactsCursor.isClosed()) {
                contactsCursor.close();
            }
        }
        return contactSource;
    }

    public static List<CallRecordBean> fetchCallRecord(Context context, UploadTime mUploadTimeBean) {
        List<CallRecordBean> recordSource = new ArrayList<>();
        ContentResolver mResolver = context.getContentResolver();
        //Uri mCallUri = Uri.parse("content://call_log/calls");
        Cursor cursor = null;
        try {
            cursor = mResolver.query(
                    CallLog.Calls.CONTENT_URI,
                    new String[]{Calls._ID, Calls.NUMBER, Calls.CACHED_NAME, Calls.DATE, Calls.DURATION, CallLog.Calls.TYPE},
                    null,
                    null,
                    Calls.DATE + " DESC");
            if (cursor == null) {
                return recordSource;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));//呼叫人电话
                String name = cursor.getString(cursor.getColumnIndex(Calls.CACHED_NAME));//呼叫人姓名
                String date = cursor.getString(cursor.getColumnIndex(Calls.DATE));//呼叫开始时间
                String duration = cursor.getString(cursor.getColumnIndex(Calls.DURATION));//持续时间 通话时长(秒为单位)
                //String callType = getCallType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));
                int callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                CallRecordBean recordBean = new CallRecordBean();
                recordBean.setName(name);
                recordBean.setPhone(number);
                recordBean.setCallStartTime(date);
                recordBean.setCallTimeLong(duration);
                int time = Integer.parseInt(duration);
                if (callType == CallLog.Calls.INCOMING_TYPE) {
                    //来电
                    recordBean.setIsCall(1);//呼入
                    if (time > 0) {
                        //通话时间>0那么就表示 已接
                        recordBean.setStatus(1);//已接
                    } else {
                        recordBean.setStatus(0);//未接
                    }
                } else if (callType == Calls.OUTGOING_TYPE) {
                    //去电
                    recordBean.setIsCall(0);//呼出
                    if (time > 0) {
                        //通话时间>0那么就表示 已接
                        recordBean.setStatus(1);//已接
                    } else {
                        recordBean.setStatus(0);//未接
                    }
                } else if (callType == Calls.MISSED_TYPE) {
                    //未接
                    recordBean.setIsCall(1);//呼入
                    recordBean.setStatus(0);//未接
                } else if (callType == Calls.REJECTED_TYPE) {
                    //拒绝
                    recordBean.setIsCall(1);//呼入
                    recordBean.setStatus(2);
                } else {
                    //未知
                    recordBean.setIsCall(1);//呼入
                    recordBean.setStatus(3);//未知
                }
                if (mUploadTimeBean != null && Long.parseLong(recordBean.getCallStartTime()) > mUploadTimeBean.getCallRecordLastTime()) {
                    recordSource.add(recordBean);
                } else if (mUploadTimeBean == null) {
                    recordSource.add(recordBean);
                }
                cursor.moveToNext();
            }

        } catch (Exception e) {

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return recordSource;
    }

    private static String getCallType(int anInt) {
        switch (anInt) {
            case CallLog.Calls.INCOMING_TYPE:
                return "呼入";
            case CallLog.Calls.OUTGOING_TYPE:
                return "呼出";
            case CallLog.Calls.MISSED_TYPE:
                return "未接";
            default:
                break;
        }
        return null;
    }

    public static List<SmsRecordBean> fetchSmsRecord(Context context, UploadTime mUploadTimeBean) {
        List<SmsRecordBean> smsRecordSource = new ArrayList<>();
        String SMS_URI_ALL = "content://sms/";
        Uri mSMSUri = Uri.parse(SMS_URI_ALL);
        ContentResolver mResolver = context.getContentResolver();
        Cursor cursor = mResolver.query(mSMSUri, new String[]{
                Telephony.Sms.ADDRESS,   //
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.READ,
                Telephony.Sms.STATUS,
                Telephony.Sms.TYPE,
        }, null, null, "date");
        if (cursor != null) {
            while (cursor.moveToNext()) {

                String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));//电话号码
                String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                String date = cursor.getString(cursor.getColumnIndex(Telephony.Sms.DATE));
                //String read = getMessageRead(cursor.getInt(cursor.getColumnIndex(Telephony.Sms.READ)));
                //String status = getMessageStatus(cursor.getInt(cursor.getColumnIndex(Telephony.Sms.STATUS)));
                //String type = getMessageType(cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE)));
                int type = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE));
                String person = getPerson(context, address);
                SmsRecordBean recordBean = new SmsRecordBean();
                recordBean.setPhone(address);
                recordBean.setName(person);
                recordBean.setContent(body);
                recordBean.setTime(date);
                if (type == 1) {
                    //接收
                    recordBean.setFrom(0);
                } else {
                    recordBean.setFrom(1);
                }
                int readStatus = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.READ));
                if (readStatus == 1) {
                    //已读
                    recordBean.setIsRead(1);
                } else {
                    //未读
                    recordBean.setIsRead(0);
                }
                if (mUploadTimeBean != null && Long.parseLong(recordBean.getTime()) > mUploadTimeBean.getSmsLastTime()) {
                    smsRecordSource.add(recordBean);
                } else if (mUploadTimeBean == null) {
                    smsRecordSource.add(recordBean);
                }
            }
            cursor.close();
        }
        return smsRecordSource;
    }

    private static String getPerson(Context context, String address) {
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, address);
            Cursor cursor;
            cursor = resolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.getCount() != 0) {
                        cursor.moveToFirst();
                        String name = cursor.getString(0);
                        return name;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getMessageType(int anInt) {
        if (1 == anInt) {
            return "收到的";
        }
        if (2 == anInt) {
            return "已发出";
        }
        return null;
    }

    private static String getMessageRead(int anInt) {
        if (1 == anInt) {
            return "已读";
        }
        if (0 == anInt) {
            return "未读";
        }
        return null;
    }

    private static String getMessageStatus(int anInt) {
        switch (anInt) {
            case -1:
                return "接收";
            case 0:
                return "complete";
            case 64:
                return "pending";
            case 128:
                return "failed";
            default:
                break;
        }
        return null;
    }
}
