package com.vmb.picker.videoorphoto.listener;

import android.content.Context;

import com.vmb.picker.videoorphoto.config.PictureSelectionConfig;


/**
 * @author：luck
 * @date：2020/4/27 3:24 PM
 * @describe：OnCustomCameraInterfaceListener
 */
public interface OnCustomCameraInterfaceListener {
    /**
     * Camera Menu
     *
     * @param context
     * @param config
     * @param type
     */
    void onCameraClick(Context context, PictureSelectionConfig config, int type);
}
