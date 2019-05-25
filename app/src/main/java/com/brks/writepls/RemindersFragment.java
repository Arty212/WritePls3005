package com.brks.writepls;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.text.DateFormat.getDateInstance;

public class RemindersFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference remRef;
    DatabaseReference posRef;

    FirebaseAuth mAuth;

    FloatingActionButton addBtn;
    Dialog newReminderDialog;
    Button cancelBtn;
    Button doneBtn;
    EditText mainText;
    TimePicker timePicker;



    private RecyclerView reminderRecyclerView;
    private List<Reminder> lstReminder = new ArrayList<>();
    ReminderRecyclerViewAdapter recyclerAdapter;

    private AlarmManager alarmManager;

    public int namePosition;

    PendingIntent pendingIntent;

    Calendar calendar;
    //a
//

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reminders, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        posRef = database.getReference().child(mAuth.getCurrentUser().getUid()).child("remPosition");
        remRef = database.getReference().child(mAuth.getCurrentUser().getUid()).child("remList");

        readPositionFromDatabase();
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

         calendar= Calendar.getInstance();


        recyclerAdapter = new ReminderRecyclerViewAdapter(getContext(), lstReminder);
        reminderRecyclerView = v.findViewById(R.id.reminders_recyclerView);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reminderRecyclerView.setAdapter(recyclerAdapter);

        newReminderDialog = new Dialog(getContext());
        newReminderDialog.setContentView(R.layout.dialog_new_reminder);
        mainText = newReminderDialog.findViewById(R.id.text_new_reminder);
        timePicker = newReminderDialog.findViewById(R.id.timePicker);
        cancelBtn = newReminderDialog.findViewById(R.id.cancelBtn);
        doneBtn = newReminderDialog.findViewById(R.id.doneBtn);




        recyclerAdapter.setOnItemClickListener(new ReminderRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onStatusClick(int position) {
                boolean flag = !lstReminder.get(position).isFlag();
                changeReminder(position, flag);
            }
        });

        timePicker.setIs24HourView(true);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newReminderDialog.cancel(); // Отмена
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (!mainText.getText().toString().equals("")) {
                    addReminder();

                    AlarmReceiver.textNot(mainText.getText().toString());


                    createNotification(timePicker.getHour(),timePicker.getMinute());

                    newReminderDialog.cancel();
                    mainText.setText("");
                }

            }
        });

        addBtn = v.findViewById(R.id.addBtnReminder);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newReminderDialog.show();

            }
        });

        lstReminder.clear();
        updateList();
        return v;
    }

    private void updateList() {
        remRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                lstReminder.add(dataSnapshot.getValue(Reminder.class));
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Reminder reminder = dataSnapshot.getValue(Reminder.class);
                int index = getItemIndex(reminder);
                lstReminder.set(index, reminder);
                recyclerAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Reminder reminder = dataSnapshot.getValue(Reminder.class);
                int index = getItemIndex(reminder);
                lstReminder.remove(index);
                recyclerAdapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 0:
                removeReminder(item.getGroupId());

        }

        return super.onContextItemSelected(item);
    }

    private int getItemIndex(Reminder reminder) {

        int index = -1;
        for (int i = 0; i < lstReminder.size(); i++) {
            if (lstReminder.get(i).getKey().equals(reminder.getKey())) {
                index = i;
                break;
            }
        }

        return index;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addReminder() {
        String id = remRef.child(mAuth.getCurrentUser().getUid()).push().getKey();
        Reminder newReminder = new Reminder(timePicker.getHour(), timePicker.getMinute(), true, mainText.getText().toString(), id);


        Map<String, Object> noteValue = newReminder.toMap();

        Map<String, Object> reminder = new HashMap<>();
        reminder.put(id, noteValue);

        remRef.updateChildren(reminder);
        // namePosition++;
        writePositionToDatabase();
    }

    private void removeReminder(int position){
        remRef.child(lstReminder.get(position).getKey()).removeValue();

    }

    private void writePositionToDatabase() {
        posRef.setValue(namePosition);
    }

    private void readPositionFromDatabase() {
        // Read from the database
        posRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(Integer.class);
                namePosition = value;
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void changeReminder(int position, boolean status) {
        Reminder reminder = lstReminder.get(position);

        reminder.setFlag(status);

        Map<String, Object> remValue = reminder.toMap();

        Map<String, Object> newReminder = new HashMap<>();

        newReminder.put(reminder.getKey(), remValue);

        remRef.updateChildren(newReminder);

    }

    public void createNotification(int hour,int minute) {

        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(getActivity(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);



    }
}