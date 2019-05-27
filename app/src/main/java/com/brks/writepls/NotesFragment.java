package com.brks.writepls;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.text.DateFormat.getDateInstance;

public class NotesFragment extends Fragment {

    FloatingActionButton addBtn;

    private RecyclerView notesRecyclerView;
    private List<Note> lstNote = new ArrayList<>();
    int namePosition = 1;            // номер новой заметки при ее создании
    NotesRecyclerViewAdapter recyclerAdapter;

    private FirebaseDatabase database;
    private DatabaseReference positionRef;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private ChildEventListener childEventListener;

    Dialog mDialog;
    String editedText;
    String editedName;

    EditText textNote;
    EditText titleNote;
    Button saveBtn;


    public NotesFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notes, container, false);

        FirebaseInit();

        notesRecyclerView = v.findViewById(R.id.notes_recyclerView);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new NotesRecyclerViewAdapter(getContext(), lstNote);
        notesRecyclerView.setAdapter(recyclerAdapter);

        recyclerAdapter.setOnItemClickListener(new NotesRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onStatusClick(int position) {
                if (lstNote.get(position).isVisible()) {

                    changeNote(position, false);
                    lstNote.get(position).setVisible(false);

                } else {
                    changeNote(position, true);
                    lstNote.get(position).setVisible(true);

                }
            }
        });


        addBtn = v.findViewById(R.id.addBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
                System.out.println(123);


            }
        });


        lstNote.clear();

        return v;
    }

    private void FirebaseInit() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child(mAuth.getCurrentUser().getUid()).child("notes");
        positionRef = database.getReference().child(mAuth.getCurrentUser().getUid()).child("namePosition");

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                lstNote.add(dataSnapshot.getValue(Note.class));
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Note note = dataSnapshot.getValue(Note.class);
                int index = getItemIndex(note);
                lstNote.set(index, note);
                recyclerAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Note note = dataSnapshot.getValue(Note.class);
                int index = getItemIndex(note);
                lstNote.remove(index);
                recyclerAdapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        myRef.addChildEventListener(childEventListener);

        readPositionFromDatabase();
    }

    @Override
    public void onStop() {
        super.onStop();
        myRef.removeEventListener(childEventListener);
        childEventListener = null;
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                removeNote(item.getGroupId());
                break;
            case 1:

                int position = item.getGroupId();

                mDialog = new Dialog(getContext());
                mDialog.setContentView(R.layout.dialog_edit_note);
                saveBtn = mDialog.findViewById(R.id.save_btn);
                textNote = mDialog.findViewById(R.id.main_text_note);
                titleNote = mDialog.findViewById(R.id.title_note);

                titleNote.setText(lstNote.get(position).getName());
                textNote.setText(lstNote.get(position).getText());


                mDialog.show();
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editedName = titleNote.getText().toString();
                        editedText = textNote.getText().toString();
                        mDialog.cancel();
                        changeNote(item.getGroupId());
                    }
                });

                break;

        }

        return super.onContextItemSelected(item);
    }


    private int getItemIndex(Note note) {

        int index = -1;
        for (int i = 0; i < lstNote.size(); i++) {
            if (lstNote.get(i).getKey().equals(note.getKey())) {
                index = i;
                break;
            }
        }

        return index;
    }

    private void removeNote(int position) {
        myRef.child(lstNote.get(position).getKey()).removeValue();

    }

    private void addNote() {
        String id = myRef.child(mAuth.getCurrentUser().getUid()).push().getKey();
        Note newNote = new Note("Новая заметка " + namePosition,
                getDateInstance().format(System.currentTimeMillis()), "Текст заметки", id, true);


        Map<String, Object> noteValue = newNote.toMap();

        Map<String, Object> note = new HashMap<>();
        note.put(id, noteValue);

        myRef.updateChildren(note);
        namePosition++;
        writePositionToDatabase();
    }

    private void changeNote(int position) {
        Note note = lstNote.get(position);

        note.setText(editedText);
        note.setName(editedName);

        Map<String, Object> noteValue = note.toMap();

        Map<String, Object> newNote = new HashMap<>();

        newNote.put(note.getKey(), noteValue);

        myRef.updateChildren(newNote);

    }

    private void changeNote(int position, boolean visible) {
        Note note = lstNote.get(position);

        note.setVisible(visible);

        Map<String, Object> noteValue = note.toMap();

        Map<String, Object> newNote = new HashMap<>();

        newNote.put(note.getKey(), noteValue);

        myRef.updateChildren(newNote);

    }

    private void writePositionToDatabase() {
        positionRef.setValue(namePosition);
    }

    private void readPositionFromDatabase() {
        // Read from the database
        positionRef.addValueEventListener(new ValueEventListener() {
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

}