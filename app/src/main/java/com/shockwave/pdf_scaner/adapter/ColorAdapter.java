package com.shockwave.pdf_scaner.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circleview.CircleView;
import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.base.BaseRecyclerAdapter;
import com.shockwave.pdf_scaner.model.ColorModel;

import java.util.List;

public class ColorAdapter extends BaseRecyclerAdapter<ColorModel, ColorAdapter.ViewHolder> {

    public ColorAdapter(Context context, List<ColorModel> list) {
        super(context, list);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ColorModel colorModel = getItembyPostion(position);
        holder.viewColor.setCircleColor(colorModel.color);
        if (colorModel.isSelected) {
            holder.rlImage.setBackgroundResource(R.drawable.bg_border_circle);
        } else {
            holder.rlImage.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_color, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rlImage;
        private CircleView viewColor;

        public ViewHolder(View root) {
            super(root);

            rlImage = root.findViewById(R.id.rlImage);
            viewColor = root.findViewById(R.id.viewColor);
            root.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
