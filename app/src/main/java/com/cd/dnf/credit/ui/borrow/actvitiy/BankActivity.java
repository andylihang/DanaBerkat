package com.cd.dnf.credit.ui.borrow.actvitiy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.activity.CreditSwipBackActivity;
import com.cd.dnf.credit.api.CreditApi;
import com.cd.dnf.credit.api.CreditApiRequstCallback;
import com.cd.dnf.credit.bean.BankBean;
import com.cd.dnf.credit.bean.CreditResponse;
import com.cd.dnf.credit.ui.borrow.adapter.BankAdapter;
import com.cd.dnf.credit.util.CreditUtil;
import com.cd.dnf.credit.view.ToastUtil;
import com.cd.dnf.credit.view.headerlistview.BladeView;
import com.cd.dnf.credit.view.headerlistview.MySectionIndexer;
import com.cd.dnf.credit.view.headerlistview.PinnedHeaderListView;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jack on 2018/1/31.
 */

public class BankActivity extends CreditSwipBackActivity {
    @Bind(R.id.title_layout)
    RelativeLayout mTitleLayout;
    @Bind(R.id.status_bar_view)
    ImageView mStatusbarImageView;


    @Bind(R.id.listview)
    PinnedHeaderListView mListView;
    @Bind(R.id.bladeview)
    BladeView mBladeView;
    private static final String ALL_CHARACTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";
    private String[] sections = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    private MySectionIndexer mIndexer;
    private List<BankBean> mBankSource = new ArrayList<>();
    private int[] counts = new int[sections.length];
    private Map<String, Integer> map = new HashMap<String, Integer>();
    private BankAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_layout);
        ButterKnife.bind(this);
        fetchBankSource();
    }

    private void fetchBankSource(){
/*        String bankJson= CreditUtil.getBankSource(this);
        CreditResponse response = JSON.parseObject(bankJson, CreditResponse.class);
        mBankSource=JSON.parseArray(response.getData(),BankBean.class);
        initBank();*/
        OkGo.<String>get(CreditApi.API_FETCH_BANK).tag("fetchBank").execute(
                new CreditApiRequstCallback(this, true) {
                    @Override
                    public void RequestSuccess(String body) {
                        CreditResponse response = JSON.parseObject(body, CreditResponse.class);
                        mBankSource=JSON.parseArray(response.getData(),BankBean.class);
                        initBank();
                    }

                    @Override
                    public void RequestFail(String body) {

                    }

                });
    }

    private void initBank() {
        //bankCode 为这个的时候 需要提到最前面  BRI BNI  BCA
        List<BankBean> mFirstSource=new ArrayList<>();//这个是需要提到最前面
        List<BankBean> mDataSource=new ArrayList<>();//银行数据备份
        for (int i = 0; i < mBankSource.size(); i++) {
            BankBean bankBean = mBankSource.get(i);
            String letter = bankBean.getBankName().substring(0, 1);
            bankBean.setFirstLetter(letter);
            if(TextUtils.equals("BRI",bankBean.getBankCode())||TextUtils.equals("BNI",bankBean.getBankCode())||TextUtils.equals("BCA",bankBean.getBankCode())){
                mFirstSource.add(bankBean);
            }else {
                mDataSource.add(bankBean);
            }
        }
        mDataSource.addAll(mFirstSource);
        mBankSource=mDataSource;
        Collections.sort(mBankSource);
        for (int i = 0; i < sections.length; i++) {
            map.put(sections[i], 0);
        }
        for (int i = 0; i < mBankSource.size(); i++) {
            BankBean bankBean = mBankSource.get(i);
            String letter = bankBean.getBankName().substring(0, 1);
            bankBean.setFirstLetter(letter);
            if (!map.containsKey(letter)) {
                letter = sections[sections.length - 1];
                bankBean.setFirstLetter(letter);
            }
            int count = map.get(letter);
            map.put(bankBean.getFirstLetter(), ++count);
        }
        for (int i = 0; i < sections.length; i++) {
            counts[i] = map.get(sections[i]);
        }
        mIndexer = new MySectionIndexer(sections, counts);
        mAdapter = new BankAdapter(this, mBankSource, mIndexer);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(mAdapter);
        mListView.setPinnedHeaderView(LayoutInflater.from(this)
                .inflate(R.layout.item_bank_group_layout,
                        mListView, false));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BankBean bankBean=(BankBean)mAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("chooseBank",bankBean);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mBladeView.setOnItemClickListener(new BladeView.OnItemClickListener() {
            @Override
            public void onItemClick(String s) {
                if (s != null) {
                    int section = ALL_CHARACTER.indexOf(s);
                    int position = mIndexer.getPositionForSection(section);
                    if (position != -1) {
                        mListView.setSelection(position);
                    } else {

                    }
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消网络请求
        OkGo.getInstance().cancelTag("fetchBank");
    }
    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        FrameLayout.LayoutParams params=(FrameLayout.LayoutParams)mStatusbarImageView.getLayoutParams();
        params.height=getImageStatusBarHeight();
        mStatusbarImageView.setLayoutParams(params);
        StatusBarUtil.setTransparentForImageView(this,mTitleLayout);
    }
    @OnClick(value = {R.id.back_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
        }
    }
}
