package com.cd.dnf.credit.ui.mine.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.sdk.android.push.CommonCallback;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.activity.LoginActivity;
import com.cd.dnf.credit.activity.LoginRegisterActivity;
import com.cd.dnf.credit.activity.WebViewActivity;
import com.cd.dnf.credit.api.CreditWebApi;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.CreditNotification;
import com.cd.dnf.credit.fragment.CreditBaseFragment;
import com.cd.dnf.credit.ui.mine.activity.AboutUsActivity;
import com.cd.dnf.credit.ui.mine.activity.BindCardActivity;
import com.cd.dnf.credit.ui.mine.activity.BorrowHistoryActivity;
import com.cd.dnf.credit.ui.mine.activity.CustomCallActivity;
import com.cd.dnf.credit.ui.mine.activity.NoticeActivity;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.AppPreferences;
import com.cd.dnf.credit.util.CheckSoftUpdate;
import com.cd.dnf.credit.util.CreditNotifyType;
import com.cd.dnf.credit.util.CreditUtil;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by jack on 2018/1/23.
 */

public class MineFragment extends CreditBaseFragment {
    @Bind(R.id.version_code_view)
    TextView mVersionCodeView;
    @Bind(R.id.login_out_layout)
    TextView mLoginOutView;//退出登陆
    @Bind(R.id.phone_number_view)
    TextView mPhoneNumberView;
    private CheckSoftUpdate mCheckUpdate;
    private AppPreferences mAppPreference;
    private String userToken = "";//用户token
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    /**
     * 获取当前应用程序的版本号
     */
    private String getVersion() {
        String st = "";
        PackageManager pm = getActivity().getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(getActivity().getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return st;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String version = "v" + getVersion();
        mVersionCodeView.setText(version);
        mCheckUpdate = new CheckSoftUpdate(getActivity(), true);
        mAppPreference = new AppPreferences(getActivity());
        initView();
        EventBus.getDefault().register(this);
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(getActivity());
    }

    private void initView() {
        userToken = mAppPreference.get(AppPreferences.USER_TOKEN, "");
        if (TextUtils.isEmpty(userToken)) {
            mLoginOutView.setVisibility(View.GONE);
            mPhoneNumberView.setText(R.string.mine_go_to_login);
        } else {
            mLoginOutView.setVisibility(View.VISIBLE);
            String phone = mAppPreference.get(AppPreferences.USER_PHONE);
            String phoneStr = CreditUtil.getUserPhone(phone);
            mPhoneNumberView.setText(phoneStr);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCheckUpdate != null) {
            mCheckUpdate.unregisterReceiver();
        }
        EventBus.getDefault().unregister(this);
    }

    //采用通知的方式来进行操作
    @Subscribe
    public void onEventMainThread(CreditNotification notifyBean) {
        if (TextUtils.equals(notifyBean.getNotifyType(), CreditNotifyType.NOTIFY_LOGIN_OK)) {
            //登陆成功 那么就直接
            initView();
        }
    }

    private void showLoginOut() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(getActivity());
        mMaterialDialog.setMessage(R.string.logout_title_str);
        mMaterialDialog.setPositiveButton(R.string.ok_str, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                //退出推送
                if(CreditApplication.getInstance().getPushService()!=null){
                    CreditApplication.getInstance().getPushService().clearNotifications();
                    CreditApplication.getInstance().getPushService().unbindAccount(new CommonCallback() {
                        @Override
                        public void onSuccess(String s) {

                        }

                        @Override
                        public void onFailed(String s, String s1) {

                        }
                    });
                }
                //退出账号
                mAppPreference.save(AppPreferences.USER_TOKEN, "");
                mAppPreference.save(AppPreferences.USER_ID, "");
                mAppPreference.save(AppPreferences.USER_PHONE,"");
                userToken = "";
                initView();
                //通知注销
                CreditNotification notification = new CreditNotification();
                notification.setNotifyType(CreditNotifyType.NOTIFY_LOGIN_OUT);
                notification.setNotifyData("");
                EventBus.getDefault().post(notification);

                Intent mIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(mIntent);
                if(mFirebaseAnalytics!=null){
                    mFirebaseAnalytics.logEvent(AnalyticUtil.LOGIN_OUT_CLICK,null);
                }
            }
        });

        mMaterialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.show();
    }

    @OnClick(value = {R.id.agreement_layout, R.id.custom_call_layout, R.id.about_us_layout, R.id.check_update_layout
            , R.id.bind_card_layout, R.id.borrow_history_layout, R.id.nofity_layout, R.id.login_out_layout, R.id.mine_info_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.agreement_layout:
                //服务协议
           /*     Intent agreeIntent = new Intent(getActivity(), AgreementActvitiy.class);
                startActivity(agreeIntent);*/
                Intent agreeIntent = new Intent(getActivity(), WebViewActivity.class);
                agreeIntent.putExtra("title", getResources().getString(R.string.mine_agreement_str));
                agreeIntent.putExtra("url", CreditWebApi.WEB_URL_USER_ARGREEMENT);
                startActivity(agreeIntent);
                if(mFirebaseAnalytics!=null){
                    mFirebaseAnalytics.logEvent(AnalyticUtil.AGREE_PAGE,null);
                }
                break;
            case R.id.custom_call_layout:
                //客服电话
                Intent customIntent = new Intent(getActivity(), CustomCallActivity.class);
                startActivity(customIntent);
                break;
            case R.id.about_us_layout:
                //关于我们
                Intent aboutIntent = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.check_update_layout:
                //检测版本更新
                mCheckUpdate.checkUpdate();
                if(mFirebaseAnalytics!=null){
                    mFirebaseAnalytics.logEvent(AnalyticUtil.CHECK_VERSION_CLICK,null);
                }
                break;
            case R.id.bind_card_layout:
                //绑定银行卡
                if (!CreditUtil.checkLoginStatus(getActivity())) {
                    Intent bindCardIntent = new Intent(getActivity(), BindCardActivity.class);
                    startActivity(bindCardIntent);
                }
                break;
            case R.id.borrow_history_layout:
                //还款历史
                if (!CreditUtil.checkLoginStatus(getActivity())) {
                    Intent borrowHistoryIntent = new Intent(getActivity(), BorrowHistoryActivity.class);
                    startActivity(borrowHistoryIntent);
                }
                break;
            case R.id.nofity_layout:
                //通知
                if (!CreditUtil.checkLoginStatus(getActivity())){
                    Intent noticeIntent = new Intent(getActivity(), NoticeActivity.class);
                    startActivity(noticeIntent);
                }

                break;
            case R.id.login_out_layout:
                showLoginOut();
                break;
            case R.id.mine_info_layout:
                if (TextUtils.isEmpty(userToken)) {
                    Intent loginIntent = new Intent(getActivity(), LoginRegisterActivity.class);
                    startActivity(loginIntent);
                }
                break;
        }
    }
}
