package com.cd.dnf.credit.util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;

import com.cd.dnf.credit.application.CreditApplication;

public class KeyBordUtils {

    public static void hideSoft(Activity mAcitity) {
        InputMethodManager imm = (InputMethodManager) CreditApplication.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mAcitity.getCurrentFocus() != null && mAcitity.getCurrentFocus().getWindowToken() != null) {
            imm.hideSoftInputFromWindow(mAcitity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideSoft(Fragment mFragment) {
        InputMethodManager imm = (InputMethodManager) CreditApplication.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mFragment.getActivity().getCurrentFocus() != null && mFragment.getActivity().getCurrentFocus().getWindowToken() != null) {
            imm.hideSoftInputFromWindow(mFragment.getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            mFragment.getActivity().getCurrentFocus().clearFocus();
        }
    }

    public static final void hideSoftInput(Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mContext != null && mContext instanceof Activity && ((Activity) mContext).getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }
}
