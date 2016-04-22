package com.haosu.schedulebook.callbacks;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.haosu.schedulebook.listeners.OnMoveAndSwipedListener;
import com.haosu.schedulebook.model.ScheduleItem;

/**
 * Created by haosu on 2016/4/22.
 */
public class ScheduleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private OnMoveAndSwipedListener adapter;

    public ScheduleItemTouchHelperCallback(OnMoveAndSwipedListener listener){
        this.adapter = listener;
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final int dragFlags = 0;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
        return 0;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
