package com.brks.writepls.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brks.writepls.R;

public class NoteActivity extends AppCompatActivity {


    private EditText textNote;
    private EditText titleNote;
    private Button saveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        saveBtn = findViewById(R.id.save_bt1n);

        titleNote = findViewById(R.id.title_n1ote);
        textNote = findViewById(R.id.main_text_n1ote);

        setTitle("Изменение заметки");
        setTitleColor(R.color.white);

        setTheme(R.style.AppThemeBar);

        Intent intent = getIntent();
        titleNote.setText(intent.getStringExtra("Title"));
        textNote.setText(intent.getStringExtra("Text"));


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChangedNote();
            }
        });




    }

    private void saveChangedNote() {
        String title = titleNote.getText().toString();
        String text = textNote.getText().toString();

        if (title.trim().isEmpty() || text.trim().isEmpty()) {
            Toast.makeText(this, "Введите текст и название заметки", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
       int id = getIntent().getIntExtra("id",-1);

        data.putExtra("id",id);
        data.putExtra("Title",title);
        data.putExtra("Text",text);

        setResult(RESULT_OK, data);
        finish();
    }
}
