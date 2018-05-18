package com.cd.dnf.credit.ui.borrow.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.utils.BinaryUtil;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.AppendObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cd.dnf.credit.R;
import com.cd.dnf.credit.application.CreditApplication;
import com.cd.dnf.credit.bean.BorrowOrderBean;
import com.cd.dnf.credit.bean.BorrowOrderStatusBean;
import com.cd.dnf.credit.fragment.CreditBaseFragment;
import com.cd.dnf.credit.ui.borrow.actvitiy.BorrowOrderActivity;
import com.cd.dnf.credit.util.AnalyticUtil;
import com.cd.dnf.credit.util.AppPreferences;
import com.cd.dnf.credit.util.CreditUtil;
import com.cd.dnf.credit.util.GlideRoundTransform;
import com.cd.dnf.credit.util.PlatFormUtil;
import com.cd.dnf.credit.util.ScreenUtils;
import com.cd.dnf.credit.view.RoundProgressBar;
import com.cd.dnf.credit.view.ToastUtil;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jack on 2018/1/25.
 * 照片认证
 */

public class BorrowStepThreeFragment extends CreditBaseFragment {
    @Bind(R.id.identify_card_view)
    ImageView IdentifyCardView;//身份证正面照
    @Bind(R.id.handle_card_view)
    ImageView mHandleCardView;//手持身份证照
    @Bind(R.id.work_card_view)
    ImageView mWorkCardView;//工作证照
    @Bind(R.id.artic_wage_view)
    ImageView mArticWageView;//工资条

    @Bind(R.id.btn_next_three)
    TextView mBtnNext;

    @Bind(R.id.identify_progress)
    RoundProgressBar mIdentifyProgress;//身份证正面照 上传进度
    @Bind(R.id.handle_progress)
    RoundProgressBar mHandleProgress;//手持身份证照片 上传进度
    @Bind(R.id.work_progress)
    RoundProgressBar mWorkProgress;//工作证照 上传进度
    @Bind(R.id.wage_progress)
    RoundProgressBar mWageProgress;//工资条 上传进度

    private BorrowOrderActivity mActivity;
    private final static int INDENTIFYCARD = 0X1023;//身份证正面照
    private final static int HANDLECARD = 0X1024;//手持身份证照
    private final static int WORKCARD = 0X1025;//工作证照
    private final static int ARTICWAGE = 0X1026;//工资条
    private String indentifyCardPath = "";//身份证正面照图片地址
    private String handleCardPath = "";//手持身份证图片地址
    private String workCardPath = "";//工作证照
    private String articWagePath = "";//工资条
    private final String IndentifyType = "indentify";//身份证正面照的类型
    private final String HandleCardType = "handle";//手持身份证照片
    private final String WorkCardType = "work";//工作证照
    private final String ArticleType = "article";//工资条
    private String indentifyOssKey = "";//身份证正面照的oss的key
    private String handleOssKey = "";//手持身份证的oss的key
    private String workOssKey = "";//工作证照的oss的key
    private String articOssKey = "";//工资条的oss的key


    private BorrowOrderBean mBorrowBean;
    private OSS mOSS;
    private AppPreferences mPreference;
    private FirebaseAnalytics mFirebaseAnalytics;
    private final int HANDLE_PIC_STATUS = 0x1239;//图片状态改变
    private final int HANDLE_OK_STATUS = 0x1249;//成功状态返回
    private final int HANDLE_UPLOAD_PROGRESS = 0x1287;//上传进度
    private final int HANDLE_DOWNLOAD_PROGRESS = 0x1288;//上传进度

    private UpLoadStatusHandler mUploadStatusHandler;

    //任务
    private OSSAsyncTask mIndentifyTask;//正面照任务
    private OSSAsyncTask mHandleTask;//手持身份证任务
    private OSSAsyncTask mWorkTask;//工作证照任务
    private OSSAsyncTask mWageTask;//工资条任务

    private int PROGRESS_ING=98;//在上传的过程中 当进度为100的时候 是98

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow_step_three_layout, container, false);
        ButterKnife.bind(this, view);
        Glide.with(this).load(R.mipmap.logo_identity_card).into(IdentifyCardView);
        Glide.with(this).load(R.mipmap.logo_handle_card).into(mHandleCardView);
        Glide.with(this).load(R.mipmap.logo_work_card).into(mWorkCardView);
        Glide.with(this).load(R.mipmap.logo_artic_wage).into(mArticWageView);
        Log.e("sakura","BorrowStepThreeFragment--onCreateView");
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   setRetainInstance(true);
        Log.e("sakura","BorrowStepThreeFragment--onCreate");
        if (savedInstanceState != null) {

            Log.e("sakura","BorrowStepThreeFragment--onCreate savedInstanceState != null 并且恢复数据");
            indentifyCardPath = savedInstanceState.getString("indentifyCardPath", "");
            handleCardPath = savedInstanceState.getString("handleCardPath", "");
            workCardPath = savedInstanceState.getString("workCardPath", "");
            articWagePath = savedInstanceState.getString("articWagePath", "");
            indentifyOssKey = savedInstanceState.getString("indentifyOssKey", "");
            handleOssKey = savedInstanceState.getString("handleOssKey", "");
            workOssKey = savedInstanceState.getString("workOssKey", "");
            articOssKey = savedInstanceState.getString("articOssKey", "");

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString("indentifyCardPath", indentifyCardPath);
            outState.putString("handleCardPath", handleCardPath);
            outState.putString("workCardPath", workCardPath);
            outState.putString("articWagePath", articWagePath);
            outState.putString("indentifyOssKey", indentifyOssKey);
            outState.putString("handleOssKey", handleOssKey);
            outState.putString("workOssKey", workOssKey);
            outState.putString("articOssKey", articOssKey);
        }
    }

    /*
       */
    public void setBorrowOrder(BorrowOrderBean borrowOrder) {
        mBorrowBean = borrowOrder;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOSS = CreditApplication.getInstance().getOSS();
        mActivity = (BorrowOrderActivity) getActivity();
        mBorrowBean = getArguments().getParcelable("orderBean");
        mPreference = new AppPreferences(getActivity());
        mUploadStatusHandler = new UpLoadStatusHandler();
        initView();
        Log.e("sakura","BorrowStepThreeFragment--onActivityCreated");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }


    Handler handler = new Handler();
    private void initView() {


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                indentifyOssKey = mBorrowBean.getIdCardPhotoFront();//身份证正面照
                if (!TextUtils.isEmpty(indentifyOssKey)) {
                    fetchServerPhoto(indentifyOssKey);
                }
            }
        },800);

    }

    private void fetchServerPhoto(String objectKey) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(CreditApplication.BUCK_NAME, objectKey);
        getObjectRequest.setCRC64(OSSRequest.CRC64Config.YES);

        getObjectRequest.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                String objectKey = request.getObjectKey();
                Message message = new Message();
                message.what = HANDLE_DOWNLOAD_PROGRESS;
                Bundle bundle = new Bundle();
                bundle.putString("type", objectKey);
                bundle.putLong("currentSize", currentSize);
                bundle.putLong("totalSize", totalSize);
                message.setData(bundle);
                mUploadStatusHandler.sendMessage(message);

            }
        });


        OSSAsyncTask task = mOSS.asyncGetObject(getObjectRequest, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                Log.e("sakura"," 开始下载照片 objectKey="+request.getObjectKey());

                InputStream inputStream = result.getObjectContent();
                if (request.getObjectKey().contains(IndentifyType)) {
                    //身份证正面照
                    Log.e("sakura","---正面照请求成功");
                    try {
                    Bitmap bitmap = CreditUtil.autoResizeFromStream(inputStream, IdentifyCardView);
                    Log.e("sakura","---设置照片  1");
                    IdentifyCardView.setImageBitmap(bitmap);
                    } catch (Exception e) {

                    }

                    handleOssKey = mBorrowBean.getPeopleIdCardPhoto();//手持身份证照
                    if (!TextUtils.isEmpty(handleOssKey)) {
                        fetchServerPhoto(handleOssKey);
                    }



                } else if (request.getObjectKey().contains(HandleCardType)) {
                    //手持身份证
                    try {
                        Bitmap bitmap = CreditUtil.autoResizeFromStream(inputStream, mHandleCardView);
                        mHandleCardView.setImageBitmap(bitmap);
                        Log.e("sakura","---设置照片  2");
                    } catch (Exception e) {

                    }
                    workOssKey = mBorrowBean.getWorkCardPhoto();//工作证照
                    if (!TextUtils.isEmpty(workOssKey)) {
                        fetchServerPhoto(workOssKey);
                    }

                } else if (request.getObjectKey().contains(WorkCardType)) {
                    //工作证照
                    try {
                        Bitmap bitmap = CreditUtil.autoResizeFromStream(inputStream, mWorkCardView);
                        mWorkCardView.setImageBitmap(bitmap);
                        Log.e("sakura","---设置照片  3");

                    } catch (Exception e) {

                    }
                    articOssKey = mBorrowBean.getPayrollPhoto();//工资条

                    if (!TextUtils.isEmpty(articOssKey)) {
                        fetchServerPhoto(articOssKey);
                    }

                } else if (request.getObjectKey().contains(ArticleType)) {
                    //工资条
                    try {
                        Bitmap bitmap = CreditUtil.autoResizeFromStream(inputStream, mArticWageView);
                        mArticWageView.setImageBitmap(bitmap);
                        Log.e("sakura","---设置照片  4");

                    } catch (Exception e) {

                    }
                }

                checkCanNext();
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException, ServiceException serviceException) {

            }
        });
    }


    private void setPhotoSource() {
/*        if (TextUtils.isEmpty(indentifyOssKey)) {
            ToastUtil.showShort(R.string.upload_indentify_card_tip_str);
            return;
        }
        if (TextUtils.isEmpty(handleOssKey)) {
            ToastUtil.showShort(R.string.upload_handle_card_tip_str);
            return;
        }
        if (TextUtils.isEmpty(workOssKey)) {
            ToastUtil.showShort(R.string.upload_work_card_tip_str);
            return;
        }
        if (TextUtils.isEmpty(articOssKey)) {
            ToastUtil.showShort(R.string.upload_article_card_tip_str);
            return;
        }*/
/*        File indentifyCardFile = new File(indentifyCardPath);
        File handleCardFile = new File(handleCardPath);
        File workCardFile = new File(workCardPath);
        File articWageFile = new File(articWagePath);
        if (!indentifyCardFile.exists()) {
            indentifyCardPath = "";
        }
        if (!handleCardFile.exists()) {
            handleCardPath = "";
        }
        if (!workCardFile.exists()) {
            workCardPath = "";
        }
        if (!articWageFile.exists()) {
            articWagePath = "";
        }*/
        if (TextUtils.isEmpty(indentifyOssKey)) {
            ToastUtil.showShort(R.string.upload_indentify_card_tip_str);
            return;
        }
        if (TextUtils.isEmpty(handleOssKey)) {
            ToastUtil.showShort(R.string.upload_handle_card_tip_str);
            return;
        }
        if (TextUtils.isEmpty(workOssKey)) {
            ToastUtil.showShort(R.string.upload_work_card_tip_str);
            return;
        }
        if (TextUtils.isEmpty(articOssKey)) {
            ToastUtil.showShort(R.string.upload_article_card_tip_str);
            return;
        }
        mBorrowBean.setIdCardPhotoFront(indentifyOssKey);
        mBorrowBean.setPeopleIdCardPhoto(handleOssKey);
        mBorrowBean.setWorkCardPhoto(workOssKey);
        mBorrowBean.setPayrollPhoto(articOssKey);
        mActivity.gotoBankInfo();

    }

    private void storeSource() {
        mBorrowBean.setIdCardPhotoFront(indentifyOssKey);
        mBorrowBean.setPeopleIdCardPhoto(handleOssKey);
        mBorrowBean.setWorkCardPhoto(workOssKey);
        mBorrowBean.setPayrollPhoto(articOssKey);
    }

    @Override
    public void onPause() {
        super.onPause();
        storeSource();
    }

    //显示提示框
    //身份证正面照
    private void showIdentifyCardTip() {
        final MaterialDialog dialog = new MaterialDialog(getActivity());
        dialog.setCanceledOnTouchOutside(true);
        dialog.setBackgroundResource(android.R.color.transparent);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo_tip_layout, null);
        dialog.setView(view);
        ImageView typeImageView = (ImageView) view.findViewById(R.id.card_tip_pic);
        TextView tipView = (TextView) view.findViewById(R.id.tipe_view);
        tipView.setText(R.string.identity_card_str);
        Glide.with(this).load(R.mipmap.card_front_example).centerCrop().transform(new GlideRoundTransform(getActivity(), 10)).into(typeImageView);
        TextView sureLayout = (TextView) view.findViewById(R.id.btn_sure);
        sureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indentifyCardPath = PlatFormUtil.takePhoto(getActivity(), "identify.jpg", INDENTIFYCARD);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //手持身份证
    private void showHandleCardTip() {
        final MaterialDialog dialog = new MaterialDialog(getActivity());
        dialog.setCanceledOnTouchOutside(true);
        dialog.setBackgroundResource(android.R.color.transparent);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo_tip_layout, null);
        dialog.setView(view);
        ImageView typeImageView = (ImageView) view.findViewById(R.id.card_tip_pic);
        TextView tipView = (TextView) view.findViewById(R.id.tipe_view);
        tipView.setText(R.string.handle_card_str);
        Glide.with(this).load(R.mipmap.handle_card_example).centerCrop().transform(new GlideRoundTransform(getActivity(), 10)).into(typeImageView);
        TextView sureLayout = (TextView) view.findViewById(R.id.btn_sure);
        sureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCardPath = PlatFormUtil.takePhoto(getActivity(), "handle.jpg", HANDLECARD);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //工作证
    private void showWorkCardTip() {
        final MaterialDialog dialog = new MaterialDialog(getActivity());
        dialog.setCanceledOnTouchOutside(true);
        dialog.setBackgroundResource(android.R.color.transparent);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo_tip_layout, null);
        dialog.setView(view);
        ImageView typeImageView = (ImageView) view.findViewById(R.id.card_tip_pic);
        TextView tipView = (TextView) view.findViewById(R.id.tipe_view);
        tipView.setText(R.string.work_card_str);
        Glide.with(this).load(R.mipmap.work_card_example).centerCrop().transform(new GlideRoundTransform(getActivity(), 10)).into(typeImageView);
        TextView sureLayout = (TextView) view.findViewById(R.id.btn_sure);
        sureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workCardPath = PlatFormUtil.takePhoto(getActivity(), "work.jpg", WORKCARD);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //工资条
    private void showWageCardTip() {
        final MaterialDialog dialog = new MaterialDialog(getActivity());
        dialog.setCanceledOnTouchOutside(true);
        dialog.setBackgroundResource(android.R.color.transparent);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo_tip_layout, null);
        dialog.setView(view);
        ImageView typeImageView = (ImageView) view.findViewById(R.id.card_tip_pic);
        TextView tipView = (TextView) view.findViewById(R.id.tipe_view);
        tipView.setText(R.string.argic_wage_str);
        Glide.with(this).load(R.mipmap.article_wage_example).centerCrop().transform(new GlideRoundTransform(getActivity(), 10)).into(typeImageView);
        TextView sureLayout = (TextView) view.findViewById(R.id.btn_sure);
        sureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                articWagePath = PlatFormUtil.takePhoto(getActivity(), "wage.jpg", ARTICWAGE);
                Log.e("sakura","----点击了拍照，得到的图片地址="+articWagePath);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @OnClick(value = {R.id.btn_next_three, R.id.identify_card_layout,
            R.id.handle_card_layout, R.id.work_card_layout, R.id.artic_wage_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next_three:
                setPhotoSource();
                if (mFirebaseAnalytics != null) {
                    mFirebaseAnalytics.logEvent(AnalyticUtil.BORROW_STEP_THREE_CLICK, null);
                }
                break;
            case R.id.identify_card_layout:
                //身份证正面照
                if (!CreditUtil.isFastDoubleClick()) {
                    showIdentifyCardTip();
                    if (mFirebaseAnalytics != null) {
                        mFirebaseAnalytics.logEvent(AnalyticUtil.BORROW_STEP_THREE_CARD_CLICK, null);
                    }
                }
                //methodRequireIndentifyCard();
                //indentifyCardPath = PlatFormUtil.takePhoto(getActivity(), "identify.jpg", INDENTIFYCARD);
                break;
            case R.id.handle_card_layout:
                //手持身份证照
                if (!CreditUtil.isFastDoubleClick()) {
                    showHandleCardTip();
                    if (mFirebaseAnalytics != null) {
                        mFirebaseAnalytics.logEvent(AnalyticUtil.BORROW_STEP_THREE_HANDLE_CLICK, null);
                    }
                }
                // handleCardPath = PlatFormUtil.takePhoto(getActivity(), "handle.jpg", HANDLECARD);
                break;
            case R.id.work_card_layout:
                //工作证
                if (!CreditUtil.isFastDoubleClick()) {
                    showWorkCardTip();
                    if (mFirebaseAnalytics != null) {
                        mFirebaseAnalytics.logEvent(AnalyticUtil.BORROW_STEP_THREE_WORK_CLICK, null);
                    }
                }
                //workCardPath = PlatFormUtil.takePhoto(getActivity(), "work.jpg", WORKCARD);
                break;
            case R.id.artic_wage_layout:
                //工资条
                if (!CreditUtil.isFastDoubleClick()) {
                    showWageCardTip();
                    if (mFirebaseAnalytics != null) {
                        mFirebaseAnalytics.logEvent(AnalyticUtil.BORROW_STEP_THREE_WAGE_CLICK, null);
                    }
                }
                //articWagePath = PlatFormUtil.takePhoto(getActivity(), "wage.jpg", ARTICWAGE);
                break;
        }
    }

    private void checkCanNext() {
        if (!TextUtils.isEmpty(indentifyOssKey) && !TextUtils.isEmpty(handleOssKey)
                && !TextUtils.isEmpty(workOssKey) && !TextUtils.isEmpty(articOssKey)) {
            mBtnNext.setTextColor(CreditApplication.getInstance().getResources().getColor(R.color.white));
            mBtnNext.setBackgroundResource(R.mipmap.bg_register);
        } else {
            mBtnNext.setTextColor(CreditApplication.getInstance().getResources().getColor(R.color.need_input_color));
            mBtnNext.setBackgroundResource(R.drawable.bg_need_input);
        }
        Log.e("sakura","---indentifyOssKey="+indentifyOssKey+"---handleOssKey="+handleOssKey
        +"---workOssKey="+workOssKey+"---articOssKey="+articOssKey);
    }

    class UpLoadStatusHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String objectKey = msg.getData().getString("type");
            switch (msg.what) {
                case HANDLE_PIC_STATUS:
                    if (objectKey.contains(IndentifyType)) {
                        //身份证 正面照
                        indentifyOssKey = "";
                        mIdentifyProgress.setVisibility(View.GONE);
                        if(null !=CreditApplication.getInstance()) {
                            Glide.with(CreditApplication.getInstance()).load(R.mipmap.icon_need_pic).centerCrop().into(IdentifyCardView);
                        }
                    } else if (objectKey.contains(HandleCardType)) {
                        //手持身份证
                        handleOssKey = "";
                        mHandleProgress.setVisibility(View.GONE);
                        if(null !=CreditApplication.getInstance()) {
                            Glide.with(CreditApplication.getInstance()).load(R.mipmap.icon_need_pic).centerCrop().into(mHandleCardView);
                        }
                    } else if (objectKey.contains(WorkCardType)) {
                        //工作证照
                        workOssKey = "";
                        mWorkProgress.setVisibility(View.GONE);
                        if(null !=CreditApplication.getInstance()) {
                            Glide.with(CreditApplication.getInstance()).load(R.mipmap.icon_need_pic).centerCrop().into(mWorkCardView);
                        }
                    } else if (objectKey.contains(ArticleType)) {
                        //工资条
                        articOssKey = "";
                        mWageProgress.setVisibility(View.GONE);
                        if(null !=CreditApplication.getInstance()) {
                            Glide.with(CreditApplication.getInstance()).load(R.mipmap.icon_need_pic).centerCrop().into(mArticWageView);
                        }
                    }
                    checkCanNext();
                    break;
                case HANDLE_OK_STATUS:
                    if (objectKey.contains(IndentifyType)) {
                        //身份证 正面照
                        mIdentifyProgress.setProgress(100);
                        mIdentifyProgress.setVisibility(View.GONE);
                        //IdentifyCardView.setAlpha(1.0f);
                    } else if (objectKey.contains(HandleCardType)) {
                        //手持身份证
                        mHandleProgress.setProgress(100);
                        mHandleProgress.setVisibility(View.GONE);
                        //mHandleCardView.setAlpha(1.0f);
                    } else if (objectKey.contains(WorkCardType)) {
                        //工作证照
                        mWorkProgress.setProgress(100);
                        mWorkProgress.setVisibility(View.GONE);
                        //mWorkCardView.setAlpha(1.0f);
                    } else if (objectKey.contains(ArticleType)) {
                        //工资条
                        mWageProgress.setProgress(100);
                        mWageProgress.setVisibility(View.GONE);
                        //mArticWageView.setAlpha(1.0f);
                    }
                    checkCanNext();
                    break;
                case HANDLE_UPLOAD_PROGRESS:
                    long currentSize = msg.getData().getLong("currentSize");
                    long totalSize = msg.getData().getLong("totalSize");
                    int progress = (int) (currentSize * 100 / totalSize);
                    float alpha = 1.0f * currentSize / totalSize;
                    if (objectKey.contains(IndentifyType)) {
                        //身份证 正面照
                        mIdentifyProgress.setProgress(progress);
                        if (currentSize == totalSize) {
                        //    mIdentifyProgress.setProgress(PROGRESS_ING);
                            mIdentifyProgress.setVisibility(View.VISIBLE);
                        }
                        //IdentifyCardView.setAlpha(alpha);
                    } else if (objectKey.contains(HandleCardType)) {
                        //手持身份证
                        mHandleProgress.setProgress(progress);
                        if (currentSize == totalSize) {
                       //     mHandleProgress.setProgress(PROGRESS_ING);
                            mHandleProgress.setVisibility(View.VISIBLE);
                        }
                        //mHandleCardView.setAlpha(alpha);
                    } else if (objectKey.contains(WorkCardType)) {
                        //工作证照
                        mWorkProgress.setProgress(progress);
                        if (currentSize == totalSize) {
                       //     mWorkProgress.setProgress(PROGRESS_ING);
                            mWorkProgress.setVisibility(View.VISIBLE);
                        }
                        //mWorkCardView.setAlpha(alpha);
                    } else if (objectKey.contains(ArticleType)) {
                        //工资条
                        mWageProgress.setProgress(progress);
                        if (currentSize == totalSize) {
                       //     mWageProgress.setProgress(PROGRESS_ING);
                            mWageProgress.setVisibility(View.VISIBLE);
                        }
                        //mArticWageView.setAlpha(alpha);
                    }
                    break;
					case HANDLE_DOWNLOAD_PROGRESS:
                    long currentSize_down = msg.getData().getLong("currentSize");
                    long totalSize_down = msg.getData().getLong("totalSize");
                    int progress_down = (int) (currentSize_down * 100 / totalSize_down);
                    float alpha_down=1.0f*currentSize_down/totalSize_down;
                    if (objectKey.contains(IndentifyType)) {
                        //身份证 正面照
                        mIdentifyProgress.setProgress(progress_down);

                        if(currentSize_down==totalSize_down){
                            mIdentifyProgress.setVisibility(View.GONE);
                            Log.e("sakura"," IndentifyType下载照片完成");

                        }
                        //IdentifyCardView.setAlpha(alpha);
                    } else if (objectKey.contains(HandleCardType)) {
                        //手持身份证
                        mHandleProgress.setProgress(progress_down);
                        if(currentSize_down==totalSize_down){
                            mHandleProgress.setVisibility(View.GONE);
                            Log.e("sakura"," HandleCardType下载照片完成");

                        }
                        //mHandleCardView.setAlpha(alpha);
                    } else if (objectKey.contains(WorkCardType)) {
                        //工作证照
                        mWorkProgress.setProgress(progress_down);
                        if(currentSize_down==totalSize_down){
                            mWorkProgress.setVisibility(View.GONE);
                            Log.e("sakura"," WorkCardType下载照片完成");
                        }
                        //mWorkCardView.setAlpha(alpha);
                    } else if (objectKey.contains(ArticleType)) {
                        //工资条
                        mWageProgress.setProgress(progress_down);
                        if(currentSize_down==totalSize_down){
                            mWageProgress.setVisibility(View.GONE);
                            Log.e("sakura"," ArticleType下载照片完成");

                        }
                        //mArticWageView.setAlpha(alpha);
                    }
                        checkCanNext();
                    break;
            }
        }
    }


    boolean is_uploading = false;
    private void uploadFileToOss(final String type,final  String filePath) {

        is_uploading = true;

        if (type.contains(IndentifyType)) {
            //身份证 正面照
            mIdentifyProgress.setProgress(0);
            mIdentifyProgress.setVisibility(View.VISIBLE);
            indentifyOssKey = "";
            if (mIndentifyTask != null && !mIndentifyTask.isCanceled()) {
                mIndentifyTask.cancel();
            }
        } else if (type.contains(HandleCardType)) {
            //手持身份证
            mHandleProgress.setProgress(0);
            mHandleProgress.setVisibility(View.VISIBLE);
            handleOssKey = "";
            if (mHandleTask != null && !mHandleTask.isCanceled()) {
                mHandleTask.cancel();
            }
        } else if (type.contains(WorkCardType)) {
            //工作证照
            mWorkProgress.setProgress(0);
            mWorkProgress.setVisibility(View.VISIBLE);
            workOssKey = "";
            if (mWorkTask != null && !mWorkTask.isCanceled()) {
                mWorkTask.cancel();
            }
        } else if (type.contains(ArticleType)) {
            //工资条
            mWageProgress.setProgress(0);
            mWageProgress.setVisibility(View.VISIBLE);
            articOssKey = "";
            if (mWageTask != null && !mWageTask.isCanceled()) {
                mWageTask.cancel();
            }
        }
        checkCanNext();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String timeStr = simpleDateFormat.format(date);
        String userId = mPreference.get(AppPreferences.USER_ID, "");
        File file = new File(filePath);
        String fileName = file.getName();
        String objectKey = timeStr + File.separator + "order" + File.separator + type + File.separator + userId + "_" + fileName;
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(CreditApplication.BUCK_NAME, objectKey, filePath);
        put.setCRC64(OSSRequest.CRC64Config.YES);
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                String objectKey = request.getObjectKey();
                Message message = new Message();
                message.what = HANDLE_UPLOAD_PROGRESS;
                Bundle bundle = new Bundle();
                bundle.putString("type", objectKey);
                bundle.putLong("currentSize", currentSize);
                bundle.putLong("totalSize", totalSize);
                message.setData(bundle);
                mUploadStatusHandler.sendMessage(message);
            }
        });
        // 异步上传时可以设置进度回调
        OSSAsyncTask task = mOSS.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.e("sakura","---上传成功"+"---type="+type);

                is_uploading  = false;
                String objectKey = request.getObjectKey();
                if (objectKey.contains(IndentifyType)) {
                    //身份证 正面照
                    indentifyOssKey = objectKey;
                    mBorrowBean.setIdCardPhotoFront(indentifyOssKey);
                } else if (objectKey.contains(HandleCardType)) {
                    //手持身份证
                    handleOssKey = objectKey;
                    mBorrowBean.setPeopleIdCardPhoto(handleOssKey);
                } else if (objectKey.contains(WorkCardType)) {
                    //工作证照
                    workOssKey = objectKey;
                    mBorrowBean.setWorkCardPhoto(workOssKey);
                } else if (objectKey.contains(ArticleType)) {
                    //工资条
                    articOssKey = objectKey;
                    mBorrowBean.setPayrollPhoto(articOssKey);
                }
                Message message = new Message();
                message.what = HANDLE_OK_STATUS;
                Bundle bundle = new Bundle();
                bundle.putString("type", objectKey);
                message.setData(bundle);
                mUploadStatusHandler.sendMessage(message);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                //上传失败
                Log.e("sakura","---上传失败"+"---type="+type+"---"+serviceException.toString());
                is_uploading  = false;
                String objectKey = request.getObjectKey();
                Message message = new Message();
                message.what = HANDLE_PIC_STATUS;
                Bundle bundle = new Bundle();
                bundle.putString("type", objectKey);
                message.setData(bundle);
                mUploadStatusHandler.sendMessage(message);
            }
        });
        if (type.contains(IndentifyType)) {
            //身份证 正面照
            mIndentifyTask = task;
        } else if (type.contains(HandleCardType)) {
            //手持身份证
            mHandleTask = task;
        } else if (type.contains(WorkCardType)) {
            //工作证照
            mWorkTask = task;
        } else if (type.contains(ArticleType)) {
            //工资条
            mWageTask = task;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == INDENTIFYCARD) {
                //身份证正面照
                indentifyCardPath = PlatFormUtil.savePhoto(getActivity(), indentifyCardPath);
                File indentifyCardFile = new File(indentifyCardPath);
                if (!TextUtils.isEmpty(indentifyCardPath) && indentifyCardFile.exists()) {
                    Glide.with(CreditApplication.getInstance()).load(new File(indentifyCardPath)).diskCacheStrategy(DiskCacheStrategy.SOURCE).centerCrop().into(IdentifyCardView);
                    uploadFileToOss(IndentifyType, indentifyCardPath);
                } else {
                    indentifyCardPath = "";
                }
            } else if (requestCode == HANDLECARD) {
                //手持身份证照
                handleCardPath = PlatFormUtil.savePhoto(getActivity(), handleCardPath);
                File handleCardFile = new File(handleCardPath);
                if (!TextUtils.isEmpty(handleCardPath) && handleCardFile.exists()) {
                    Glide.with(CreditApplication.getInstance()).load(new File(handleCardPath)).diskCacheStrategy(DiskCacheStrategy.SOURCE).centerCrop().into(mHandleCardView);
                    uploadFileToOss(HandleCardType, handleCardPath);
                } else {
                    handleCardPath = "";
                }
            } else if (requestCode == WORKCARD) {
                //工作证
                workCardPath = PlatFormUtil.savePhoto(getActivity(), workCardPath);
                File workCardFile = new File(workCardPath);
                if (!TextUtils.isEmpty(workCardPath) && workCardFile.exists()) {
                    Glide.with(CreditApplication.getInstance()).load(new File(workCardPath)).diskCacheStrategy(DiskCacheStrategy.SOURCE).centerCrop().into(mWorkCardView);
                    uploadFileToOss(WorkCardType, workCardPath);
                } else {
                    workCardPath = "";
                }
            } else if (requestCode == ARTICWAGE) {
                //工资条
                articWagePath = PlatFormUtil.savePhoto(getActivity(), articWagePath);
                File articWageFile = new File(articWagePath);
                if (!TextUtils.isEmpty(articWagePath) && articWageFile.exists()) {
                    Glide.with(CreditApplication.getInstance()).load(new File(articWagePath)).diskCacheStrategy(DiskCacheStrategy.SOURCE).centerCrop().into(mArticWageView);
                    uploadFileToOss(ArticleType, articWagePath);
                } else {
                    articWagePath = "";
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUploadStatusHandler != null) {
            mUploadStatusHandler.removeCallbacksAndMessages(null);
        }
    }
}
