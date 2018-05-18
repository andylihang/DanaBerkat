package com.cd.dnf.credit.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import com.cd.dnf.credit.R;


/**
 * Created by jack on 2018/3/14.
 * 检测长度的edittext
 */

public class CheckEditText extends AppCompatEditText implements View.OnFocusChangeListener,
        TextWatcher {
    private boolean hasFoucs;
    private int minLength = 0;
    private int maxLength = 0;
    private int needLength = 0;//需要的长度
    private int normalColor = 0;
    private int errorColor = 0;

    public CheckEditText(Context context) {
        super(context);
    }

    public CheckEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CheckEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LengthEditText);
        minLength = typedArray.getInt(R.styleable.LengthEditText_minNumber, 0);
        maxLength = typedArray.getInt(R.styleable.LengthEditText_maxNumber, 0);
        needLength = typedArray.getInt(R.styleable.LengthEditText_needNumber, 0);
        normalColor = typedArray.getColor(R.styleable.LengthEditText_normalColor, getResources().getColor(R.color.borrow_input_color));
        errorColor = typedArray.getColor(R.styleable.LengthEditText_errorColor, getResources().getColor(R.color.borrow_error_check_color));
        addTextChangedListener(this);
        setOnFocusChangeListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    private void checkLength() {
        if (!hasFoucs) {
            int textLength = getText().toString().length();
            if (textLength > 0) {
                if (needLength > 0) {
                    //现在判断的是 需要多少位
                    if (textLength == needLength) {
                        setTextColor(normalColor);
                    } else {
                        setTextColor(errorColor);
                    }
                }
            }
        } else {
            setTextColor(normalColor);
        }

    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkLength();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        checkLength();
    }
}
