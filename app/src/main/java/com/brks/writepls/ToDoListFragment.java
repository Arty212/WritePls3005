package com.brks.writepls;


import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.text.DateFormat.getDateInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ToDoListFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference myRef1;
    private DatabaseReference listRef;

    private FirebaseAuth mAuth;

    private RecyclerView toDoRecyclerView;
    private List<ToDo> lstToDo = new ArrayList<>();
    private List<ToDo> lstText = new ArrayList<>();
    Compare compare = new Compare();
    private ChildEventListener childEventListener;

    TextView textList;
    Button clearBtn;

    ToDoRecyclerViewAdapter toDoRecyclerViewAdapter;
    Button addBtn;

    Dialog mDialog;

    EditText editImportance;
    EditText editName;
    Button ok;
    Button cancel;

    public ToDoListFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_todo_list, container, false);

        FirebaseInit();

        toDoRecyclerView = v.findViewById(R.id.todo_recyclerView);
        toDoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        toDoRecyclerViewAdapter = new ToDoRecyclerViewAdapter(getContext(), lstToDo);
        toDoRecyclerView.setAdapter(toDoRecyclerViewAdapter);


        textList = v.findViewById(R.id.textList);
        clearBtn = v.findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTextList();
            }
        });

        addBtn = v.findViewById(R.id.addToDo);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDo();
            }
        });
        readListFromDatabase();

        lstToDo.clear();
        return v;
    }

    private void FirebaseInit() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef1 = database.getReference().child(mAuth.getCurrentUser().getUid()).child("toDoList");
        listRef = database.getReference().child(mAuth.getCurrentUser().getUid()).child("list");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                lstToDo.add(dataSnapshot.getValue(ToDo.class));
                toDoRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ToDo toDo = dataSnapshot.getValue(ToDo.class);
                int index = getItemIndex(toDo);
                lstToDo.set(index, toDo);
                toDoRecyclerViewAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                ToDo toDo = dataSnapshot.getValue(ToDo.class);
                int index = getItemIndex(toDo);
                lstToDo.remove(index);
                toDoRecyclerViewAdapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef1.addChildEventListener(childEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        myRef1.removeEventListener(childEventListener);
        childEventListener = null;
    }

    private int getItemIndex(ToDo toDo) {

        int index = -1;
        for (int i = 0; i < lstToDo.size(); i++) {
            if (lstToDo.get(i).getKey().equals(toDo.getKey())) {
                index = i;
                break;
            }
        }

        return index;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onContextItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                addToList(item.getGroupId());
                sortList(lstText);
                updateTextList(lstText);

                break;
            case 1:
                removeToDo(item.getGroupId());
                break;

        }

        return super.onContextItemSelected(item);
    }

    public void addToDo() {
        mDialog = new Dialog(getContext());
        mDialog.setContentView(R.layout.dialog_new_todo);
        editImportance = mDialog.findViewById(R.id.editImp);
        editName = mDialog.findViewById(R.id.editName);
        ok = mDialog.findViewById(R.id.okBtn);
        cancel = mDialog.findViewById(R.id.cancelBtn1);

        mDialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!editName.getText().toString().equals("")) {
                    if (Integer.valueOf(editImportance.getText().toString()) > 0 && Integer.valueOf(editImportance.getText().toString()) < 6) {

                        String id = myRef1.child(mAuth.getCurrentUser().getUid()).push().getKey();

                        ToDo newToDo = new ToDo(editName.getText().toString(), Integer.valueOf(editImportance.getText().toString()), id);

                        Map<String, Object> toDoValue = newToDo.toMap();

                        Map<String, Object> toDo = new HashMap<>();
                        toDo.put(id, toDoValue);

                        myRef1.updateChildren(toDo);

                    } else Toast.makeText(getContext(), "Важность 1-5", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Проверьте название дела", Toast.LENGTH_SHORT).show();

                mDialog.cancel();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });


    }

    private void removeToDo(int position) {
        myRef1.child(lstToDo.get(position).getKey()).removeValue();
    }

    private void addToList(int position) {
        lstText.add(lstToDo.get(position));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortList(List<ToDo> list) {
        Collections.sort(list, compare);
    }

    private void updateTextList(List<ToDo> list) {
        textList.setText(" ");
        String text;
        for (int i = 0; i < list.size(); i++) {
            int num = i + 1;
            text = list.get(i).getTitle();
            if (textList.getText() == " ")
                textList.setText(num + ". " + text + "\n");
            else
                textList.setText(textList.getText() + "\n" + num + ". " + text + "\n");
        }
        writeListToDatabase();

    }

    private void clearTextList() {
        textList.setText(" ");
        lstText.clear();
        writeListToDatabase();
    }

    private void writeListToDatabase() {
        listRef.setValue(textList.getText());
    }

    private void readListFromDatabase() {
        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                textList.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

}
