package com.shockwave.pdf_scaner.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseFragment;
import com.shockwave.pdf_scaner.ui.PageSizeActivity;
import com.shockwave.pdf_scaner.util.ParamUtils;
import com.shockwave.pdf_scaner.util.SPUtils;

public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout rlPageSize;
    private AppCompatTextView txtPageSize;
    private AppCompatTextView txtLocation;
    private AppCompatTextView txtRate;
    private AppCompatTextView txtMore;
    private AppCompatTextView txtShare;
    private AppCompatTextView txtFeedback;
    private AppCompatTextView txtPolicy;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView(View view) {
        rlPageSize = view.findViewById(R.id.rl_page_size);
        txtPageSize = view.findViewById(R.id.txtPageSize);
        txtLocation = view.findViewById(R.id.txtLocation);
        txtRate = view.findViewById(R.id.txtRate);
        txtMore = view.findViewById(R.id.txtMore);
        txtShare = view.findViewById(R.id.txtShare);
        txtFeedback = view.findViewById(R.id.txtFeedback);
        txtPolicy = view.findViewById(R.id.txtPolicy);
    }

    @Override
    protected void initData() {
        txtLocation.setText(ParamUtils.PDF_FOLDER);
        getSizePage();
    }

    private void getSizePage() {
        String pageSelected = SPUtils.getString(getActivity(), ParamUtils.PAGE_SIZE);
        if (pageSelected.isEmpty()) {
            txtPageSize.setText("A4");
        } else {
            txtPageSize.setText(pageSelected);
        }
    }

    @Override
    protected void listener() {
        txtRate.setOnClickListener(this);
        txtMore.setOnClickListener(this);
        txtShare.setOnClickListener(this);
        txtFeedback.setOnClickListener(this);
        txtPolicy.setOnClickListener(this);
        rlPageSize.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getSizePage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtRate:
                break;
            case R.id.txtMore:
                break;
            case R.id.txtShare:
                break;
            case R.id.txtFeedback:
                break;
            case R.id.txtPolicy:
                break;
            case R.id.rl_page_size:
                startActivity(new Intent(requireActivity(), PageSizeActivity.class));
                break;
        }
    }
}