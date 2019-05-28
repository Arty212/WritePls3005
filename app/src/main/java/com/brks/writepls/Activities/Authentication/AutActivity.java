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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AutActivity extends AppCompatActivity implements View.OnClickListener{


    private FirebaseAuth mAuth;
    private EditText ETemail;
    private EditText ETpassword;
    private Button autBtn;
    private Button regBtn;
    private final String TAG = " hello";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auten);

        FirebaseApp.initializeApp(getApplicationContext());

        ETemail = findViewById(R.id.et_email);
        ETpassword = findViewById(R.id.et_password);
        autBtn = findViewById(R.id.btn_sign_in);
        regBtn = findViewById(R.id.btn_registration);
        autBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
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
                case R.id.btn_sign_in:
                    if(!ETemail.getText().toString().equals("") && !ETpassword.getText().toString().equals("")) {
                    signIn(ETemail.getText().toString(), ETpassword.getText().toString());
                    }
                    else Toast.makeText(getApplicationContext(),"Проверьте данные",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_registration:
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                    break;
            }
        }


    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(AutActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }


}



