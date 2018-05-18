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
import com.cd.dnf.credit.bean.BankInfoBean;
import com.cd.dnf.credit.bean.CreditNotification;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.ui.borrow.actvitiy.BankActivity;
import com.cd.dnf.credit.util.CreditNotifyType;
import com.cd.dnf.credit.view.ToastUtil;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by jack on 2018/2/2.
 */

public class ModifyBankCardActivity extends CreditSwipBackActivity {
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
    private BankInfoBean mBankInfo;
    private BankBean mChooseBank;//选择的银行
    private final int CHOOSE_BANK=0x76;//选择银行
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_bank_card_layout);
        ButterKnife.bind(this);
        mBankInfo=getIntent().getParcelableExtra("bankInfo");
        mChooseBank=new BankBean();
        mChooseBank.setBankCode(mBankInfo.getBankCode());
        mChooseBank.setBankName(mBankInfo.getBankName());
        handleBankInfo();
    }
    private void handleBankInfo(){
        mBankNameView.setText(mChooseBank.getBankName());
        mBankAccountView.setText(mBankInfo.getCardNumber());
        mBankUserNameView.setText(mBankInfo.getCardUsername());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==CHOOSE_BANK){
                mChooseBank=data.getParcelableExtra("chooseBank");
                mBankNameView.setText(mChooseBank.getBankName());
            }
        }
    }
    private void modifyBindCard(){
        String accountStr=mBankAccountView.getText().toString();//银行卡号
        String userNameStr=mBankUserNameView.getText().toString();//开户名
        if (TextUtils.isEmpty(accountStr)) {
            ToastUtil.showShort(R.string.input_bank_accout_tip);
            return;
        }
        if (TextUtils.isEmpty(userNameStr)) {
            ToastUtil.showShort(R.string.input_bank_open_account_name_tip);
            return;
        }
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("id",""+mBankInfo.getId());
        hashMap.put("bankCode",""+mChooseBank.getBankCode());
        hashMap.put("bankName",""+mChooseBank.getBankName());
        hashMap.put("cardUsername",""+userNameStr);
        hashMap.put("cardNumber",""+accountStr);
        String jsonStr= JSON.toJSONString(hashMap);
        OkGo.<String>put(CreditApi.API_HANDLE_MINE_BANK).upJson(jsonStr).tag("moidfyMineBank").execute(
                new CreditApiRequstCallback(this, true) {
                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        //修改成功
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
        OkGo.getInstance().cancelTag("moidfyMineBank");
    }
    @OnClick(value = {R.id.back_layout,R.id.btn_save,R.id.bank_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                finish();
                break;
            case R.id.btn_save:
                modifyBindCard();
                break;
            case R.id.bank_layout:
                //获取银行卡
                Intent bankIntent=new Intent(this, BankActivity.class);
                startActivityForResult(bankIntent,CHOOSE_BANK);
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
