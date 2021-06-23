package com.shockwave.pdf_scaner.ui;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.adapter.ColorAdapter;
import com.shockwave.pdf_scaner.adapter.ListImagePagerAdapter;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.util.ParamUtils;
import com.shockwave.pdf_scaner.util.RecyclerUtils;
import com.shockwave.pdf_scaner.widget.DisableSwipeViewPager;

import java.util.ArrayList;

public class WaterMarkActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener, TextWatcher {

    private AppCompatImageView imgBack;
    private DisableSwipeViewPager viewPager;
    private AppCompatTextView txtEdit;
    private AppCompatTextView txtClear;
    private AppCompatTextView txtShare;
    private AppCompatTextView txtNext;
    private LinearLayout rlTool;
    private AppCompatImageView imgClose;
    private AppCompatImageView imgDone;
    private AppCompatEditText edWatermark;
    private SeekBar seekBarSize;
    private AppCompatTextView txtPercentSize;
    private SeekBar seekBarAlpha;
    private AppCompatTextView txtPercentAlpha;
    private RecyclerView recyclerViewColor;

    private ColorAdapter colorAdapter;
    private int currentColor;
    private ListImagePagerAdapter listImagePagerAdapter;
    private ArrayList<String> listImage = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_water_mark;
    }

    @Override
    protected void initView() {
        imgBack = findViewById(R.id.imgBack);
        viewPager = findViewById(R.id.view_pager);
        txtEdit = findViewById(R.id.txtEdit);
        txtClear = findViewById(R.id.txtClear);
        txtShare = findViewById(R.id.txtShare);
        txtNext = findViewById(R.id.txtNext);
        rlTool = findViewById(R.id.rlTool);
        imgClose = findViewById(R.id.imgClose);
        imgDone = findViewById(R.id.imgDone);
        edWatermark = findViewById(R.id.edWatermark);
        seekBarSize = findViewById(R.id.seek_bar_size);
        txtPercentSize = findViewById(R.id.txtPercentSize);
        seekBarAlpha = findViewById(R.id.seek_bar_alpha);
        txtPercentAlpha = findViewById(R.id.txtPercentAlpha);
        recyclerViewColor = findViewById(R.id.recyclerViewColor);
    }

    @Override
    protected void initData() {
        currentColor = ContextCompat.getColor(this, R.color.black);
        listImage = getIntent().getStringArrayListExtra(ParamUtils.LINK);
        setUpAdapter();
        setViewPager();
    }

    private void setUpAdapter() {
        currentColor = ParamUtils.getDefaultColors2(this).get(0).color;
        colorAdapter = new ColorAdapter(this, ParamUtils.getDefaultColors2(this));
        RecyclerUtils.layoutLinearHorizontal(this, recyclerViewColor);
        recyclerViewColor.setAdapter(colorAdapter);
        colorAdapter.setOnItemClickListener(position -> {
            for (int i = 0; i < colorAdapter.list.size(); i++) {
                colorAdapter.list.get(i).isSelected = false;
            }
            currentColor = colorAdapter.list.get(position).color;
            colorAdapter.list.get(position).isSelected = true;
            colorAdapter.notifyDataSetChanged();
            handlerCreateWaterMark();
        });
    }

    private void handlerCreateWaterMark() {
        for (int i = 0; i < listImage.size(); i++) {
            listImagePagerAdapter.setWatermark(viewPager.findViewWithTag("BasePaperAdapter" + i), listImage.get(i), edWatermark.getText().toString(), currentColor, seekBarAlpha.getProgress() * 2, seekBarSize.getProgress());
        }
    }

    private void setViewPager() {
        listImagePagerAdapter = new ListImagePagerAdapter(listImage);
        viewPager.setAdapter(listImagePagerAdapter);
        viewPager.setOffscreenPageLimit(listImage.size());
        viewPager.setPagingEnabled(true);

        new Handler().postDelayed(this::handlerCreateWaterMark, 1000);
    }

    @Override
    protected void listener() {
        imgBack.setOnClickListener(this);
        txtEdit.setOnClickListener(this);
        txtClear.setOnClickListener(this);
        txtShare.setOnClickListener(this);
        txtNext.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        imgDone.setOnClickListener(this);

        seekBarAlpha.setOnSeekBarChangeListener(this);
        seekBarSize.setOnSeekBarChangeListener(this);
        edWatermark.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.txtNext:
                showProgressDialog();
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < listImage.size(); i++) {
                    list.add(listImagePagerAdapter.getImageWatermark(viewPager.findViewWithTag("BasePaperAdapter" + i)));
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra(ParamUtils.LINK, list);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.txtEdit:
                rlTool.setVisibility(View.VISIBLE);
                break;
            case R.id.txtClear:
                for (int i = 0; i < listImage.size(); i++) {
                    listImagePagerAdapter.setImage(viewPager.findViewWithTag("BasePaperAdapter" + i), listImage.get(i));
                }
                break;
            case R.id.imgClose:
                rlTool.setVisibility(View.GONE);
                break;
            case R.id.imgDone:
                rlTool.setVisibility(View.GONE);
                handlerCreateWaterMark();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.seek_bar_alpha) {
            txtPercentAlpha.setText(seekBar.getProgress() + "%");
        } else {
            txtPercentSize.setText(String.valueOf(seekBar.getProgress()));
        }
        handlerCreateWaterMark();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        handlerCreateWaterMark();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}