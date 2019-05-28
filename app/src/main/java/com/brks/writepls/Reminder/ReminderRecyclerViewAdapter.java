package com.brks.writepls.Reminder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.brks.writepls.R;

import java.util.List;

public class ReminderRecyclerViewAdapter extends RecyclerView.Adapter<ReminderRecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<Reminder> mData;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onStatusClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mListener = onItemClickListener;
    }

    public ReminderRecyclerViewAdapter(Context mContext, List<Reminder> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.reminders_list_item,viewGroup,false);
        final MyViewHolder viewHolder = new MyViewHolder(v,mListener);

        viewHolder.item_reminder.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(viewHolder.getAdapterPosition(),0,0,"Удалить");
              //  menu.add(viewHolder.getAdapterPosition(),1,0,"Изменить");
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if( mData.get(position).getMinute() > 10) {
            if(mData.get(position).getHour() > 10){
                holder.time.setText(mData.get(position).getHour() + ":" + mData.get(position).getMinute());
            }
            else{
                holder.time.setText("0"+ mData.get(position).getHour() + ":" + mData.get(position).getMinute());
            }
        }else{
            if(mData.get(position).getHour() > 10){
                holder.time.setText(mData.get(position).getHour() + ":0" + mData.get(position).getMinute());
            }
            else{
                holder.time.setText("0"+ mData.get(position).getHour() + ":0" + mData.get(position).getMinute());
            }
        }


        holder.aSwitch.setChecked(mData.get(position).isFlag());
        holder.text.setText(mData.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView time;
        private Switch aSwitch;
        private TextView text;
        private LinearLayout item_reminder;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            item_reminder = itemView.findViewById(R.id.reminder_item);
            time = itemView.findViewById(R.id.time_reminder);
            aSwitch = itemView.findViewById(R.id.switch_reminder);
            text = itemView.findViewById(R.id.text_reminder);

            aSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onStatusClick(getAdapterPosition());
                }
            });
        }
    }

}