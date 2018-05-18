package com.cd.dnf.credit.ui.borrow.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.activity.WebViewActivity;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.api.CreditWebApi;
import com.cd.dnf.credit.bean.BorrowOrderStatusBean;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.bean.VirtualBankBean;
import com.cd.dnf.credit.fragment.CreditBaseFragment;
import com.cd.dnf.credit.ui.borrow.adapter.VirtualCardAdapter;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.AppPreferences;
import com.cd.dnf.credit.util.BuildConstant;
import com.cd.dnf.credit.util.CreditUtil;
import com.cd.dnf.credit.util.ScreenUtils;
import com.cd.dnf.credit.view.ToastUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jakewharton.rxbinding.view.RxView;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by jack on 2018/2/2.
 * 放款成功  逾期 不逾期
 */

public class BorrowLoanFragment extends CreditBaseFragment {
    @Bind(R.id.add_virtual_bank)
    TextView mBtnAddVirtualBank;//添加虚拟银行
    @Bind(R.id.virtual_layout)
    LinearLayout mVirtualLayout;//固定的还款银行卡
    @Bind(R.id.bank_number_view)
    TextView mVCABankView;//虚拟银行
    @Bind(R.id.money_view)
    TextView moneyView;//金额
    @Bind(R.id.more_than_view)
    TextView mMoreThanView;//逾期天数
    @Bind(R.id.repay_time_view)
    TextView mRepayTimeView;//应还款时间
    @Bind(R.id.money_layout)
    LinearLayout mMoneyLayout;
    @Bind(R.id.header_layout)
    FrameLayout mHeaderLayout;
    private BorrowOrderStatusBean mOrderStatusBean;//订单状态Bean
    private List<VirtualBankBean> mVirtualBankSource = new ArrayList<>();
    private VirtualBankBean virtualBankBean;
    private AppPreferences mAppPreference;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_loan_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mOrderStatusBean = getArguments().getParcelable("order");
        //handleOrderStatus();
        if(mOrderStatusBean!=null){
            handleOrderStatus();
        }
        initRxBinding();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    public void setOrderStatus(BorrowOrderStatusBean orderStatus) {
        mOrderStatusBean = orderStatus;
        handleOrderStatus();
    }

    private void handleOrderStatus() {
        if (moneyView == null) {
            return;
        }
        if(getActivity()==null){
            return;
        }
        String moneyStr = CreditUtil.moneySwitch(mOrderStatusBean.getAmount());
        moneyView.setText(moneyStr);
        String repayTime = CreditUtil.dataSwitchLoan(mOrderStatusBean.getRepaymentTime());
        mRepayTimeView.setText(repayTime);
        if (mOrderStatusBean.getStatus() == 3) {
            //放款成功 不逾期
            mMoreThanView.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHeaderLayout.getLayoutParams();
            params.height =getActivity().getResources().getDimensionPixelSize(R.dimen.borrow_header_height);
            mHeaderLayout.setLayoutParams(params);
        } else if (mOrderStatusBean.getStatus() == 5) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHeaderLayout.getLayoutParams();
            params.height =getActivity().getResources().getDimensionPixelSize(R.dimen.borrow_loan_limit_height);
            mHeaderLayout.setLayoutParams(params);
            //放款成功 逾期
            mMoreThanView.setVisibility(View.VISIBLE);
            String timeStr = getResources().getString(R.string.borrow_loan_over_time_str);
            String formatStr = String.format(timeStr, mOrderStatusBean.getOverdueDays());
            mMoreThanView.setText("" + formatStr);
        }
        mAppPreference = new AppPreferences(getActivity());
        fetchVABank();
    }

    private void fetchVABank() {
        OkGo.<String>get(CreditApi.API_FETCH_VA_BANK).tag("fetchVABank").execute(
                new CreditApiRequstCallback(getActivity(), false) {
                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        mVirtualBankSource = JSON.parseArray(response.getData(), VirtualBankBean.class);
                        handleVALayout();
                    }

                    @Override
                    public void RequestFail(String body) {

                    }

                });
    }

    private void handleVALayout() {
        if (getActivity() != null) {
            mVirtualLayout.removeAllViews();
            for (int i = 0; i < mVirtualBankSource.size(); i++) {
                VirtualBankBean bankBean = mVirtualBankSource.get(i);
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_virtual_bank_layout, null);
                TextView mBankNameView = (TextView) view.findViewById(R.id.bank_name_view);
                TextView mCardNumberView = (TextView) view.findViewById(R.id.bank_number_view);
                mBankNameView.setText(bankBean.getBankName());
                mCardNumberView.setText(bankBean.getCardNumber());
                mVirtualLayout.addView(view);
            }
        }
    }

    private void handleBCABank() {
        mBtnAddVirtualBank.setVisibility(View.GONE);
        mVCABankView.setVisibility(View.VISIBLE);
        mVCABankView.setText(virtualBankBean.getCardNumber());

    }

    private void fetchBCABank() {
        OkGo.<String>get(CreditApi.API_FETCH_BCA_BANK).tag("fetchBCABank").execute(
                new CreditApiRequstCallback(getActivity(), true) {
                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        virtualBankBean = JSON.parseObject(response.getData(), VirtualBankBean.class);
                        handleBCABank();
                    }

                    @Override
                    public void RequestFail(String body) {

                    }

                });
    }

    private void initRxBinding() {
        RxView.clicks(mBtnAddVirtualBank)
                .debounce(BuildConstant.RXBINT_VIEW_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        fetchBCABank();
                    }
                });
    }

    @OnClick(value = {R.id.pay_atm_layout, R.id.internet_guid_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.pay_atm_layout:
                //atm
                goToGuidIntent(getResources().getString(R.string.payment_guid_title_str), CreditWebApi.WEB_URL_GUID_VA);
                if(mFirebaseAnalytics!=null){
                    mFirebaseAnalytics.logEvent(AnalyticUtil.REPAY_GUID_CLICK,null);
                }
                break;
            case R.id.internet_guid_layout:
                //internet
                goToGuidIntent(getResources().getString(R.string.payment_guid_title_str), CreditWebApi.WEB_URL_GUID_VA);
                if(mFirebaseAnalytics!=null){
                    mFirebaseAnalytics.logEvent(AnalyticUtil.REPAY_GUID_CLICK,null);
                }
                break;
        }
    }

    private void goToGuidIntent(String title, String url) {
        Intent guidIntent = new Intent(getActivity(), WebViewActivity.class);
        guidIntent.putExtra("title", title);
        guidIntent.putExtra("url", url);
        startActivity(guidIntent);
    }
}
