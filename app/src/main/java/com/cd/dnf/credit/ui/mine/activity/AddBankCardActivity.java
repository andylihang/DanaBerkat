package com.cd.dnf.credit.ui.mine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.activity.CreditSwipBackActivity;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.bean.BankBean;
import com.cd.dnf.credit.bean.CreditNotification;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.ui.borrow.actvitiy.BankActivity;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.CreditNotifyType;
import com.cd.dnf.credit.view.ToastUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by jack on 2018/1/31.
 * 添加银行卡
 */

public class AddBankCardActivity extends CreditSwipBackActivity {
    @Bind(R.id.title_layout)
    RelativeLayout mTitleLayout;
    @Bind(R.id.status_bar_view)
    ImageView mStatusbarImageView;

    @Bind(R.id.bank_view)
    TextView mBankNameView;//银行卡名称
    @Bind(R.id.bank_account_view)
    EditText mBankAccountView;//银行账户
    @Bind(R.id.bank_user_name_view)
    EditText mBankUserNameView;//开户名
    private BankBean mChooseBank;//选择的银行
    private final int CHOOSE_BANK = 0x79;//选择银行
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card_layout);
        ButterKnife.bind(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(AnalyticUtil.BANK_CARD_PAGE, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_BANK) {
                mChooseBank = data.getParcelableExtra("chooseBank");
                mBankNameView.setText(mChooseBank.getBankName());
            }
        }
    }

    private void saveBindCard() {
        String accountStr = mBankAccountView.getText().toString();//银行卡号
        String userNameStr = mBankUserNameView.getText().toString();//开户名
        if (mChooseBank == null) {
            //未选择银行卡
            ToastUtil.showShort(R.string.choose_open_bank);
            return;
        }
        if (TextUtils.isEmpty(accountStr)) {
            ToastUtil.showShort(R.string.input_bank_accout_tip);
            return;
        }
        if (TextUtils.isEmpty(userNameStr)) {
            ToastUtil.showShort(R.string.input_bank_open_account_name_tip);
            return;
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("bankCode", "" + mChooseBank.getBankCode());
        hashMap.put("bankName", "" + mChooseBank.getBankName());
        hashMap.put("cardUsername", "" + userNameStr);
        hashMap.put("cardNumber", "" + accountStr);
        String jsonStr = JSON.toJSONString(hashMap);
        OkGo.<String>post(CreditApi.API_HANDLE_MINE_BANK).upJson(jsonStr).tag("addMineBank").execute(
                new CreditApiRequstCallback(this, true) {
                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        //添加成功
                        CreditNotification notification = new CreditNotification();
                        notification.setNotifyType(CreditNotifyType.NOTIFY_BIND_CARD_CHANGE);
                        notification.setNotifyData("");
                        EventBus.getDefault().post(notification);
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
        OkGo.getInstance().cancelTag("addMineBank");
    }

    @OnClick(value = {R.id.back_layout, R.id.bank_layout, R.id.btn_save})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                finish();
                break;
            case R.id.bank_layout:
                //获取银行卡
                Intent bankIntent = new Intent(this, BankActivity.class);
                startActivityForResult(bankIntent, CHOOSE_BANK);
                break;
            case R.id.btn_save:
                saveBindCard();
                if (mFirebaseAnalytics != null) {
                    mFirebaseAnalytics.logEvent(AnalyticUtil.BIND_CARD_CLICK, null);
                }
                break;
        }
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mStatusbarImageView.getLayoutParams();
        params.height = getImageStatusBarHeight();
        mStatusbarImageView.setLayoutParams(params);
        StatusBarUtil.setTransparentForImageView(this, mTitleLayout);
    }
}
