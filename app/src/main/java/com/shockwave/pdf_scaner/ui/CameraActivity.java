package com.shockwave.pdf_scaner.ui;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.util.FileUtils;
import com.shockwave.pdf_scaner.util.ParamUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.selector.FlashSelectorsKt;
import io.fotoapparat.selector.FocusModeSelectorsKt;
import io.fotoapparat.selector.JpegQualitySelectorsKt;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.view.CameraView;
import io.fotoapparat.view.FocusView;

public class CameraActivity extends BaseActivity implements View.OnClickListener {

    private AppCompatImageView imgCancel;
    private AppCompatImageView btnCapture;
    private AppCompatImageView imgImage;
    private AppCompatImageView imgStorage;
    private AppCompatImageView imgDone;
    private AppCompatTextView txtDoc;
    private AppCompatTextView txtOrc;
    private CameraView camera;
    private FocusView focusView;

    private RelativeLayout rlImage;
    private RelativeLayout rlOrc;
    private AppCompatTextView txtSize;

    private Fotoapparat fotoapparat;
    private PhotoResult photoResult;

    private ArrayList<String> arrayListPath = new ArrayList<>();

    private boolean isOrc = false;
    private boolean isOnLy = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initView() {
        imgCancel = findViewById(R.id.imgCancel);
        btnCapture = findViewById(R.id.btn_capture);
        imgImage = findViewById(R.id.imgImage);
        imgStorage = findViewById(R.id.imgStorage);
        imgDone = findViewById(R.id.imgDone);
        camera = findViewById(R.id.camera);
        rlImage = findViewById(R.id.rlImage);
        txtSize = findViewById(R.id.txtSize);
        focusView = findViewById(R.id.focusView);
        txtDoc = findViewById(R.id.txtDoc);
        txtOrc = findViewById(R.id.txtOrc);
        rlOrc = findViewById(R.id.rlOrc);
    }

    @Override
    protected void initData() {
        isOnLy = getIntent().getBooleanExtra(ParamUtils.IS_ONLY, false);
        imgImage.setClipToOutline(true);
        initCamera();
        checkIsOrc();
        if (isOnLy) rlOrc.setVisibility(View.GONE);
    }

    private void initCamera() {
        CameraConfiguration cameraConfiguration = CameraConfiguration.builder()
                .flash(FlashSelectorsKt.autoFlash())
                .focusMode(FocusModeSelectorsKt.autoFocus())
                .jpegQuality(JpegQualitySelectorsKt.highestQuality())
                .build();

        fotoapparat = new Fotoapparat(this,
                camera, focusView, LensPositionSelectorsKt.back(),
                ScaleType.CenterCrop, cameraConfiguration,
                e -> null);
    }

    private void handlerEnableOrc(boolean isEnable) {
        if (isEnable) {
            txtOrc.setEnabled(true);
            txtOrc.setTextColor(getResources().getColor(R.color.white));
        } else {
            txtOrc.setEnabled(false);
            txtOrc.setTextColor(getResources().getColor(R.color.gray_blur_));
        }
    }

    private void checkIsOrc() {
        if (isOrc) {
            txtOrc.setTextColor(getResources().getColor(R.color.car_care_green_base));
            txtDoc.setTextColor(getResources().getColor(R.color.white));
        } else {
            txtDoc.setTextColor(getResources().getColor(R.color.car_care_green_base));
            txtOrc.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (fotoapparat != null) {
            fotoapparat.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fotoapparat != null) {
            fotoapparat.stop();
        }
    }

    @Override
    protected void listener() {
        imgDone.setOnClickListener(this);
        imgCancel.setOnClickListener(this);
        btnCapture.setOnClickListener(this);
        imgStorage.setOnClickListener(this);
        imgImage.setOnClickListener(this);
        txtOrc.setOnClickListener(this);
        txtDoc.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == RESULT_OK) {
            ArrayList<String> list = data.getStringArrayListExtra(ParamUtils.LINK);
            if (list.size() == 0) {
                imgDone.setVisibility(View.GONE);
                rlImage.setVisibility(View.GONE);
                handlerEnableOrc(true);
            } else {
                Glide.with(this).load(list.get(list.size() - 1)).into(imgImage);
                txtSize.setText(String.valueOf(list.size()));
            }
            arrayListPath = list;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgDone:
                Intent intentDone = new Intent(this, CropImageActivity.class);
                intentDone.putStringArrayListExtra(ParamUtils.LINK, arrayListPath);
                intentDone.putExtra(ParamUtils.IS_ORC, false);
                startActivity(intentDone);
                break;
            case R.id.imgCancel:
                finish();
                break;
            case R.id.imgImage:
                Intent intent = new Intent(this, ListImageActivity.class);
                intent.putStringArrayListExtra(ParamUtils.LINK, arrayListPath);
                startActivityForResult(intent, 99);
                break;
            case R.id.btn_capture:
                showProgressDialog();
                photoResult = fotoapparat.takePicture();
                try {
                    File createFile = FileUtils.createOfferMoreFile(this, false, ParamUtils.pngExtension);
                    photoResult.saveToFile(createFile).whenDone(file -> {
                        if (isOnLy) {
                            Intent intentBack = new Intent();
                            intentBack.putExtra(ParamUtils.LINK, createFile.getPath());
                            setResult(RESULT_OK, intentBack);
                            finish();
                        } else {
                            arrayListPath.add(createFile.getPath());
                            if (isOrc) {
                                Intent intentOrc = new Intent(this, CropImageActivity.class);
                                intentOrc.putStringArrayListExtra(ParamUtils.LINK, arrayListPath);
                                intentOrc.putExtra(ParamUtils.IS_ORC, true);
                                startActivity(intentOrc);
                            } else {
                                Glide.with(this).load(createFile.getPath()).into(imgImage);
                                imgDone.setVisibility(View.VISIBLE);
                                rlImage.setVisibility(View.VISIBLE);
                                txtSize.setText(String.valueOf(arrayListPath.size()));
                                handlerEnableOrc(false);
                            }
                        }
                        dismissProgressDialog();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imgStorage:
                startActivity(new Intent(this, ImageActivity.class));
                break;
            case R.id.txtOrc:
                isOrc = true;
                checkIsOrc();
                break;
            case R.id.txtDoc:
                isOrc = false;
                checkIsOrc();
                break;
        }
    }
}