package com.cd.dnf.credit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.api.CreditWebApi;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.CreditNotification;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.interfaces.KaptchaInterface;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.AppPreferences;
import com.cd.dnf.credit.util.BuildConstant;
import com.cd.dnf.credit.util.CreditNotifyType;
import com.cd.dnf.credit.util.CreditUtil;
import com.cd.dnf.credit.util.KeyBordUtils;
import com.cd.dnf.credit.view.ToastUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jaeger.library.StatusBarUtil;
import com.jakewharton.rxbinding.view.RxView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by jack on 2018/1/23.
 */

public class LoginActivity extends CreditSwipBackActivity {
    @Bind(R.id.title_layout)
    RelativeLayout mTitleLayout;
    @Bind(R.id.status_bar_view)
    ImageView mStatusbarImageView;


    @Bind(R.id.phone_number_view)
    EditText mPhoneNumberView;//手机号码
    @Bind(R.id.password_view)
    EditText mPasswordView;//密码
    @Bind(R.id.btn_show_pwd_view)
    ImageView mBtnShowPwdView;//显示密码眼睛
    @Bind(R.id.btn_login)
    TextView mBtnLogin;
    @Bind(R.id.checkbox_flag)
    CheckBox mCheckBox;
    private boolean password = true;//当时密码输入框 是显示密码
    private AppPreferences preferences;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        ButterKnife.bind(this);
        initRxBinding();
        preferences = new AppPreferences(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void loginServer() {
        String phoneStr = mPhoneNumberView.getText().toString().trim();//手机号
        String passwordStr = mPasswordView.getText().toString();//密码
        if (TextUtils.isEmpty(phoneStr)) {
            ToastUtil.showShort(R.string.phone_empty_tip_str);
            return;
        }
        if(CreditUtil.PHONE_NUMBER_MIN_LENGTH_NINE>phoneStr.length() ||  phoneStr.length()>CreditUtil.PHONE_NUMBER_MIN_LENGTH){
            ToastUtil.showShort(R.string.input_right_phone_tip_str);
            return;
        }
        if (TextUtils.isEmpty(passwordStr)) {
            ToastUtil.showShort(R.string.password_empty_tip_str);
            return;
        }
        boolean checkexed = mCheckBox.isChecked();
        if (!checkexed) {
            ToastUtil.showShort(R.string.agree_user_agreement_tip_str);
            return;
        }
/*        if (!CreditUtil.checkRightStylePwd(passwordStr)) {
            //密码输入错误
            ToastUtil.showShort(R.string.password_error_tip_str);
            return;
        }*/
        String serialId = CreditUtil.getSerialNumber(this);
        HashMap<String, Object> hashMap = new HashMap<>();
        //hashMap.put("phone","62"+phoneStr);
        hashMap.put("phone", "62" + phoneStr);
        hashMap.put("password", "" + passwordStr);
        hashMap.put("deviceId", serialId);
        String jsonStr = JSON.toJSONString(hashMap);
        OkGo.<String>post(CreditApi.API_LOGIN).upJson(jsonStr).tag("login").execute(
                new CreditApiRequstCallback(this, true) {
                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        try {
                            JSONObject jsonObject = new JSONObject(response.getData());
                            String token = jsonObject.getString("apiKey");
                            String phone = jsonObject.getString("phone");
                            preferences.save(AppPreferences.USER_TOKEN, token);
                            String userId = jsonObject.getString("id");
                            preferences.save(AppPreferences.USER_ID, userId);
                            preferences.save(AppPreferences.USER_PHONE, phone);
                            CreditApplication.getInstance().LoginToFetchServer();
                            CreditNotification notification = new CreditNotification();
                            notification.setNotifyType(CreditNotifyType.NOTIFY_LOGIN_OK);
                            notification.setNotifyData("");
                            EventBus.getDefault().post(notification);
                        } catch (Exception e) {

                        }
  /*                      Intent mIntent = new Intent(LoginActivity.this, HomeActivity.class);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);*/
                        finish();
                    }

                    @Override
                    public void RequestFail(String body) {

                    }

                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消网络请求
        OkGo.getInstance().cancelTag("login");
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
        RxView.clicks(mBtnLogin)
                .debounce(BuildConstant.RXBINT_VIEW_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        loginServer();
                        if (mFirebaseAnalytics != null) {
                            mFirebaseAnalytics.logEvent(AnalyticUtil.LOGIN_CLICK, null);
                        }
                    }
                });
    }

    @OnClick(value = {R.id.back_layout, R.id.btn_register, R.id.show_pwd, R.id.btn_forget_pwd, R.id.see_agree_view})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                finish();
                break;
            case R.id.btn_register:
                //跳转到注册页面
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.show_pwd:
                if (password) {
                    mPasswordView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mBtnShowPwdView.setImageResource(R.mipmap.icon_btn_show_pwd_selected);
                } else {
                    mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mBtnShowPwdView.setImageResource(R.mipmap.icon_btn_show_pwd_normal);
                }
                password = !password;
                mPasswordView.setSelection(mPasswordView.getText().toString().length());
                break;
            case R.id.btn_forget_pwd:
                //忘记密码
                Intent forgetIntent = new Intent(this, ForgetPasswordActivity.class);
                startActivity(forgetIntent);
                break;
            case R.id.see_agree_view:
                Intent guidIntent = new Intent(this, WebViewActivity.class);
                guidIntent.putExtra("title", getResources().getString(R.string.user_agree_title_str));
                guidIntent.putExtra("url", CreditWebApi.WEB_PRIVACY_AGREEMENT);
                startActivity(guidIntent);
                break;
        }
    }

    @Override
    protected void setStatusBar() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mStatusbarImageView.getLayoutParams();
        params.height = getImageStatusBarHeight();
        mStatusbarImageView.setLayoutParams(params);
        StatusBarUtil.setTransparentForImageView(this, mTitleLayout);
    }
}
