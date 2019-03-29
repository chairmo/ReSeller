package com.android.reseller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Login Page
public class Authentication extends AppCompatActivity {

    private EditText emailText;
    private EditText passwordText;
    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);

        fbAuth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    // if already signed in, send them directly to their profile.
                    notifyUser("Already Signed In");
                    Intent intent = new Intent(Authentication.this, UserProfile.class);
                    startActivity(intent);

                }
            }
        };

        // automatically log in when user presses "done" on keyboard after typing password
        passwordText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // simulate click on "Sign In" button
                    findViewById(R.id.sign_in).performClick();
                    return true;
                }
                return false;
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
        Toast.makeText(Authentication.this, message, Toast.LENGTH_SHORT).show();
    }

    //Sign In
    public void signIn(View view) {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if(fbAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, NavigationDrawerActivity.class);
            startActivity(intent);
        }

        if (email.length() == 0) {
            emailText.setError("Enter an email address");
            return;
        }

        if (password.length() < 7) {
            passwordText.setError("Password must be at least 7 characters");
            return;
        }

        fbAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            notifyUser("Incorrect email or password");
                        }
                        else {
                            // successful login, go to Welcome activity
                            notifyUser("Login Successful");
                            Intent intent = new Intent();
                            intent.putExtra("loginStatus", true);
                            setResult(RESULT_OK, intent);
                            finish();
//                            Intent intent = new Intent(Authentication.this, Welcome.class);
//                            startActivity(intent);
                        }
                    }
                });
        if(fbAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, NavigationDrawerActivity.class);
            startActivity(intent);
        }

    }

    //reset password
    public void resetPassword(View view){
        String email = emailText.getText().toString();

        if (email.length() == 0) {
            emailText.setError("Enter an email address");
            return;
        }

        fbAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            notifyUser("Reset email sent");
                        }
                    }
                });
    }


}
