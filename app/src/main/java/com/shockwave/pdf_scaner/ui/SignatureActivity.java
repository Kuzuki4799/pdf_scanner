package com.shockwave.pdf_scaner.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.adapter.ColorAdapter;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.util.FileUtils;
import com.shockwave.pdf_scaner.util.ParamUtils;
import com.shockwave.pdf_scaner.util.RecyclerUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignatureActivity extends BaseActivity implements View.OnClickListener {

    private AppCompatImageView imgBack;
    private AppCompatTextView imgDone;
    private SignaturePad signaturePad;
    private AppCompatTextView txtClear;
    private RecyclerView recyclerViewColor;

    private ColorAdapter colorAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_signature;
    }

    @Override
    protected void initView() {
        imgBack = findViewById(R.id.imgBack);
        imgDone = findViewById(R.id.imgDone);
        signaturePad = findViewById(R.id.signature_pad);
        txtClear = findViewById(R.id.txtClear);
        recyclerViewColor = findViewById(R.id.recyclerViewColor);
    }

    @Override
    protected void initData() {
        setUpAdapter();
    }

    private void setUpAdapter() {
        colorAdapter = new ColorAdapter(this, ParamUtils.getDefaultColors(this));
        RecyclerUtils.layoutLinearHorizontal(this, recyclerViewColor);
        recyclerViewColor.setAdapter(colorAdapter);
        colorAdapter.setOnItemClickListener(position -> {
            for (int i = 0; i < colorAdapter.list.size(); i++) {
                colorAdapter.list.get(i).isSelected = false;
            }
            colorAdapter.list.get(position).isSelected = true;
            colorAdapter.notifyDataSetChanged();
            signaturePad.setPenColor(colorAdapter.list.get(position).color);
        });
    }

    @Override
    protected void listener() {
        imgBack.setOnClickListener(this);
        imgDone.setOnClickListener(this);
        txtClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.imgDone:
                showProgressDialog();
                try {
                    File file = FileUtils.createOfferMoreFile(this, false, ParamUtils.pngExtension);
                    FileOutputStream out = new FileOutputStream(file.getPath());
                    signaturePad.getTransparentSignatureBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
                    Intent intent = new Intent();
                    intent.putExtra(ParamUtils.LINK, file.getPath());
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.txtClear:
                signaturePad.clear();
                break;
        }
    }
}