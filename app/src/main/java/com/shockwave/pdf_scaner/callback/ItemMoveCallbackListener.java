package com.shockwave.pdf_scaner.callback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.shockwave.pdf_scaner.adapter.MiniSelectAdapter;

public class ItemMoveCallbackListener extends ItemTouchHelper.Callback {
    private MiniSelectAdapter miniSelectAdapter;

    public ItemMoveCallbackListener(MiniSelectAdapter miniSelectAdapter) {
        this.miniSelectAdapter = miniSelectAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        miniSelectAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            miniSelectAdapter.onRowSelected((MiniSelectAdapter.ViewHolder) viewHolder);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        miniSelectAdapter.onRowClear((MiniSelectAdapter.ViewHolder) viewHolder);
    }

    public interface Listener {
        void onRowMoved(int fromPosition, int toPosition);

        void onRowSelected(MiniSelectAdapter.ViewHolder itemViewHolder);

        void onRowClear(MiniSelectAdapter.ViewHolder itemViewHolder);
    }
}
