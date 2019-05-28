package com.brks.writepls.Helper;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.brks.writepls.Note.NotesRecyclerViewAdapter;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListener itemTouchHelperListener;


    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs,RecyclerItemTouchHelperListener itemTouchHelperListener) {
        super(dragDirs, swipeDirs);

        this.itemTouchHelperListener = itemTouchHelperListener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if(itemTouchHelperListener != null){
            itemTouchHelperListener.onSwiped(viewHolder,i,viewHolder.getAdapterPosition());
        }

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        View v = ((NotesRecyclerViewAdapter.MyViewHolder)viewHolder).itemView;
        getDefaultUIUtil().clearView(v);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View v = ((NotesRecyclerViewAdapter.MyViewHolder)viewHolder).itemView;
        getDefaultUIUtil().onDraw(c,recyclerView,v,dX,dY,actionState,isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View v = ((NotesRecyclerViewAdapter.MyViewHolder)viewHolder).itemView;
        getDefaultUIUtil().onDrawOver(c,recyclerView,v,dX,dY,actionState,isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {

        if(viewHolder != null){
            View v = ((NotesRecyclerViewAdapter.MyViewHolder)viewHolder).itemView;
            getDefaultUIUtil().onSelected(v);
        }

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);


    }
}
