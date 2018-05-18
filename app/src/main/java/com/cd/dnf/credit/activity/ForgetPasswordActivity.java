package com.cd.dnf.credit.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.bean.KaptchaBean;
import com.cd.dnf.credit.interfaces.KaptchaInterface;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.BuildConstant;
import com.cd.dnf.credit.util.CreditUtil;
import com.cd.dnf.credit.util.KeyBordUtils;
import com.cd.dnf.credit.view.CheckCodeButton;
import com.cd.dnf.credit.view.KaptchaDialog;
import com.cd.dnf.credit.view.ToastUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jaeger.library.StatusBarUtil;
import com.jakewharton.rxbinding.view.RxView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by jack on 2018/1/23.
 */

public class ForgetPasswordActivity extends CreditSwipBackActivity  implements KaptchaInterface {
    @Bind(R.id.title_layout)
    RelativeLayout mTitleLayout;
    @Bind(R.id.status_bar_view)
    ImageView mStatusbarImageView;


    @Bind(R.id.content_layout)
    LinearLayout mContentLayout;
    @Bind(R.id.phone_number_view)
    EditText mPhoneNumberView;//手机号
    @Bind(R.id.password_view)
    EditText mPasswordView;//密码
    @Bind(R.id.verify_code_view)
    EditText mVerifyCodeView;//短信验证码
    @Bind(R.id.btn_pic_code)
    CheckCodeButton mCheckCodeButton;
    @Bind(R.id.btn_forget_pwd)
    TextView mBtnForgetPwd;
    private KaptchaDialog mKaptchaDialog;
    private KaptchaBean mKaptcha;
    private boolean password=true;//当时密码输入框 是显示密码
    private FirebaseAnalytics mFirebaseAnalytics;
    @Bind(R.id.btn_show_pwd_view)
    ImageView mBtnShowPwdView;//显示密码眼睛
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_layout);
        ButterKnife.bind(this);
        initRxBinding();
        initKaptchDialog();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void initKaptchDialog(){
        mKaptchaDialog=new KaptchaDialog(this);
        mKaptchaDialog.setKaptchaInterface(this);
    }
    private void forgetPwdServer(){
        //忘记密码网络请求
        String phoneStr = mPhoneNumberView.getText().toString().trim();//手机号
        String verifyCodeStr = mVerifyCodeView.getText().toString().trim();//短信验证码
        String passwordStr = mPasswordView.getText().toString();//密码
        if (TextUtils.isEmpty(phoneStr)) {
            ToastUtil.showShort(R.string.phone_empty_tip_str);
            return;
        }
        if(CreditUtil.PHONE_NUMBER_MIN_LENGTH_NINE>phoneStr.length() ||  phoneStr.length()>CreditUtil.PHONE_NUMBER_MIN_LENGTH){
            ToastUtil.showShort(R.string.input_right_phone_tip_str);
            return;
        }
        if (TextUtils.isEmpty(verifyCodeStr)) {
            ToastUtil.showShort(R.string.verify_code_empty_tip);
            return;
        }
        if (verifyCodeStr.length() != CreditUtil.SMS_CODE_LENGTH) {
            ToastUtil.showShort(R.string.verify_code_error_tip);
            return;
        }
        if (TextUtils.isEmpty(passwordStr)) {
            ToastUtil.showShort(R.string.password_empty_tip_str);
            return;
        }
        if (!CreditUtil.checkRightStylePwd(passwordStr)) {
            //密码输入错误
            ToastUtil.showShort(R.string.password_error_tip_str);
            return;
        }
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("phone","62"+phoneStr);
        hashMap.put("password",""+passwordStr);
        hashMap.put("verifyCode",""+verifyCodeStr);
        String jsonStr=JSON.toJSONString(hashMap);
        OkGo.<String>post(CreditApi.API_FORGET_PASSWORD).upJson(jsonStr).tag("forgetPassword").execute(
                new CreditApiRequstCallback(this, true) {
                    @Override
                    public void RequestSuccess(String body) {
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
        OkGo.getInstance().cancelTag("forgetPassword");
        OkGo.getInstance().cancelTag("forgetpwd_fetchPicCode");
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
        RxView.clicks(mBtnForgetPwd)
                .debounce(BuildConstant.RXBINT_VIEW_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        forgetPwdServer();
                        if(mFirebaseAnalytics!=null){
                            mFirebaseAnalytics.logEvent(AnalyticUtil.FORGET_PASSWORD_CLICK,null);
                        }
                    }
                });
    }
    @OnClick(value = {R.id.back_layout, R.id.btn_pic_code,R.id.show_pwd})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                finish();
                break;
            case R.id.btn_pic_code:
                //获取图片二维码
                //mKaptchaDialog.show();
                String phoneStr = mPhoneNumberView.getText().toString();//手机号
                if (TextUtils.isEmpty(phoneStr)) {
                    ToastUtil.showShort(R.string.phone_empty_tip_str);
                    return;
                }
                if(mFirebaseAnalytics!=null){
                    mFirebaseAnalytics.logEvent(AnalyticUtil.FETCH_PIC_CODE,null);
                }
                fetchPictureCode(true);
                break;
            case R.id.show_pwd:
                if(password){
                    mPasswordView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mBtnShowPwdView.setImageResource(R.mipmap.icon_btn_show_pwd_selected);
                }else {
                    mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mBtnShowPwdView.setImageResource(R.mipmap.icon_btn_show_pwd_normal);

                }
                password=!password;
                mPasswordView.setSelection(mPasswordView.getText().toString().length());
                break;
        }
    }

    private void fetchPictureCode(boolean showDilaog) {
       HttpParams params = new HttpParams();
        OkGo.<String>get(CreditApi.API_FETCH_PICTURE_CODE).params(params).tag("forgetpwd_fetchPicCode").execute(
                new CreditApiRequstCallback(this, showDilaog) {
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if(mKaptchaDialog!=null){
                            mKaptchaDialog.clearBtnRefresh();
                        }
                    }

                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        mKaptcha=JSON.parseObject(response.getData(),KaptchaBean.class);
                        if(mKaptchaDialog!=null){
                            mKaptchaDialog.setKaptha(mKaptcha);
                            if(!mKaptchaDialog.isShow()){
                                mKaptchaDialog.show();
                            }
                            mKaptchaDialog.clearBtnRefresh();
                        }
                    }

                    @Override
                    public void RequestFail(String body) {
                        if(mKaptchaDialog!=null){
                            mKaptchaDialog.clearBtnRefresh();
                        }
                    }

                });
    }

    @Override
    public void inputKaptcha(String kaptcha,KaptchaBean kaptchaBean) {
        String phoneStr = mPhoneNumberView.getText().toString();//手机号
        if (TextUtils.isEmpty(phoneStr)) {
            ToastUtil.showShort(R.string.phone_empty_tip_str);
            return;
        }

        if(CreditUtil.PHONE_NUMBER_MIN_LENGTH_NINE>phoneStr.length() ||  phoneStr.length()>CreditUtil.PHONE_NUMBER_MIN_LENGTH){
            ToastUtil.showShort(R.string.input_right_phone_tip_str);
            return;
        }
        mCheckCodeButton.startGet();
        //发送短信验证码
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("phone", "62" + phoneStr);
        hashMap.put("captchaId", kaptchaBean.getId());
        hashMap.put("captchaCode", kaptcha);
        String jsonStr = JSON.toJSONString(hashMap);
        OkGo.<String>post(CreditApi.API_SEND_SMS_CODE).upJson(jsonStr).tag("smsCode").execute(
                new CreditApiRequstCallback(this, false) {

                    @Override
                    public void RequestSuccess(String body) {


                    }

                    @Override
                    public void RequestFail(String body) {

                    }

                });
    }

    @Override
    public void refreshKaptcha() {
        fetchPictureCode(false);
    }

    @Override
    protected void setStatusBar() {
        FrameLayout.LayoutParams params=(FrameLayout.LayoutParams)mStatusbarImageView.getLayoutParams();
        params.height=getImageStatusBarHeight();
        mStatusbarImageView.setLayoutParams(params);
        StatusBarUtil.setTransparentForImageView(this,mTitleLayout);
    }
}
