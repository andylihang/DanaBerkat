package com.cd.dnf.credit.activity;

import android.app.DownloadManager;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.ClientVersionBean;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.receiver.DownloadReceiver;
import com.cd.dnf.credit.ui.borrow.fragment.BorrowFragment;
import com.cd.dnf.credit.ui.mine.fragment.MineFragment;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.CheckSoftUpdate;
import com.cd.dnf.credit.util.DownloadUtils;
import com.cd.dnf.credit.view.ToastUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;

import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by jack on 2018/1/23.
 */

public class HomeActivity extends CreditBaseActvitiy {
    private Button[] mTabs;
    private Fragment[] fragments;
    private int index;
    // 当前fragment的index
    private int currentTabIndex = 0;
    private BorrowFragment mBorrowFragment;//借贷
    private MineFragment mineFragment;//我的
    private CheckSoftUpdate mCheckUpdate;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout);
        ButterKnife.bind(this);
        initView();
        mBorrowFragment = new BorrowFragment();
        mineFragment = new MineFragment();
        fragments = new Fragment[]{mBorrowFragment, mineFragment};
        getSupportFragmentManager().beginTransaction().
                add(R.id.fragment_container, mBorrowFragment).show(mBorrowFragment).commit();
        mCheckUpdate = new CheckSoftUpdate(this, false);
        mCheckUpdate.checkUpdate();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if(mFirebaseAnalytics!=null){
            mFirebaseAnalytics.logEvent(AnalyticUtil.HOME_PAGE,null);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCheckUpdate != null) {
            mCheckUpdate.unregisterReceiver();
        }
    }
    private void showExitDialog() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(R.string.exit_app_str);
        dialog.setPositiveButton(R.string.sure_str, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //退出成功
                finish();
            }
        }).setNegativeButton(R.string.cancel_str, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 初始化组件
     */
    private void initView() {
        // 把第一个tab设为选中状态
        mTabs = new Button[2];
        mTabs[0] = (Button) findViewById(R.id.btn_borrow);
        mTabs[1] = (Button) findViewById(R.id.btn_mine);
        // 把第一个tab设为选中状态
        mTabs[0].setSelected(true);
        currentTabIndex = 0;
    }

    /**
     * button点击事件
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_borrow:
                index = 0;
                break;
            case R.id.btn_mine:
                index = 1;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            if(index==0){
                if(mBorrowFragment!=null){
                    mBorrowFragment.refreshBorrowStatus();
                }
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }
    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparentForImageViewInFragment(HomeActivity.this, null);
    }
}
