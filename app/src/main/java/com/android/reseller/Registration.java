package com.android.reseller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.reseller.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    private EditText emailText;
    private EditText passwordText;
    private TextView nameText;
    private Button registerButton;
    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerButton = findViewById(R.id.registration);
        nameText = findViewById((R.id.registrationNameText));
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);


        database = FirebaseDatabase.getInstance();
        fbAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fbAuth.getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

            }
        };

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // createAccount function defined below
                createAccount(view);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        fbAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(authListener != null) {
            fbAuth.removeAuthStateListener(authListener);
        }
    }

    private void notifyUser(String message) {
        Toast.makeText(Registration.this, message, Toast.LENGTH_SHORT).show();
    }

    //Create an account
    public void createAccount(View view) {
        final String name = nameText.getText().toString();
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();

        if (name.length() == 0) {
            nameText.setError("Enter a name");
            return;
        }

        if (email.length() == 0) {
            emailText.setError("Enter an email address");
            return;
        }

        if (password.length() < 7) {
            passwordText.setError("Password must be at least 7 characters");
            return;
        }

        fbAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            notifyUser("Account creation failed");
                        }
                        else {
                            // account successfully created

                            // create User with given info (excluding password)
                            User dbUser = new User(name, email);

                            // get firebase uid of current logged in user
                            String uid = task.getResult().getUser().getUid();

                            dbUser.setUid(uid);

                            // add user to RealtimeDatabase
                            database.getReference("users").child(uid).setValue(dbUser);

                            // go back to home activity
                            Intent intent = new Intent();
                            intent.putExtra("registrationStatus", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });

    }

}
