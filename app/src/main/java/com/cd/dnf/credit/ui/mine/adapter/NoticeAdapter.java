package com.cd.dnf.credit.ui.mine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.bean.BorrowOrderStatusBean;
import com.cd.dnf.credit.bean.NoticeBean;
import com.cd.dnf.credit.util.CreditUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jack on 2018/1/31.
 * 消息通知
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<NoticeBean> mDataSource=new ArrayList<>();//消息
    public NoticeAdapter(Context context){
        mContext=context;
        mInflater = LayoutInflater.from(mContext);
    }
    public void setDataSource(List<NoticeBean> dataSource){
        mDataSource=dataSource;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_notice_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NoticeBean noticeBean=mDataSource.get(position);
        String timeStr=CreditUtil.dataSwitch(noticeBean.getTime());
        holder.mTimeView.setText(timeStr);
        holder.mTitleView.setText(noticeBean.getTitle());
        holder.mContentView.setText(noticeBean.getContent());
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.time_view)
        TextView mTimeView;//时间
        @Bind(R.id.title_view)
        TextView mTitleView;//标题
        @Bind(R.id.content_view)
        TextView mContentView;//内容
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
