package com.shockwave.pdf_scaner.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.adapter.ListImagePagerAdapter;
import com.shockwave.pdf_scaner.adapter.MiniImageAdapter;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.base.BaseRecyclerAdapter;
import com.shockwave.pdf_scaner.model.PhotoModel;
import com.shockwave.pdf_scaner.util.ParamUtils;
import com.shockwave.pdf_scaner.util.RecyclerUtils;
import com.shockwave.pdf_scaner.widget.DisableSwipeViewPager;

import java.util.ArrayList;

public class ListImageActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private AppCompatImageView imgBack;
    private AppCompatTextView txSize;
    private AppCompatTextView txtNext;
    private DisableSwipeViewPager viewPager;
    private RecyclerView recyclerViewImageSelected;

    private ListImagePagerAdapter listImagePagerAdapter;
    private MiniImageAdapter miniImageAdapter;
    private ArrayList<String> listImage = new ArrayList<>();
    private ArrayList<PhotoModel> listMiniImage = new ArrayList<>();

    private int originSize = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_list_image;
    }

    @Override
    protected void initView() {
        txSize = findViewById(R.id.txSize);
        imgBack = findViewById(R.id.imgBack);
        txtNext = findViewById(R.id.txtNext);
        viewPager = findViewById(R.id.view_pager);
        recyclerViewImageSelected = findViewById(R.id.recyclerViewImageSelected);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        listImage = getIntent().getStringArrayListExtra(ParamUtils.LINK);
        originSize = listImage.size();
        handlerList();
        setViewPager();
        setUpAdapter();
        txSize.setText("1/" + listImage.size());
    }

    private void handlerList() {
        for (int i = 0; i < listImage.size(); i++) {
            if (i == 0) listMiniImage.add(new PhotoModel(listImage.get(i), true));
            else listMiniImage.add(new PhotoModel(listImage.get(i)));
        }
    }

    private void setViewPager() {
        listImagePagerAdapter = new ListImagePagerAdapter(listImage);
        viewPager.setAdapter(listImagePagerAdapter);
        viewPager.setOffscreenPageLimit(listImage.size());
        viewPager.setPagingEnabled(true);
    }

    private void setUpAdapter() {
        miniImageAdapter = new MiniImageAdapter(this, listMiniImage);
        RecyclerUtils.layoutLinearHorizontal(this, recyclerViewImageSelected);
        recyclerViewImageSelected.setAdapter(miniImageAdapter);

        miniImageAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for (int i = 0; i < listMiniImage.size(); i++) {
                    listMiniImage.get(i).isSelected = position == i;
                }
                miniImageAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(position);
            }
        });

        miniImageAdapter.setOnItem2ClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(int position) {
                listImage.remove(position);
                if (listImage.size() == 0) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra(ParamUtils.LINK, listImage);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    listMiniImage.remove(position);
                    listImagePagerAdapter.notifyDataSetChanged();
                    for (int i = 0; i < listMiniImage.size(); i++) {
                        listImagePagerAdapter.setImage(viewPager.findViewWithTag("BasePaperAdapter" + i), listImage.get(i));
                    }

                    viewPager.setCurrentItem(position - 1);
                    for (int i = 0; i < listMiniImage.size(); i++) {
                        listMiniImage.get(i).isSelected = viewPager.getCurrentItem() == i;
                    }
                    miniImageAdapter.notifyDataSetChanged();
                    txSize.setText((position) + "/" + listImage.size());
                }
            }
        });
    }

    @Override
    protected void listener() {
        imgBack.setOnClickListener(this);
        txtNext.setOnClickListener(this);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onPageSelected(int position) {
        txSize.setText((position + 1) + "/" + listImage.size());
        for (int i = 0; i < listMiniImage.size(); i++) {
            listMiniImage.get(i).isSelected = position == i;
        }
        miniImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                if (listImage.size() != originSize) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra(ParamUtils.LINK, listImage);
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
            case R.id.txtNext:
                if (viewPager.getCurrentItem() + 1 != listImage.size()) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else {
                    Intent intent = new Intent(this, CropImageActivity.class);
                    intent.putStringArrayListExtra(ParamUtils.LINK, listImage);
                    intent.putExtra(ParamUtils.IS_ORC, false);
                    startActivity(intent);
                }
                break;
        }
    }
}