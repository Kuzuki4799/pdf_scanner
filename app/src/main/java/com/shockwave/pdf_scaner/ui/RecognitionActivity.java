package com.shockwave.pdf_scaner.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.util.ParamUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class RecognitionActivity extends BaseActivity implements View.OnClickListener {

    private AppCompatImageView imgBack;
    private AppCompatTextView txtPreviewImage;
    private AppCompatEditText edContent;
    private AppCompatTextView txtRecognitionAgain;
    private AppCompatTextView txtCopy;
    private AppCompatTextView txtExport;
    private AppCompatTextView txtNext;
    private AppCompatImageView imgPreview;

    private String currentPath;
    private boolean isShow = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recognition;
    }

    @Override
    protected void initView() {
        imgBack = findViewById(R.id.imgBack);
        txtCopy = findViewById(R.id.txtCopy);
        txtNext = findViewById(R.id.txtNext);
        edContent = findViewById(R.id.edContent);
        txtExport = findViewById(R.id.txtExport);
        imgPreview = findViewById(R.id.imgPreview);
        txtPreviewImage = findViewById(R.id.txtPreviewImage);
        txtRecognitionAgain = findViewById(R.id.txtRecognitionAgain);
    }

    @Override
    protected void initData() {
        currentPath = getIntent().getStringExtra(ParamUtils.LINK);
        Glide.with(this).load(currentPath).into(imgPreview);
        handlerRecorder();
    }

    private void handlerRecorder() {
        showProgressDialog();
        try {
            StringBuilder strBuilder = new StringBuilder();
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            detector.processImage(FirebaseVisionImage.fromFilePath(this, Uri.fromFile(new File(currentPath)))).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    for (int i = 0; i < firebaseVisionText.getTextBlocks().size(); i++) {
                        strBuilder.append(firebaseVisionText.getTextBlocks().get(i).getText());
                        strBuilder.append("\n");
                    }

                    edContent.setText(strBuilder.toString());
                    dismissProgressDialog();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void listener() {
        imgBack.setOnClickListener(this);
        txtPreviewImage.setOnClickListener(this);
        txtRecognitionAgain.setOnClickListener(this);
        txtCopy.setOnClickListener(this);
        txtExport.setOnClickListener(this);
        txtNext.setOnClickListener(this);
    }

    private void handlerShow() {
        if (isShow) {
            imgPreview.setVisibility(View.GONE);
            txtPreviewImage.setText(getString(R.string.show_image));
            Drawable left = getResources().getDrawable(R.drawable.ic_image);
            txtPreviewImage.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
        } else {
            imgPreview.setVisibility(View.VISIBLE);
            txtPreviewImage.setText(getString(R.string.hide_image));
            Drawable left = getResources().getDrawable(R.drawable.ic_arrow_up);
            txtPreviewImage.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
        }
        isShow = !isShow;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.txtPreviewImage:
                handlerShow();
                break;
            case R.id.txtRecognitionAgain:
                handlerRecorder();
                break;
            case R.id.txtCopy:
                ClipData clipData;
                ClipboardManager cbManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipData = ClipData.newPlainText("text", edContent.getText().toString());
                cbManager.setPrimaryClip(clipData);
                Toast.makeText(this, getString(R.string.copy_successful), Toast.LENGTH_SHORT).show();
                break;
            case R.id.txtExport:
                break;
            case R.id.txtNext:
                break;
        }
    }
}