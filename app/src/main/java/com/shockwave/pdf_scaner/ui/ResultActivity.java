package com.shockwave.pdf_scaner.ui;

import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.util.ParamUtils;

import java.io.File;
import java.util.ArrayList;

public class ResultActivity extends BaseActivity implements View.OnClickListener {

    private AppCompatTextView txtName;
    private Button btnAddPage;
    private Button btnShare;
    private AppCompatImageView imgPdf;
    private AppCompatTextView txtPdfPreview;
    private AppCompatTextView txtEmail;
    private AppCompatTextView txtSignature;
    private AppCompatTextView txtWatermark;

    private String pathFile = "";
    private ArrayList<String> listImage = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_result;
    }

    @Override
    protected void initView() {
        imgPdf = findViewById(R.id.imgPdf);
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
//        pathFile = getIntent().getStringExtra(ParamUtils.URI);
        listImage = getIntent().getStringArrayListExtra(ParamUtils.LINK);
        txtName.setText(new File(pathFile).getName());
        Glide.with(this).load(listImage.get(0)).into(imgPdf);
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