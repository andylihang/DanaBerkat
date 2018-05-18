package com.cd.dnf.credit.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cd.dnf.credit.R;
import com.jaeger.library.StatusBarUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jack on 2018/2/13.
 */

public class WebViewActivity extends CreditSwipBackActivity {
    @Bind(R.id.title_layout)
    RelativeLayout mTitleLayout;
    @Bind(R.id.status_bar_view)
    ImageView mStatusbarImageView;
    @Bind(R.id.title_view)
    TextView mTitleView;
    @Bind(R.id.webview)
    WebView mWebView;
    private String titileStr="";
    private String urlStr="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_layout);
        ButterKnife.bind(this);
        titileStr=getIntent().getStringExtra("title");
        urlStr=getIntent().getStringExtra("url");
        mTitleView.setText(titileStr);
        initWebview();
    }
    private void initWebview(){
        WebSettings webSettings =mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setTextSize(WebSettings.TextSize.SMALLER);
        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);// 使用当前WebView处理跳转
                return true;// true表示此事件在此处被处理，不需要再广播
            }
        });
        mWebView.loadUrl(urlStr);
    }
    @OnClick(value = {R.id.back_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                finish();
                break;
        }
    }
    @Override
    protected void setStatusBar() {
        super.setStatusBar();
        FrameLayout.LayoutParams params=(FrameLayout.LayoutParams)mStatusbarImageView.getLayoutParams();
        params.height=getImageStatusBarHeight();
        mStatusbarImageView.setLayoutParams(params);
        StatusBarUtil.setTransparentForImageView(this,mTitleLayout);
    }
}
