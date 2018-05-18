package com.cd.dnf.credit.ui.mine.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.activity.CreditSwipBackActivity;
import com.jaeger.library.StatusBarUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jack on 2018/1/26.
 */

public class AgreementActvitiy extends CreditSwipBackActivity {
    @Bind(R.id.title_layout)
    RelativeLayout mTitleLayout;
    @Bind(R.id.status_bar_view)
    ImageView mStatusbarImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_layout);
        ButterKnife.bind(this);
    }



    @OnClick(value = {R.id.back_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                finish();
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
