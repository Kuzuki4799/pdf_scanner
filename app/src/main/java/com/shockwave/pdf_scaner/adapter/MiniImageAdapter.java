package com.shockwave.pdf_scaner.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseRecyclerAdapter;
import com.shockwave.pdf_scaner.callback.ItemMoveCallbackListener;
import com.shockwave.pdf_scaner.callback.OnStartDragListener;
import com.shockwave.pdf_scaner.model.PhotoDirectory;
import com.shockwave.pdf_scaner.model.PhotoModel;

import java.util.Collections;
import java.util.List;

public class MiniImageAdapter extends BaseRecyclerAdapter<PhotoModel, MiniImageAdapter.ViewHolder> {

    public MiniImageAdapter(Context context, List<PhotoModel> list) {
        super(context, list);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PhotoModel item = getItembyPostion(position);
        holder.imgMain.setClipToOutline(true);
        Glide.with(mContext)
                .load(item.path).transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform().dontAnimate()
                .override(100).into(holder.imgMain);

        holder.viewSelected.setVisibility(item.isSelected ? View.VISIBLE : View.GONE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_mini_image, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView imgMain;
        private AppCompatImageView imgRemove;
        private View viewSelected;

        public ViewHolder(View root) {
            super(root);
            imgMain = root.findViewById(R.id.imgMain);
            imgRemove = root.findViewById(R.id.imgRemove);
            viewSelected = root.findViewById(R.id.viewSelected);
            imgMain.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
            imgRemove.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItem2ClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
