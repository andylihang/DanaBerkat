package com.cd.dnf.credit.ui.borrow.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.bean.BorrowOrderStatusBean;
import com.cd.dnf.credit.fragment.CreditBaseFragment;
import com.cd.dnf.credit.util.CreditUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jack on 2018/1/29.
 */

public class BorrowAuditFragment extends CreditBaseFragment {
    @Bind(R.id.btn_borrow_view)
    TextView mBtnBorrowView;//点击到借款界面
    @Bind(R.id.money_view)
    TextView moneyView;//贷款金额
    @Bind(R.id.audit_step_in)
    LinearLayout mAuditStepIn;//审核状态Layout
    @Bind(R.id.audit_step_fail)
    LinearLayout mAuditStepFail;//审核失败状态
    @Bind(R.id.loan_step_in)
    LinearLayout mLoadnStepIn;//放款中Layout
    private BorrowOrderStatusBean mOrderStatusBean;//订单状态Bean
    //当前审核状态 0为审核中 1为审核失败
    private BorrowFragment mBorrowFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_audit_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBorrowFragment = (BorrowFragment) getParentFragment();
        if(mOrderStatusBean!=null){
            handleOrderStatus();
        }
    }

    public void setOrderStatus(BorrowOrderStatusBean orderStatus) {
        mOrderStatusBean = orderStatus;
        handleOrderStatus();
    }

    private void handleOrderStatus() {
        if(moneyView==null){
            return;
        }
        if(getActivity()==null){
            return;
        }
        String moneyStr = CreditUtil.moneySwitch(mOrderStatusBean.getAmount());
        moneyView.setText(moneyStr);
        if (mOrderStatusBean.getStatus() == 0) {
            mAuditStepIn.setVisibility(View.VISIBLE);
            mAuditStepFail.setVisibility(View.GONE);
            mLoadnStepIn.setVisibility(View.GONE);
            mBtnBorrowView.setVisibility(View.GONE);
        } else if (mOrderStatusBean.getStatus() == 1) {
            mAuditStepIn.setVisibility(View.GONE);
            mAuditStepFail.setVisibility(View.VISIBLE);
            mLoadnStepIn.setVisibility(View.GONE);
            mBtnBorrowView.setVisibility(View.VISIBLE);
        } else if (mOrderStatusBean.getStatus() == 2) {
            mAuditStepIn.setVisibility(View.GONE);
            mAuditStepFail.setVisibility(View.GONE);
            mLoadnStepIn.setVisibility(View.VISIBLE);
            mBtnBorrowView.setVisibility(View.GONE);
        }
    }

    @OnClick(value = {R.id.btn_borrow_view})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_borrow_view:
                //还款成功 可以继续借款
                mBorrowFragment.gotoBorrow();
                break;
        }
    }
}
