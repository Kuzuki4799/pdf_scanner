package com.shockwave.pdf_scaner.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseRecyclerAdapter;
import com.shockwave.pdf_scaner.model.PageModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PageSizeAdapter extends BaseRecyclerAdapter<PageModel, PageSizeAdapter.ViewHolder> {

    public PageSizeAdapter(Context context, ArrayList<PageModel> list) {
        super(context, list);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PageModel item = getItembyPostion(position);
        holder.title.setText(item.name);
        holder.size.setText(item.detail);

        if (item.isSelected) {
            holder.checkbox.setImageResource(R.drawable.ic_pdf_size_selected);
        } else {
            holder.checkbox.setImageResource(R.drawable.ic_pdf_size_unselected);
        }
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

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
