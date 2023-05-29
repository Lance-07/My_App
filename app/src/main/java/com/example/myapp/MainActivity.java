package com.example.myapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText inputName, inputUsername, inputEmail, inputPassword, confirmInputPassword;
    private Button signUpButton;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private TextView signInLink;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), AdminDashboard.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputName = findViewById(R.id.inputFullName);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        confirmInputPassword = findViewById(R.id.confirmInputPassword);
        signUpButton = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.progressBar);
        signInLink = findViewById(R.id.signInTextLink);

        auth = FirebaseAuth.getInstance();

        FirebaseApp firebaseApp = FirebaseApp.getApps(this).stream()
                .filter(app -> app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                .findFirst()
                .orElse(null);

        if (firebaseApp == null) {
            // FirebaseApp with name "DEFAULT" doesn't exist, initialize it
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setProjectId("my-app-79f2f")
                    .setApplicationId("1:226995870561:android:0210c3d19a42a025948320")
                    .setApiKey("AIzaSyCbe6Qh8frz0pI6kVCL2JLL9iUW24WYHZQ")
                    .setDatabaseUrl("https://my-app-79f2f-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .build();
            FirebaseApp.initializeApp(this /* Context */, options);

            // Enable Firebase Realtime Database persistence
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            FirebaseDatabase.getInstance().setPersistenceCacheSizeBytes(5 * 1024 * 1024);
        } else {
            Log.d(TAG, "FirebaseApp with name DEFAULT already exists");
        }


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString().trim();
                String username = inputUsername.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confirmPassword = confirmInputPassword.getText().toString().trim();

                if (!name.isEmpty() && !username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    addAccount(name, username, email, password);
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(MainActivity.this, "Password mismatch.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please fill all information.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });
    }

    private void addAccount(String name, String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Account creation", "createUserWithEmail:success");
                            Toast.makeText(MainActivity.this, "Account created.", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = auth.getCurrentUser();
                            navigateToSignInActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Account creation", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToSignInActivity() {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
    }
}