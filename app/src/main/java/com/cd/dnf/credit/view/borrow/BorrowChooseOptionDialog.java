package com.cd.dnf.credit.view.borrow;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.view.borrow.interfaces.BorrowChooseOptionInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jack on 2018/3/15.
 */

public class BorrowChooseOptionDialog {
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    private int width = WindowManager.LayoutParams.MATCH_PARENT;
    private int height = WindowManager.LayoutParams.WRAP_CONTENT;
    private List<String> dataSource = new ArrayList<>();//数据
    private Dialog mDialog;
    private Context mContext;
    private int chooseOption = -1;//选中的位置
    private BorrowChooseOptionInterface mChooseInterface;
    private BorrowChooseOptionAdapter mAdapter;

    public BorrowChooseOptionDialog(Context context, List<String> dataSource) {
        this(context, dataSource, Gravity.CENTER_VERTICAL);
    }

    public BorrowChooseOptionDialog(Context context, List<String> dataSource, int gravity) {
        mContext = context;
        this.dataSource = dataSource;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (ViewGroup) inflater.inflate(R.layout.dialog_borrow_choose_layout, null);
        ButterKnife.bind(this, view);
        mDialog = new Dialog(mContext, R.style.BorrowChooseChooseDialog);
        Window win = mDialog.getWindow();
        win.setGravity(Gravity.CENTER);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setContentView(view);
        setupRecycleview(gravity);
    }

    private void setupRecycleview(int gravity) {
        mAdapter = new BorrowChooseOptionAdapter(mContext);
        mAdapter.setChooseOption(chooseOption);
        mAdapter.setDataSource(dataSource);
        mAdapter.setOptionGravity(gravity);
        mAdapter.setChooseOptionInterface(mChooseInterface);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setChooseOption(int chooseOption) {
        this.chooseOption = chooseOption;
        mAdapter.setChooseOption(chooseOption);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(chooseOption);
    }

    public void setChooseOptionInterface(BorrowChooseOptionInterface chooseOption) {
        mChooseInterface = chooseOption;
        if (mAdapter != null) {
            mAdapter.setChooseOptionInterface(mChooseInterface);
        }
    }

    public void initDataSource(int choosePosition, List<String> dataSource) {
        this.dataSource = dataSource;
        this.chooseOption = choosePosition;
        mAdapter.setChooseOption(chooseOption);
        mAdapter.setDataSource(dataSource);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(choosePosition);
    }

    public void show() {
        mDialog.show();
        Window win = mDialog.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = width;
        params.height = height;
        win.setAttributes(params);
    }

    public boolean isShow() {
        return mDialog.isShowing();
    }

    public void dismiss() {
        mDialog.dismiss();
    }
}
