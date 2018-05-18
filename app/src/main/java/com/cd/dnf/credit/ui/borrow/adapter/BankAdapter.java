package com.cd.dnf.credit.ui.borrow.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.bean.BankBean;
import com.cd.dnf.credit.view.headerlistview.MySectionIndexer;
import com.cd.dnf.credit.view.headerlistview.PinnedHeaderListView;
import com.cd.dnf.credit.view.headerlistview.PinnedHeaderListView.PinnedHeaderAdapter;

import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 2018/1/31.
 */

public class BankAdapter extends BaseAdapter implements PinnedHeaderAdapter, OnScrollListener {
    private List<BankBean> mDataSource = new ArrayList<>();
    private MySectionIndexer mIndexer;
    private Context mContext;
    private int mLocationPosition = -1;

    public BankAdapter(Context context, List<BankBean> dataSource, MySectionIndexer indexer) {
        mContext = context;
        mDataSource = dataSource;
        mIndexer = indexer;
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_bank_layout, null);

            holder = new ViewHolder();
            holder.group_title = (TextView) view.findViewById(R.id.group_title);
            holder.name = (TextView) view.findViewById(R.id.title_view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        BankBean bean = mDataSource.get(position);

        int section = mIndexer.getSectionForPosition(position);
        if (mIndexer.getPositionForSection(section) == position) {
            holder.group_title.setVisibility(View.VISIBLE);
            holder.group_title.setText(bean.getFirstLetter());
        } else {
            holder.group_title.setVisibility(View.GONE);
        }
        holder.name.setText(bean.getBankName());
        return view;
    }

    public static class ViewHolder {
        public TextView group_title;
        public TextView name;
    }

    @Override
    public int getPinnedHeaderState(int position) {
        int realPosition = position;
        if (realPosition < 0
                || (mLocationPosition != -1 && mLocationPosition == realPosition)) {
            return PINNED_HEADER_GONE;
        }
        mLocationPosition = -1;
        int section = mIndexer.getSectionForPosition(realPosition);
        int nextSectionPosition = mIndexer.getPositionForSection(section + 1);
        if (nextSectionPosition != -1
                && realPosition == nextSectionPosition - 1) {
            return PINNED_HEADER_PUSHED_UP;
        }
        return PINNED_HEADER_VISIBLE;
    }

    @Override
    public void configurePinnedHeader(View header, int position, int alpha) {
        if (position<0) {
            return;
        }
        int realPosition = position;
        int section = mIndexer.getSectionForPosition(realPosition);
        String title = (String) mIndexer.getSections()[section];
        ((TextView) header.findViewById(R.id.group_item_view)).setText(title);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);

        }
    }
}
