package com.shockwave.pdf_scaner.ui.setting;

import androidx.recyclerview.widget.RecyclerView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.model.PageSize;
import com.shockwave.pdf_scaner.util.RecyclerUtils;

import java.util.Arrays;

public class PageSizeActivity extends BaseActivity {

    private RecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_page_size;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        initPageSizeAdapter();
    }

    private void initPageSizeAdapter() {
        PageSizeAdapter adapter = new PageSizeAdapter(this, Arrays.asList(PageSize.values()));
        RecyclerUtils.layoutLinear(this, recyclerView);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void listener() {

    }
}
