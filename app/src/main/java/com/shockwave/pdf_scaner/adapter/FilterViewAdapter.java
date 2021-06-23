package com.shockwave.pdf_scaner.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.model.FilterModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoFilter;

/**
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.2
 * @since 5/23/2018
 */
public class FilterViewAdapter extends RecyclerView.Adapter<FilterViewAdapter.ViewHolder> {

    private FilterListener mFilterListener;
    private ArrayList<FilterModel> list = new ArrayList<>();

    public FilterViewAdapter(FilterListener filterListener) {
        mFilterListener = filterListener;
        setupFilters();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilterModel filterPair = list.get(position);
        Bitmap fromAsset = getBitmapFromAsset(holder.itemView.getContext(), filterPair.name);
        holder.mImageFilterView.setClipToOutline(true);
        holder.mImageFilterView.setImageBitmap(fromAsset);
        holder.mTxtFilterName.setText(filterPair.photoFilter.name().replace("_", " "));
        if (filterPair.isSelected) {
            holder.mTxtFilterName.setBackgroundResource(R.drawable.bg_border_text_primary);
            holder.viewSelected.setVisibility(View.VISIBLE);
        } else {
            holder.mTxtFilterName.setBackgroundResource(R.drawable.bg_border_text_gray);
            holder.viewSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView mImageFilterView;
        AppCompatTextView mTxtFilterName;
        private View viewSelected;

        ViewHolder(View itemView) {
            super(itemView);
            viewSelected = itemView.findViewById(R.id.viewSelected);
            mImageFilterView = itemView.findViewById(R.id.imgFilterView);
            mTxtFilterName = itemView.findViewById(R.id.txtFilterName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).isSelected = false;
                    }
                    list.get(getLayoutPosition()).isSelected = true;
                    notifyDataSetChanged();
                    mFilterListener.onFilterSelected(list.get(getLayoutPosition()).photoFilter);
                }
            });
        }
    }

    private Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream istr = assetManager.open(strName);
            return BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setupFilters() {
        list.add(new FilterModel("filters/original.jpg", PhotoFilter.NONE, true));
        list.add(new FilterModel("filters/auto_fix.png", PhotoFilter.AUTO_FIX));
        list.add(new FilterModel("filters/brightness.png", PhotoFilter.BRIGHTNESS));
        list.add(new FilterModel("filters/contrast.png", PhotoFilter.CONTRAST));
        list.add(new FilterModel("filters/documentary.png", PhotoFilter.DOCUMENTARY));
        list.add(new FilterModel("filters/dual_tone.png", PhotoFilter.DUE_TONE));
        list.add(new FilterModel("filters/fill_light.png", PhotoFilter.FILL_LIGHT));
        list.add(new FilterModel("filters/fish_eye.png", PhotoFilter.FISH_EYE));
        list.add(new FilterModel("filters/grain.png", PhotoFilter.GRAIN));
        list.add(new FilterModel("filters/gray_scale.png", PhotoFilter.GRAY_SCALE));
        list.add(new FilterModel("filters/lomish.png", PhotoFilter.LOMISH));
        list.add(new FilterModel("filters/negative.png", PhotoFilter.NEGATIVE));
        list.add(new FilterModel("filters/posterize.png", PhotoFilter.POSTERIZE));
        list.add(new FilterModel("filters/saturate.png", PhotoFilter.SATURATE));
        list.add(new FilterModel("filters/sepia.png", PhotoFilter.SEPIA));
        list.add(new FilterModel("filters/sharpen.png", PhotoFilter.SHARPEN));
        list.add(new FilterModel("filters/temprature.png", PhotoFilter.TEMPERATURE));
        list.add(new FilterModel("filters/tint.png", PhotoFilter.TINT));
        list.add(new FilterModel("filters/vignette.png", PhotoFilter.VIGNETTE));
        list.add(new FilterModel("filters/cross_process.png", PhotoFilter.CROSS_PROCESS));
        list.add(new FilterModel("filters/b_n_w.png", PhotoFilter.BLACK_WHITE));
//        list.add(new FilterModel("filters/flip_horizental.png", PhotoFilter.FLIP_HORIZONTAL));
//        list.add(new FilterModel("filters/flip_vertical.png", PhotoFilter.FLIP_VERTICAL));
//        list.add(new FilterModel("filters/rotate.png", PhotoFilter.ROTATE));
    }

    public interface FilterListener {
        void onFilterSelected(PhotoFilter photoFilter);
    }
}
