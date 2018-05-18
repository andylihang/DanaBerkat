package com.cd.dnf.credit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cd.dnf.credit.R;
import com.jaeger.library.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jack on 2018/4/6.
 */

public class LoginRegisterActivity extends CreditSwipBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register_layout);
        ButterKnife.bind(this);
    }

    @OnClick(value = {R.id.btn_go_login, R.id.btn_go_register})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_go_login:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                break;
            case R.id.btn_go_register:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
                break;
        }
    }

    @Override
    protected void setStatusBar() {
        //StatusBarUtil.setTransparentForImageView(this, null);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.primaryColor),0);
    }
}
