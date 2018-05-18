package com.cd.dnf.credit.ui.borrow.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.BorrowOrderBean;
import com.cd.dnf.credit.fragment.CreditBaseFragment;
import com.cd.dnf.credit.ui.borrow.actvitiy.BorrowOrderActivity;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.AppPreferences;
import com.cd.dnf.credit.util.BorrowStatusUtil;
import com.cd.dnf.credit.util.CreditUtil;
import com.cd.dnf.credit.util.KeyBordUtils;
import com.cd.dnf.credit.view.ToastUtil;
import com.cd.dnf.credit.view.borrow.BorrowChooseOptionDialog;
import com.cd.dnf.credit.view.borrow.interfaces.BorrowChooseOptionInterface;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

/**
 * Created by jack on 2018/1/25.
 * 个人信息
 */

public class BorrowStepOneFragment extends CreditBaseFragment {
    private BorrowOrderActivity mActivity;
    @Bind(R.id.full_name_view)
    EditText mFullNameView;
    @Bind(R.id.sex_view)
    TextView mSexView;//性别
    @Bind(R.id.education_view)
    TextView mEducationView;//教育
    @Bind(R.id.job_view)
    TextView mJobView;//工作
    @Bind(R.id.nik_view)
    EditText mCardView;//身份证号
    @Bind(R.id.province_view)
    TextView mProvinceView;//省
    @Bind(R.id.city_view)
    TextView mCityView;//市
    @Bind(R.id.area_view)
    TextView mAreaView;//区
    @Bind(R.id.full_address_view)
    EditText mDetailAddressView;//详细地址

    @Bind(R.id.btn_next)
    TextView mBtnNext;
    private BorrowChooseOptionDialog mSexDialog;
    private List<String> mSexSource = new ArrayList<>();//性别数据
    private int chooseSexPosition = -1;//选择的性别
    private BorrowChooseOptionDialog mEducationDialog;//学历
    private List<String> mEducationSource = new ArrayList<>();//教育数据
    private int chooseEducationPosition = -1;//选中的学历的position
    private BorrowChooseOptionDialog mJobDialog;//工作
    private List<String> mJobSource = new ArrayList<>();//工作数据
    private int chooseJobPosition = -1;//选中的工作的position

    private BorrowChooseOptionDialog mProvinceDialog;//省的dialog
    private List<String> mProvinceSource = new ArrayList<>();//省的数据
    private int chooseProvince = -1;//选中的省的position
    private String mChooseProvince;//选择的省

    private BorrowChooseOptionDialog mCityDialog;//市的dialog
    private List<String> mCitySource = new ArrayList<>();//市的数据
    private int chooseCity = -1;//选择的市的position
    private String mChooseCity;//选择的市

    private BorrowChooseOptionDialog mAreaDialog;//区的dialog
    private List<String> mAreaSource = new ArrayList<>();//区的数据
    private int chooseArea = -1;//选择的区的position
    private String mChooseArea;//选择的区


    private BorrowOrderBean mBorrowBean;
    private int borrowStatus = -1;//上次的订单状态

    private String jsonStr;//json数据

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_step_one_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (BorrowOrderActivity) getActivity();
        mBorrowBean = getArguments().getParcelable("orderBean");
        borrowStatus = getArguments().getInt("borrowStatus", -1);
        initView();
        initDialog();
        initListener();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    private void initListener() {
        mFullNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCanNext();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCardView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCanNext();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDetailAddressView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCanNext();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkCanNext() {
        String fullNameStr = mFullNameView.getText().toString();//姓名
        String sexStr = mSexView.getText().toString();//性别
        String educationStr = mEducationView.getText().toString();//教育
        String jobStr = mJobView.getText().toString();//工作
        String cardstr = mCardView.getText().toString();//身份证号
        String proviceStr = mProvinceView.getText().toString();//省
        String cityStr = mCityView.getText().toString();//市
        String areaStr = mAreaView.getText().toString();//区
        String detailAddress = mDetailAddressView.getText().toString();//详细地址
        if (!TextUtils.isEmpty(fullNameStr.trim()) && !TextUtils.isEmpty(sexStr) && !TextUtils.isEmpty(educationStr)
                && !TextUtils.isEmpty(jobStr) && !TextUtils.isEmpty(cardstr) && !TextUtils.isEmpty(proviceStr)
                && !TextUtils.isEmpty(cityStr) && !TextUtils.isEmpty(areaStr) && !TextUtils.isEmpty(detailAddress.trim())) {
            mBtnNext.setTextColor(CreditApplication.getInstance().getResources().getColor(R.color.white));
            mBtnNext.setBackgroundResource(R.mipmap.bg_register);
        }else {
            mBtnNext.setTextColor(CreditApplication.getInstance().getResources().getColor(R.color.need_input_color));
            mBtnNext.setBackgroundResource(R.drawable.bg_need_input);
        }
    }

    private void initView() {
        //全名
        if (!TextUtils.isEmpty(mBorrowBean.getUserName())) {
            mFullNameView.setText(mBorrowBean.getUserName());
        }
        //性别
        if (mBorrowBean.getGender() >= 0) {
            chooseSexPosition = mBorrowBean.getGender();
            List<String> sexSource = Arrays.asList(getResources().getStringArray(R.array.sex_array));
            String sexStr = sexSource.get(chooseSexPosition);
            mSexView.setText(sexStr);
        }
        //教育
        if (mBorrowBean.getEducation() >= 0) {
            chooseEducationPosition = mBorrowBean.getEducation();
            List<String> educationSource = Arrays.asList(getResources().getStringArray(R.array.education_array));
            String educationStr = educationSource.get(chooseEducationPosition);
            mEducationView.setText(educationStr);
        }
        //工作
        if (mBorrowBean.getWork() >= 0) {
            chooseJobPosition = mBorrowBean.getWork();
            List<String> workSource = Arrays.asList(getResources().getStringArray(R.array.job_array));
            String jobStr = workSource.get(chooseJobPosition);
            mJobView.setText(jobStr);
        }
        //身份证号
        if (!TextUtils.isEmpty(mBorrowBean.getIdCardNumber())) {
            mCardView.setText(mBorrowBean.getIdCardNumber());
        }
        //地址
        mChooseProvince = mBorrowBean.getProvince();
        mChooseCity = mBorrowBean.getCity();
        mChooseArea = mBorrowBean.getArea();
        //省
        if (!TextUtils.isEmpty(mChooseProvince)) {
            mProvinceView.setText(mChooseProvince);
        }
        //市
        if (!TextUtils.isEmpty(mChooseCity)) {
            mCityView.setText(mChooseCity);
        }
        //区
        if (!TextUtils.isEmpty(mChooseArea)) {
            mAreaView.setText(mChooseArea);
        }
        //地址详情
        if (!TextUtils.isEmpty(mBorrowBean.getAddressDetail())) {
            mDetailAddressView.setText(mBorrowBean.getAddressDetail());
        }
        checkCanNext();
/*        if(borrowStatus== BorrowStatusUtil.BORROW_STATUS_PATCH){
            mCardView.setEnabled(false);
        }else {
            mCardView.setEnabled(true);
        }*/
    }
/*
    //设置borrowOrder
    public void setBorrowOrder(BorrowOrderBean borrowOrder) {
        mBorrowBean = borrowOrder;
    }*/

    private void initDialog() {
        //性别
        mSexSource = Arrays.asList(getActivity().getResources().getStringArray(R.array.sex_array));
        mSexDialog = new BorrowChooseOptionDialog(getActivity(), mSexSource);
        mSexDialog.setChooseOption(chooseSexPosition);
        mSexDialog.setChooseOptionInterface(new BorrowChooseOptionInterface() {
            @Override
            public void chooseOption(int chooseOption) {
                mSexDialog.dismiss();
                if (chooseSexPosition != chooseOption) {
                    chooseSexPosition = chooseOption;
                    mSexDialog.setChooseOption(chooseOption);
                    if (mSexSource.size() > 0) {
                        String sexStr = mSexSource.get(chooseSexPosition);
                        mSexView.setText(sexStr);
                        checkCanNext();
                    }
                }
            }
        });


        //教育
        mEducationSource = Arrays.asList(getActivity().getResources().getStringArray(R.array.education_array));
        mEducationDialog = new BorrowChooseOptionDialog(getActivity(), mEducationSource);
        mEducationDialog.setChooseOption(chooseEducationPosition);
        mEducationDialog.setChooseOptionInterface(new BorrowChooseOptionInterface() {
            @Override
            public void chooseOption(int chooseOption) {
                mEducationDialog.dismiss();
                if (chooseEducationPosition != chooseOption) {
                    chooseEducationPosition = chooseOption;
                    mEducationDialog.setChooseOption(chooseOption);
                    if (mEducationSource.size() > 0) {
                        String educationStr = mEducationSource.get(chooseEducationPosition);
                        mEducationView.setText(educationStr);
                        checkCanNext();
                    }
                }
            }
        });

        //工作
        mJobSource = Arrays.asList(getActivity().getResources().getStringArray(R.array.job_array));
        mJobDialog = new BorrowChooseOptionDialog(getActivity(), mJobSource);
        mJobDialog.setChooseOption(chooseJobPosition);
        mJobDialog.setChooseOptionInterface(new BorrowChooseOptionInterface() {
            @Override
            public void chooseOption(int chooseOption) {
                mJobDialog.dismiss();
                if (chooseJobPosition != chooseOption) {
                    chooseJobPosition = chooseOption;
                    mJobDialog.setChooseOption(chooseOption);
                    if (mJobSource.size() > 0) {
                        String jobStr = mJobSource.get(chooseJobPosition);
                        mJobView.setText(jobStr);
                        checkCanNext();
                    }
                }
            }
        });

        jsonStr = CreditUtil.getCitySource(getActivity());
        //省分
        getProvinceSource();
        if (!TextUtils.isEmpty(mChooseProvince)) {
            chooseProvince = mProvinceSource.indexOf(mChooseProvince);
        }
        mProvinceDialog = new BorrowChooseOptionDialog(getActivity(), mProvinceSource);
        mProvinceDialog.setChooseOption(chooseProvince);
        mProvinceDialog.setChooseOptionInterface(new BorrowChooseOptionInterface() {
            @Override
            public void chooseOption(int chooseOption) {
                mProvinceDialog.dismiss();
                if (chooseProvince != chooseOption) {

                    chooseProvince = chooseOption;
                    mProvinceDialog.setChooseOption(chooseOption);
                    if (mProvinceSource.size() > 0) {
                        mChooseProvince = mProvinceSource.get(chooseOption);
                        mProvinceView.setText(mChooseProvince);
                        checkCanNext();
                    }

                    //已经有了选择的东西 那么需要将市和县 全部清空
                    //初始化市
                    mChooseCity = "";
                    chooseCity = -1;
                    mCityView.setText("");
                    getCitySource();
                    if (mCityDialog != null) {
                        mCityDialog.initDataSource(-1, mCitySource);
                    }

                    mChooseArea = "";
                    chooseArea = -1;
                    mAreaView.setText("");
                    getAreaSource();
                    if (mAreaDialog != null) {
                        mAreaDialog.initDataSource(-1, mAreaSource);
                    }
                }

            }
        });

        //市
        if (!TextUtils.isEmpty(mChooseProvince)) {
            getCitySource();
            if (!TextUtils.isEmpty(mChooseCity)) {
                chooseCity = mCitySource.indexOf(mChooseCity);
            }
        }
        mCityDialog = new BorrowChooseOptionDialog(getActivity(), mCitySource);
        mCityDialog.setChooseOption(chooseCity);
        mCityDialog.setChooseOptionInterface(new BorrowChooseOptionInterface() {
            @Override
            public void chooseOption(int chooseOption) {
                mCityDialog.dismiss();
                if (chooseCity != chooseOption) {

                    chooseCity = chooseOption;
                    mCityDialog.setChooseOption(chooseOption);
                    if (mCitySource.size() > 0) {
                        mChooseCity = mCitySource.get(chooseOption);
                        mCityView.setText(mChooseCity);
                        checkCanNext();
                    }

                    //已经有了选择的东西 那么需要县 全部清空
                    mChooseArea = "";
                    chooseArea = -1;
                    mAreaView.setText("");
                    getAreaSource();
                    if (mAreaDialog != null) {
                        mAreaDialog.initDataSource(-1, mAreaSource);
                    }
                }
            }
        });

        //区
        if (!TextUtils.isEmpty(mChooseCity)) {
            getAreaSource();
            if (!TextUtils.isEmpty(mChooseArea)) {
                chooseArea = mAreaSource.indexOf(mChooseArea);
            }
        }
        //getAreaSource();
        mAreaDialog = new BorrowChooseOptionDialog(getActivity(), mAreaSource);
        mAreaDialog.setChooseOption(chooseArea);
        mAreaDialog.setChooseOptionInterface(new BorrowChooseOptionInterface() {
            @Override
            public void chooseOption(int chooseOption) {
                mAreaDialog.dismiss();
                if (chooseArea != chooseOption) {
                    chooseArea = chooseOption;
                    mAreaDialog.setChooseOption(chooseOption);
                    if (mAreaSource.size() > 0) {
                        mChooseArea = mAreaSource.get(chooseOption);
                        mAreaView.setText(mChooseArea);
                        checkCanNext();
                    }
                }
            }
        });

    }

    //获取省份数据
    private void getProvinceSource() {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            Iterator<String> sIterator = jsonObject.keys();
            while (sIterator.hasNext()) {
                // 获得key
                String key = sIterator.next();
                mProvinceSource.add(key);
            }
        } catch (Exception e) {

        }
    }

    //获取市的数据
    private void getCitySource() {
        try {
            mCitySource.clear();
            JSONObject jsonObject = new JSONObject(jsonStr);
            String cityStr = jsonObject.getString(mChooseProvince);
            JSONObject cityObject = new JSONObject(cityStr);
            Iterator<String> sIterator = cityObject.keys();
            while (sIterator.hasNext()) {
                // 获得key
                String key = sIterator.next();
                mCitySource.add(key);
            }
        } catch (Exception e) {

        }
    }

    //获取区的数据
    private void getAreaSource() {
        try {
            mAreaSource.clear();
            JSONObject jsonObject = new JSONObject(jsonStr);
            String cityJson = jsonObject.getString(mChooseProvince);
            JSONObject cityObject = new JSONObject(cityJson);
            String areaStr = cityObject.getString(mChooseCity);
            mAreaSource = JSON.parseArray(areaStr, String.class);
        } catch (Exception e) {

        }
    }


    private void checkInputInformation() {
        String userNameStr = mFullNameView.getText().toString();//用户名
        String sexStr = mSexView.getText().toString();//性别
        String educationStr = mEducationView.getText().toString();//教育
        String jobStr = mJobView.getText().toString();//工作
        String cardStr = mCardView.getText().toString();//别名
        String provinceStr = mProvinceView.getText().toString();//省
        String cityStr = mCityView.getText().toString();//市
        String areaStr = mAreaView.getText().toString();//区
        String detailAddressStr = mDetailAddressView.getText().toString();//详细地址
        if (TextUtils.isEmpty(userNameStr.trim())) {
            ToastUtil.showShort(R.string.input_username_tip);
            return;
        }
        if (TextUtils.isEmpty(sexStr)) {
            ToastUtil.showShort(R.string.choose_sex_tip);
            return;
        }
        if (TextUtils.isEmpty(educationStr)) {
            ToastUtil.showShort(R.string.choose_education_tip);
            return;
        }
        if (TextUtils.isEmpty(jobStr)) {
            ToastUtil.showShort(R.string.choose_job_tip);
            return;
        }
        if (TextUtils.isEmpty(cardStr)) {
            ToastUtil.showShort(R.string.input_card_tip);
            return;
        }
        if (cardStr.length() != 16) {
            ToastUtil.showShort(R.string.input_right_card_tip);
            return;
        }
        if (TextUtils.isEmpty(provinceStr)) {
            ToastUtil.showShort(R.string.choose_province_tip);
            return;
        }
        if (TextUtils.isEmpty(cityStr)) {
            ToastUtil.showShort(R.string.choose_city_tip);
            return;
        }
        if (TextUtils.isEmpty(areaStr)) {
            ToastUtil.showShort(R.string.choose_area_tip);
            return;
        }
        if (TextUtils.isEmpty(detailAddressStr.trim())) {
            ToastUtil.showShort(R.string.input_detail_address_tip);
            return;
        }
        mBorrowBean.setUserName(userNameStr);//设置用户名
        mBorrowBean.setGender(chooseSexPosition);//设置性别
        mBorrowBean.setEducation(chooseEducationPosition);//设置教育
        mBorrowBean.setWork(chooseJobPosition);
        mBorrowBean.setIdCardNumber(cardStr);
        mBorrowBean.setProvince(mChooseProvince);
        mBorrowBean.setCity(mChooseCity);
        mBorrowBean.setArea(mChooseArea);
        mBorrowBean.setAddressDetail(detailAddressStr);
        mActivity.goToAdditionalInfo();
    }

    private void storeSource() {
        String userNameStr = mFullNameView.getText().toString();//用户名
        String cardStr = mCardView.getText().toString();//别名
        String detailAddressStr = mDetailAddressView.getText().toString();//详细地址

        mBorrowBean.setUserName(userNameStr);//设置用户名
        mBorrowBean.setGender(chooseSexPosition);//设置性别
        mBorrowBean.setEducation(chooseEducationPosition);//设置教育
        mBorrowBean.setWork(chooseJobPosition);
        mBorrowBean.setIdCardNumber(cardStr);
        mBorrowBean.setProvince(mChooseProvince);
        mBorrowBean.setCity(mChooseCity);
        mBorrowBean.setArea(mChooseArea);
        mBorrowBean.setAddressDetail(detailAddressStr);
    }

    @Override
    public void onPause() {
        super.onPause();
        storeSource();
    }

    @OnTouch(value = {R.id.touch_panel})
    boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.touch_panel:
                KeyBordUtils.hideSoft(this);
                break;
        }
        return false;
    }

    @OnClick(value = {R.id.btn_next, R.id.sex_layout, R.id.education_layout, R.id.job_layout, R.id.province_layout, R.id.city_layout, R.id.area_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                checkInputInformation();
                if (mFirebaseAnalytics != null) {
                    mFirebaseAnalytics.logEvent(AnalyticUtil.BORROW_STEP_ONE_CLICK, null);
                }
                break;
            case R.id.sex_layout:
                //选折性别
                if (!mSexDialog.isShow()) {
                    mSexDialog.show();
                }
                break;
            case R.id.education_layout:
                //教育程度
                if (!mEducationDialog.isShow()) {
                    mEducationDialog.show();
                }
                break;
            case R.id.job_layout:
                //工作
                if (!mJobDialog.isShow()) {
                    mJobDialog.show();
                }
                break;
            case R.id.province_layout:
                //省
                if (!mProvinceDialog.isShow()) {
                    mProvinceDialog.show();
                }
                break;
            case R.id.city_layout:
                if (!TextUtils.isEmpty(mChooseProvince)) {
                    //市
                    if (!mCityDialog.isShow()) {
                        mCityDialog.show();
                    }
                } else {
                    ToastUtil.showShort(R.string.choose_province_tip);
                }
                break;
            case R.id.area_layout:
                if (!TextUtils.isEmpty(mChooseProvince) && !TextUtils.isEmpty(mChooseCity)) {
                    //区
                    if (!mAreaDialog.isShow()) {
                        mAreaDialog.show();
                    }
                } else {
                    if (TextUtils.isEmpty(mChooseProvince)) {
                        ToastUtil.showShort(R.string.choose_province_tip);
                    } else if (TextUtils.isEmpty(mChooseCity)) {
                        ToastUtil.showShort(R.string.choose_city_tip);
                    }
                }
                break;
        }
    }
}
