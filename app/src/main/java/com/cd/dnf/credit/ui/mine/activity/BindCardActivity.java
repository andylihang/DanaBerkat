package com.cd.dnf.credit.ui.mine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.activity.CreditSwipBackActivity;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.bean.BankInfoBean;
import com.cd.dnf.credit.bean.CreditNotification;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.ui.mine.adapter.BindCardAdapter;
import com.cd.dnf.credit.util.CreditNotifyType;
import com.cd.dnf.credit.view.ToastUtil;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by jack on 2018/1/31.
 */

public class BindCardActivity extends CreditSwipBackActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {
    @Bind(R.id.title_layout)
    RelativeLayout mTitleLayout;
    @Bind(R.id.status_bar_view)
    ImageView mStatusbarImageView;
    @Bind(R.id.refresh)
    BGARefreshLayout mRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.no_data_layout)
    LinearLayout mNoDataLayout;//暂无数据的界面
    private BindCardAdapter mAdapter;
    private List<BankInfoBean> mDataSource=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_card_layout);
        ButterKnife.bind(this);
        setupRecycleview();
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(this, false));
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.beginRefreshing();
        EventBus.getDefault().register(this);
    }
    private void setupRecycleview() {
        mAdapter = new BindCardAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }
    private void handleNoDataSource(){
       if(mDataSource.size()>0){
           mNoDataLayout.setVisibility(View.GONE);
       }else {
           mNoDataLayout.setVisibility(View.VISIBLE);
       }
    }
    private void fetchMineBindCard(){
        OkGo.<String>get(CreditApi.API_HANDLE_MINE_BANK).tag("getMineBank").execute(
                new CreditApiRequstCallback(this, false) {
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mRefreshLayout.endRefreshing();
                        handleNoDataSource();
                    }

                    @Override
                    public void RequestSuccess(String body) {
                        mRefreshLayout.endRefreshing();
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        //查询成功
                        mDataSource=JSON.parseArray(response.getData(),BankInfoBean.class);
                        mAdapter.setDataSource(mDataSource);
                        mAdapter.notifyDataSetChanged();
                        handleNoDataSource();
                    }

                    @Override
                    public void RequestFail(String body) {
                        mRefreshLayout.endRefreshing();
                        handleNoDataSource();
                    }

                });
    }
    //采用通知的方式来进行操作
    @Subscribe
    public void onEventMainThread(CreditNotification notifyBean) {
        if (TextUtils.equals(notifyBean.getNotifyType(), CreditNotifyType.NOTIFY_BIND_CARD_CHANGE)) {
            //提交申请成功 需要进入待审核状态
            mRefreshLayout.beginRefreshing();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //取消网络请求
        OkGo.getInstance().cancelTag("getMineBank");
    }
    @OnClick(value = {R.id.back_layout, R.id.btn_bind_card})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                finish();
                break;
            case R.id.btn_bind_card:
                //添加银行卡
                Intent addCardIntent=new Intent(this,AddBankCardActivity.class);
                startActivity(addCardIntent);
                break;
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        fetchMineBindCard();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        FrameLayout.LayoutParams params=(FrameLayout.LayoutParams)mStatusbarImageView.getLayoutParams();
        params.height=getImageStatusBarHeight();
        mStatusbarImageView.setLayoutParams(params);
        StatusBarUtil.setTransparentForImageView(this,mTitleLayout);
    }
}
