package com.shockwave.pdf_scaner.base;

import android.content.Context;

import com.orhanobut.dialogplus.DialogPlus;


public class BaseDialog {

    public DialogPlus dialogPlus;
    public Context context;

    public BaseDialog(Context context) {
        this.context = context;
    }

    public void show() {
        if (dialogPlus != null) {
            dialogPlus.show();
        }
    }

    public void dismiss() {
        if (dialogPlus != null) {
            dialogPlus.dismiss();
        }
    }

}
