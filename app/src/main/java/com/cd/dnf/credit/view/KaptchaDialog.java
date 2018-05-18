package com.cd.dnf.credit.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.util.Base64;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.bean.KaptchaBean;
import com.cd.dnf.credit.interfaces.KaptchaInterface;
import com.cd.dnf.credit.util.CreditUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KaptchaDialog {
    @Bind(R.id.code_view)
    EditText mCodeView;//输入的值
    @Bind(R.id.pic_view)
    ImageView mPicView;//图片码
    @Bind(R.id.sure_layout)
    RelativeLayout mSureLayout;
    @Bind(R.id.cancel_layout)
    RelativeLayout mCancelLayout;
    @Bind(R.id.btn_refresh)
    ImageView mBtnRefresh;
    private Dialog mDialog;
    private KaptchaBean mKaptcha;
    private int width = WindowManager.LayoutParams.MATCH_PARENT;
    private int height = WindowManager.LayoutParams.WRAP_CONTENT;
    private Context mContext;
    private KaptchaInterface mKaptchaInterface;

    public KaptchaDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        View view = (ViewGroup) inflater.inflate(R.layout.dialog_pic_code_layout, null);
        ButterKnife.bind(this, view);
        mDialog = new Dialog(mContext, R.style.UnusualTypeChooseDialog);
        Window win = mDialog.getWindow();
        win.setGravity(Gravity.CENTER);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setContentView(view);
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        mCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mSureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击确认
                String code = mCodeView.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showShort(R.string.input_kaptcha_code_str);
                    return;
                }
                if (code.length() != CreditUtil.PIC_CODE_LENGTH) {
                    ToastUtil.showShort(R.string.input_ok_kaptcha_code_str);
                    return;
                }
                mDialog.dismiss();
                if (mKaptchaInterface != null) {
                    mKaptchaInterface.inputKaptcha(code,mKaptcha);
                }
            }
        });
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //刷新图片
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.roatate_animation);
                mBtnRefresh.setAnimation(animation);
                mBtnRefresh.startAnimation(animation);
                if(mKaptchaInterface!=null){
                    mKaptchaInterface.refreshKaptcha();
                }
            }
        });

    }

    public void clearBtnRefresh() {
        if (mBtnRefresh != null) {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBtnRefresh.clearAnimation();
                }
            }, 1000);
        }

    }
    //清空图片码值
    public void clearPicCode(){
        mPicView.setImageBitmap(null);
    }
    public void setKaptchaInterface(KaptchaInterface kaptchaInterface) {
        mKaptchaInterface = kaptchaInterface;
    }

    public void setKaptha(KaptchaBean katcha) {
        mCodeView.setText("");
        mKaptcha = katcha;
        if (mKaptcha != null) {
            handleKaptPic();
        }
    }

    private void handleKaptPic() {
        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... params) {
                byte source[] = Base64.decodeFast(mKaptcha.getCodeImage());
                Bitmap bitmap = BitmapFactory.decodeByteArray(source, 0, source.length);
                return bitmap;

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Bitmap mBitMap) {
                mPicView.setImageBitmap(mBitMap);
                super.onPostExecute(mBitMap);
            }


        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    public void show() {
        mDialog.show();
        Window win = mDialog.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = width;
        params.height = height;
        win.setAttributes(params);
    }

    public boolean isShow() {
        return mDialog.isShowing();
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        if (dismissListener != null)
            mDialog.setOnDismissListener(dismissListener);
    }

    public void dismiss() {
        mDialog.dismiss();
    }

}
