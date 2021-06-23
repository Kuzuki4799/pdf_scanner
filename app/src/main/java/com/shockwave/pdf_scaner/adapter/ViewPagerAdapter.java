package com.shockwave.pdf_scaner.adapter;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shockwave.pdf_scaner.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<BaseFragment> mFragmentList;

    public ViewPagerAdapter(FragmentManager manager, List<BaseFragment> fragmentList) {
        super(manager);
        this.mFragmentList = new ArrayList<>(fragmentList);
    }

    @Override
    public BaseFragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(BaseFragment fragment, String title) {
        mFragmentList.add(fragment);
    }
}
