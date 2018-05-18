package com.cd.dnf.credit.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.cd.dnf.credit.R;


/**
 * 下载Urilts
 */
public class DownloadUtils {
    public static String MINETYPE_APPLCATION = "application/vnd.android.package-archive";
    public static String DONWLOAD_SPORT_FINISH="download_finish_sport_application";
    private static Context mContext;
    public static long downloadWithProgress(Context context, String url) {
        mContext = context;
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDestinationInExternalPublicDir("/Download", "Pinjam.apk");
            String update = context.getString(R.string.downloading);
            request.setTitle(update + context.getString(R.string.app_name));
            request.setMimeType(MINETYPE_APPLCATION);
            long downloadId = downloadManager.enqueue(request);
            return downloadId;
        } catch (Exception e) {
            //这里可以考虑是否是用浏览器来进行下载
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            mContext.startActivity(intent);
            return  -1;
        }

    }
}
