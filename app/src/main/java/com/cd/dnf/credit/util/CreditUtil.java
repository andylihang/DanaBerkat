package com.cd.dnf.credit.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.sdk.android.push.CloudPushService;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.activity.LoginRegisterActivity;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.ContactBean;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jack on 2018/1/23.
 */

public class CreditUtil {
    public static int PIC_CODE_LENGTH = 4;//图片码长度
    public static int SMS_CODE_LENGTH = 6;//短信验证码位数
    public static int PHONE_NUMBER_MIN_LENGTH = 12;//最低位数是12位
    public static int PHONE_NUMBER_MIN_LENGTH_NINE = 9;//最低位数是9位
    public static String NOTIFICATION_JPUSH_ORDER_TYPE = "1";//推送的订单状态改变

    //检查密码的格式的正确性 需要4-10位，只包含数字和字母
    public static boolean checkRightStylePwd(String password) {
        Pattern p = Pattern.compile("[0-9A-Za-z]{4,10}$");
        Matcher m = p.matcher(password);
        return m.matches();
    }

    //检验银行卡用户名 只能是字母和空格
    public static boolean checkBankUserName(String userName) {
        Pattern p = Pattern.compile("[A-Za-z\\s]+");
        Matcher m = p.matcher(userName);
        return m.matches();
    }

    public static String getImeiValue(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imeiStr = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StringBuilder builder = new StringBuilder();
            for (int slot = 0; slot < telephonyManager.getPhoneCount(); slot++) {
                String imei = telephonyManager.getDeviceId(slot);
                if (slot == (telephonyManager.getPhoneCount() - 1)) {
                    builder.append(imei);
                } else {
                    builder.append(imei + ",");
                }
            }
            imeiStr = builder.toString();
        } else {
            imeiStr = telephonyManager.getDeviceId();
        }

        return imeiStr;
    }

    private static final String getDeviceSerial() {
        String s;
        try {
            Method method = Class.forName("android.os.Build").getDeclaredMethod("getString", new Class[]{
                    Class.forName("java.lang.String")
            });
            if (!method.isAccessible())
                method.setAccessible(true);
            s = (String) method.invoke(new Build(), new Object[]{
                    "ro.serialno"
            });
        } catch (ClassNotFoundException classnotfoundexception) {
            classnotfoundexception.printStackTrace();
            return "";
        } catch (NoSuchMethodException nosuchmethodexception) {
            nosuchmethodexception.printStackTrace();
            return "";
        } catch (InvocationTargetException invocationtargetexception) {
            invocationtargetexception.printStackTrace();
            return "";
        } catch (IllegalAccessException illegalaccessexception) {
            illegalaccessexception.printStackTrace();
            return "";
        }
        return s;
    }

    private static final String getMD5String(String s)
            throws NoSuchAlgorithmException {
        byte abyte0[] = MessageDigest.getInstance("SHA-1").digest(s.getBytes());
        Formatter formatter = new Formatter();
        int i = abyte0.length;
        for (int j = 0; j < i; j++) {
            byte byte0 = abyte0[j];
            Object aobj[] = new Object[1];
            aobj[0] = Byte.valueOf(byte0);
            formatter.format("%02x", aobj);
        }

        return formatter.toString();
    }

    /**
     * 根据手机设备的IMEI、设备序列号、MAC地址经过MD5加密，得到手机专有的序号
     *
     * @param context
     * @return
     */
    public static String getSerialNumber(Context context) {
/*        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();

        }
        if(TextUtils.isEmpty(serial)){
            serial="Aa1234567890";
        }
        return serial;*/
        String s = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (s == null)
            s = "";//设备的IMEI
        //String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        String s1 = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (s1 == null)
            s1 = "";//ANDROID_ID
        String s2;//序列号
        String s3;//MAC地址
        WifiInfo wifiinfo;
        String s4;
        if (Build.VERSION.SDK_INT >= 9)//指定版本才有
        {
            s2 = Build.SERIAL;
            if (s2 == null)
                s2 = "";
        } else {
            s2 = getDeviceSerial();
        }
        s3 = "";//Mac地址
        wifiinfo = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        if (wifiinfo != null) {
            s3 = wifiinfo.getMacAddress();
            if (s3 == null)
                s3 = "";
        }
        try {
            s4 = getMD5String((new StringBuilder()).append(s).append(s1).append(s2).append(s3).toString());
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
            return "Aa123456";
        }
        return s4;
    }

    public static String getUserPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return "";
        }
        String phoneStr = "";
        if (phone.startsWith("62")) {
            phone = phone.substring(2, phone.length());
        }
        if (phone.length() >= 8) {
            phoneStr = phone.substring(0, 3) + "****" + phone.substring(7);
        } else {
            phoneStr = phone;
        }
        return phoneStr;
    }

    //获取联系人电话
    public static ContactBean getContactPhone(Context context, Cursor cursor) {
        int phoneCount = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        String phoneResult = "";
        String phoneUserName = "";
        String phoneNumber = "";
        if (phoneCount > 0) {
            phoneUserName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            ContentResolver cr = context.getContentResolver();
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            //int phoneCount = phones.getCount();
            //allPhoneNum = new ArrayList<String>(phoneCount);
            if (phones.moveToFirst()) {
                // 遍历所有的电话号码
                for (; !phones.isAfterLast(); phones.moveToNext()) {
                    int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int phone_type = phones.getInt(typeindex);
                        phoneNumber = phones.getString(index);
                    switch (phone_type) {
                        case 2:
                            phoneResult = phoneNumber;
                            break;
                    }
                    //allPhoneNum.add(phoneNumber);
                }
                if (!phones.isClosed()) {
                    phones.close();
                }
            }
        }
        ContactBean contactBean = new ContactBean();
        contactBean.setName(phoneUserName);
        if (TextUtils.isEmpty(phoneResult)) {
            contactBean.setPhone(phoneNumber);
        } else {
            contactBean.setPhone(phoneResult);
        }
        return contactBean;
    }

    public static String getCitySource(Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open("city.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String getBankSource(Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open("bank.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String dataSwitch(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }

    public static String dataSwitchDay(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }

    public static String dataSwitchLoan(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd", Locale.getDefault());
        return format.format(date);
    }

    public static String moneySwitch(long money) {
        String moneyStr = Long.toString(money);
        //先进行倒叙
        StringBuilder builderOne = new StringBuilder();
        for (int i = moneyStr.length() - 1; i >= 0; i--) {
            char value = moneyStr.charAt(i);
            builderOne.append(value);
        }
        String text = builderOne.toString();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char value = text.charAt(i);
            builder.append(value);
            if (i % 3 == 2 && (text.length() - 1) > i) {
                //需要添加
                builder.append(".");
            }
        }
        String textStr = builder.toString();
        StringBuilder builderTwo = new StringBuilder();
        for (int i = textStr.length() - 1; i >= 0; i--) {
            char value = textStr.charAt(i);
            builderTwo.append(value);
        }
        return builderTwo.toString();
    }

    public static String borrowStatusSwitch(Context context, int status) {
        //0 审核中 1 审核失败 2 放款中 3 放款成功(不逾期) 4 放款失败 5 逾期 6 已完成     （放款中 在页面中展示为审核中)  通过状态来
        String value = "";
        switch (status) {
            case 0:
                //审核中
                value = context.getResources().getString(R.string.borrow_status_0_str);
                break;
            case 1:
                //审核失败
                value = context.getResources().getString(R.string.borrow_status_1_str);
                break;
            case 2:
                //放款中
                value = context.getResources().getString(R.string.borrow_status_2_str);
                break;
            case 3:
                //放款成功(不逾期)
                value = context.getResources().getString(R.string.borrow_status_3_str);
                break;
            case 4:
                //放款失败
                value = context.getResources().getString(R.string.borrow_status_4_str);
                break;
            case 5:
                //放款成功(逾期)
                value = context.getResources().getString(R.string.borrow_status_5_str);
                break;
            case 6:
                //已完成
                value = context.getResources().getString(R.string.borrow_status_6_str);
                break;
            case 7:
                //补件
                value = context.getResources().getString(R.string.borrow_status_7_str);
                break;
        }
        return value;
    }

    /**
     * 获取当前应用程序的版本号
     */
    public static String getVersion(Context mContext) {
        String st = "";
        if (mContext == null) {
            mContext = CreditApplication.getContext();
        }
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(mContext.getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return st;
        }
    }

    //获取图片
    public static Bitmap autoResizeFromStream(InputStream stream, ImageView imageView) throws IOException {

        byte[] data;
        {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int len = 0;
            while ((len = stream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            outStream.close();
            data = outStream.toByteArray();
            stream.close();
        }

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, imageView.getWidth(), imageView.getHeight());
        Log.d("ImageHeight", String.valueOf(options.outHeight));
        Log.d("ImageWidth", String.valueOf(options.outWidth));
        Log.d("Height", String.valueOf(imageView.getWidth()));
        Log.d("Width", String.valueOf(imageView.getWidth()));
        //options.inSampleSize = 10;

        Log.d("SampleSize", String.valueOf(options.inSampleSize));
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    //计算图片缩放比例
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static boolean checkLoginStatus(Context context) {
        AppPreferences mAppreference = new AppPreferences(context);
        String token = mAppreference.get(AppPreferences.USER_TOKEN);
        boolean goLogin = false;
        if (TextUtils.isEmpty(token)) {
            //需要登录才能借款
            Intent loginIntent = new Intent(context, LoginRegisterActivity.class);
            context.startActivity(loginIntent);
            goLogin = true;
        }
        return goLogin;
    }

    public static long getPlanAmount(String plantId) {
        long money = 0;
        if (TextUtils.equals("5071475846808576", plantId)) {
            money = 642000;
        } else if (TextUtils.equals("5079948368970751", plantId)) {
            money = 684000;
        } else if (TextUtils.equals("5079948368970753", plantId)) {
            money = 1284000;
        } else if (TextUtils.equals("5079948368970752", plantId)) {
            money = 1368000;
        }
        return money;
    }

    private boolean isForeground(Context context) {
        if (context != null) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String currentPackageName = cn.getPackageName();
            if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
                return true;
            }
            return false;
        }
        return false;
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


}
