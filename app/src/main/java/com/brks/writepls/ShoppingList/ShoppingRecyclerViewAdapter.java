package com.brks.writepls.ShoppingList;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brks.writepls.R;

import java.util.List;

public class ShoppingRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingRecyclerViewAdapter.MyViewHolder> {
    Context mContext;
    List<ShoppingElement> mData;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onStatusClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mListener = onItemClickListener;
    }

    public ShoppingRecyclerViewAdapter(Context mContext, List<ShoppingElement> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.shopping_list_item,viewGroup,false);
        final MyViewHolder viewHolder = new MyViewHolder(v,mListener);

        viewHolder.item_shopping_element.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(viewHolder.getAdapterPosition(),0,0,"Удалить");
            }
        });

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.toBuyText.setText(mData.get(position).getToBuy());

        if(!mData.get(position).isStatus() ){
            holder.statusBtn.setBackgroundResource(R.drawable.btn_rounded_grey);
            holder.statusBtn.setText("Готово");
            holder.toBuyText.setTextColor(Color.GRAY);

        }
        else {
            holder.statusBtn.setBackgroundResource(R.drawable.btn_rounded_light);
            holder.statusBtn.setText("Купить");
            holder.toBuyText.setTextColor(Color.BLACK);

        }

    }

    @Override
    public int getItemCount() { return mData.size(); }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView toBuyText;
        public Button statusBtn;
        public LinearLayout item_shopping_element;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            toBuyText = itemView.findViewById(R.id.shopping_buy_text);
            statusBtn = itemView.findViewById(R.id.statusBtn);
            item_shopping_element = itemView.findViewById(R.id.shopping_item);
            toBuyText.setTextColor(Color.BLACK);

            statusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onStatusClick(getAdapterPosition());
                }
            });

        }
    }
}