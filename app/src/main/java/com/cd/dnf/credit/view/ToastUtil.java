package com.cd.dnf.credit.view;

import android.widget.Toast;

import com.cd.dnf.credit.application.CreditApplication;


public class ToastUtil {
    private static Toast toast;

    /**
     * @param message
     */
    public static void showShort(CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(CreditApplication.getContext(), message, Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    /**
     * @param message
     */
    public static void showShort(int message) {
        if (null == toast) {
            toast = Toast.makeText(CreditApplication.getContext(), message, Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

}
