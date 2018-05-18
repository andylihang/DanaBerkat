package com.cd.dnf.credit.ui.borrow.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.BankBean;
import com.cd.dnf.credit.bean.BankInfoBean;
import com.cd.dnf.credit.bean.BorrowOperateBean;
import com.cd.dnf.credit.bean.BorrowOrderBean;
import com.cd.dnf.credit.bean.BorrowOrderStatusBean;
import com.cd.dnf.credit.bean.CallRecordBean;
import com.cd.dnf.credit.bean.ContactBean;
import com.cd.dnf.credit.bean.CreditNotification;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.bean.SmsRecordBean;
import com.cd.dnf.credit.bean.UploadTime;
import com.cd.dnf.credit.fragment.CreditBaseFragment;
import com.cd.dnf.credit.ui.borrow.actvitiy.BankActivity;
import com.cd.dnf.credit.ui.borrow.actvitiy.BorrowOrderActivity;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.AppPreferences;
import com.cd.dnf.credit.util.BuildConstant;
import com.cd.dnf.credit.util.ContactUtil;
import com.cd.dnf.credit.util.CreditNotifyType;
import com.cd.dnf.credit.util.CreditUtil;
import com.cd.dnf.credit.util.KeyBordUtils;
import com.cd.dnf.credit.view.ToastUtil;
import com.cd.dnf.credit.view.borrow.BorrowChooseOptionDialog;
import com.cd.dnf.credit.view.borrow.interfaces.BorrowChooseOptionInterface;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jakewharton.rxbinding.view.RxView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jack on 2018/1/25.
 * 银行信息
 */

public class BorrowStepFourFragment extends CreditBaseFragment {
    @Bind(R.id.bank_view)
    TextView mBankNameView;//银行名称
    @Bind(R.id.bank_account_viw)
    EditText mBankAccountView;//银行卡号
    @Bind(R.id.bank_user_name_view)
    EditText mBankAccountUserName;//开户名 只能是字母大小写和空格
    @Bind(R.id.tax_number_view)
    EditText mTaxNumberView;//税号
    @Bind(R.id.contact_autorize_view)
    TextView mContactAutoRizeView;//联系人认证view
    @Bind(R.id.sms_autorize_view)
    TextView mSmsAutoRizeView;//短信认证view
    @Bind(R.id.call_autorize_view)
    TextView mCallAutoRizeView;//通话记录认证
    @Bind(R.id.btn_next)
    TextView mBtnNext;//下一步
    @Bind(R.id.sms_progress_view)
    ProgressBar mSmsProgressView;//短信上传进度条

    @Bind(R.id.contact_layout)
    LinearLayout mContactLayout;//联系人
    @Bind(R.id.sms_layout)
    LinearLayout mSmsLayout;//短信
    @Bind(R.id.call_layout)
    LinearLayout mCallLayout;//通话记录
    private BorrowOrderActivity mActivity;
    private List<ContactBean> contactSource = new ArrayList<>();//所有联系人
    private List<CallRecordBean> callRecordSource = new ArrayList<>();//所有通话记录
    private List<SmsRecordBean> smsRecordSource = new ArrayList<>();//短信记录
    private ContactHander mContactHander;
    private final int READ_CONTACT = 0X0231;//读取联系人
    private final int READ_CALL_LOG = 0x0232;//获取通话记录
    private final int READ_SMS = 0x0234;//读取短信内容
    private BorrowOrderBean mBorrowBean;
    private final int CHOOSE_BANK = 0x78;//选择银行
    private BankBean mChooseBank;//选择的银行
    private boolean isCheckContact = false;//是否验证了联系人
    private boolean isCheckCall = false;//是否验证了通话记录
    private boolean isCheckSms = false;//是否验证了短信
    private List<BankInfoBean> mBankInfoSource = new ArrayList<>();
    private List<String> mBankShowSource = new ArrayList<>();//弹框显示的名称
    private int chooseBankIndex = -1;
    private BorrowChooseOptionDialog mBankDialog;
    private BankInfoBean mChooseBankInfo;
    private UploadTime mUploadTimeBean;//最后上传时间
    private final int PROGRESS_SMS = 0x134;//短信进度条
    private ProgressHandler mProgressHandler;
    private int smsProgressValue = 1;
    private AppPreferences mAppPreference;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_step_four_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

 /*   public void setBorrowOrder(BorrowOrderBean borrowOrder) {
        mBorrowBean = borrowOrder;
    }*/

    class ContactHander extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case READ_CONTACT:
                    dismissProgressDialog();
                    uploadContactSource();
                    break;
                case READ_CALL_LOG:
                    dismissProgressDialog();
                    uploadCallSource();
                    break;
                case READ_SMS:
                    dismissProgressDialog();
                    uploadSmsSource();
                    break;
            }
        }
    }

    class ProgressHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS_SMS:
                    int progress = smsProgressValue + 5;
                    if (progress < 100) {
                        mSmsProgressView.setProgress(progress);
                    }
                    mProgressHandler.sendEmptyMessageDelayed(PROGRESS_SMS, 100);
                    break;
            }
        }
    }


    private void uploadSmsSource() {
/*        List<SmsRecordBean> smsRecordSource = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            SmsRecordBean smsRecordBean = new SmsRecordBean();
            smsRecordBean.setContent("测试测试测试短信测试短信");
            smsRecordBean.setName("张三" + i);
            smsRecordBean.setPhone("163788289");
            smsRecordBean.setTime("1515413278564");
            smsRecordBean.setFrom(1);
            smsRecordBean.setIsRead(1);
            smsRecordSource.add(smsRecordBean);
        }*/

        smsProgressValue = 5;
        mProgressHandler.sendEmptyMessage(PROGRESS_SMS);
        String jsonStr = JSON.toJSONString(smsRecordSource);
        OkGo.<String>post(CreditApi.API_UPLOAD_SMS_LOG).upJson(jsonStr).tag("uploadSms").execute(
                new CreditApiRequstCallback(getActivity(), false) {
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mSmsLayout.setClickable(true);
                    }

                    @Override
                    public void RequestSuccess(String body) {
                        //fetchUpLoadTime();
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        mUploadTimeBean = JSON.parseObject(response.getData(), UploadTime.class);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                            if (mSmsAutoRizeView.isAttachedToWindow()) {
                                mSmsAutoRizeView.setText(R.string.authorizaiton_str);
                                mSmsAutoRizeView.setTextColor(getResources().getColor(R.color.borrow_autorize_ok_color));
                                mSmsProgressView.setProgress(100);
                            }
                        } else {
                            mSmsAutoRizeView.setText(R.string.authorizaiton_str);
                            mSmsAutoRizeView.setTextColor(getResources().getColor(R.color.borrow_autorize_ok_color));
                            mSmsProgressView.setProgress(100);
                        }

                        mSmsLayout.setClickable(true);
                        isCheckSms = true;
                        mProgressHandler.removeCallbacksAndMessages(null);
                        checkCanNext();
                    }

                    @Override
                    public void RequestFail(String body) {
                        mSmsLayout.setClickable(true);
                        mSmsProgressView.setProgress(0);
                        mProgressHandler.removeCallbacksAndMessages(null);
                        if (isCheckSms) {
                            mSmsProgressView.setProgress(100);
                        }
                    }

                });
    }

    private void uploadCallSource() {
/*        List<CallRecordBean> callRecordSource = new ArrayList<>();
        for (int i = 1; i < 60; i++) {
            CallRecordBean callRecordBean = new CallRecordBean();
            callRecordBean.setName("崔浩军");
            callRecordBean.setPhone("13402832322");
            callRecordBean.setCallStartTime("1515413278564");
            callRecordBean.setCallTimeLong("100");
            callRecordBean.setIsCall(1);
            callRecordSource.add(callRecordBean);
        }*/


        String jsonStr = JSON.toJSONString(callRecordSource);
        OkGo.<String>post(CreditApi.API_UPLOAD_CALL_LOG).upJson(jsonStr).tag("uploadCall").execute(
                new CreditApiRequstCallback(getActivity(), true) {
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mCallLayout.setClickable(true);
                    }

                    @Override
                    public void RequestSuccess(String body) {
                        mCallLayout.setClickable(true);
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        mUploadTimeBean = JSON.parseObject(response.getData(), UploadTime.class);
                        //fetchUpLoadTime();
                        isCheckCall = true;
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                            if (mCallAutoRizeView.isAttachedToWindow()) {
                                mCallAutoRizeView.setText(R.string.authorizaiton_str);
                                mCallAutoRizeView.setTextColor(getResources().getColor(R.color.borrow_autorize_ok_color));
                            }
                        } else {
                            mCallAutoRizeView.setText(R.string.authorizaiton_str);
                            mCallAutoRizeView.setTextColor(getResources().getColor(R.color.borrow_autorize_ok_color));
                        }
                        checkCanNext();
                    }

                    @Override
                    public void RequestFail(String body) {
                        mCallLayout.setClickable(true);
                    }

                });
    }

    private void uploadContactSource() {
/*        List<ContactBean> contactSource = new ArrayList<>();
        for (int i = 1; i < 1000; i++) {
            ContactBean contactBean = new ContactBean();
            contactBean.setName("崔浩军");
            contactBean.setPhone("13402832322");
            contactSource.add(contactBean);
        }*/


        String jsonStr = JSON.toJSONString(contactSource);
        OkGo.<String>post(CreditApi.API_UPLOAD_CONTACT).upJson(jsonStr).tag("uploadContact").execute(
                new CreditApiRequstCallback(getActivity(), true) {
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mContactLayout.setClickable(true);
                    }

                    @Override
                    public void RequestSuccess(String body) {
                        mContactLayout.setClickable(true);
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        //fetchUpLoadTime();
                        isCheckContact = true;
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                            if (mContactAutoRizeView.isAttachedToWindow()) {
                                mContactAutoRizeView.setText(R.string.authorizaiton_str);
                                mContactAutoRizeView.setTextColor(getResources().getColor(R.color.borrow_autorize_ok_color));
                            }
                        } else {
                            mContactAutoRizeView.setText(R.string.authorizaiton_str);
                            mContactAutoRizeView.setTextColor(getResources().getColor(R.color.borrow_autorize_ok_color));
                        }
                        checkCanNext();
                    }

                    @Override
                    public void RequestFail(String body) {
                        mContactLayout.setClickable(true);
                    }

                });
    }
    private void initListener(){
        mBankAccountView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCanNext();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBankAccountUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCanNext();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    private void checkCanNext(){
        String bankAccountStr = mBankAccountView.getText().toString();//银行卡号
        String bankUserNameStr = mBankAccountUserName.getText().toString().trim();//开户名
        String bankNameStr=mBankNameView.getText().toString();//银行名称
        if(!TextUtils.isEmpty(bankNameStr)&&!TextUtils.isEmpty(bankUserNameStr)&&!TextUtils.isEmpty(bankAccountStr)
                &&isCheckContact&&isCheckCall&&isCheckSms){
            mBtnNext.setTextColor(CreditApplication.getInstance().getResources().getColor(R.color.white));
            mBtnNext.setBackgroundResource(R.mipmap.bg_register);
        }else {
            mBtnNext.setTextColor(CreditApplication.getInstance().getResources().getColor(R.color.need_input_color));
            mBtnNext.setBackgroundResource(R.drawable.bg_need_input);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppPreference = new AppPreferences(getActivity());
        mActivity = (BorrowOrderActivity) getActivity();
        mContactHander = new ContactHander();
        mProgressHandler = new ProgressHandler();
        mBorrowBean = getArguments().getParcelable("orderBean");
        mBankInfoSource = CreditApplication.getInstance().getMindBankInfo();
        initView();
        initRxBinding();
        initListener();
        if (mBankInfoSource != null && mBankInfoSource.size() > 0) {
            initBankDilog();
        }
        fetchUpLoadTime();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    private void initView() {
        //银行卡
        if (!TextUtils.isEmpty(mBorrowBean.getBankName())) {
            mBankNameView.setText(mBorrowBean.getBankName());
        }
        if (!TextUtils.isEmpty(mBorrowBean.getBankAccount())) {
            mBankAccountView.setText(mBorrowBean.getBankAccount());
        }
        if (!TextUtils.isEmpty(mBorrowBean.getBankUserName())) {
            mBankAccountUserName.setText(mBorrowBean.getBankUserName());
        }
        if (!TextUtils.isEmpty(mBorrowBean.getBankName())) {
            mChooseBank = new BankBean();
            mChooseBank.setBankCode(mBorrowBean.getBankCode());
            mChooseBank.setBankName(mBorrowBean.getBankName());
            handleChooseBankIndex(mChooseBank);
        }
        //税号
        if (!TextUtils.isEmpty(mBorrowBean.getDutyParagraph())) {
            mTaxNumberView.setText(mBorrowBean.getDutyParagraph());
        }
        checkCanNext();
    }

    private void handleChooseBankIndex(BankBean mChooseBank) {
        for (int i = 0; i < mBankInfoSource.size(); i++) {
            BankInfoBean mBankInfoBean = mBankInfoSource.get(i);
            if (TextUtils.equals(mBankInfoBean.getBankCode(), mChooseBank.getBankCode())) {
                chooseBankIndex = i;
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消网络请求
        Log.e("sakura","----第四步，取消网络请求 所有的");
        OkGo.getInstance().cancelTag("uploadContact");
        OkGo.getInstance().cancelTag("uploadCall");
        OkGo.getInstance().cancelTag("uploadSms");
        OkGo.getInstance().cancelTag("fetchupdatetime");
        OkGo.getInstance().cancelTag("borrowOrder");
        if (mContactHander != null) {
            mContactHander.removeCallbacksAndMessages(null);
        }
        if (mProgressHandler != null) {
            mProgressHandler.removeCallbacksAndMessages(null);
        }
    }

    //获取上传的时间
    private void fetchUpLoadTime() {
        OkGo.<String>get(CreditApi.API_FETCH_UPDATE_TIME).tag("fetchupdatetime").execute(
                new CreditApiRequstCallback(getActivity(), false) {
                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        mUploadTimeBean = JSON.parseObject(response.getData(), UploadTime.class);
                    }

                    @Override
                    public void RequestFail(String body) {

                    }

                });
    }

    private void checkInputInformation() {
        if (mChooseBankInfo == null && mChooseBank == null) {
            mBtnNext.setClickable(true);
            //未选择银行卡
            ToastUtil.showShort(R.string.choose_open_bank);
            return;
        }
        String bankAccountStr = mBankAccountView.getText().toString();//银行卡号
        String bankUserNameStr = mBankAccountUserName.getText().toString().trim();//开户名
        String taxStr = mTaxNumberView.getText().toString();//税号
        if (TextUtils.isEmpty(bankAccountStr)) {
            mBtnNext.setClickable(true);
            ToastUtil.showShort(R.string.input_bank_accout_tip);
            return;
        }
        if (TextUtils.isEmpty(bankUserNameStr)) {
            mBtnNext.setClickable(true);
            ToastUtil.showShort(R.string.input_bank_open_account_name_tip);
            return;
        }
        if (!CreditUtil.checkBankUserName(bankUserNameStr)) {
            mBtnNext.setClickable(true);
            ToastUtil.showShort(R.string.input_right_account_user_name_tip);
            return;
        }
        if (mChooseBank != null) {
            mBorrowBean.setBankName(mChooseBank.getBankName());
            mBorrowBean.setBankCode(mChooseBank.getBankCode());
        } else {
            mBorrowBean.setBankName(mChooseBankInfo.getBankName());
            mBorrowBean.setBankCode(mChooseBankInfo.getBankCode());
        }
        mBorrowBean.setBankUserName(bankUserNameStr);
        mBorrowBean.setBankAccount(bankAccountStr);
        mBorrowBean.setDutyParagraph(taxStr);


        if (!isCheckContact) {
            mBtnNext.setClickable(true);
            ToastUtil.showShort(R.string.upload_contact_tip_str);
            return;
        }
        if (!isCheckCall) {
            mBtnNext.setClickable(true);
            ToastUtil.showShort(R.string.upload_call_tip_str);
            return;
        }
        if (!isCheckSms) {
            mBtnNext.setClickable(true);
            ToastUtil.showShort(R.string.upload_sms_tip_str);
            return;
        }


        String indentifyOssKey = mBorrowBean.getIdCardPhotoFront();
        String handleOssKey = mBorrowBean.getPeopleIdCardPhoto();
        String workOssKey = mBorrowBean.getWorkCardPhoto();
        String articOssKey = mBorrowBean.getPayrollPhoto();
        if (TextUtils.isEmpty(indentifyOssKey)) {
            mBtnNext.setClickable(true);
            ToastUtil.showShort(R.string.need_upload_indentify_card_tip_str);
            return;
        }
        if (TextUtils.isEmpty(handleOssKey)) {
            mBtnNext.setClickable(true);
            ToastUtil.showShort(R.string.need_upload_handle_card_tip_str);
            return;
        }
        if (TextUtils.isEmpty(workOssKey)) {
            mBtnNext.setClickable(true);
            ToastUtil.showShort(R.string.need_upload_work_card_tip_str);
            return;
        }
        if (TextUtils.isEmpty(articOssKey)) {
            mBtnNext.setClickable(true);
            ToastUtil.showShort(R.string.need_upload_article_card_tip_str);
            return;
        }
        submitBorrowOrder();
    }

    private void deleteUserBorrowStatus() {
        //清空用户订单的状态
        String userId = mAppPreference.get(AppPreferences.USER_ID, "");
        DataSupport.deleteAll(BorrowOperateBean.class, "userId='" + userId + "'");
    }

    private void submitBorrowOrder() {
        String serialId = CreditUtil.getSerialNumber(getActivity());
        mBorrowBean.setDeviceId(serialId);
        String imeiStr = CreditUtil.getImeiValue(getActivity());
        mBorrowBean.setImeis(imeiStr);
        String jsonStr = JSON.toJSONString(mBorrowBean);
        OkGo.<String>post(CreditApi.API_APPLY_BORROW).upJson(jsonStr).tag("borrowOrder").execute(
                new CreditApiRequstCallback(getActivity(), true) {
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mBtnNext.setClickable(true);
                    }

                    @Override
                    public void RequestSuccess(String body) {
                        mBtnNext.setClickable(true);
                        deleteUserBorrowStatus();
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        BorrowOrderStatusBean orderStatusBean=JSON.parseObject(response.getData(),BorrowOrderStatusBean.class);
                        CreditNotification notification = new CreditNotification();
                        notification.setNotifyType(CreditNotifyType.NOTIFY_BORROW_ORDER_APPLY_OK);
                        notification.setNotifyData(orderStatusBean);
                        EventBus.getDefault().post(notification);
                        //ToastUtil.showShort(response.getMsg());
                        //提交订单成功 需要更新首页状态
/*                        UserOrderBean userOrderBean = null;
                        try {
                            JSONObject jsonObject = new JSONObject(response.getData());
                            String orderId = jsonObject.getString("id");
                            String planId = jsonObject.getString("planId");
                            String userId = jsonObject.getString("userId");
                            userOrderBean = new UserOrderBean();
                            userOrderBean.setOrderId(orderId);
                            userOrderBean.setUserId(userId);
                            userOrderBean.setPlanId(planId);
                            userOrderBean.setCreateTime(response.getTimestamp());
                        } catch (Exception e) {

                        }

                        CreditNotification notification = new CreditNotification();
                        notification.setNotifyType(CreditNotifyType.NOTIFY_BORROW_ORDER_APPLY_OK);
                        notification.setNotifyData("");
                        EventBus.getDefault().post(notification);*/
                        mActivity.finish();
                    }

                    @Override
                    public void RequestFail(String body) {
                        mBtnNext.setClickable(true);
                    }

                });
    }


    private void storeSource() {
        if (mChooseBank != null) {
            mBorrowBean.setBankName(mChooseBank.getBankName());
            mBorrowBean.setBankCode(mChooseBank.getBankCode());
        } else if (mChooseBankInfo != null) {
            mBorrowBean.setBankName(mChooseBankInfo.getBankName());
            mBorrowBean.setBankCode(mChooseBankInfo.getBankCode());
        }
        String bankAccountStr = mBankAccountView.getText().toString();//银行卡号
        String bankUserNameStr = mBankAccountUserName.getText().toString();//开户名
        String taxStr = mTaxNumberView.getText().toString();//税号
        mBorrowBean.setBankUserName(bankUserNameStr);
        mBorrowBean.setBankAccount(bankAccountStr);
        mBorrowBean.setDutyParagraph(taxStr);
    }

    @Override
    public void onPause() {
        super.onPause();
        storeSource();
    }

    @OnTouch(value = {R.id.touch_panel})
    boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.touch_panel:
                KeyBordUtils.hideSoft(this);
                break;
        }
        return false;
    }

    private void initRxBinding() {
        RxView.clicks(mBtnNext)
                .debounce(BuildConstant.RXBINT_VIEW_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mBtnNext.setClickable(false);
                        checkInputInformation();
                        if (mFirebaseAnalytics != null) {
                            mFirebaseAnalytics.logEvent(AnalyticUtil.BORROW_STEP_FOUR_CLICK, null);
                        }
                    }
                });

        //通讯录
        RxView.clicks(mContactLayout)
                .debounce(BuildConstant.RXBINT_VIEW_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mContactLayout.setClickable(false);
                        new ContactAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                        if (mFirebaseAnalytics != null) {
                            mFirebaseAnalytics.logEvent(AnalyticUtil.BORROW_STEP_FOUR_CONTACT_CLICK, null);
                        }
                    }
                });
        //短信
        RxView.clicks(mSmsLayout)
                .debounce(BuildConstant.RXBINT_VIEW_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mSmsLayout.setClickable(false);
                        new SMSAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                        if (mFirebaseAnalytics != null) {
                            mFirebaseAnalytics.logEvent(AnalyticUtil.BORROW_STEP_FOUR_SMS_CLICK, null);
                        }
                    }
                });
        //通话记录
        RxView.clicks(mCallLayout)
                .debounce(BuildConstant.RXBINT_VIEW_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mCallLayout.setClickable(false);
                        new CallAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                        if (mFirebaseAnalytics != null) {
                            mFirebaseAnalytics.logEvent(AnalyticUtil.BORROW_STEP_FOUR_CALL_CLICK, null);
                        }
                    }
                });

    }


    class ContactAsyncTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(getResources().getString(R.string.fetch_contact_str));
        }


        @Override
        protected Object doInBackground(Object[] params) {
            contactSource = ContactUtil.fetchContact(getActivity());
            return null;
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mContactHander.sendEmptyMessage(READ_CONTACT);
        }
    }

    class SMSAsyncTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(getResources().getString(R.string.fetch_sms_str));
        }


        @Override
        protected Object doInBackground(Object[] params) {
            smsRecordSource = ContactUtil.fetchSmsRecord(getActivity(), mUploadTimeBean);
            return null;
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mContactHander.sendEmptyMessage(READ_SMS);
        }
    }

    class CallAsyncTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(getResources().getString(R.string.fetch_call_record_str));
        }


        @Override
        protected Object doInBackground(Object[] params) {
            callRecordSource = ContactUtil.fetchCallRecord(getActivity(), mUploadTimeBean);
            return null;
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mContactHander.sendEmptyMessage(READ_CALL_LOG);
        }
    }

    @OnClick(value = {R.id.bank_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.bank_layout:
                //选中银行
                if (mBankInfoSource.size() > 0) {
                    if (!mBankDialog.isShow()) {
                        mBankDialog.show();
                    }
                } else {
                    Intent bankIntent = new Intent(getActivity(), BankActivity.class);
                    startActivityForResult(bankIntent, CHOOSE_BANK);
                }
                break;
        }
    }

    //显示bankDialog
    private void initBankDilog() {
        for (int i = 0; i < mBankInfoSource.size(); i++) {
            BankInfoBean bankInfoBean = mBankInfoSource.get(i);
            String bankName = bankInfoBean.getBankName();
            mBankShowSource.add(bankName);
        }
        mBankShowSource.add(getActivity().getString(R.string.bank_other_tip_str));
        mBankDialog = new BorrowChooseOptionDialog(getActivity(), mBankShowSource);
        mBankDialog.setChooseOption(chooseBankIndex);
        mBankDialog.setChooseOptionInterface(new BorrowChooseOptionInterface() {
            @Override
            public void chooseOption(int chooseOption) {
                mBankDialog.dismiss();
                if (chooseBankIndex != chooseOption) {
                    chooseBankIndex = chooseOption;
                    if (chooseBankIndex == (mBankShowSource.size() - 1)) {
                        chooseBankIndex = -1;
                        mBankDialog.setChooseOption(chooseBankIndex);
                        //选择的是其他 那么就需要跳转到银行列表页
                        mChooseBankInfo = null;
                        mBankNameView.setText("");
                        mBankAccountView.setText("");
                        mBankAccountUserName.setText("");
                        //需要跳转到银行页
                        Intent bankIntent = new Intent(getActivity(), BankActivity.class);
                        startActivityForResult(bankIntent, CHOOSE_BANK);
                    } else {
                        mBankDialog.setChooseOption(chooseBankIndex);
                        //选择了哪个银行
                        mChooseBankInfo = mBankInfoSource.get(chooseOption);
                        mBankNameView.setText(mChooseBankInfo.getBankName());
                        mBankAccountView.setText(mChooseBankInfo.getCardNumber());
                        mBankAccountUserName.setText(mChooseBankInfo.getCardUsername());
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_BANK) {
                mChooseBank = data.getParcelableExtra("chooseBank");
                mBankNameView.setText(mChooseBank.getBankName());
                checkCanNext();
            }
        }
    }
}
