package com.shockwave.pdf_scaner.ui;

import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatTextView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseActivity;

public class ResultActivity extends BaseActivity implements View.OnClickListener {

    private AppCompatTextView txtName;
    private Button btnAddPage;
    private Button btnShare;
    private AppCompatTextView txtPdfPreview;
    private AppCompatTextView txtEmail;
    private AppCompatTextView txtSignature;
    private AppCompatTextView txtWatermark;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_result;
    }

    @Override
    protected void initView() {
        txtName = findViewById(R.id.txtName);
        btnAddPage = findViewById(R.id.btnAddPage);
        btnShare = findViewById(R.id.btnShare);
        txtPdfPreview = findViewById(R.id.txtPdfPreview);
        txtEmail = findViewById(R.id.txtEmail);
        txtSignature = findViewById(R.id.txtSignature);
        txtWatermark = findViewById(R.id.txtWatermark);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void listener() {
        btnAddPage.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        txtPdfPreview.setOnClickListener(this);
        txtEmail.setOnClickListener(this);
        txtSignature.setOnClickListener(this);
        txtWatermark.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddPage:
                break;
            case R.id.btnShare:
                break;
            case R.id.txtPdfPreview:
                break;
            case R.id.txtEmail:
                break;
            case R.id.txtSignature:
                break;
            case R.id.txtWatermark:
                break;
        }
    }
}