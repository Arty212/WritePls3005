package com.brks.writepls.ToDoList;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brks.writepls.R;

import java.util.List;

public class ToDoRecyclerViewAdapter extends RecyclerView.Adapter<ToDoRecyclerViewAdapter.MyViewHolder>{

    Context mContext;
    List<ToDo> mData;

    public ToDoRecyclerViewAdapter(Context mContext, List<ToDo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.todo_list_item,viewGroup,false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        viewHolder.item_toDo.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(viewHolder.getAdapterPosition(),0,0,"Добавить в список");
                menu.add(viewHolder.getAdapterPosition(),1,0,"Удалить");
            }
        });

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.mainText.setText(mData.get(position).getTitle());
    }

    @Override
    public int getItemCount() { return mData.size(); }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView mainText;
        private LinearLayout item_toDo;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_toDo = itemView.findViewById(R.id.to_do_item);
            mainText = itemView.findViewById(R.id.textToDo);

        }
    }
}

