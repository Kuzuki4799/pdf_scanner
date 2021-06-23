package com.shockwave.pdf_scaner.adapter;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseActivity;
import com.shockwave.pdf_scaner.util.FileUtils;
import com.shockwave.pdf_scaner.util.ParamUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import me.pqpo.smartcropperlib.view.CropImageView;

public class CropPagerAdapter extends PagerAdapter {

    private final ArrayList<String> listImage;
    private final BaseActivity baseActivity;

    public CropPagerAdapter(BaseActivity baseActivity, ArrayList<String> listImage) {
        this.listImage = listImage;
        this.baseActivity = baseActivity;
    }

    @Override
    public int getCount() {
        return listImage.size();
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_crop_image, container, false);
        CropImageView imgMain = view.findViewById(R.id.imgMain);
        Glide.with(container.getContext()).asBitmap().load(listImage.get(position))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        imgMain.setImageToCrop(resource);
                        if (position == listImage.size() - 1) baseActivity.dismissProgressDialog();
                    }

                    @Override
                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                    }
                });
        view.setTag("BasePaperAdapter" + position);
        container.addView(view);
        return view;
    }

    public String getCropImage(View view) {
        try {
            File file = FileUtils.createOfferMoreFile(view.getContext(), false, ParamUtils.jpgExtension);
            CropImageView imgMain = view.findViewById(R.id.imgMain);
            saveImage(imgMain.crop(), file);
            return file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveImage(Bitmap bitmap, File saveFile) {
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNoCut(View view, boolean isNoCut, String path) {
        CropImageView imgMain = view.findViewById(R.id.imgMain);
        imgMain.setAutoScanEnable(!isNoCut);
        Glide.with(baseActivity).asBitmap().load(path).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                imgMain.setImageToCrop(resource);
                baseActivity.dismissProgressDialog();
            }

            @Override
            public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {
            }
        });
    }

    public void setRotate(View view, boolean isRight) {
        CropImageView imgMain = view.findViewById(R.id.imgMain);
        Bitmap bitmapImg = imgMain.getBitmap();
        Matrix matrix = new Matrix();
        if (isRight) {
            matrix.postRotate(90);
        } else {
            matrix.postRotate(-90);
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapImg, 0, 0, bitmapImg.getWidth(), bitmapImg.getHeight(), matrix, true);
        imgMain.setImageToCrop(rotatedBitmap);

    }

    public void setImage(BaseActivity baseActivity, View view, String path) {
        CropImageView imgMain = view.findViewById(R.id.imgMain);
        Glide.with(baseActivity).asBitmap().load(path).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                imgMain.setImageToCrop(resource);
                baseActivity.dismissProgressDialog();
            }

            @Override
            public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {
            }
        });
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }
}
