package com.shockwave.pdf_scaner.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.adapter.FilterViewAdapter;
import com.shockwave.pdf_scaner.adapter.ListImagePagerAdapter;
import com.shockwave.pdf_scaner.adapter.SignatureAdapter;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.base.BaseRecyclerAdapter;
import com.shockwave.pdf_scaner.model.PhotoModel;
import com.shockwave.pdf_scaner.util.ParamUtils;
import com.shockwave.pdf_scaner.util.RecyclerUtils;
import com.shockwave.pdf_scaner.util.SPUtils;
import com.shockwave.pdf_scaner.widget.DisableSwipeViewPager;
import com.watermark.androidwm.utils.Constant;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class EditImageActivity extends BaseActivity implements View.OnClickListener {

    private AppCompatImageView imgBack;
    private AppCompatTextView txtName;
    private AppCompatImageView imgDone;
    private DisableSwipeViewPager viewPager;
    private AppCompatTextView txtFilter;
    private AppCompatTextView txtSignature;
    private AppCompatTextView txtWatermark;
    private AppCompatTextView txtSetting;
    private RecyclerView recyclerViewFilter;
    private final int CODE_SIGNATURE = 19;
    private final int CODE_WATERMARK = 999;

    private RelativeLayout rlSignature;
    private AppCompatImageView imgAdd;
    private RecyclerView recyclerViewSignature;

    private ListImagePagerAdapter listImagePagerAdapter;
    private FilterViewAdapter filterAdapter;
    private SignatureAdapter signatureAdapter;
    private ArrayList<String> listImage = new ArrayList<>();
    private ArrayList<PhotoModel> listSignature = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_image;
    }

    @Override
    protected void initView() {
        imgBack = findViewById(R.id.imgBack);
        txtName = findViewById(R.id.txtName);
        imgDone = findViewById(R.id.imgDone);
        viewPager = findViewById(R.id.view_pager);
        txtFilter = findViewById(R.id.txtFilter);
        txtSignature = findViewById(R.id.txtSignature);
        txtWatermark = findViewById(R.id.txtWatermark);
        txtSetting = findViewById(R.id.txtSetting);
        recyclerViewFilter = findViewById(R.id.recyclerViewFilter);
        rlSignature = findViewById(R.id.rlSignature);
        imgAdd = findViewById(R.id.imgAdd);
        recyclerViewSignature = findViewById(R.id.recyclerViewSignature);
    }

    @Override
    protected void initData() {
        listImage = getIntent().getStringArrayListExtra(ParamUtils.LINK);
        try {
            listSignature.addAll(SPUtils.getArray(this, PhotoModel.class));
        } catch (Exception e) {
        }
        setViewPager();
        setUpAdapter();
    }

    private void setUpAdapter() {
        filterAdapter = new FilterViewAdapter(new FilterViewAdapter.FilterListener() {
            @Override
            public void onFilterSelected(PhotoFilter photoFilter) {
                listImagePagerAdapter.setFilter(viewPager.findViewWithTag("BasePaperAdapter" + viewPager.getCurrentItem()), photoFilter);
            }
        });
        RecyclerUtils.layoutLinearHorizontal(this, recyclerViewFilter);
        recyclerViewFilter.setAdapter(filterAdapter);

        signatureAdapter = new SignatureAdapter(this, listSignature);
        RecyclerUtils.layoutLinearHorizontal(this, recyclerViewSignature);
        recyclerViewSignature.setAdapter(signatureAdapter);
        signatureAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for (int i = 0; i < listSignature.size(); i++) {
                    listSignature.get(i).isSelected = false;
                }
                listSignature.get(position).isSelected = true;
                signatureAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setViewPager() {
        listImagePagerAdapter = new ListImagePagerAdapter(listImage);
        viewPager.setAdapter(listImagePagerAdapter);
        viewPager.setOffscreenPageLimit(listImage.size());
        viewPager.setPagingEnabled(true);
    }

    @Override
    protected void listener() {
        imgBack.setOnClickListener(this);
        imgDone.setOnClickListener(this);
        txtFilter.setOnClickListener(this);
        txtSignature.setOnClickListener(this);
        txtWatermark.setOnClickListener(this);
        txtSetting.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.txtFilter:
                recyclerViewFilter.setVisibility(View.VISIBLE);
                rlSignature.setVisibility(View.GONE);
                break;
            case R.id.imgAdd:
                startActivityForResult(new Intent(this, SignatureActivity.class), CODE_SIGNATURE);
                break;
            case R.id.txtSignature:
                recyclerViewFilter.setVisibility(View.GONE);
                rlSignature.setVisibility(View.VISIBLE);
                if (listSignature.size() == 0) {
                    startActivityForResult(new Intent(this, SignatureActivity.class), CODE_SIGNATURE);
                }
                break;
            case R.id.txtWatermark:
                recyclerViewFilter.setVisibility(View.GONE);
                rlSignature.setVisibility(View.GONE);
                Intent intent = new Intent(this, WaterMarkActivity.class);
                intent.putStringArrayListExtra(ParamUtils.LINK, listImage);
                startActivityForResult(intent, CODE_WATERMARK);
                break;
            case R.id.txtSetting:
                recyclerViewFilter.setVisibility(View.GONE);
                rlSignature.setVisibility(View.GONE);
                startActivity(new Intent(this, SettingPDFActivity.class));
                break;
            case R.id.imgDone:
                exportPDF();
                break;
        }
    }

    @SuppressLint("CheckResult")
    private void exportPDF() {
        showProgressDialog();
        Observable.just(listImagePagerAdapter.saveImage(viewPager))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(files -> {
            System.out.println("FUKKKKKKKK" + files);
            Observable.just(listImagePagerAdapter.convertPDF(files))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull Boolean aBoolean) {
                            System.out.println("FUKKKKKKKK" + aBoolean);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            dismissProgressDialog();
                        }

                        @Override
                        public void onComplete() {
                            dismissProgressDialog();
                        }
                    });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_WATERMARK && resultCode == RESULT_OK) {
            showProgressDialog();
            ArrayList<String> list = new ArrayList<>();
            list.addAll(data.getStringArrayListExtra(ParamUtils.LINK));
            listImage = list;
            for (int i = 0; i < list.size(); i++) {
                int finalI = i;
                listImagePagerAdapter.setImage(viewPager.findViewWithTag("BasePaperAdapter" + i), list.get(i), () -> {
                    if (finalI == list.size() - 1) {
                        dismissProgressDialog();
                    }
                });
            }
        }

        if (requestCode == CODE_SIGNATURE && resultCode == RESULT_OK) {
            String path = data.getStringExtra(ParamUtils.LINK);
            for (int i = 0; i < listImage.size(); i++) {
                listImagePagerAdapter.addSticker(viewPager.findViewWithTag("BasePaperAdapter" + i), path);
            }

            listSignature.add(new PhotoModel(path, false));
            SPUtils.putArray(this, PhotoModel.class.getName(), listSignature);
            signatureAdapter.notifyDataSetChanged();
        }
    }
}