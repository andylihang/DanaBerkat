package com.cd.dnf.credit.ui.mine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.bean.BorrowOrderStatusBean;
import com.cd.dnf.credit.bean.LoanHistoryBean;
import com.cd.dnf.credit.util.CreditUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jack on 2018/1/31.
 * 借款历史
 */

public class BorrowHistoryAdapter extends RecyclerView.Adapter<BorrowHistoryAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<BorrowOrderStatusBean> mDataSource=new ArrayList<>();//历史数据
    private int totalSize=0;
    public BorrowHistoryAdapter(Context context){
        mContext=context;
        mInflater = LayoutInflater.from(mContext);
    }
    public void setDataSource(List<BorrowOrderStatusBean> dataSource, int size){
        mDataSource=dataSource;
        totalSize=size;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_borrow_history_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BorrowOrderStatusBean historyBean=mDataSource.get(position);
        String moneyStr=CreditUtil.moneySwitch(historyBean.getAmount());
        holder.mMoneyView.setText(moneyStr);
        String timeStr=CreditUtil.dataSwitch(historyBean.getApplyTime());
        holder.mBorrowTimeView.setText(timeStr);
        String statusStr=CreditUtil.borrowStatusSwitch(mContext,historyBean.getStatus());
        holder.mBorrowStatusView.setText(statusStr);
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.money_view)
        TextView mMoneyView;//金额
        @Bind(R.id.borrow_time_view)
        TextView mBorrowTimeView;//借款时间
        @Bind(R.id.borrow_status)
        TextView mBorrowStatusView;//借款状态
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
