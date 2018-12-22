package com.dorm.muro.dormitory.presentation.todo;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

public class TodoItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private TodoItemTouchHelperListener listener;

    public TodoItemTouchHelper(int dragDirs, int swipeDirs, TodoItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View view = ((TodoAdapter.ViewHolder) viewHolder).foregroundView;
            getDefaultUIUtil().onSelected(view);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View view = ((TodoAdapter.ViewHolder) viewHolder).foregroundView;
        getDefaultUIUtil().onDrawOver(c, recyclerView, view, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final View view = ((TodoAdapter.ViewHolder) viewHolder).foregroundView;
        getDefaultUIUtil().clearView(view);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View view = ((TodoAdapter.ViewHolder) viewHolder).foregroundView;
        getDefaultUIUtil().onDraw(c, recyclerView, view, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        listener.onSwipe(viewHolder, i, viewHolder.getAdapterPosition());
    }

    public interface TodoItemTouchHelperListener {
        void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
