package com.cd.dnf.credit.ui.borrow.actvitiy;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.activity.CreditSwipBackActivity;
import com.cd.dnf.credit.activity.HomeActivity;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.BorrowOperateBean;
import com.cd.dnf.credit.bean.BorrowOrderBean;
import com.cd.dnf.credit.bean.BorrowOrderStatusBean;
import com.cd.dnf.credit.bean.CreditNotification;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.ui.borrow.fragment.BorrowStepFourFragment;
import com.cd.dnf.credit.ui.borrow.fragment.BorrowStepOneFragment;
import com.cd.dnf.credit.ui.borrow.fragment.BorrowStepThreeFragment;
import com.cd.dnf.credit.ui.borrow.fragment.BorrowStepTwoFragment;
import com.cd.dnf.credit.util.AppPreferences;
import com.cd.dnf.credit.util.BuildConstant;
import com.cd.dnf.credit.util.CreditNotifyType;
import com.cd.dnf.credit.util.CreditUtil;
import com.cd.dnf.credit.util.ScreenUtils;
import com.cd.dnf.credit.view.ToastUtil;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by jack on 2018/1/25.
 */

public class BorrowOrderActivity extends CreditSwipBackActivity {
    @Bind(R.id.title_layout)
    RelativeLayout mTitleLayout;
    @Bind(R.id.status_bar_view)
    ImageView mStatusbarImageView;
    @Bind(R.id.title_view)
    TextView mTitleView;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    private List<Fragment> mFragmentSource = new ArrayList<>();
    private List<String> mTitleSource = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private BorrowOrderBean mBorrowBean = new BorrowOrderBean();//借款订单
    private String planId;//借款方案id
    private int borrowStatus = -1;//上次订单的状态
    private AppPreferences mAppPreference;
    private BorrowStepOneFragment oneStepFragment;//第一步
    private BorrowStepTwoFragment twoStepFragment;//第二步
    private BorrowStepThreeFragment threeStepFragment;//第三步
    private BorrowStepFourFragment fourStepFragment;//第四步
    //从网络获取的和本地的信息
    private BorrowOrderBean mServerOrderBean;//服务端的订单
    private BorrowOrderBean mLocalOrderBean;//本地存储的订单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_order_layout);
        ButterKnife.bind(this);
        borrowStatus = getIntent().getIntExtra("borrowStatus", -1);
        mAppPreference = new AppPreferences(this);
        initOrder();
        planId = getIntent().getStringExtra("id");
        //5079948368970752 测试id
        mBorrowBean.setPlanId(planId);
        mTitleView.setText(R.string.borrow_step_one_title_str);
        initViewPager();
    }

    private void initViewPager() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("orderBean", mBorrowBean);
        bundle.putInt("borrowStatus", borrowStatus);
        //第一步
        oneStepFragment = new BorrowStepOneFragment();
        oneStepFragment.setArguments(bundle);
        mFragmentSource.add(oneStepFragment);
        mTitleSource.add(getResources().getString(R.string.borrow_step_one_title_str));
        //第二步
        twoStepFragment = new BorrowStepTwoFragment();
        twoStepFragment.setArguments(bundle);
        mFragmentSource.add(twoStepFragment);
        mTitleSource.add(getResources().getString(R.string.borrow_step_two_title_str));
        //第三步
        threeStepFragment = new BorrowStepThreeFragment();
        threeStepFragment.setArguments(bundle);
        mFragmentSource.add(threeStepFragment);
        mTitleSource.add(getResources().getString(R.string.borrow_step_three_title_str));
        //第四步
        fourStepFragment = new BorrowStepFourFragment();
        fourStepFragment.setArguments(bundle);
        mFragmentSource.add(fourStepFragment);
        mTitleSource.add(getResources().getString(R.string.borrow_step_four_title_str));

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mFragmentSource.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentSource.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitleSource.get(position);
            }
        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentSource.size());
        mViewPager.setCurrentItem(0);
    }

    private void initOrder() {
        BorrowOrderStatusBean orderStatusBean = CreditApplication.getInstance().getBorrowOrderStatus();
        if (orderStatusBean != null) {
            mServerOrderBean = orderStatusBean.getApplyInfo();
        }
        String userId = mAppPreference.get(AppPreferences.USER_ID, "");
        List<BorrowOrderBean> dataSource = DataSupport.where("userId = ?", userId).find(BorrowOrderBean.class);
        if (dataSource.size() > 0) {
            mLocalOrderBean = dataSource.get(0);
        }
        if (borrowStatus == BuildConstant.BORROW_STATUS_PATCH) {
            mBorrowBean = mServerOrderBean;
        } else {
            if (mServerOrderBean != null) {
                if (mLocalOrderBean != null) {
                    if (!TextUtils.isEmpty(mLocalOrderBean.getUserName())) {
                        mBorrowBean = mLocalOrderBean;
                    } else {
                        mBorrowBean = mServerOrderBean;
                    }
                } else {
                    mBorrowBean = mServerOrderBean;
                }
            } else if (mLocalOrderBean != null) {
                if (!TextUtils.isEmpty(mLocalOrderBean.getUserName())) {
                    mBorrowBean = mLocalOrderBean;
                } else {
                    mBorrowBean = mServerOrderBean;
                }
            }
        }
        if (mBorrowBean == null) {
            mBorrowBean = new BorrowOrderBean();
        }
    }

    //第二步 附加信息
    public void goToAdditionalInfo() {
        mTitleView.setText(R.string.borrow_step_two_title_str);
        mViewPager.setCurrentItem(1);
    }

    //第三步  照片认证
    public void gotoPhotoApprove() {
        mTitleView.setText(R.string.borrow_step_three_title_str);
        mViewPager.setCurrentItem(2);
    }

    //第四步 银行信息
    public void gotoBankInfo() {
        mTitleView.setText(R.string.borrow_step_four_title_str);
        mViewPager.setCurrentItem(3);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            gotoPrevStep();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void gotoPrevStep() {
        int currentPosition = mViewPager.getCurrentItem();
        if (currentPosition == 0) {
            finish();
        } else {
            mViewPager.setCurrentItem(currentPosition - 1);
            switch (currentPosition - 1) {
                case 0:
                    mTitleView.setText(R.string.borrow_step_one_title_str);
                    break;
                case 1:
                    mTitleView.setText(R.string.borrow_step_two_title_str);
                    break;
                case 2:
                    mTitleView.setText(R.string.borrow_step_three_title_str);
                    break;
            }
        }
    }

    @OnClick(value = {R.id.back_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                gotoPrevStep();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null) return;
        for (Fragment fragment : fragments) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mStatusbarImageView.getLayoutParams();
        params.height = getImageStatusBarHeight();
        mStatusbarImageView.setLayoutParams(params);
        StatusBarUtil.setTransparentForImageView(this, mTitleLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消网络请求
    }

    @Override
    protected void onPause() {
        super.onPause();
        //进行本地数据存储
        String userId = mAppPreference.get(AppPreferences.USER_ID, "");
        mBorrowBean.setUserId(userId);
        DataSupport.deleteAll(BorrowOrderBean.class, "userId='" + userId + "'");
        mBorrowBean.save();
    }

}
