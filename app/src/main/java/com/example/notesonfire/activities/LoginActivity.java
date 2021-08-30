package com.example.notesonfire.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesonfire.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //ui
    private TextInputLayout textInputLayoutLoginEmail;
    private TextInputLayout textInputLayoutLoginPassword;
    private TextInputEditText textInputEditTextLoginEmail;
    private TextInputEditText textInputEditTextLoginPassword;
    private TextView textViewForgotPassword;
    private TextView textViewSignUp;
    private Button buttonLogin;
    private Toolbar toolbar;

    //vars
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUi();
        initToolbar();
        firebaseAuth = FirebaseAuth.getInstance();
        setListeners();
        clearHelperTexts();
    }

    private void clearHelperTexts() {
        textInputLayoutLoginEmail.setHelperText("");
        textInputLayoutLoginPassword.setHelperText("");
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setListeners() {
        buttonLogin.setOnClickListener(this);
        textViewForgotPassword.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
    }

    private void initUi() {
        textInputLayoutLoginEmail = findViewById(R.id.textInputLayoutLoginEmail);
        textInputLayoutLoginPassword = findViewById(R.id.textInputLayoutLoginPassword);
        textInputEditTextLoginEmail = findViewById(R.id.textInputEditTextLoginEmail);
        textInputEditTextLoginPassword = findViewById(R.id.textInputEditTextLoginPassword);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        buttonLogin = findViewById(R.id.buttonLogin);
        toolbar = findViewById(R.id.toolbarLoginActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                login();
                break;
            case R.id.textViewSignUp:
                goToRegisterActivity();
                break;
            case R.id.textViewForgotPassword:
                Toast.makeText(this, "Sorry, can't help you. \nNext time try to remember!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void goToRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void login() {
        String email = textInputEditTextLoginEmail.getText().toString();
        String password = textInputEditTextLoginPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            textInputLayoutLoginEmail.setHelperText("Required");
            textInputLayoutLoginPassword.setHelperText("Required");
            Toast.makeText(this, "Please, enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            clearHelperTexts();
            startActivity(new Intent(this, ListOfNotesActivity.class));
            Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

}