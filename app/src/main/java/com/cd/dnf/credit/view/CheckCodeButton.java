package com.cd.dnf.credit.view;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.Button;

import com.cd.dnf.credit.R;


public class CheckCodeButton extends Button {
    private int time = 60;
    private Context mContext;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //setText("剩余"+leftTime + "秒");
                    String timeStr = getResources().getString(R.string.count_down_left_time);
                    String formatStr = String.format(timeStr, ""+leftTime);
                    setText(formatStr);
                    break;
                case 1:
                    resetGet();
                    break;
            }
        }
    };

    public CheckCodeButton(Context context) {
        super(context);
        mContext=context;
        mCountDownHandler=new CountDownHandler();
    }

    public CheckCodeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        mCountDownHandler=new CountDownHandler();
    }

    public void startGet() {
        setEnabled(false);
        leftTime=time;
        if(handler!=null){
            handler.sendEmptyMessage(0);
        }
        if(mCountDownHandler!=null){
            mCountDownHandler.sendEmptyMessageDelayed(COUNT_DOWN_TIME, 1000);
        }
    }

    public void resetGet() {
        setEnabled(true);
        setText(mContext.getResources().getString(R.string.register_picture_code));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mCountDownHandler!=null){
            mCountDownHandler.removeCallbacksAndMessages(null);
            mCountDownHandler=null;
        }
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
    }

    private CountDownHandler mCountDownHandler;
    private static final int COUNT_DOWN_TIME=0x0089;//倒计时
    private int leftTime = 0;
    class CountDownHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case COUNT_DOWN_TIME:
                    leftTime=leftTime-1;
                    if(leftTime<=0){
                        if(handler!=null){
                            handler.sendEmptyMessage(1);
                        }
                    }else {
                        if(handler!=null){
                            handler.sendEmptyMessage(0);
                        }
                        if(mCountDownHandler!=null){
                            mCountDownHandler.sendEmptyMessageDelayed(COUNT_DOWN_TIME,1000);
                        }
                    }
                    break;
            }
        }
    }
}
