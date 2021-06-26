package com.shockwave.pdf_scaner.ui.setting;

import android.content.Context;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.model.PageSize;

public class PageSizeManager {
    public static String getTitle(Context context, PageSize pageSize) {
        int i = 0;
        switch (pageSize.ordinal() + 1) {
            case 1:
                i = R.string.a3;
                break;
            case 2:
                i = R.string.a4;
                break;
            case 3:
                i = R.string.a5;
                break;
            case 4:
                i = R.string.b4;
                break;
            case 5:
                i = R.string.b5;
                break;
            case 6:
                i = R.string.letter;
                break;
            case 7:
                i = R.string.legal;
                break;
            case 8:
                i = R.string.executive;
                break;
            case 9:
                i = R.string.business_card;
                break;
        }
        return context.getString(i);
    }
}
