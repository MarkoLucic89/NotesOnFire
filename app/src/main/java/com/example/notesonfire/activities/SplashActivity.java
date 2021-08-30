package com.example.notesonfire.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.notesonfire.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth = FirebaseAuth.getInstance();
        goToListOfNotesActivity();
    }

    private void goToListOfNotesActivity() {
        new Handler().postDelayed(() -> {
            if (firebaseAuth.getCurrentUser() != null) {
                finish();
                startActivity(new Intent(this, ListOfNotesActivity.class));
            } else {
                firebaseAuth.signInAnonymously().addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Logged in With Temporary Account", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(this, ListOfNotesActivity.class));
                }).addOnFailureListener(e -> {
                    finish();
                    Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
                });
            }
        }, 2000);
    }
}