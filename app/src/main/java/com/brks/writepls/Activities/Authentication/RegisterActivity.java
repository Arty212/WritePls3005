package com.brks.writepls.Activities.Authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brks.writepls.Activities.MainActivity;
import com.brks.writepls.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{


    private FirebaseDatabase database;
    private DatabaseReference positionRef;
    private DatabaseReference listRef;
    private DatabaseReference remRef;
    private EditText ETemail;
    private EditText ETpassword;
    private Button backBtn;
    private Button regBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backBtn = findViewById(R.id.btn_back);
        regBtn = findViewById(R.id.registration);
        ETemail = findViewById(R.id.email);
        ETpassword = findViewById(R.id.password);

        backBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        setTitle("Регистрация");
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        } else {

        }
    }

    @Override
    public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_back:
                    Intent intent = new Intent(getApplicationContext(),AutActivity.class);
                    startActivity(intent);
                    break;
                case R.id.registration:
                    if(!ETemail.getText().toString().equals("") && !ETpassword.getText().toString().equals("")) {
                    registration(ETemail.getText().toString(), ETpassword.getText().toString());
            } else Toast.makeText(getApplicationContext(),"Проверьте данные",Toast.LENGTH_SHORT).show();
                    break;
            }

    }
    public void registration(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            database = FirebaseDatabase.getInstance();
                            positionRef = database.getReference().child(mAuth.getCurrentUser().getUid()).child("namePosition");
                            remRef = database.getReference().child(mAuth.getCurrentUser().getUid()).child("remPosition");
                            listRef = database.getReference().child(mAuth.getCurrentUser().getUid()).child("list");
                            positionRef.setValue(1);
                            listRef.setValue(" ");
                            remRef.setValue(1);

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Авторизация не удалась." +
                                            "\n Пароль должен содержать не менее 6 символов.",
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
