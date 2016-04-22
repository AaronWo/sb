package com.haosu.schedulebook.listeners;

/**
 * Created by haosu on 2016/4/22.
 */
public interface OnMoveAndSwipedListener {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
