package com.shockwave.pdf_scaner.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.adapter.ViewPagerAdapter;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.base.BaseFragment;
import com.shockwave.pdf_scaner.ui.fragment.MainFragment;
import com.shockwave.pdf_scaner.ui.fragment.SettingFragment;
import com.shockwave.pdf_scaner.widget.DisableSwipeViewPager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private DisableSwipeViewPager viewPager;
    private FloatingActionButton fabCamera;
    private BottomNavigationView bottomView;

    private ArrayList<BaseFragment> listFragments = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        viewPager = findViewById(R.id.view_pager);
        fabCamera = findViewById(R.id.fabCamera);
        bottomView = findViewById(R.id.bottomView);
    }

    @Override
    protected void initData() {
        checkPermissionStorage();
    }

    @Override
    protected void listener() {
        bottomView.setOnNavigationItemSelectedListener(this);
        fabCamera.setOnClickListener(v -> checkPermissionCamera());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.navigation_file) {
            viewPager.setCurrentItem(0, false);
            fabCamera.setVisibility(View.VISIBLE);
        } else {
            viewPager.setCurrentItem(1, false);
            fabCamera.setVisibility(View.GONE);
        }
        return true;
    }

    private void handlerViewPager() {
        listFragments.add(new MainFragment());
        listFragments.add(new SettingFragment());
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), listFragments);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(listFragments.size());
        viewPager.setPagingEnabled(false);
    }

    private void checkPermissionStorage() {
        if (!AndPermission.hasPermissions(this, Permission.Group.STORAGE)) {
            AndPermission.with(this)
                    .runtime()
                    .permission(Permission.Group.STORAGE)
                    .onGranted(data -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (!Environment.isExternalStorageManager()) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, 1999);
                            } else handlerViewPager();
                        } else handlerViewPager();
                    })
                    .onDenied(data -> finish())
                    .start();
        } else {
            handlerViewPager();
        }
    }

    private void checkPermissionCamera() {
        if (!AndPermission.hasPermissions(this, Permission.Group.CAMERA)) {
            AndPermission.with(this)
                    .runtime()
                    .permission(Permission.Group.CAMERA)
                    .onGranted(data -> startActivity(new Intent(MainActivity.this, CameraActivity.class)))
                    .start();
        } else {
            startActivity(new Intent(MainActivity.this, CameraActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1999) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    handlerViewPager();
                }
            }
        }
    }
}