package com.shockwave.pdf_scaner.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseRecyclerAdapter;
import com.shockwave.pdf_scaner.model.PhotoDirectory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class SelectAdapter extends BaseRecyclerAdapter<PhotoDirectory.Media, SelectAdapter.ViewHolder> {

    public SelectAdapter(Context context, List<PhotoDirectory.Media> list) {
        super(context, list);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PhotoDirectory.Media item = getItembyPostion(position);
        if (item.isSelected) {
            holder.txtSelected.setText(String.valueOf(item.pos));
            holder.txtSelected.setBackgroundResource(R.drawable.item_border);
            holder.viewSelected.setVisibility(View.VISIBLE);
        } else {
            holder.txtSelected.setText("");
            holder.viewSelected.setVisibility(View.GONE);
            holder.txtSelected.setBackgroundResource(R.drawable.item_border_stroke);
        }
        Glide.with(mContext)
                .load(item.getPath(holder.imgMain.getContext())).transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform().dontAnimate()
                .override(100).into(holder.imgMain);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView imgMain;
        private AppCompatTextView txtSelected;
        private View viewSelected;

        public ViewHolder(View root) {
            super(root);
            imgMain = root.findViewById(R.id.imgMain);
            txtSelected = root.findViewById(R.id.txtSelected);
            viewSelected = root.findViewById(R.id.view_selected);
            root.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
