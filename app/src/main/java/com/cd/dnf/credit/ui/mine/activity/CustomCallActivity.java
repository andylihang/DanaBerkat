package com.cd.dnf.credit.ui.mine.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.activity.CreditSwipBackActivity;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.AppPreferences;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jaeger.library.StatusBarUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jack on 2018/1/26.
 */

public class CustomCallActivity extends CreditSwipBackActivity {
    @Bind(R.id.title_layout)
    RelativeLayout mTitleLayout;
    @Bind(R.id.status_bar_view)
    ImageView mStatusbarImageView;

    @Bind(R.id.custom_phone_view)
    TextView mCustomPhoneView;
    private AppPreferences mAppPreferences;
    private String customCall;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_call_layout);
        ButterKnife.bind(this);
        mAppPreferences=new AppPreferences(this);
        customCall=mAppPreferences.get(AppPreferences.CUSTOM_CALL,"+62 81288385791");
        mCustomPhoneView.setText(getResources().getString(R.string.custom_call_str,customCall));
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(AnalyticUtil.CUSTOM_CALL,null);
    }
    @OnClick(value = {R.id.back_layout,R.id.btn_call})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                finish();
                break;
            case R.id.btn_call:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+customCall));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                if(mFirebaseAnalytics!=null){
                    mFirebaseAnalytics.logEvent(AnalyticUtil.CUSTOM_CALL_CLICK,null);
                }
                break;
        }
    }
    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        FrameLayout.LayoutParams params=(FrameLayout.LayoutParams)mStatusbarImageView.getLayoutParams();
        params.height=getImageStatusBarHeight();
        mStatusbarImageView.setLayoutParams(params);
        StatusBarUtil.setTransparentForImageView(this,mTitleLayout);
    }
}
