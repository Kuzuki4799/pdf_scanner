package com.shockwave.pdf_scaner.ui;

import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.adapter.PageSizeAdapter;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.base.BaseRecyclerAdapter;
import com.shockwave.pdf_scaner.util.ParamUtils;
import com.shockwave.pdf_scaner.util.RecyclerUtils;
import com.shockwave.pdf_scaner.util.SPUtils;

public class PageSizeActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private PageSizeAdapter adapter;
    private AppCompatImageView imgBack;
    private AppCompatTextView submit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_page_size;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        imgBack = findViewById(R.id.imgBack);
        submit = findViewById(R.id.submit);
    }

    @Override
    protected void initData() {
        adapter = new PageSizeAdapter(this, ParamUtils.getListPage());
        RecyclerUtils.layoutLinear(this, recyclerView);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for (int i = 0; i < adapter.list.size(); i++) {
                    if (adapter.list.get(i).isSelected) {
                        adapter.list.get(i).isSelected = false;
                    }
                }
                adapter.list.get(position).isSelected = true;
                adapter.notifyDataSetChanged();
            }
        });

        String pageSelected = SPUtils.getString(this, ParamUtils.PAGE_SIZE);
        if (pageSelected.isEmpty()) {
            for (int i = 0; i < adapter.list.size(); i++) {
                if (adapter.list.get(i).name.equals("A4")) {
                    adapter.list.get(i).isSelected = true;
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        } else {
            for (int i = 0; i < adapter.list.size(); i++) {
                if (adapter.list.get(i).name.equals(pageSelected)) {
                    adapter.list.get(i).isSelected = true;
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    protected void listener() {
        imgBack.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.submit:
                for (int i = 0; i < adapter.list.size(); i++) {
                    if (adapter.list.get(i).isSelected) {
                        SPUtils.saveString(this, ParamUtils.PAGE_SIZE, adapter.list.get(i).name);
                        break;
                    }
                }
                finish();
                break;
        }
    }
}
