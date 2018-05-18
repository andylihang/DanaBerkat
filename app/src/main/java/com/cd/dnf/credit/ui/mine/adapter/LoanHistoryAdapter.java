package com.cd.dnf.credit.ui.mine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.bean.LoanHistoryBean;
import com.cd.dnf.credit.util.CreditUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jack on 2018/1/31.
 */

public class LoanHistoryAdapter extends RecyclerView.Adapter<LoanHistoryAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<LoanHistoryBean> mDataSource=new ArrayList<>();//历史数据
    private int totalSize=0;
    public LoanHistoryAdapter(Context context){
        mContext=context;
        mInflater = LayoutInflater.from(mContext);
    }
    public void setDataSource(List<LoanHistoryBean> dataSource, int size){
        mDataSource=dataSource;
        totalSize=size;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_loan_history_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LoanHistoryBean historyBean=mDataSource.get(position);
        holder.mNumberView.setText(""+(totalSize-position)+"/"+totalSize);
        String money=CreditUtil.moneySwitch(historyBean.getAmount())+"";
        holder.mMoneyView.setText(money);
        holder.mReplyTimeView.setText(CreditUtil.dataSwitch(Long.parseLong(historyBean.getRepayTime())));
        String endTime=CreditUtil.dataSwitchDay(Long.parseLong(historyBean.getEndTime()));
        String endTimeStr=mContext.getResources().getString(R.string.end_time_tip_str)+endTime;
        holder.mEndTimeView.setText(endTimeStr);
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.money_view)
        TextView mMoneyView;//金额
        @Bind(R.id.replytime)
        TextView mReplyTimeView;//还款时间
        @Bind(R.id.endtime)
        TextView mEndTimeView;//到期时间
        @Bind(R.id.number_view)
        TextView mNumberView;//显示数值
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
