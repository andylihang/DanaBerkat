package com.cd.dnf.credit.ui.borrow.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.activity.LoginRegisterActivity;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.fragment.CreditBaseFragment;
import com.cd.dnf.credit.ui.borrow.actvitiy.BorrowOrderActivity;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.AppPreferences;
import com.cd.dnf.credit.util.CreditUtil;
import com.cd.dnf.credit.view.ToastUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.lzy.okgo.OkGo;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jack on 2018/1/25.
 * 可以借贷的界面
 */

public class BorrowIngFragment extends CreditBaseFragment {
    private AppPreferences mAppreference;
    @Bind(R.id.limit_group)
    RadioGroup mLimitGroup;//借款额度
    @Bind(R.id.day_group)
    RadioGroup mDayGroup;//借款时间
    @Bind(R.id.limit_one)
    RadioButton mLimitOne;//第一个借款额度
    @Bind(R.id.limit_two)
    RadioButton mLimitTwo;//第二个借款额度
    @Bind(R.id.day_one)
    RadioButton mdayOne;//第一个借款天数
    @Bind(R.id.day_two)
    RadioButton mDayTwo;//第二个借款天数
    @Bind(R.id.money_view)
    TextView moneyView;//显示的金额
    private String borrowId = "";//金融方案
    private String planId = "";//后端返回来的计划Id
    private int borrowStatus = -1;//上一次的订单状态
    private final String planOne = "5071475846808576";//计划1
    private final String planTwo = "5079948368970751";//计划2
    private final String planThree = "5079948368970753";//计划3
    private final String planFour = "5079948368970752";//计划4
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_ing_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppreference = new AppPreferences(getActivity());
        planId = getArguments().getString("planId", "");
        borrowStatus=getArguments().getInt("orderStatus",-1);
        initGroup();
        initPlant();
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(getActivity());
    }

    private void initPlant() {
        if (!TextUtils.isEmpty(planId)) {
            //如果有planId 那么就更新界面
            if (TextUtils.equals(planOne, planId)) {
                //计划1
                mLimitOne.setChecked(true);
                mdayOne.setChecked(true);
                hanldeChoose(R.id.limit_one, R.id.day_one);
            } else if (TextUtils.equals(planTwo, planId)) {
                //计划2
                mLimitOne.setChecked(true);
                mDayTwo.setChecked(true);
                hanldeChoose(R.id.limit_one, R.id.day_two);
            } else if (TextUtils.equals(planThree, planId)) {
                //计划 3
                mLimitTwo.setChecked(true);
                mdayOne.setChecked(true);
                hanldeChoose(R.id.limit_two, R.id.day_one);
            } else if (TextUtils.equals(planFour, planId)) {
                //计划 4
                mLimitTwo.setChecked(true);
                mDayTwo.setChecked(true);
                hanldeChoose(R.id.limit_two, R.id.day_two);
            }
        } else {
            int chooseDay = mDayGroup.getCheckedRadioButtonId();
            int chooseLimit = mLimitGroup.getCheckedRadioButtonId();
            hanldeChoose(chooseLimit, chooseDay);
        }
    }

    private void initGroup() {
        mLimitGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int chooseLimit) {
                int chooseDay = mDayGroup.getCheckedRadioButtonId();
                hanldeChoose(chooseLimit, chooseDay);
            }
        });
        mDayGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int chooseDay) {
                int chooseLimit = mLimitGroup.getCheckedRadioButtonId();
                hanldeChoose(chooseLimit, chooseDay);
            }
        });
    }

    //选择改变
    private void hanldeChoose(int chooseLimit, int chooseDay) {
        int limit = 0;
        if (chooseLimit == R.id.limit_one) {
            limit = Integer.parseInt(getResources().getString(R.string.borrow_money_600_str));
        } else if (chooseLimit == R.id.limit_two) {
            limit = Integer.parseInt(getResources().getString(R.string.borrow_money_1200_str));
        }
        int day = 0;
        if (chooseDay == R.id.day_one) {
            day = Integer.parseInt(getResources().getString(R.string.borrow_day_7_str));
        } else if (chooseDay == R.id.day_two) {
            day = Integer.parseInt(getResources().getString(R.string.borrow_day_14_str));
        }
        long totalMoney = limit + limit * day * 1 / 100;
        String moneyStr = CreditUtil.moneySwitch(totalMoney);
        moneyView.setText(moneyStr);
    }

    @OnClick(value = {R.id.btn_borrow})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_borrow:
                if (!CreditUtil.isFastDoubleClick()){
                    checkBorrow();
                    if (mFirebaseAnalytics != null) {
                        mFirebaseAnalytics.logEvent(AnalyticUtil.GO_BORROW_CLICK, null);
                    }
                }
                break;
        }
    }

    private void checkCanBorrowStatus() {
        OkGo.<String>get(CreditApi.API_FETCH_CAN_ORDER).tag("checkBorrowStatus").execute(
                new CreditApiRequstCallback(getActivity(), true) {
                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        if(TextUtils.isEmpty(response.getData())){
                            goToBorrow();
                        }else {
                            int remainDay=Integer.parseInt(response.getData());
                            if(remainDay>30){
                                //当剩余0天的时候可以申请
                                goToBorrow();
                            }else {
                                int leftdDay=30-remainDay;
                                ToastUtil.showShort(getResources().getString(R.string.code_day_try_str,""+leftdDay));
                            }
                        }

                    }

                    @Override
                    public void RequestFail(String body) {

                    }

                });
    }

    private void goToBorrow(){
        int limitId = mLimitGroup.getCheckedRadioButtonId();
        int dayId = mDayGroup.getCheckedRadioButtonId();
        if (limitId == R.id.limit_one && dayId == R.id.day_one) {
            //方案1
            borrowId = "5071475846808576";
        } else if (limitId == R.id.limit_one && dayId == R.id.day_two) {
            borrowId = "5079948368970751";
            //方案2
        } else if (limitId == R.id.limit_two && dayId == R.id.day_one) {
            //方案3
            borrowId = "5079948368970753";
        } else if (limitId == R.id.limit_two && dayId == R.id.day_two) {
            //方案4
            borrowId = "5079948368970752";
        }
        if(null != BorrowOrderActivity.class && null != getActivity()) {
            Intent intent = new Intent(getActivity(), BorrowOrderActivity.class);
            intent.putExtra("id", borrowId);
            intent.putExtra("borrowStatus", borrowStatus);
            startActivity(intent);
        }
    }


    //借款
    private void checkBorrow() {
        String token = mAppreference.get(AppPreferences.USER_TOKEN);
        if (TextUtils.isEmpty(token)) {
            //需要登录才能借款
            Intent loginIntent = new Intent(getActivity(), LoginRegisterActivity.class);
            startActivity(loginIntent);
        } else {
            checkCanBorrowStatus();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消网络请求
        OkGo.getInstance().cancelTag("checkBorrowStatus");
    }

}
