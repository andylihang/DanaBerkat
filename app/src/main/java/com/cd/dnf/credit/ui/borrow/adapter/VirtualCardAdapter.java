package com.cd.dnf.credit.ui.borrow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.bean.BankInfoBean;
import com.cd.dnf.credit.bean.VirtualBankBean;
import com.cd.dnf.credit.ui.mine.activity.ModifyBankCardActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jack on 2018/1/31.
 */

public class VirtualCardAdapter extends RecyclerView.Adapter<VirtualCardAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<VirtualBankBean> mDataSource = new ArrayList<>();

    public VirtualCardAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setDataSource(List<VirtualBankBean> dataSource) {
        mDataSource = dataSource;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_virtual_bank_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VirtualBankBean bankBean = mDataSource.get(position);
        holder.mBankCodeView.setText(bankBean.getBankCode());
        holder.mCardNumberView.setText(bankBean.getCardNumber());
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.bank_name_view)
        TextView mBankCodeView;//银行卡名字
        @Bind(R.id.bank_number_view)
        TextView mCardNumberView;//卡号

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
