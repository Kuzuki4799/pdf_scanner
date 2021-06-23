package com.shockwave.pdf_scaner.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseRecyclerAdapter;
import com.shockwave.pdf_scaner.callback.ItemMoveCallbackListener;
import com.shockwave.pdf_scaner.callback.OnStartDragListener;
import com.shockwave.pdf_scaner.model.PhotoDirectory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.Collections;
import java.util.List;

public class MiniSelectAdapter extends BaseRecyclerAdapter<PhotoDirectory.Media, MiniSelectAdapter.ViewHolder> implements ItemMoveCallbackListener.Listener {

    private OnStartDragListener startDragListener;

    private OnReset onReset;

    public MiniSelectAdapter(Context context, List<PhotoDirectory.Media> list, OnStartDragListener startDragListener, OnReset onReset) {
        super(context, list);
        this.startDragListener = startDragListener;
        this.onReset = onReset;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PhotoDirectory.Media item = getItembyPostion(position);
        holder.imgMain.setClipToOutline(true);
        Glide.with(mContext)
                .load(item.getPath(holder.imgMain.getContext())).transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform().dontAnimate()
                .override(100).into(holder.imgMain);
        holder.itemView.setOnLongClickListener(v -> {
            startDragListener.onStartDrag(holder);
            return false;
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_mini_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
//        if (fromPosition != list.size() - 1) {
//            if (!(fromPosition == list.size() - 2 && toPosition == list.size() - 1)) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(list, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(list, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            notifyItemChanged(fromPosition);
            notifyItemChanged(toPosition);
            onReset.onResetList();
//            }
//        }
    }

    @Override
    public void onRowSelected(ViewHolder itemViewHolder) {
    }

    @Override
    public void onRowClear(ViewHolder itemViewHolder) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView imgMain;
        private AppCompatImageView imgRemove;


        public ViewHolder(View root) {
            super(root);
            imgMain = root.findViewById(R.id.imgMain);
            imgRemove = root.findViewById(R.id.imgRemove);
            imgRemove.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnReset {
        void onResetList();
    }
}
