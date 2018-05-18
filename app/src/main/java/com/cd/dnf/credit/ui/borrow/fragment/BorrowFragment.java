package com.cd.dnf.credit.ui.borrow.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.BorrowOperateBean;
import com.cd.dnf.credit.bean.BorrowOrderBean;
import com.cd.dnf.credit.bean.BorrowOrderStatusBean;
import com.cd.dnf.credit.bean.CreditNotification;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.fragment.CreditBaseFragment;
import com.cd.dnf.credit.ui.mine.activity.LoanHistoryActivity;
import com.cd.dnf.credit.ui.mine.activity.NoticeActivity;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.AppPreferences;
import com.cd.dnf.credit.util.CreditNotifyType;
import com.cd.dnf.credit.util.CreditUtil;
import com.cd.dnf.credit.util.ScreenUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by jack on 2018/1/23.
 */

public class BorrowFragment extends CreditBaseFragment {
    @Bind(R.id.title_layout)
    RelativeLayout mTitleLayout;
    @Bind(R.id.repay_title_layout)
    RelativeLayout mRepayTitleLayout;
    @Bind(R.id.status_bar_view)
    ImageView mStatusbarImageView;

    @Bind(R.id.point_view)
    ImageView mNoticePointView;//红点提示view
    @Bind(R.id.btn_replay_history)
    TextView mReplayHistoryView;//还款历史
    //0 审核中 1 审核失败 2 放款中 3 放款成功(不逾期) 4 放款失败 5 逾期 6 已完成     （放款中 在页面中展示为审核中)  通过状态来     8 申请中 状态
    private BorrowIngFragment borrowIngFragment;//借款
    private BorrowAuditFragment mAuditFragment;//审核
    private RepaySuccessFragment mRepayOkFragment;//还款成功
    private BorrowLoanFailFragment mBorrowLoanFailFragment;//放款失败
    private BorrowLoanFragment mBorrowLoanFragment;//放款成功 逾期 不逾期
    private BorrowPatchFragment mBorrowPatchFragment;//补件
    private BorrowApplyFragment mBorrowApplyFragment;//申请中的状态
    private CreditBaseFragment mCurrentFragment;//当前的Fragment
    private BorrowOrderStatusBean mOrderStatusBean;//订单状态Bean
    private AppPreferences mPreference;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setStatusBar();
        mPreference = new AppPreferences(getActivity());
        String token = mPreference.get(AppPreferences.USER_TOKEN, "");
        if (TextUtils.isEmpty(token)) {
            mOrderStatusBean = null;
        } else {
            mOrderStatusBean = CreditApplication.getInstance().getBorrowOrderStatus();
        }
        handleOrderStatus();
        EventBus.getDefault().register(this);
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshBorrowStatus();
    }

    private void handleTitleLayout(boolean isReplyOK) {
        if (isReplyOK) {
            mTitleLayout.setVisibility(View.GONE);
            mRepayTitleLayout.setVisibility(View.VISIBLE);
        } else {
            mTitleLayout.setVisibility(View.VISIBLE);
            mRepayTitleLayout.setVisibility(View.GONE);
        }
        setStatusBar();
    }

    public void refreshBorrowStatus() {
        String token = mPreference.get(AppPreferences.USER_TOKEN, "");
        if (TextUtils.isEmpty(token)) {
            mOrderStatusBean = null;
            handleOrderStatus();
        } else {
/*            if (mCurrentFragment != null && borrowIngFragment != null && mOrderStatusBean != null && mCurrentFragment == borrowIngFragment) {

            } else {
                fetchBorrowOrder();
            }*/
            fetchBorrowOrder();
        }
        handleNotification();
    }

    private void handleOrderStatus() {
        mReplayHistoryView.setVisibility(View.GONE);
        if (mOrderStatusBean == null) {
            handleTitleLayout(false);
            //当前没有数据  那么就是还没有订单就是在需要去申请
            if (borrowIngFragment == null) {
                borrowIngFragment = new BorrowIngFragment();
                Bundle bundle = new Bundle();
                bundle.putString("planId", "");
                borrowIngFragment.setArguments(bundle);
            }
            mCurrentFragment = borrowIngFragment;
        } else {
            if (mOrderStatusBean.getStatus() == 6) {
                handleTitleLayout(true);
            } else {
                handleTitleLayout(false);
            }
            BorrowOperateBean operateBean = null;
            String userId = mPreference.get(AppPreferences.USER_ID, "");
            if (!TextUtils.isEmpty(userId) && mOrderStatusBean != null) {
                List<BorrowOperateBean> operateSource = DataSupport.where("userId = ? and orderStatus = ? and orderId = ?", userId, "" + mOrderStatusBean.getStatus(), "" + mOrderStatusBean.getOrderId()).find(BorrowOperateBean.class);
                if (operateSource != null && operateSource.size() > 0) {
                    operateBean = operateSource.get(0);
                }
            }
/*            Bundle borrowBundle = new Bundle();
            borrowBundle.putParcelable("order", mOrderStatusBean);*/
            switch (mOrderStatusBean.getStatus()) {
                case 0:
                    //审核中
                case 1:
                    //审核失败
                case 2:
                    //放款中
                    if (operateBean != null && TextUtils.equals(operateBean.getOrderId(), mOrderStatusBean.getOrderId())) {
                        gotoBorrow(true);
                        return;
                    } else {
                        if (mAuditFragment == null) {
                            mAuditFragment = new BorrowAuditFragment();
                            mAuditFragment.setOrderStatus(mOrderStatusBean);
                        } else {
                            mAuditFragment.setOrderStatus(mOrderStatusBean);
                        }
                        mCurrentFragment = mAuditFragment;
                    }
/*                    if (operateBean == null || (mUserOrderBean != null &&!TextUtils.equals(operateBean.getOrderId(), mUserOrderBean.getOrderId()))) {

                    } else {

                    }*/
/*                    mAuditFragment = new BorrowAuditFragment();
                    mAuditFragment.setArguments(borrowBundle);
                    mCurrentFragment = mAuditFragment;*/
                    break;
                case 3:
                    //放款成功 不逾期
                case 5:
                    //放款成功 逾期
                    mReplayHistoryView.setVisibility(View.VISIBLE);
                    if (mBorrowLoanFragment == null) {
                        mBorrowLoanFragment = new BorrowLoanFragment();
                        mBorrowLoanFragment.setOrderStatus(mOrderStatusBean);
                    } else {
                        mBorrowLoanFragment.setOrderStatus(mOrderStatusBean);
                    }
                    mCurrentFragment = mBorrowLoanFragment;
                    break;
                case 4:
                    //放款失败
                    if (operateBean != null && TextUtils.equals(operateBean.getOrderId(), mOrderStatusBean.getOrderId())) {
                        gotoBorrow(true);
                        return;
                    } else {
                        if (mBorrowLoanFailFragment == null) {
                            mBorrowLoanFailFragment = new BorrowLoanFailFragment();
                            mBorrowLoanFailFragment.setOrderStatus(mOrderStatusBean);
                        } else {
                            mBorrowLoanFailFragment.setOrderStatus(mOrderStatusBean);
                        }
                        mCurrentFragment = mBorrowLoanFailFragment;
                    }
/*                    mBorrowLoanFailFragment = new BorrowLoanFailFragment();
                    mBorrowLoanFailFragment.setArguments(borrowBundle);
                    mCurrentFragment = mBorrowLoanFailFragment;*/
                    break;
                case 6:
                    //已完成
                    if (operateBean != null && TextUtils.equals(operateBean.getOrderId(), mOrderStatusBean.getOrderId())) {
                        gotoBorrow(true);
                        return;
                    } else {
                        if (mRepayOkFragment == null) {
                            mRepayOkFragment = new RepaySuccessFragment();
                        }
                        mCurrentFragment = mRepayOkFragment;
                    }

/*                    mRepayOkFragment = new RepaySuccessFragment();
                    mCurrentFragment = mRepayOkFragment;*/
                    break;
                case 7:
                    //补件  补件需要从新提交订单
                    if (operateBean != null && TextUtils.equals(operateBean.getOrderId(), mOrderStatusBean.getOrderId())) {
                        gotoBorrow(true);
                        return;
                    } else {
                        if (mBorrowPatchFragment == null) {
                            mBorrowPatchFragment = new BorrowPatchFragment();
                            mBorrowPatchFragment.setOrderStatus(mOrderStatusBean);
                        } else {
                            mBorrowPatchFragment.setOrderStatus(mOrderStatusBean);
                        }
                        mCurrentFragment = mBorrowPatchFragment;
                    }
/*                    mBorrowPatchFragment = new BorrowPatchFragment();
                    mBorrowPatchFragment.setArguments(borrowBundle);
                    mCurrentFragment = mBorrowPatchFragment;*/
                    break;
                case 8:
                    //申请中
                    if (mBorrowApplyFragment == null) {
                        mBorrowApplyFragment = new BorrowApplyFragment();
                        mBorrowApplyFragment.setOrderStatus(mOrderStatusBean);
                    } else {
                        mBorrowApplyFragment.setOrderStatus(mOrderStatusBean);
                    }
                    mCurrentFragment = mBorrowApplyFragment;
                    break;

            }
        }
        if (mCurrentFragment != null) {
            try {
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, mCurrentFragment).show(mCurrentFragment).commitAllowingStateLoss();
            }catch (Exception e){
                Log.e("sakura","----e="+e.toString());
            }
        }
    }

    private void fetchBorrowOrder() {
        OkGo.<String>get(CreditApi.API_FETCH_BORROW_ORDER_DETAIL).tag("fetchBorrowOrderDetail").execute(
                new CreditApiRequstCallback(getActivity(), false, false, false) {
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        handleOrderStatus();
                    }

                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        mOrderStatusBean = JSON.parseObject(response.getData(), BorrowOrderStatusBean.class);
                        CreditApplication.getInstance().setBorrowOrderStatus(mOrderStatusBean);
                        //定义数据
                        handleOrderStatus();
                    }

                    @Override
                    public void RequestFail(String body) {
                        handleOrderStatus();
                    }

                });
    }

    //是否存储操作
    private void gotoBorrow(boolean operate) {
        handleTitleLayout(false);
        //进入借款Fragment
        if (mCurrentFragment==null||borrowIngFragment==null||mCurrentFragment != borrowIngFragment) {
            borrowIngFragment = new BorrowIngFragment();
            String planId = "";//计划Id
            int borrowStatus = -1;//订单状态
            if (mOrderStatusBean != null) {
                planId = mOrderStatusBean.getPlanId();
                borrowStatus = mOrderStatusBean.getStatus();
            }
            String userId = mPreference.get(AppPreferences.USER_ID, "");
            List<BorrowOrderBean> dataSource = DataSupport.where("userId = ?", userId).find(BorrowOrderBean.class);
            if (dataSource.size() > 0) {
                BorrowOrderBean borrowOrderBean = dataSource.get(0);
                planId=borrowOrderBean.getPlanId();
            }
            Bundle bundle = new Bundle();
            bundle.putString("planId", planId);
            bundle.putInt("orderStatus", borrowStatus);
            borrowIngFragment.setArguments(bundle);
            mCurrentFragment = borrowIngFragment;
        }
        try {
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, mCurrentFragment).show(mCurrentFragment).commitAllowingStateLoss();
        }catch (Exception e){

        }
        }



    public void gotoBorrow() {
        handleTitleLayout(false);
        //进入借款Fragment
        borrowIngFragment = new BorrowIngFragment();
        String planId = "";//计划Id
        int borrowStatus = -1;//订单状态
        if (mOrderStatusBean != null) {
            planId = mOrderStatusBean.getPlanId();
            borrowStatus = mOrderStatusBean.getStatus();
        }
        Bundle bundle = new Bundle();
        bundle.putString("planId", planId);
        bundle.putInt("orderStatus", borrowStatus);
        borrowIngFragment.setArguments(bundle);
        mCurrentFragment = borrowIngFragment;
        try {
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, mCurrentFragment).show(mCurrentFragment).commitAllowingStateLoss();
        }catch (Exception e){

        }
        //判断当前状态 需要进行存储
        String userId = mPreference.get(AppPreferences.USER_ID, "");
        if (!TextUtils.isEmpty(userId) && mOrderStatusBean != null) {
            BorrowOperateBean operateBean = new BorrowOperateBean();
            operateBean.setUserId(userId);
            operateBean.setOrderStatus(borrowStatus);
            operateBean.setOrderId(mOrderStatusBean.getOrderId());
            operateBean.save();
        }
    }

    //采用通知的方式来进行操作
    @Subscribe
    public void onEventMainThread(CreditNotification notifyBean) {
        if (TextUtils.equals(notifyBean.getNotifyType(), CreditNotifyType.NOTIFY_BORROW_ORDER_APPLY_OK)) {
            //这里因为后台状态还没有 现在显示订单审核状态
            //fetchBorrowOrder();
            mOrderStatusBean=(BorrowOrderStatusBean)notifyBean.getNotifyData();
            CreditApplication.getInstance().setBorrowOrderStatus(mOrderStatusBean);
        } else if (TextUtils.equals(notifyBean.getNotifyType(), CreditNotifyType.NOTIFY_LOGIN_OUT)) {
            mOrderStatusBean = null;
            CreditApplication.getInstance().setBorrowOrderStatus(mOrderStatusBean);
            handleOrderStatus();
            mNoticePointView.setVisibility(View.GONE);
            //fetchBorrowOrder();
        } else if (TextUtils.equals(notifyBean.getNotifyType(), CreditNotifyType.NOTIFY_JPUSH_ORDER_STATUS_CHANGE)) {
            //推送过来表示订单状态改变
            handleNotification();
            fetchBorrowOrder();
        } else if (TextUtils.equals(notifyBean.getNotifyType(), CreditNotifyType.NOTIFY_JPUSH_CHANGE)) {
            //处理推送信息
            handleNotification();
        }
    }

    private void handleNotification() {
        String userId = mPreference.get(AppPreferences.USER_ID, "");
        String url = CreditApi.API_FETCH_UNREAD_MESSAGE + "/" + userId;
        OkGo.<String>get(url).tag("fetchUnReadMessage").execute(
                new CreditApiRequstCallback(getActivity(), false, false, true) {
                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        try {
                            JSONObject jsonObject = new JSONObject(response.getData());
                            int unReadCount = jsonObject.getInt("unReadCount");
                            if (unReadCount > 0) {
                                mNoticePointView.setVisibility(View.VISIBLE);
                            } else {
                                mNoticePointView.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void RequestFail(String body) {
                        handleOrderStatus();
                    }

                });



/*        String userId = mPreference.get(AppPreferences.USER_ID, "");
        List<NotificationBean> notificationSource = DataSupport.where("userId = ?", userId).find(NotificationBean.class);
        if (notificationSource.size() > 0) {
            //有数据 那么就显示红点
            mNoticePointView.setVisibility(View.VISIBLE);
        } else {
            mNoticePointView.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消网络请求
        OkGo.getInstance().cancelTag("fetchBorrowOrderDetail");
        EventBus.getDefault().unregister(this);
    }

    private void setStatusBar() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mStatusbarImageView.getLayoutParams();
        params.height = getImageStatusBarHeight();
        mStatusbarImageView.setLayoutParams(params);
        if(getActivity()!=null){
            if (mTitleLayout.getVisibility() == View.VISIBLE) {
                StatusBarUtil.setTransparentForImageView(getActivity(), mTitleLayout);
            } else {
                StatusBarUtil.setTransparentForImageView(getActivity(), mRepayTitleLayout);
            }
        }
    }

    @OnClick(value = {R.id.btn_replay_history, R.id.notice_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_replay_history:
                //还款历史
                boolean goLogin = CreditUtil.checkLoginStatus(getActivity());
                if (!goLogin) {
                    Intent mIntent = new Intent(getActivity(), LoanHistoryActivity.class);
                    startActivity(mIntent);
                }
                break;
            case R.id.notice_layout:
                //通知
                boolean goLogin2 = CreditUtil.checkLoginStatus(getActivity());
                if (!goLogin2) {
                    String userId = mPreference.get(AppPreferences.USER_ID, "");
                    Intent noticeIntent = new Intent(getActivity(), NoticeActivity.class);
                    startActivity(noticeIntent);
                }
                if(mFirebaseAnalytics!=null){
                    mFirebaseAnalytics.logEvent(AnalyticUtil.NOTICE_BTN_CLICK,null);
                }
                break;
        }
    }
}
