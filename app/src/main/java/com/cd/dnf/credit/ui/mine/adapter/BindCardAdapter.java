package com.cd.dnf.credit.ui.mine.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.bean.BankInfoBean;
import com.cd.dnf.credit.ui.mine.activity.ModifyBankCardActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jack on 2018/1/31.
 */

public class BindCardAdapter extends RecyclerView.Adapter<BindCardAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<BankInfoBean> mDataSource=new ArrayList<>();
    public BindCardAdapter(Context context){
        mContext=context;
        mInflater = LayoutInflater.from(mContext);
    }
    public void setDataSource(List<BankInfoBean> dataSource){
        mDataSource=dataSource;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_bind_card_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BankInfoBean bankInfoBean=mDataSource.get(position);
        holder.mBankNameView.setText(bankInfoBean.getBankName());
        holder.mCardNumberView.setText(bankInfoBean.getCardNumber());
        holder.mUserNameView.setText(bankInfoBean.getCardUsername());
        holder.mBtnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, ModifyBankCardActivity.class);
                intent.putExtra("bankInfo",bankInfoBean);
                mContext.startActivity(intent);
            }
        });
        holder.mRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ModifyBankCardActivity.class);
                intent.putExtra("bankInfo",bankInfoBean);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.bank_name_view)
        TextView mBankNameView;//银行卡名字
        @Bind(R.id.card_number_view)
        TextView mCardNumberView;//卡号
        @Bind(R.id.user_name_view)
        TextView mUserNameView;//持卡人姓名
        @Bind(R.id.btn_modify)
        TextView mBtnModify;
        @Bind(R.id.root_layout)
        FrameLayout mRootLayout;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
