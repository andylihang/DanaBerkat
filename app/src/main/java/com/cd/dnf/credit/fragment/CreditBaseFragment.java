package com.cd.dnf.credit.fragment;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.cd.dnf.credit.R;
import com.cd.dnf.credit.util.ScreenUtils;

/**
 * Created by jack on 2018/1/23.
 */

public class CreditBaseFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    public void showProgressDialog(){
        if(isProgressDialogShowing()){
            return;
        }
        mProgressDialog=new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage(getResources().getString(R.string.network_requesting));
        mProgressDialog.show();
    }
    public void showProgressDialog(String message){
        if(isProgressDialogShowing()){
            return;
        }
        mProgressDialog=new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage(message);
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
    public int getStatusBarHeight(){
        Resources resources = getActivity().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Status height:" + height);
        return height;
    }
    public int getImageStatusBarHeight(){
        if(getActivity()!=null){
            int titleBarHeight=(int)getActivity().getResources().getDimension(R.dimen.window_title_height);
            return titleBarHeight+getStatusBarHeight();
        }else {
            return ScreenUtils.dip2px(44)+ScreenUtils.dip2px(25);
        }
    }
}
