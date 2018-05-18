package com.cd.dnf.credit.ui.borrow.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.bean.BorrowOrderStatusBean;
import com.cd.dnf.credit.fragment.CreditBaseFragment;
import com.cd.dnf.credit.util.CreditUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jack on 2018/2/12.
 * 申请中 状态
 */

public class BorrowApplyFragment extends CreditBaseFragment {
    @Bind(R.id.money_view)
    TextView moneyView;//贷款金额
    private BorrowOrderStatusBean mOrderStatusBean;//订单状态Bean
    private BorrowFragment mBorrowFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_apply_layout, container, false);
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
    public void setOrderStatus(BorrowOrderStatusBean orderStatus){
        mOrderStatusBean=orderStatus;
        handleOrderStatus();
    }
    private void handleOrderStatus(){
        if(moneyView==null){
            return;
        }
        if(getActivity()==null){
            return;
        }
        String moneyStr= CreditUtil.moneySwitch(mOrderStatusBean.getAmount());
        moneyView.setText(moneyStr);
    }
}
