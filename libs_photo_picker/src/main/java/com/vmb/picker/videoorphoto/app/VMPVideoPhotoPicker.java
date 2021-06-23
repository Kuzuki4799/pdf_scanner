package com.vmb.picker.videoorphoto.app;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.vmb.picker.videoorphoto.config.PictureMimeType;
import com.vmb.picker.videoorphoto.config.PictureSelectionConfig;
import com.vmb.picker.videoorphoto.entity.LocalMedia;
import com.vmb.picker.videoorphoto.entity.LocalMediaFolder;
import com.vmb.picker.videoorphoto.listener.OnResultCallbackListener;
import com.vmb.picker.videoorphoto.model.LocalMediaLoader;
import com.vmb.picker.videoorphoto.thread.PictureThreadUtils;
import com.vmb.picker.videoorphoto.tools.PictureFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VMPVideoPhotoPicker {

    public static void getListVideoOrPhoto(Context context, PictureSelectionConfig config, OnResultCallbackListener<LocalMedia> onResultCallbackListener) {
        PictureThreadUtils.executeByCached(new PictureThreadUtils.SimpleTask<List<LocalMediaFolder>>() {

            @Override
            public List<LocalMediaFolder> doInBackground() {
                return new LocalMediaLoader(context, config).loadAllMedia();
            }

            @Override
            public void onSuccess(List<LocalMediaFolder> folders) {
                List<LocalMedia> images = new ArrayList<>();
                List<LocalMediaFolder> foldersList;
                int oldCurrentListSize = 0;
                PictureThreadUtils.cancel(PictureThreadUtils.getCachedPool());
                if (folders != null) {
                    if (folders.size() > 0) {
                        foldersList = folders;
                        LocalMediaFolder folder = folders.get(0);
                        if (folder != null) {
                            folder.setChecked(true);
                            List<LocalMedia> result = folder.getData();
                            if (images == null) {
                                images = new ArrayList<>();
                            }
                            int currentSize = images.size();
                            int resultSize = result.size();
                            oldCurrentListSize = oldCurrentListSize + currentSize;
                            if (resultSize >= currentSize) {
                                if (currentSize > 0 && currentSize < resultSize && oldCurrentListSize != resultSize) {
                                    images.addAll(result);
                                    LocalMedia media = images.get(0);
                                    folder.setFirstImagePath(media.getPath());
                                    folder.getData().add(0, media);
                                    folder.setCheckedNum(1);
                                    folder.setImageNum(folder.getImageNum() + 1);
                                    updateMediaFolder(context, foldersList, media, config);
                                } else {
                                    images = result;
                                }
                            }
                        }
                    }
                    if (onResultCallbackListener != null) onResultCallbackListener.onResult(images);
                } else {
                    if (onResultCallbackListener != null)
                        onResultCallbackListener.onResult(new ArrayList<>());
                }
            }
        });
    }


    private static void updateMediaFolder(Context context, List<LocalMediaFolder> imageFolders, LocalMedia media, PictureSelectionConfig config) {
        File imageFile = new File(PictureMimeType.isContent(media.getPath())
                ? Objects.requireNonNull(PictureFileUtils.getPath(context, Uri.parse(media.getPath()))) : media.getPath());
        File folderFile = imageFile.getParentFile();
        int size = imageFolders.size();
        for (int i = 0; i < size; i++) {
            LocalMediaFolder folder = imageFolders.get(i);
            String name = folder.getName();
            if (TextUtils.isEmpty(name)) {
                continue;
            }
            if (name.equals(folderFile.getName())) {
                folder.setFirstImagePath(config.cameraPath);
                folder.setImageNum(folder.getImageNum() + 1);
                folder.setCheckedNum(1);
                folder.getData().add(0, media);
                break;
            }
        }
    }
}
