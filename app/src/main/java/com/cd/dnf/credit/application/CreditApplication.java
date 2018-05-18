package com.cd.dnf.credit.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.bean.BankInfoBean;
import com.cd.dnf.credit.bean.BorrowOrderStatusBean;
import com.cd.dnf.credit.bean.CreditNotification;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.util.AppPreferences;
import com.cd.dnf.credit.util.CreditNotifyType;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.alibaba.sdk.android.push.register.GcmRegister;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.tendcloud.tenddata.TCAgent;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import okhttp3.OkHttpClient;

import static com.alibaba.sdk.android.push.AgooMessageReceiver.TAG;

/**
 * Created by jack on 2018/1/23.
 */

public class CreditApplication extends Application {
    private static CreditApplication mInstance;
    private static Context mContext;
    public static final int pageSize = 10;
    /**
     * 用于记录当前所有压入栈的activity
     */
    private static Stack<Activity> mActivityStack;
    private AppPreferences mAppPreferences;
    private List<BankInfoBean> mBankInfoSource = new ArrayList<>();//我绑定的银行卡
    private BorrowOrderStatusBean mBorrowOrderStatusBean;//最后一笔订单的详情
    private OSS mOSS;
    private CloudPushService mPushService;
  //  public static final String BUCK_NAME="pindev";//OSS的buck name 测试环境
    public static final String BUCK_NAME = "pinjamplus";//OSS的buck name 正式环境

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mInstance = this;
        mActivityStack = new Stack<>();
        mAppPreferences = new AppPreferences(mContext);
        LitePal.initialize(this);
        TCAgent.init(this);
        TCAgent.setReportUncaughtExceptions(true);
        initOkGo();
        initOSS();
        initCloudChannel();
        fetchMineBindCard();
        fetchBorrowOrder();
        fetchCustomCall();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBus.getDefault().unregister(this);
    }

    //采用通知的方式来进行操作
    @Subscribe
    public void onEventMainThread(CreditNotification notifyBean) {
        if (TextUtils.equals(notifyBean.getNotifyType(), CreditNotifyType.NOTIFY_BIND_CARD_CHANGE)) {
            //提交申请成功 需要进入待审核状态
            fetchMineBindCard();
        }
    }

    public static Context getContext() {
        return mContext;
    }

    public static CreditApplication getInstance() {
        return mInstance;
    }

    public void setBorrowOrderStatus(BorrowOrderStatusBean borrowOrderStatus) {
        mBorrowOrderStatusBean = borrowOrderStatus;
    }

    public BorrowOrderStatusBean getBorrowOrderStatus() {
        return mBorrowOrderStatusBean;
    }

    private void fetchBorrowOrder() {
        OkGo.<String>get(CreditApi.API_FETCH_BORROW_ORDER_DETAIL).tag("fetchBorrowOrderDetailApp").execute(
                new CreditApiRequstCallback(mContext, false, false, true) {

                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        mBorrowOrderStatusBean = JSON.parseObject(response.getData(), BorrowOrderStatusBean.class);
                    }

                    @Override
                    public void RequestFail(String body) {

                    }

                });
    }
    //获取客服电话
    private void fetchCustomCall(){
        OkGo.<String>get(CreditApi.API_FETCH_CUSTOM_CALL).tag("fetchCustomCall").execute(
                new CreditApiRequstCallback(mContext, false, false, true) {

                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        try{
                            //存储最新的电话
                            JSONObject jsonObject=new JSONObject(response.getData());
                            String customCall=jsonObject.getString("serviceTel");
                            mAppPreferences.save(AppPreferences.CUSTOM_CALL,customCall);
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void RequestFail(String body) {

                    }

                });
    }

    //登陆注册后 需要去操作的网络请求
    public void LoginToFetchServer() {
        initCloudChannel();
        fetchMineBindCard();
        fetchBorrowOrder();
    }

    public CloudPushService getPushService() {
        return mPushService;
    }

    /**
     * 初始化云推送通道
     */
    public void initCloudChannel() {
        PushServiceFactory.init(mContext);
        mPushService = PushServiceFactory.getCloudPushService();
        mPushService.setNotificationSmallIcon(R.mipmap.icon_logo);
        mPushService.register(mContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                //Log.d(TAG, "init cloudchannel success");

            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                //Log.d(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
        //MiPushRegister.register(mContext, "XIAOMI_ID", "XIAOMI_KEY"); // 初始化小米辅助推送
        HuaWeiRegister.register(mContext); // 接入华为辅助推送
        bindAccount();
    }

    private void bindAccount() {
        String userId = mAppPreferences.get(AppPreferences.USER_ID, "");
        if(!TextUtils.isEmpty(userId)){
            mPushService.bindAccount(userId, new CommonCallback() {
                @Override
                public void onSuccess(String s) {

                }

                @Override
                public void onFailed(String s, String s1) {

                }
            });
        }
    }

    private void initOSS() {
     //   String endpoint = "http://oss-cn-shenzhen.aliyuncs.com"; //测试环境
        String endpoint = "http://oss-ap-southeast-1.aliyuncs.com"; //正式环境
        //明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考访问控制章节
        //也可查看sample 中 sts 使用方式了解更多(https://github.com/aliyun/aliyun-oss-android-sdk/tree/master/app/src/main/java/com/alibaba/sdk/android/oss/app) <StsToken.SecurityToken>
        //"LTAITEepvhkhuQfu", "ebuBlXMy6jG3m9QM8GNIMBHVQ1WfNh", "MpGYRxWXIS6SiqvoQ4RycfoZ9QoGHR"
        OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                return OSSUtils.sign("LTAIS8iJ85fwJ8zP", "j23uB50a7hTMskmmb7nQ7KpYDJxPxY", content);
            }
        };
        //该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        mOSS = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
    }

    public OSS getOSS() {
        return mOSS;
    }

    private void initOkGo() {
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        HttpHeaders headers = new HttpHeaders();//公共header
        HttpParams params = new HttpParams();//公共参数
        //----------------------------------------------------------------------------------------//

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
        //第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        //builder.addInterceptor(new ChuckInterceptor(this));

        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间


        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(headers)                      //全局公共头
                .addCommonParams(params);                       //全局公共参数
    }

    private void fetchMineBindCard() {
        OkGo.<String>get(CreditApi.API_HANDLE_MINE_BANK).tag("getMineBank").execute(
                new CreditApiRequstCallback(mContext, false, false, true) {

                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        //查询成功
                        mBankInfoSource = JSON.parseArray(response.getData(), BankInfoBean.class);
                    }

                    @Override
                    public void RequestFail(String body) {

                    }

                });
    }

    public List<BankInfoBean> getMindBankInfo() {
        return mBankInfoSource;
    }

    /**
     * 添加activity
     */
    public static void addActivity(Activity activity) {
        mActivityStack.add(activity);
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {

            mActivityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i)) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public static void finishApp() {
        try {
            finishAllActivity();

            ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(activityManager, mContext.getPackageName());

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
        }
    }
}
