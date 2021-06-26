package com.shockwave.pdf_scaner.ui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.adapter.AlbumAdapter;
import com.shockwave.pdf_scaner.base.BaseRecyclerAdapter;
import com.shockwave.pdf_scaner.model.PageSize;
import com.shockwave.pdf_scaner.model.PhotoDirectory;
import com.shockwave.pdf_scaner.util.FormatUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PageSizeAdapter extends BaseRecyclerAdapter<PageSize, PageSizeAdapter.ViewHolder> {

    public PageSizeAdapter(Context context, List<PageSize> list) {
        super(context, list);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PageSize item = getItembyPostion(position);
        holder.title.setText(PageSizeManager.getTitle(mContext, item));
        holder.size.setText(FormatUtils.formatPageSize(item.width * 0.1f, 1)
                + " x " + FormatUtils.formatPageSize(item.height * 0.1f, 1)
                + ' ' + mContext.getString(R.string.cm));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_page_size, parent, false);
        return new PageSizeAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView checkbox;
        private AppCompatTextView title, size;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.iv_selected_state);
            title = itemView.findViewById(R.id.tv_size);
            size = itemView.findViewById(R.id.tv_size_wh);
        }
    }
}
