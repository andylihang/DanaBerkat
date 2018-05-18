package com.cd.dnf.credit.ui.borrow.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.fragment.CreditBaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jack on 2018/1/29.
 * 还款成功
 */

public class RepaySuccessFragment extends CreditBaseFragment {
    private BorrowFragment mBorrowFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repay_success_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBorrowFragment=(BorrowFragment)getParentFragment();
    }
    @OnClick(value = {R.id.btn_borrow})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_borrow:
                //还款成功 可以继续借款
                mBorrowFragment.gotoBorrow();
                break;
        }
    }
}
