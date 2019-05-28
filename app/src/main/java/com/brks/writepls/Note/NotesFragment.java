package com.brks.writepls.Note;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.brks.writepls.Activities.MainActivity;
import com.brks.writepls.Activities.NoteActivity;
import com.brks.writepls.Helper.RecyclerItemTouchHelper;
import com.brks.writepls.Helper.RecyclerItemTouchHelperListener;
import com.brks.writepls.R;
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

import static android.app.Activity.RESULT_OK;
import static java.text.DateFormat.getDateInstance;

public class NotesFragment extends Fragment implements RecyclerItemTouchHelperListener {

    private FloatingActionButton addBtn;

    private RecyclerView notesRecyclerView;
    private List<Note> lstNote = new ArrayList<>();
    private int namePosition = 1;            // номер новой заметки при ее создании
    private NotesRecyclerViewAdapter recyclerAdapter;

    private FirebaseDatabase database;
    private DatabaseReference positionRef;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private ChildEventListener childEventListener;

    private Dialog mDialog;
    private String editedText;
    private String editedName;

    private EditText textNote;
    private EditText titleNote;
    private Button saveBtn;

    private FrameLayout frameLayout;

    private boolean flag = true;


    public NotesFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notes, container, false);


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child(mAuth.getCurrentUser().getUid()).child("notes");
        positionRef = database.getReference().child(mAuth.getCurrentUser().getUid()).child("namePosition");


        FirebaseInit();
        // readPositionFromDatabase();
        frameLayout = v.findViewById(R.id.frame);


        notesRecyclerView = v.findViewById(R.id.notes_recyclerView);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        notesRecyclerView.setItemAnimator(new DefaultItemAnimator());
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

            @Override
            public void onItemClick(int position) {

                flag = false;

                Intent intent = new Intent(getActivity(), NoteActivity.class);
                intent.putExtra("Title", lstNote.get(position).getName());
                intent.putExtra("Text", lstNote.get(position).getText());
                intent.putExtra("id", getItemIndex(lstNote.get(position)));
                startActivityForResult(intent, 1);
            }
        });


        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(notesRecyclerView);


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
        if(flag){
        myRef.removeEventListener(childEventListener);
        childEventListener = null;}
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                removeNote(item.getGroupId());
                break;

            case 1:
                int position = item.getGroupId();
                showDialog(position);
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

        Snackbar snackbar = Snackbar.make(frameLayout, editedName + " обновлен", Snackbar.LENGTH_SHORT);
        snackbar.show();

    }

    private void changeNote(int position, boolean visible) {
        Note note = lstNote.get(position);

        note.setVisible(visible);

        Map<String, Object> noteValue = note.toMap();

        Map<String, Object> newNote = new HashMap<>();

        newNote.put(note.getKey(), noteValue);

        myRef.updateChildren(newNote);

    }

    private void changeNote(int position, String title, String text) {
        Note note = lstNote.get(position);

        note.setText(text);
        note.setName(title);

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


    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof NotesRecyclerViewAdapter.MyViewHolder) {


            String title = lstNote.get(viewHolder.getAdapterPosition()).getName();

            if (title == null) {
                title = "Безымянная заметка";
            }

            int i = viewHolder.getAdapterPosition();

            removeNote(i);

            Snackbar snackbar = Snackbar.make(frameLayout, title + " удален", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void showDialog(final int position) {
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
                changeNote(position);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == RESULT_OK) {
            int id = data.getIntExtra("id",-1);


            String title = data.getStringExtra("Title");
            String text = data.getStringExtra("Text");

            changeNote(id,title,text);

            Snackbar snackbar = Snackbar.make(frameLayout, title + " обновлен", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar.make(frameLayout, "Изменения не удались", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

        flag = true;
    }


/*    @Override
    public void onStart() {
        super.onStart();
        if(childEventListener == null){
            lstNote.clear();
            FirebaseInit();
        }
    }*/
}