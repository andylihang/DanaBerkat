package com.cd.dnf.credit.util;

import android.content.Context;
import android.view.WindowManager;

import com.cd.dnf.credit.application.CreditApplication;


public class ScreenUtils {
    public static int dip2px(float dipValue) {
        final float scale = CreditApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = CreditApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    public static int getScreenWidth(){
        WindowManager wm = (WindowManager) CreditApplication.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }
    public static int getScreenHeight(){
        WindowManager wm = (WindowManager) CreditApplication.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int height=wm.getDefaultDisplay().getHeight();
        return height;
    }
}
