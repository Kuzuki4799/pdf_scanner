package com.shockwave.pdf_scaner.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.util.FileUtils;
import com.shockwave.pdf_scaner.util.ParamUtils;
import com.shockwave.pdf_scaner.widget.DisableSwipeViewPager;
import com.watermark.androidwm.WatermarkBuilder;
import com.watermark.androidwm.bean.WatermarkText;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.BitmapUtil;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.ViewType;

import static com.shockwave.pdf_scaner.util.FileUtils.outMediaFilePDF;

public class ListImagePagerAdapter extends PagerAdapter {

    private final ArrayList<String> listImage;

    public ListImagePagerAdapter(ArrayList<String> listImage) {
        this.listImage = listImage;
    }

    @Override
    public int getCount() {
        return listImage.size();
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_list_image, container, false);
        PhotoEditorView imgMain = view.findViewById(R.id.imgMain);
        Glide.with(container.getContext()).load(listImage.get(position)).into(imgMain.getSource());
        getDropboxIMGSize(imgMain, listImage.get(position));
        view.setTag("BasePaperAdapter" + position);
        container.addView(view);
        return view;
    }

    private void getDropboxIMGSize(View view, String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        ((ConstraintLayout.LayoutParams) view.getLayoutParams()).dimensionRatio = "H," + imageWidth + ":" + imageHeight;
    }

    public void setImage(View view, String path) {
        PhotoEditorView imgMain = view.findViewById(R.id.imgMain);
        Glide.with(view.getContext()).load(path).into(imgMain.getSource());
        getDropboxIMGSize(imgMain, path);
    }

    public void setImage(View view, String path, OnLast onLast) {
        PhotoEditorView imgMain = view.findViewById(R.id.imgMain);
        Glide.with(view.getContext()).load(path).into(imgMain.getSource());
        getDropboxIMGSize(imgMain, path);
        onLast.onLastListener();
    }

    public interface OnLast {
        void onLastListener();
    }

    public void addSticker(View view, String path) {
        PhotoEditorView photoEditorView = view.findViewById(R.id.imgMain);
        PhotoEditor mPhotoEditor = new PhotoEditor.Builder(view.getContext(), photoEditorView)
                .setPinchTextScalable(true)
                .build();
        mPhotoEditor.addImage(path, new PhotoEditor.OnGetViewRoot() {
            @Override
            public void onGetView(View view) {

            }

            @Override
            public void onMoveEditorEventListener(View view, ViewType viewType) {

            }
        });
    }

    @SuppressLint("MissingPermission")
    public void getImage(View view, PhotoEditor.OnSaveListener callback) {
        SaveSettings saveSettings = new SaveSettings.Builder().setClearViewsEnabled(false).setTransparencyEnabled(true).build();
        PhotoEditorView photoEditorView = view.findViewById(R.id.imgMain);
        PhotoEditor mPhotoEditor = new PhotoEditor.Builder(view.getContext(), photoEditorView).setPinchTextScalable(true).build();
        try {
            File createFile = FileUtils.createOfferMoreFile(view.getContext(), false, ParamUtils.jpgExtension);
            mPhotoEditor.clearHelperBox();
            mPhotoEditor.saveAsFile(createFile.getPath(), saveSettings, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public ArrayList<File> saveImage(DisableSwipeViewPager viewPager) {
        FileOutputStream out = null;
        ArrayList<File> listPath = new ArrayList<>();
        try {
            SaveSettings saveSettings = new SaveSettings.Builder().setClearViewsEnabled(false).setTransparencyEnabled(true).build();
            for (int i = 0; i < listImage.size(); i++) {
                PhotoEditorView photoEditorView = viewPager.getChildAt(i).findViewById(R.id.imgMain);
                PhotoEditor mPhotoEditor = new PhotoEditor.Builder(viewPager.getContext(), photoEditorView).setPinchTextScalable(true).build();
                File createFile = FileUtils.createOfferMoreFile(viewPager.getContext(), false, ParamUtils.jpgExtension);
                mPhotoEditor.clearHelperBox();
                String path = createFile.getPath();
                File file = new File(path);
                try {
                    out = new FileOutputStream(file, false);
                    photoEditorView.setDrawingCacheEnabled(true);
                    Bitmap drawingCache = saveSettings.isTransparencyEnabled()
                            ? BitmapUtil.removeTransparency(photoEditorView.getDrawingCache())
                            : photoEditorView.getDrawingCache();
                    drawingCache.compress(saveSettings.getCompressFormat(), saveSettings.getCompressQuality(), out);
                    if (drawingCache.getByteCount() > 0) {
                        listPath.add(file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return listPath;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileStream(out);
        }
        return null;
    }

    public Boolean convertPDF(ArrayList<File> files) {
        Document document = new Document(PageSize.A4, 38.0f, 38.0f, 50.0f, 38.0f);
        try {
            File outputMediaFile = outMediaFilePDF();
            System.out.println("KKKKKKKKKK" + outputMediaFile);
            PdfWriter.getInstance(document, new FileOutputStream(outputMediaFile));
            document.open();
            for (int i = 0; i < files.size(); i++) {
                Image image = Image.getInstance(files.get(i).getAbsolutePath());
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - 0) / image.getWidth()) * 100;
                image.scalePercent(scaler);
                image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                image.setAbsolutePosition((document.getPageSize().getWidth() - image.getScaledWidth()) / 2.0f,
                        (document.getPageSize().getHeight() - image.getScaledHeight()) / 2.0f);
                document.add(image);
                document.newPage();
            }
            return true;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        return false;
    }

    private void closeFileStream(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWatermark(View view, String path, String content, int color, int alpha, int size) {
        PhotoEditorView imgMain = view.findViewById(R.id.imgMain);

        WatermarkText watermarkText = new WatermarkText(content)
                .setPositionX(0.5)
                .setPositionY(0.5)
                .setTextColor(color)
                .setTextAlpha(alpha)
                .setRotation(30)
                .setTextSize(size);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        WatermarkBuilder
                .create(view.getContext(), bitmap)
                .setTileMode(true)
                .loadWatermarkText(watermarkText)
                .getWatermark()
                .setToImageView(imgMain.getSource());

        getDropboxIMGSize(imgMain, path);
    }

    public String getImageWatermark(View view) {
        PhotoEditorView imgMain = view.findViewById(R.id.imgMain);
        try {
            File file = FileUtils.createOfferMoreFile(view.getContext(), false, ParamUtils.pngExtension);
            FileOutputStream out = new FileOutputStream(file.getPath());
            BitmapDrawable drawable = (BitmapDrawable) imgMain.getSource().getDrawable();
            drawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
            return file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setFilter(View view, PhotoFilter photoFilter) {
        PhotoEditorView imgMain = view.findViewById(R.id.imgMain);
        PhotoEditor mPhotoEditor = new PhotoEditor.Builder(view.getContext(), imgMain).setPinchTextScalable(true).build();
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }
}
