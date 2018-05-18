package com.cd.dnf.credit.ui.borrow.fragment;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.BorrowOrderBean;
import com.cd.dnf.credit.bean.BorrowOrderStatusBean;
import com.cd.dnf.credit.bean.ContactBean;
import com.cd.dnf.credit.fragment.CreditBaseFragment;
import com.cd.dnf.credit.ui.borrow.actvitiy.BorrowOrderActivity;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.CreditUtil;
import com.cd.dnf.credit.util.KeyBordUtils;
import com.cd.dnf.credit.view.ToastUtil;
import com.cd.dnf.credit.view.borrow.BorrowChooseOptionDialog;
import com.cd.dnf.credit.view.borrow.interfaces.BorrowChooseOptionInterface;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jack on 2018/1/25.
 * 附加信息
 */

public class BorrowStepTwoFragment extends CreditBaseFragment implements EasyPermissions.PermissionCallbacks{
    private BorrowOrderActivity mActivity;
    @Bind(R.id.goal_view)
    TextView mGoalView;
    @Bind(R.id.company_name_view)
    EditText mCompanyNameView;//公司名称
    @Bind(R.id.company_phone_view)
    EditText mCompanyPhoneView;//公司电话
    @Bind(R.id.company_address_view)
    EditText mCompanyAddressView;//公司地址
    @Bind(R.id.month_income_view)
    TextView mMonthSalary;//月收入
    @Bind(R.id.saly_day_view)
    TextView mSalyDayView;//发薪日
    @Bind(R.id.relation_one_view)
    TextView mRelationOneView;//第一关系
    @Bind(R.id.relation_two_view)
    TextView mRelationTwoView;//第二关系
    @Bind(R.id.one_person_name)
    TextView mOnePersonNameView;//第一个关系人姓名
    @Bind(R.id.one_person_phone)
    TextView mOnePersonPhoneView;//第一个关系人电话
    @Bind(R.id.two_person_name)
    TextView mTwoPersonNameView;//第二个关系人姓名
    @Bind(R.id.two_person_phone)
    TextView mTwoPersonPhoneView;//第二个关系人电话

    @Bind(R.id.btn_next)
    TextView mBtnNext;
    private final static int FIRSTCONTACT = 0X2020;//第一个联系人
    private final static int SECONDCONTACT = 0X2021;//第二个联系人
    private List<String> relationSourceOne = new ArrayList<>();//第一关系
    private List<String> relationSourceTwo = new ArrayList<>();//第二关系
    private BorrowChooseOptionDialog oneRelationDialog;
    private BorrowChooseOptionDialog twoRelationDialog;
    private int chooseRelationOnePosition = -1;//第一关系
    private int chooseRelationTwoPosition = -1;//第二关系
    private BorrowOrderBean mBorrowBean;
    private BorrowChooseOptionDialog mSalaryDialog;//月收入dialog
    private List<String> mSalarySource = new ArrayList<>();//月收入数据
    private int chooseSalaryPosition = -1;//选择的月收入
    private BorrowChooseOptionDialog mSalaryDayDialog;//发薪日dialog
    private int chooseSalaryDayPosition = -1;//选择的发薪日的位置
    private List<String> mSalaryDaySource = new ArrayList<>();//发薪日数据
    private BorrowChooseOptionDialog mGoalDialog;//贷款目的dialog
    private int chooseGoalPosition = -1;//选择的贷款目的 这里指的是文字的位置
    private List<String> mGoalSource = new ArrayList<>();//贷款目的数据
    private List<String> mGoalCodeSource = new ArrayList<>();//贷款目的的code

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final int PM_REQUEST_CONTACT_ONE_PERMISSION =0x1000;
    private static final int PM_REQUEST_CONTACT_TWO_PERMISSION =0x1001;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_step_two_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (BorrowOrderActivity) getActivity();
        mBorrowBean = getArguments().getParcelable("orderBean");
        initData();
        initView();
        initDialog();
        initListener();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    private void initData() {
        mGoalCodeSource = Arrays.asList(getActivity().getResources().getStringArray(R.array.goal_code_array));
    }

    private void initView() {
        //贷款目的
        if (mBorrowBean.getGoal() >= 0) {
            String goalValue = "" + mBorrowBean.getGoal();
            chooseGoalPosition = mGoalCodeSource.indexOf(goalValue);
            List<String> goalSource = Arrays.asList(getResources().getStringArray(R.array.goal_array));
            String goalStr = goalSource.get(chooseGoalPosition);
            mGoalView.setText(goalStr);
        }
        //公司名称
        if (!TextUtils.isEmpty(mBorrowBean.getCompany())) {
            mCompanyNameView.setText(mBorrowBean.getCompany());
        }
        //公司电话
        if (!TextUtils.isEmpty(mBorrowBean.getCompanyPhone())) {
            mCompanyPhoneView.setText(mBorrowBean.getCompanyPhone());
        }
        //公司地址
        if (!TextUtils.isEmpty(mBorrowBean.getCompanyAddress())) {
            mCompanyAddressView.setText(mBorrowBean.getCompanyAddress());
        }
        //月收入
        if (mBorrowBean.getSalary() >= 0) {
            chooseSalaryPosition = mBorrowBean.getSalary();
            List<String> dataSource = Arrays.asList(getResources().getStringArray(R.array.salary_array));
            String textStr = dataSource.get(chooseSalaryPosition);
            mMonthSalary.setText(textStr);
        }
        //发薪日
        if (!TextUtils.isEmpty(mBorrowBean.getSalaryDay())) {
            mSalyDayView.setText(mBorrowBean.getSalaryDay());
            int value = Integer.parseInt(mBorrowBean.getSalaryDay());
            if (value > 0) {
                chooseSalaryDayPosition = value - 1;
            }
        }
        //第一关系
        if (mBorrowBean.getRelationship1() >= 0) {
            chooseRelationOnePosition = mBorrowBean.getRelationship1();
            List<String> dataSource = Arrays.asList(getResources().getStringArray(R.array.relation_one_array));
            String textStr = dataSource.get(chooseRelationOnePosition);
            mRelationOneView.setText(textStr);
        }
        if (!TextUtils.isEmpty(mBorrowBean.getContactsName1())) {
            mOnePersonNameView.setText(mBorrowBean.getContactsName1());
        }
        if (!TextUtils.isEmpty(mBorrowBean.getContactsPhone1())) {
            mOnePersonPhoneView.setText(mBorrowBean.getContactsPhone1());
        }
        //第二关系
        if (mBorrowBean.getRelationship2() >= 0) {
            chooseRelationTwoPosition = mBorrowBean.getRelationship2();
            List<String> dataSource = Arrays.asList(getResources().getStringArray(R.array.relation_two_array));
            String textStr = dataSource.get(chooseRelationTwoPosition);
            mRelationTwoView.setText(textStr);
        }
        if (!TextUtils.isEmpty(mBorrowBean.getContactsName2())) {
            mTwoPersonNameView.setText(mBorrowBean.getContactsName2());
        }
        if (!TextUtils.isEmpty(mBorrowBean.getContactsPhone2())) {
            mTwoPersonPhoneView.setText(mBorrowBean.getContactsPhone2());
        }
        checkCanNext();
    }

    private void initListener(){
        mCompanyNameView.addTextChangedListener(new TextWatcher() {
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
        mCompanyPhoneView.addTextChangedListener(new TextWatcher() {
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
        mCompanyAddressView.addTextChangedListener(new TextWatcher() {
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

    private void checkCanNext(){
        String goalStr = mGoalView.getText().toString();//贷款目的
        String companyNameStr = mCompanyNameView.getText().toString();//公司名称
        String companyPhoneStr = mCompanyPhoneView.getText().toString();//公司电话
        String companyAddressStr = mCompanyAddressView.getText().toString();//公司地址
        String salaryStr = mMonthSalary.getText().toString();//月收入
        String salaryDayStr = mSalyDayView.getText().toString();//发薪日
        String relationOneStr = mRelationOneView.getText().toString();//第一联系人
        String relationOneName = mOnePersonNameView.getText().toString();//第一关系人姓名
        String relationOnePhone = mOnePersonPhoneView.getText().toString();//第一关系人电话
        String relationTwoStr = mRelationTwoView.getText().toString();//第二联系人
        String relationTwoName = mTwoPersonNameView.getText().toString();//第二关系人姓名
        String relationTwoPhone = mTwoPersonPhoneView.getText().toString();//第二关系人电话
        if(!TextUtils.isEmpty(goalStr)&&!TextUtils.isEmpty(companyNameStr.trim())&&!TextUtils.isEmpty(companyPhoneStr.trim())
                &&!TextUtils.isEmpty(companyAddressStr.trim())&&!TextUtils.isEmpty(salaryStr)&&!TextUtils.isEmpty(salaryDayStr)
                &&!TextUtils.isEmpty(relationOneStr)&&!TextUtils.isEmpty(relationOneName)&&!TextUtils.isEmpty(relationOnePhone)
                &&!TextUtils.isEmpty(relationTwoStr)&&!TextUtils.isEmpty(relationTwoName)&&!TextUtils.isEmpty(relationTwoPhone)){
            mBtnNext.setTextColor(CreditApplication.getInstance().getResources().getColor(R.color.white));
            mBtnNext.setBackgroundResource(R.mipmap.bg_register);
        }else {
            mBtnNext.setTextColor(CreditApplication.getInstance().getResources().getColor(R.color.need_input_color));
            mBtnNext.setBackgroundResource(R.drawable.bg_need_input);
        }
    }


    /*public void setBorrowOrder(BorrowOrderBean borrowOrder) {
        mBorrowBean = borrowOrder;
    }*/

    private void initDialog() {
        relationSourceOne = Arrays.asList(getActivity().getResources().getStringArray(R.array.relation_one_array));

        relationSourceTwo = Arrays.asList(getActivity().getResources().getStringArray(R.array.relation_two_array));
        //第一关系
        oneRelationDialog = new BorrowChooseOptionDialog(getActivity(), relationSourceOne);
        oneRelationDialog.setChooseOption(chooseRelationOnePosition);
        oneRelationDialog.setChooseOptionInterface(new BorrowChooseOptionInterface() {
            @Override
            public void chooseOption(int chooseOption) {
                oneRelationDialog.dismiss();
                if (chooseRelationOnePosition != chooseOption) {
                    chooseRelationOnePosition = chooseOption;
                    oneRelationDialog.setChooseOption(chooseOption);
                    String relation = relationSourceOne.get(chooseRelationOnePosition);
                    mRelationOneView.setText(relation);
                    checkCanNext();
                }
            }
        });

        //第二关系
        twoRelationDialog = new BorrowChooseOptionDialog(getActivity(), relationSourceTwo);
        twoRelationDialog.setChooseOption(chooseRelationTwoPosition);
        twoRelationDialog.setChooseOptionInterface(new BorrowChooseOptionInterface() {
            @Override
            public void chooseOption(int chooseOption) {
                twoRelationDialog.dismiss();
                if (chooseRelationTwoPosition != chooseOption) {
                    chooseRelationTwoPosition = chooseOption;
                    twoRelationDialog.setChooseOption(chooseOption);
                    String relation = relationSourceTwo.get(chooseRelationTwoPosition);
                    mRelationTwoView.setText(relation);
                    checkCanNext();
                }
            }
        });

        //月收入
        mSalarySource = Arrays.asList(getActivity().getResources().getStringArray(R.array.salary_array));
        mSalaryDialog = new BorrowChooseOptionDialog(getActivity(), mSalarySource);
        mSalaryDialog.setChooseOption(chooseSalaryPosition);
        mSalaryDialog.setChooseOptionInterface(new BorrowChooseOptionInterface() {
            @Override
            public void chooseOption(int chooseOption) {
                mSalaryDialog.dismiss();
                if (chooseSalaryPosition != chooseOption) {
                    chooseSalaryPosition = chooseOption;
                    mSalaryDialog.setChooseOption(chooseOption);
                    String salaryStr = mSalarySource.get(chooseOption);
                    mMonthSalary.setText(salaryStr);
                    checkCanNext();
                }
            }
        });

        //发薪日
        mSalaryDaySource = Arrays.asList(getActivity().getResources().getStringArray(R.array.salary_day));
        mSalaryDayDialog = new BorrowChooseOptionDialog(getActivity(), mSalaryDaySource, Gravity.CENTER);
        mSalaryDayDialog.setChooseOption(chooseSalaryDayPosition);
        mSalaryDayDialog.setChooseOptionInterface(new BorrowChooseOptionInterface() {
            @Override
            public void chooseOption(int chooseOption) {
                mSalaryDayDialog.dismiss();
                if (chooseSalaryDayPosition != chooseOption) {
                    chooseSalaryDayPosition = chooseOption;
                    mSalaryDayDialog.setChooseOption(chooseSalaryDayPosition);
                    String salaryDayStr = mSalaryDaySource.get(chooseSalaryDayPosition);
                    mSalyDayView.setText(salaryDayStr);
                    checkCanNext();
                }
            }
        });

        //贷款目的
        mGoalSource = Arrays.asList(getActivity().getResources().getStringArray(R.array.goal_array));
        mGoalDialog = new BorrowChooseOptionDialog(getActivity(), mGoalSource);
        mGoalDialog.setChooseOption(chooseGoalPosition);
        mGoalDialog.setChooseOptionInterface(new BorrowChooseOptionInterface() {
            @Override
            public void chooseOption(int chooseOption) {
                mGoalDialog.dismiss();
                if (chooseGoalPosition != chooseOption) {
                    chooseGoalPosition = chooseOption;
                    mGoalDialog.setChooseOption(chooseOption);
                    String goalStr = mGoalSource.get(chooseGoalPosition);
                    mGoalView.setText(goalStr);
                    checkCanNext();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FIRSTCONTACT) {
                Uri contactData = data.getData();
                Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                if(null!=c) {
                    c.moveToFirst();
                    ContactBean contactBean = CreditUtil.getContactPhone(getActivity(), c);
                    mOnePersonNameView.setText(contactBean.getName());
                    mOnePersonPhoneView.setText(contactBean.getPhone());
                    checkCanNext();
                }
            } else if (requestCode == SECONDCONTACT) {
                //第二个联系人
                Uri contactData = data.getData();
                Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                if(null!=c) {
                    c.moveToFirst();
                    ContactBean contactBean = CreditUtil.getContactPhone(getActivity(), c);
                    mTwoPersonNameView.setText(contactBean.getName());
                    mTwoPersonPhoneView.setText(contactBean.getPhone());
                    checkCanNext();
                }
            }
        }
    }

    private void checkInputInformation() {
        String goalStr = mGoalView.getText().toString();//贷款目的
        String companyNameStr = mCompanyNameView.getText().toString();//公司名称
        String companyPhoneStr = mCompanyPhoneView.getText().toString();//公司电话
        String companyAddressStr = mCompanyAddressView.getText().toString();//公司地址
        String salaryStr = mMonthSalary.getText().toString();//月收入
        String salaryDayStr = mSalyDayView.getText().toString();//发薪日
        String relationOneStr = mRelationOneView.getText().toString();//第一联系人
        String relationOneName = mOnePersonNameView.getText().toString();//第一关系人姓名
        String relationOnePhone = mOnePersonPhoneView.getText().toString();//第一关系人电话
        String relationTwoStr = mRelationTwoView.getText().toString();//第二联系人
        String relationTwoName = mTwoPersonNameView.getText().toString();//第二关系人姓名
        String relationTwoPhone = mTwoPersonPhoneView.getText().toString();//第二关系人电话
        if (TextUtils.isEmpty(goalStr)) {
            ToastUtil.showShort(R.string.input_goal_tip);
            return;
        }
        if (TextUtils.isEmpty(companyNameStr.trim())) {
            ToastUtil.showShort(R.string.input_company_name_tip);
            return;
        }
        if (TextUtils.isEmpty(companyPhoneStr.trim())) {
            ToastUtil.showShort(R.string.input_company_phone_tip);
            return;
        }
        if (TextUtils.isEmpty(companyAddressStr.trim())) {
            ToastUtil.showShort(R.string.input_company_address_tip);
            return;
        }
        if (TextUtils.isEmpty(salaryStr)) {
            ToastUtil.showShort(R.string.choose_salary_tip);
            return;
        }
        if (TextUtils.isEmpty(salaryDayStr)) {
            ToastUtil.showShort(R.string.choose_salary_day_tip);
            return;
        }
        if (TextUtils.isEmpty(relationOneStr)) {
            ToastUtil.showShort(R.string.choose_relation_one);
            return;
        }
        if (TextUtils.isEmpty(relationOneName)) {
            ToastUtil.showShort(R.string.choose_relation_one_people_name);
            return;
        }
        if (TextUtils.isEmpty(relationOnePhone)) {
            ToastUtil.showShort(R.string.choose_relation_one_people_phone);
            return;
        }
        if (TextUtils.isEmpty(relationTwoStr)) {
            ToastUtil.showShort(R.string.choose_relation_two);
            return;
        }
        if (TextUtils.isEmpty(relationTwoName)) {
            ToastUtil.showShort(R.string.choose_relation_two_people_name);
            return;
        }
        if (TextUtils.isEmpty(relationTwoPhone)) {
            ToastUtil.showShort(R.string.choose_relation_two_people_phone);
            return;
        }
        if (TextUtils.equals(relationOneStr, relationTwoStr)) {
            ToastUtil.showShort(R.string.choose_same_relation_tip_str);
            return;
        }
        if (TextUtils.equals(relationOneName, relationTwoName)) {
            ToastUtil.showShort(R.string.choose_same_people_tip_str);
            return;
        }
        if (TextUtils.equals(relationOnePhone, relationTwoPhone)) {
            ToastUtil.showShort(R.string.choose_same_people_tip_str);
            return;
        }
        //设置贷款目的
        String goalCode = mGoalCodeSource.get(chooseGoalPosition);
        mBorrowBean.setGoal(Integer.parseInt(goalCode));
        mBorrowBean.setCompany(companyNameStr);
        mBorrowBean.setCompanyPhone(companyPhoneStr);
        mBorrowBean.setCompanyAddress(companyAddressStr);
        mBorrowBean.setSalary(chooseSalaryPosition);
        mBorrowBean.setSalaryDay(salaryDayStr);
        mBorrowBean.setRelationship1(chooseRelationOnePosition);
        mBorrowBean.setContactsName1(relationOneName);
        mBorrowBean.setContactsPhone1(relationOnePhone);
        mBorrowBean.setRelationship2(chooseRelationTwoPosition);
        mBorrowBean.setContactsName2(relationTwoName);
        mBorrowBean.setContactsPhone2(relationTwoPhone);
        mActivity.gotoPhotoApprove();
    }

    private void storeSource() {
        String goalStr = mGoalView.getText().toString();//贷款目的
        String companyNameStr = mCompanyNameView.getText().toString();//公司名称
        String companyPhoneStr = mCompanyPhoneView.getText().toString();//公司电话
        String companyAddressStr = mCompanyAddressView.getText().toString();//公司地址
        String salaryStr = mMonthSalary.getText().toString();//月收入
        String salaryDayStr = mSalyDayView.getText().toString();//发薪日
        String relationOneStr = mRelationOneView.getText().toString();//第一联系人
        String relationOneName = mOnePersonNameView.getText().toString();//第一关系人姓名
        String relationOnePhone = mOnePersonPhoneView.getText().toString();//第一关系人电话
        String relationTwoStr = mRelationTwoView.getText().toString();//第二联系人
        String relationTwoName = mTwoPersonNameView.getText().toString();//第二关系人姓名
        String relationTwoPhone = mTwoPersonPhoneView.getText().toString();//第二关系人电话

        if (chooseGoalPosition >= 0) {
            String goalCode = mGoalCodeSource.get(chooseGoalPosition);
            mBorrowBean.setGoal(Integer.parseInt(goalCode));
        } else {
            mBorrowBean.setGoal(-1);
        }
        mBorrowBean.setCompany(companyNameStr);
        mBorrowBean.setCompanyPhone(companyPhoneStr);
        mBorrowBean.setCompanyAddress(companyAddressStr);
        mBorrowBean.setSalary(chooseSalaryPosition);
        mBorrowBean.setSalaryDay(salaryDayStr);
        mBorrowBean.setRelationship1(chooseRelationOnePosition);
        mBorrowBean.setContactsName1(relationOneName);
        mBorrowBean.setContactsPhone1(relationOnePhone);
        mBorrowBean.setRelationship2(chooseRelationTwoPosition);
        mBorrowBean.setContactsName2(relationTwoName);
        mBorrowBean.setContactsPhone2(relationTwoPhone);
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



    @AfterPermissionGranted(PM_REQUEST_CONTACT_ONE_PERMISSION)
    private void fetchOneRelation() {
        String[] perms = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.GET_ACCOUNTS};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            Intent firstIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(firstIntent, FIRSTCONTACT);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_request_str),R.string.ok_str,R.string.cancel,
                    PM_REQUEST_CONTACT_ONE_PERMISSION, perms);
        }
    }


    @AfterPermissionGranted(PM_REQUEST_CONTACT_TWO_PERMISSION)
    private void fetchTwoRelation() {
        String[] perms = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.GET_ACCOUNTS};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            Intent secondIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(secondIntent, SECONDCONTACT);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_request_str),R.string.ok_str,R.string.cancel,
                    PM_REQUEST_CONTACT_TWO_PERMISSION, perms);
        }
    }


    @OnClick(value = {R.id.btn_next, R.id.relation_one_layout, R.id.one_person_name,
            R.id.one_person_phone, R.id.relation_two_layout, R.id.two_person_name, R.id.two_person_phone,
            R.id.month_income_layout, R.id.goal_layout, R.id.saly_day_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                checkInputInformation();
                if (mFirebaseAnalytics != null) {
                    mFirebaseAnalytics.logEvent(AnalyticUtil.BORROW_STEP_TWO_CLICK, null);
                }
                break;
            case R.id.relation_one_layout:
                //第一关系
                if (!oneRelationDialog.isShow()) {
                    oneRelationDialog.show();
                }
                break;
            case R.id.relation_two_layout:
                //第二关系
                if (!twoRelationDialog.isShow()) {
                    twoRelationDialog.show();
                }
                break;
            case R.id.one_person_name:
            case R.id.one_person_phone:
                //获取第一个人
                fetchOneRelation();
                break;
            case R.id.two_person_name:
            case R.id.two_person_phone:
                //第二关系
                fetchTwoRelation();
                break;
            case R.id.month_income_layout:
                //收入
                if (!mSalaryDialog.isShow()) {
                    mSalaryDialog.show();
                }
                break;
            case R.id.saly_day_layout:
                //发薪日
                if (!mSalaryDayDialog.isShow()) {
                    mSalaryDayDialog.show();
                }
                break;
            case R.id.goal_layout:
                //贷款目的
                if (!mGoalDialog.isShow()) {
                    mGoalDialog.show();
                }
                break;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
