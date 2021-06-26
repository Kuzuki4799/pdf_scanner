package com.shockwave.pdf_scaner.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatTextView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseFragment;
import com.shockwave.pdf_scaner.ui.setting.PageSizeActivity;
import com.shockwave.pdf_scaner.util.ParamUtils;

public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private Spinner spPageSize;
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
        spPageSize = view.findViewById(R.id.spPageSize);
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
    }

    @Override
    protected void listener() {
        txtRate.setOnClickListener(this);
        txtMore.setOnClickListener(this);
        txtShare.setOnClickListener(this);
        txtFeedback.setOnClickListener(this);
        txtPolicy.setOnClickListener(this);
        getView().findViewById(R.id.rl_page_size).setOnClickListener(this);
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