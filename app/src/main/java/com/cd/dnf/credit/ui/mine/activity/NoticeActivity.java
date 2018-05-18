package com.cd.dnf.credit.ui.mine.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.bean.NoticeBean;
import com.cd.dnf.credit.bean.PageBean;
import com.cd.dnf.credit.ui.mine.adapter.NoticeAdapter;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.view.ToastUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * Created by jack on 2018/1/26.
 */

public class NoticeActivity extends CreditSwipBackActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {
    @Bind(R.id.title_layout)
    RelativeLayout mTitleLayout;
    @Bind(R.id.status_bar_view)
    ImageView mStatusbarImageView;

    @Bind(R.id.refresh)
    BGARefreshLayout mRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.no_data_layout)
    LinearLayout mNoDataLayout;
    private NoticeAdapter mAdapter;
    private int totalCout;//总数据量
    private int currentPage = 1;//当前页号
    private List<NoticeBean> mDataSource=new ArrayList<>();
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_layout);
        ButterKnife.bind(this);
        setupRecycleview();
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(this, true));
        mRefreshLayout.setDelegate(this);
        mRefreshLayout.beginRefreshing();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(AnalyticUtil.NOTICE_PAGE,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消网络请求
        OkGo.getInstance().cancelTag("getMineMessageList");
    }

    private void setupRecycleview() {
        mAdapter=new NoticeAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }
    @OnClick(value = {R.id.back_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                finish();
                break;
        }
    }
    private void handleNoDataSource(){
        if(mDataSource.size()>0){
            mNoDataLayout.setVisibility(View.GONE);
        }else {
            mNoDataLayout.setVisibility(View.VISIBLE);
        }
    }
    private void fetchNotification(final int page){
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("start",""+((page-1)* CreditApplication.pageSize));
        hashMap.put("length",""+CreditApplication.pageSize);
        String jsonStr= JSON.toJSONString(hashMap);
        OkGo.<String>post(CreditApi.API_FETCH_MINE_MESSAGE).upJson(jsonStr).tag("getMineMessageList").execute(
                new CreditApiRequstCallback(this, false) {
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (page == 1) {
                            mRefreshLayout.endRefreshing();
                            handleNoDataSource();
                        } else {
                            mRefreshLayout.endLoadingMore();
                        }
                    }

                    @Override
                    public void RequestSuccess(String body) {
                        mRefreshLayout.endRefreshing();
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        PageBean pageBean=JSON.parseObject(response.getData(),PageBean.class);
                        totalCout=pageBean.getTotal();
                        if (page == 1) {
                            currentPage = 1;
                            mDataSource = JSON.parseArray(pageBean.getData(), NoticeBean.class);
                            mRefreshLayout.endRefreshing();
                            handleNoDataSource();
                        } else {
                            currentPage++;
                            mDataSource.addAll(JSON.parseArray(pageBean.getData(), NoticeBean.class));
                            mRefreshLayout.endLoadingMore();
                        }
                        mAdapter.setDataSource(mDataSource);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void RequestFail(String body) {
                        if (page == 1) {
                            mRefreshLayout.endRefreshing();
                            handleNoDataSource();
                        } else {
                            mRefreshLayout.endLoadingMore();
                        }
                    }

                });
    }
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        fetchNotification(1);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (mDataSource.size() < totalCout) {
            fetchNotification(currentPage + 1);
            return true;
        }
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
