package com.shockwave.pdf_scaner.ui;


import android.view.View;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatImageView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseActivity;

public class SettingPDFActivity extends BaseActivity {

    private AppCompatImageView imgBack;
    private Spinner spinnerPdf;
    private Spinner spinnerOrientation;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_pdf;
    }

    @Override
    protected void initView() {
        imgBack = findViewById(R.id.imgBack);
        spinnerPdf = findViewById(R.id.spinnerPdf);
        spinnerOrientation = findViewById(R.id.spinnerOrientation);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void listener() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}