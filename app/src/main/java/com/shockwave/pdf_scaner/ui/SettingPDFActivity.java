package com.shockwave.pdf_scaner.ui;


import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.util.ParamUtils;
import com.shockwave.pdf_scaner.util.SPUtils;

public class SettingPDFActivity extends BaseActivity {

    private AppCompatImageView imgBack;
    private AppCompatTextView txtPageSize;
    private AppCompatTextView txtPageOrientation;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_pdf;
    }

    @Override
    protected void initView() {
        imgBack = findViewById(R.id.imgBack);
        txtPageSize = findViewById(R.id.txtPageSize);
        txtPageOrientation = findViewById(R.id.txtPageOrientation);
    }

    @Override
    protected void initData() {
        getSizePage();
    }

    private void getSizePage() {
        String pageSelected = SPUtils.getString(this, ParamUtils.PAGE_SIZE);
        if (pageSelected.isEmpty()) {
            txtPageSize.setText("A4");
        } else {
            txtPageSize.setText(pageSelected);
        }

        boolean pageOrientationSelected = SPUtils.getBoolean(this, ParamUtils.PAGE_LANDSCAPE);
        if (pageOrientationSelected) {
            txtPageSize.setText(getString(R.string.landscape));
        } else {
            txtPageSize.setText(getString(R.string.portrait));
        }
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