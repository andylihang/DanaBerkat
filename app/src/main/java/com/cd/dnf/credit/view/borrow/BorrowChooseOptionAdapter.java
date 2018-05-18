package com.cd.dnf.credit.view.borrow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.util.ScreenUtils;
import com.cd.dnf.credit.view.borrow.interfaces.BorrowChooseOptionInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jack on 2018/3/15.
 */

public class BorrowChooseOptionAdapter extends RecyclerView.Adapter<BorrowChooseOptionAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mDataSource = new ArrayList<>();
    private int mChooseOption=-1;
    private BorrowChooseOptionInterface mOptionInterface;
    private TextView mChooseView;
    private int optionGravity=Gravity.CENTER_VERTICAL;
    public BorrowChooseOptionAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }
    public void setOptionGravity(int gravity){
        optionGravity=gravity;
    }
    public void setDataSource(List<String> dataSource) {
        mDataSource = dataSource;
    }

    public void setChooseOption(int chooseOption) {
        mChooseOption = chooseOption;
    }
    public void setChooseOptionInterface(BorrowChooseOptionInterface optionInterface){
        mOptionInterface=optionInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_borrow_option_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String option = mDataSource.get(position);
        holder.mOptionView.setText(option);
        holder.mOptionView.setGravity(optionGravity);
        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)holder.mOptionView.getLayoutParams();
        if(position==0){
            if(mDataSource.size()>1){
                //如果数据多于1个 那么下面还有数据  就需要将第一个的 marginbottom去掉
                params.bottomMargin=0;
            }else {
                params.bottomMargin= ScreenUtils.dip2px(10);
            }
        }else if(mDataSource.size()>1&&position==(mDataSource.size()-1)){
              //最后一个数据 那么 就需要marginbottom
             params.bottomMargin=ScreenUtils.dip2px(10);
        }else {
            //其他中间数据 就不需要marginbottom
            params.bottomMargin=0;
        }
        if (mChooseOption!=-1&&mChooseOption==position) {
            holder.mOptionView.setBackgroundResource(R.drawable.bg_choose_option_selected);
            holder.mOptionView.setTextColor(mContext.getResources().getColor(R.color.white));
            mChooseView=holder.mOptionView;
        }else {
            holder.mOptionView.setBackgroundResource(R.drawable.bg_choose_option_normal);
            holder.mOptionView.setTextColor(mContext.getResources().getColorStateList(R.color.borrow_option_choose_color));
        }
        holder.mOptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChooseOption!=position){
                    //不是同一个
                    mChooseOption=position;
                    notifyDataSetChanged();
                }
                if(mOptionInterface!=null){
                    mOptionInterface.chooseOption(mChooseOption);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.option_view)
        TextView mOptionView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
