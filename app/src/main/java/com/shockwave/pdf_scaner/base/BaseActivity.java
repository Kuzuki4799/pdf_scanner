package com.shockwave.pdf_scaner.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String BUNDLE_KEY = "com.shockwave.key.BUNDLE_KEY";
    private static final int BASE_RESULT_CODE = 9001;
    private boolean isActivityActive = true;

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void listener();

    private ProgressDialog progressdialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initData();
        listener();
    }

    public void showProgressDialog() {
        progressdialog = new ProgressDialog(this);
        progressdialog.setMessage("Please Wait....");
        progressdialog.show();
    }

    public void dismissProgressDialog() {
        progressdialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityActive = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void openNewActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    public void openNewActivityAndClearStack(Class c) {
        Intent intent = new Intent(this, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void openNewActivityAndClearStack(Bundle bundle, Class c) {
        Intent intent = new Intent(this, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(BUNDLE_KEY, bundle);
        startActivity(intent);
        finish();
    }

    public void openNewActivity(Bundle bundle, Class c) {
        Intent intent = new Intent(this, c);
        intent.putExtra(BUNDLE_KEY, bundle);
        startActivity(intent);
    }

    public void openNewActivity(Intent intent, Bundle bundle) {
        intent.putExtra(BUNDLE_KEY, bundle);
        startActivity(intent);
    }

    public void openNewActivityForResult(Bundle bundle, Class c) {
        try {
            Intent intent = new Intent(this, c);
            intent.putExtra(BUNDLE_KEY, bundle);
            startActivityForResult(intent, BASE_RESULT_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openNewActivityForResult(Bundle bundle, Class c, int requestcode) {
        Intent intent = new Intent(this, c);
        intent.putExtra(BUNDLE_KEY, bundle);
        startActivityForResult(intent, requestcode);
    }

    public Bundle getIntentBundle() {
        try {
            return getIntent().getBundleExtra(BUNDLE_KEY);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isActivityActive() {
        return isActivityActive;
    }
}
