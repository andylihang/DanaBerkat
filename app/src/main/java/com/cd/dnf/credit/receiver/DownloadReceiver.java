package com.cd.dnf.credit.receiver;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;


import com.cd.dnf.credit.util.DownloadUtils;

import java.io.File;

/**
 * 版本更新 receiver
 */
public class DownloadReceiver extends BroadcastReceiver {
    private long id = 0;
    public DownloadReceiver(long id) {
        super();
        this.id = id;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadManager downloadManager =
                    (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            installIntent.setDataAndType(downloadManager.getUriForDownloadedFile(completeDownloadId),
                    DownloadUtils.MINETYPE_APPLCATION);
            try {
                context.startActivity(installIntent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }else if(intent.getAction().equals(DownloadUtils.DONWLOAD_SPORT_FINISH)){
            String fileurl=intent.getStringExtra("fileurl");
            File file = new File(fileurl);
            if(file.exists()){
                Intent installintent = new Intent();
                installintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installintent.setAction(Intent.ACTION_VIEW);
                String type = "application/vnd.android.package-archive";
                Uri url;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
                    url = FileProvider.getUriForFile(context, "com.cd.dnf.credit.pinjamplus.fileprovider", file);
                    // 给目标应用一个临时授权
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    url = Uri.fromFile(file);
                }
                installintent.setDataAndType(url, type);
                context.startActivity(installintent);
            }

        }
    }
}
