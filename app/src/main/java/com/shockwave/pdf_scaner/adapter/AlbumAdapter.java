package com.shockwave.pdf_scaner.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseRecyclerAdapter;
import com.shockwave.pdf_scaner.model.PhotoDirectory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class AlbumAdapter extends BaseRecyclerAdapter<PhotoDirectory, AlbumAdapter.ViewHolder> {

    public AlbumAdapter(Context context, List<PhotoDirectory> list) {
        super(context, list);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PhotoDirectory item = getItembyPostion(position);
        holder.txtTitle.setText(item.name);
        holder.image.setClipToOutline(true);
        Glide.with(mContext)
                .load(item.getPath(holder.image)).transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform().dontAnimate()
                .override(100).into(holder.image);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView image;
        private TextView txtTitle;

        public ViewHolder(View root) {
            super(root);

            image = root.findViewById(R.id.image);
            txtTitle = root.findViewById(R.id.txtTitle);
            root.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
