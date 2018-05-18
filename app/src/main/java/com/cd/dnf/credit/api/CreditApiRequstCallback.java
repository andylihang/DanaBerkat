package com.cd.dnf.credit.api;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.activity.LoginRegisterActivity;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.util.AppPreferences;
import com.cd.dnf.credit.util.CreditUtil;
import com.cd.dnf.credit.util.NetUtil;
import com.cd.dnf.credit.view.ToastUtil;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

public abstract class CreditApiRequstCallback extends StringCallback {
    private Context context;
    private ProgressDialog dialog = null;
    private boolean dialogEnable = true;
    private final static int SUCCESS_CODE = 0;
    private final static int TOKEN_FAIL = 1011;
    private AppPreferences mPreference;
    private boolean goLogin = true;//当服务端出现登陆过期时候 是否跳转到登陆页面
    private boolean noErrorTip = false;//是否有错误提交

    public CreditApiRequstCallback(Context context) {
        this(context, true);
    }

    public CreditApiRequstCallback(Context context, boolean dialogEnable) {
        this(context, dialogEnable, true);
    }

    public CreditApiRequstCallback(Context context, boolean dialogEnable, boolean goLogin) {
        this(context, dialogEnable, goLogin, false);
    }

    public CreditApiRequstCallback(Context context, boolean dialogEnable, boolean goLogin, boolean noErrorTip) {
        this.context = context;
        this.dialogEnable = dialogEnable;
        this.goLogin = goLogin;
        this.noErrorTip = noErrorTip;
        if (dialogEnable) {
            createDialog();
        }
    }

    private void createDialog() {
        try {
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.network_requesting));
            dialog.setCanceledOnTouchOutside(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(Response<String> response) {
        CreditResponse baseBean = JSON.parseObject(response.body(), CreditResponse.class);
        if (baseBean.getCode() == SUCCESS_CODE) {
            RequestSuccess(response.body());
        } else if (baseBean.getCode() == TOKEN_FAIL) {
            RequestFail(response.body());
            clearUserToken();
            if (goLogin) {
                //用户token过期 需要从新登陆
                Intent intent = new Intent(context, LoginRegisterActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } else {
            if (!noErrorTip) {
                CreditErrorCode.handleErrorCode(baseBean.getCode());
            }
            RequestFail(response.body());
        }
    }

    private void clearUserToken() {
        if(context==null){
            mPreference=new AppPreferences(CreditApplication.getContext());
        }else {
            mPreference = new AppPreferences(context);
        }
        mPreference.save(AppPreferences.USER_TOKEN, "");
        mPreference.save(AppPreferences.USER_ID, "");
    }

    @Override
    public void onStart(Request<String, ? extends Request> request) {
        super.onStart(request);
        if(context==null){
            mPreference=new AppPreferences(CreditApplication.getContext());
        }else {
            mPreference = new AppPreferences(context);
        }
        String token = mPreference.get(AppPreferences.USER_TOKEN, "");
        request.headers("x-access-token", "" + token);
        String version = CreditUtil.getVersion(context);
        request.headers("Api-Version", "" + version);
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onError(Response<String> response) {
        super.onError(response);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (!noErrorTip) {
            if (!NetUtil.isConnectToNet(context)) {
                //没有连接到网络
                ToastUtil.showShort(R.string.network_error);
            } else {
                ToastUtil.showShort(R.string.code_other_str);
            }
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    //请求成功
    public abstract void RequestSuccess(String body);

    //失败
    public abstract void RequestFail(String body);
}
