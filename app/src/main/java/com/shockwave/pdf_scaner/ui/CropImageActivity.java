package com.shockwave.pdf_scaner.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.ViewPager;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.adapter.CropPagerAdapter;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.util.ParamUtils;
import com.shockwave.pdf_scaner.widget.DisableSwipeViewPager;

import java.util.ArrayList;

public class CropImageActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private AppCompatImageView imgBack;
    private AppCompatTextView txSize;
    private AppCompatTextView txtNext;
    private DisableSwipeViewPager viewPager;
    private AppCompatTextView txtNoCut;
    private AppCompatTextView txtCapAgainOrLeft;
    private AppCompatTextView txtRotate;
    private AppCompatImageView imgDelete;

    private CropPagerAdapter cropPagerAdapter;
    private ArrayList<String> listImage = new ArrayList<>();
    private boolean isOrc = false;
    private boolean isOnly = false;
    private boolean isNoCut = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_crop_image;
    }

    @Override
    protected void initView() {
        txSize = findViewById(R.id.txSize);
        imgBack = findViewById(R.id.imgBack);
        txtNext = findViewById(R.id.txtNext);
        txtNoCut = findViewById(R.id.txtNoCut);
        imgDelete = findViewById(R.id.imgDelete);
        txtRotate = findViewById(R.id.txtRotate);
        viewPager = findViewById(R.id.view_pager);
        txtCapAgainOrLeft = findViewById(R.id.txtCapAgainOrLeft);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        listImage = getIntent().getStringArrayListExtra(ParamUtils.LINK);
        if (listImage.size() == 1) {
            isOnly = true;
            Drawable top = getResources().getDrawable(R.drawable.ic_rotate_left);
            txtCapAgainOrLeft.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            txtCapAgainOrLeft.setText(getString(R.string.left));
        }
        isOrc = getIntent().getBooleanExtra(ParamUtils.IS_ORC, false);
        setViewPager();
        txSize.setText("1/" + listImage.size());
    }

    private void setViewPager() {
        showProgressDialog();
        cropPagerAdapter = new CropPagerAdapter(this, listImage);
        viewPager.setAdapter(cropPagerAdapter);
        viewPager.setOffscreenPageLimit(listImage.size());
        viewPager.setPagingEnabled(true);
    }

    @Override
    protected void listener() {
        imgBack.setOnClickListener(this);
        txtNext.setOnClickListener(this);
        txtNoCut.setOnClickListener(this);
        txtRotate.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
        viewPager.setOnPageChangeListener(this);
        txtCapAgainOrLeft.setOnClickListener(this);
    }

    private void isAutoCut() {
        showProgressDialog();
        if (isNoCut) {
            isNoCut = false;
            txtNoCut.setText(getString(R.string.no_cut));
            Drawable top = getResources().getDrawable(R.drawable.ic_no_cut);
            txtNoCut.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            cropPagerAdapter.setNoCut(viewPager.findViewWithTag("BasePaperAdapter" + viewPager.getCurrentItem()), false, listImage.get(viewPager.getCurrentItem()));
        } else {
            isNoCut = true;
            txtNoCut.setText(getString(R.string.auto));
            Drawable top = getResources().getDrawable(R.drawable.ic_crop);
            txtNoCut.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            cropPagerAdapter.setNoCut(viewPager.findViewWithTag("BasePaperAdapter" + viewPager.getCurrentItem()), true, listImage.get(viewPager.getCurrentItem()));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onPageSelected(int position) {
        txSize.setText((position + 1) + "/" + listImage.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == RESULT_OK) {
            showProgressDialog();
            String imagePath = data.getStringExtra(ParamUtils.LINK);
            cropPagerAdapter.setImage(this, viewPager.findViewWithTag("BasePaperAdapter" + viewPager.getCurrentItem()), imagePath);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.txtCapAgainOrLeft:
                if (!isOnly) {
                    Intent intent = new Intent(this, CameraActivity.class);
                    intent.putExtra(ParamUtils.IS_ONLY, true);
                    startActivityForResult(intent, 999);
                } else {
                    cropPagerAdapter.setRotate(viewPager.findViewWithTag("BasePaperAdapter" + viewPager.getCurrentItem()), false);
                }
                break;
            case R.id.txtNoCut:
                isAutoCut();
                break;
            case R.id.imgDelete:
                if (viewPager.getCurrentItem() != 0) {
                    listImage.remove(viewPager.getCurrentItem());
                    if (listImage.size() == 0) finish();
                    else {
                        cropPagerAdapter.notifyDataSetChanged();
                        for (int i = 0; i < listImage.size(); i++) {
                            cropPagerAdapter.setImage(this, viewPager.findViewWithTag("BasePaperAdapter" + i), listImage.get(i));
                        }
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                        txSize.setText((viewPager.getCurrentItem() + 1) + "/" + listImage.size());
                    }
                } else {
                    listImage.remove(viewPager.getCurrentItem());
                    if (listImage.size() == 0) finish();
                    else {
                        cropPagerAdapter.notifyDataSetChanged();
                        for (int i = 0; i < listImage.size(); i++) {
                            cropPagerAdapter.setImage(this, viewPager.findViewWithTag("BasePaperAdapter" + i), listImage.get(i));
                        }
                        viewPager.setCurrentItem(viewPager.getCurrentItem());
                        txSize.setText((viewPager.getCurrentItem() + 1) + "/" + listImage.size());
                    }
                }
                break;
            case R.id.txtRotate:
                cropPagerAdapter.setRotate(viewPager.findViewWithTag("BasePaperAdapter" + viewPager.getCurrentItem()), true);
                break;
            case R.id.txtNext:
                showProgressDialog();
                if (isOrc) {

                } else {
                    ArrayList<String> listCropDone = new ArrayList<>();
                    for (int i = 0; i < listImage.size(); i++) {
                        listCropDone.add(cropPagerAdapter.getCropImage(viewPager.findViewWithTag("BasePaperAdapter" + i)));
                    }

                    Intent intent = new Intent(this, EditImageActivity.class);
                    intent.putStringArrayListExtra(ParamUtils.LINK, listCropDone);
                    startActivity(intent);
                    new Handler().postDelayed(this::dismissProgressDialog, 500);
                }
                break;
        }
    }
}