package com.cd.dnf.credit.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.ClientVersionBean;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.receiver.DownloadReceiver;
import com.cd.dnf.credit.view.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;

import me.drakeet.materialdialog.MaterialDialog;

public class CheckSoftUpdate {
    private DownloadReceiver downloadReceiver;
    private Context mContext;
    private boolean mShowDialog;

    public CheckSoftUpdate(Context mContext, boolean mShowDialog) {
        this.mContext = mContext;
        this.mShowDialog = mShowDialog;
    }

    public void checkUpdate() {
        if (mContext == null) {
            mContext = CreditApplication.getContext();
        }
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
            final String version = pi.versionName;
           /* HttpParams params = new HttpParams();
            params.put("platform", "android");*/
            OkGo.<String>get(CreditApi.API_CHECK_UPDATE).execute(
                    new CreditApiRequstCallback(mContext, mShowDialog) {

                        @Override
                        public void RequestSuccess(String body) {
                            CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                            ClientVersionBean versionBean = JSON.parseObject(response.getData(), ClientVersionBean.class);
                            if (versionBean.getVersion().compareTo(version) > 0) {
                                //有需要升级的版本
                                showUpdateConfirmDialog(versionBean.getDownloadUrl(),!mShowDialog,versionBean.isForce());
                            } else if (mShowDialog) {
                                ToastUtil.showShort(R.string.no_new_version);
                            }
                        }

                        @Override
                        public void RequestFail(String body) {

                        }
                    });
        } catch (Exception e) {

        }
    }

    public void unregisterReceiver() {
        if (downloadReceiver != null) {
            mContext.unregisterReceiver(downloadReceiver);
            downloadReceiver = null;
        }
    }

    /**
     * 升级提醒 needForce是服务器需要强制更新
     */
    private void showUpdateConfirmDialog(final String url,boolean force,boolean needForce) {
        final MaterialDialog mMaterialDialog = new MaterialDialog(mContext);
        mMaterialDialog.setMessage(R.string.new_version_msg);
        mMaterialDialog.setPositiveButton(R.string.update_now, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mMaterialDialog.dismiss();
/*                long id = DownloadUtils.downloadWithProgress(mContext, url);
                downloadReceiver = new DownloadReceiver(id);
                IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                mContext.registerReceiver(downloadReceiver, filter);*/
                Intent intent =  new  Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mContext.startActivity(intent);
            }
        });
        if(!force){
            mMaterialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog.dismiss();
                }
            });
        }else if(!needForce){
            mMaterialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog.dismiss();
                }
            });
        }
        if(force&&needForce){
            mMaterialDialog.setCanceledOnTouchOutside(false);
        }else {
            mMaterialDialog.setCanceledOnTouchOutside(true);
        }
        mMaterialDialog.show();
    }


    private String getVersion() {
        String st = "版本号错误";
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(mContext.getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return st;
        }
    }
}
