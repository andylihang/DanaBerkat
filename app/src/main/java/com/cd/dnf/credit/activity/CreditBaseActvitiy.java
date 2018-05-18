package com.cd.dnf.credit.activity;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.util.ScreenUtils;
import com.jaeger.library.StatusBarUtil;

import butterknife.ButterKnife;

/**
 * Created by jack on 2018/1/23.
 */

public class CreditBaseActvitiy extends FragmentActivity {
    private ProgressDialog mProgressDialog;
    public void showProgressDialog(){
        if(isProgressDialogShowing()){
            return;
        }
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage(getResources().getString(R.string.network_requesting));
        mProgressDialog.show();
    }
    public void dismissProgressDialog(){
        if(isProgressDialogShowing()){
            mProgressDialog.dismiss();
            mProgressDialog=null;
        }
    }
    private boolean isProgressDialogShowing(){
        return null!=mProgressDialog&&mProgressDialog.isShowing();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CreditApplication.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CreditApplication.finishActivity(this);
    }
    public int getStatusBarHeight(){
        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Status height:" + height);
        return height;
    }
    public int getImageStatusBarHeight(){
        int titleBarHeight=(int)getResources().getDimension(R.dimen.window_title_height);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return titleBarHeight;
        }
        return titleBarHeight+getStatusBarHeight();
    }
    @Override
    public void setContentView(@LayoutRes int layoutResID){
        super.setContentView(layoutResID);
/*        View view=LayoutInflater.from(this).inflate(layoutResID,null);
        View titleLayout=view.findViewById(R.id.title_layout);
        if(titleLayout!=null){
            StatusBarUtil.setTranslucentForImageView(this,0,titleLayout);
        }*/
        ButterKnife.bind(this);
        setStatusBar();
    }
    protected void setStatusBar() {
        //StatusBarUtil.setColor(this, getResources().getColor(R.color.primaryColor),0);
    }
}
