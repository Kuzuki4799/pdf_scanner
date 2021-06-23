package com.shockwave.pdf_scaner.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    public Context mContext;
    public List<T> list = new ArrayList<>();
    public final LayoutInflater layoutInflater;

    public OnLongClickListener onLongClickListener;
    public OnItemClickListener onItemClickListener;
    public OnItemClickListener onItem2ClickListener;
    public OnClickItemAdapterListener<T> onClickItemAdapterListener;
    public OnClickItemListListener<T> onClickItemListListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public void setOnClickItemAdapterListener(OnClickItemAdapterListener<T> onClickItemAdapterListener) {
        this.onClickItemAdapterListener = onClickItemAdapterListener;
    }

    public void setOnItem2ClickListener(OnItemClickListener onItem2ClickListener) {
        this.onItem2ClickListener = onItem2ClickListener;
    }

    public void setOnClickItemListListener(OnClickItemListListener<T> onClickItemListListener) {
        this.onClickItemListListener = onClickItemListListener;
    }

    public BaseRecyclerAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(int position, T item) {
        list.set(position, item);
        this.notifyItemChanged(position);
    }

    public void setData(int position, T item, boolean animation) {
        list.set(position, item);
        if (!animation) {
            this.notifyDataSetChanged();
        } else {
            this.notifyItemChanged(position);
        }
    }

    public void setData(T item) {
        this.notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        list.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, list.size());
        this.notifyDataSetChanged();
    }

    public void add(int pos, T item) {
        list.add(pos, item);
        notifyItemInserted(pos);
    }

    public void add(T item) {
        list.add(item);
        notifyItemInserted(list.size() - 1);
    }

    public void addAll(List<T> listItems) {
        list.addAll(listItems);
        notifyDataSetChanged();
    }

    public void setAll(List<T> listItems) {
        list = listItems;
        notifyDataSetChanged();
    }


    public T getItembyPostion(int position) {
        return list.get(position);
    }

    public abstract void onBindViewHolder(VH holder, final int position);

    @Override
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnClickItemAdapterListener<T> {
        void onItemClick(int pos, T item);

    }

    public interface OnClickItemListListener<T> {
        void onItemClick(int pos, List<T> list);
    }

    public interface OnLongClickListener {
        void onItemLongClick(int pos);
    }

}