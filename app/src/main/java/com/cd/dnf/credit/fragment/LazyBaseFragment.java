package com.cd.dnf.credit.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by jack on 2016/12/6.
 */

public class LazyBaseFragment extends Fragment {
    protected boolean mIsLoadedData = false;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            handleOnVisibilityChangedToUser(isVisibleToUser);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(true);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(false);
        }
    }
    /**
     * 处理对用户是否可见
     *
     * @param isVisibleToUser
     */
    private void handleOnVisibilityChangedToUser(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            // 对用户可见
            if (!mIsLoadedData) {
                //Log.d("wlwu",this.getClass().getSimpleName() + " 懒加载一次");
                mIsLoadedData = true;
                onLazyLoadOnce();
            }
            //Log.d("wlwu",this.getClass().getSimpleName() + " 对用户可见");
            onVisibleToUser();
        } else {
            // 对用户不可见
           // Log.d("wlwu",this.getClass().getSimpleName() + " 对用户不可见");
            onInvisibleToUser();
        }
    }
    /**
     * 懒加载一次。如果只想在对用户可见时才加载数据，并且只加载一次数据，在子类中重写该方法
     */
    protected void onLazyLoadOnce() {
        mIsLoadedData=true;
    }

    /**
     * 对用户可见时触发该方法。如果只想在对用户可见时才加载数据，在子类中重写该方法
     */
    protected void onVisibleToUser() {
    }

    /**
     * 对用户不可见时触发该方法
     */
    protected void onInvisibleToUser() {
    }

}
