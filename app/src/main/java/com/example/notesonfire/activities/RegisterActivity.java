package com.example.notesonfire.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesonfire.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //ui
    private TextInputLayout textInputLayoutRegisterName;
    private TextInputLayout textInputLayoutRegisterEmail;
    private TextInputLayout textInputLayoutRegisterPassword;
    private TextInputLayout textInputLayoutRegisterConfirmPassword;
    private TextInputEditText textInputEditTextRegisterName;
    private TextInputEditText textInputEditTextRegisterEmail;
    private TextInputEditText textInputEditTextRegisterPassword;
    private TextInputEditText textInputEditTextRegisterConfirmPassword;
    private Button buttonSignIn;
    private TextView textViewSignIn;
    private Toolbar toolbar;

    //vars
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUi();
        initToolbar();
        setFirebase();
        setListeners();
        clearHelperTexts();
    }

    private void setFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void clearHelperTexts() {
        textInputLayoutRegisterName.setHelperText("");
        textInputLayoutRegisterEmail.setHelperText("");
        textInputLayoutRegisterPassword.setHelperText("");
        textInputLayoutRegisterConfirmPassword.setHelperText("");
    }

    private void setHelperTexts() {
        textInputLayoutRegisterName.setHelperText("Required");
        textInputLayoutRegisterEmail.setHelperText("Required");
        textInputLayoutRegisterPassword.setHelperText("Required");
        textInputLayoutRegisterConfirmPassword.setHelperText("Required");
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setListeners() {
        buttonSignIn.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    private void initUi() {
        textInputLayoutRegisterName = findViewById(R.id.textInputLayoutRegisterName);
        textInputLayoutRegisterEmail = findViewById(R.id.textInputLayoutRegisterEmail);
        textInputLayoutRegisterPassword = findViewById(R.id.textInputLayoutRegisterPassword);
        textInputLayoutRegisterConfirmPassword = findViewById(R.id.textInputLayoutRegisterConfirmPassword);
        textInputEditTextRegisterName = findViewById(R.id.textInputEditTextRegisterName);
        textInputEditTextRegisterEmail = findViewById(R.id.textInputEditTextRegisterEmail);
        textInputEditTextRegisterPassword = findViewById(R.id.textInputEditTextRegisterPassword);
        textInputEditTextRegisterConfirmPassword = findViewById(R.id.textInputEditTextRegisterConfirmPassword);
        buttonSignIn = findViewById(R.id.buttonRegister);
        textViewSignIn = findViewById(R.id.textViewSignIn);
        toolbar = findViewById(R.id.toolbarRegisterActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRegister:
                register();
                break;
            case R.id.textViewSignIn:
                goToLoginActivity();
                break;

        }
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void register() {
        String name = textInputEditTextRegisterName.getText().toString().trim();
        String email = textInputEditTextRegisterEmail.getText().toString().trim();
        String password = textInputEditTextRegisterPassword.getText().toString().trim();
        String confirmPassword = textInputEditTextRegisterConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            setHelperTexts();
            Toast.makeText(this, "Please, enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            textInputLayoutRegisterPassword.setHelperText("Password do not match");
            textInputLayoutRegisterConfirmPassword.setHelperText("Password do not match");
            textInputEditTextRegisterPassword.getText().clear();
            textInputEditTextRegisterConfirmPassword.getText().clear();
            textInputEditTextRegisterPassword.requestFocus();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        firebaseUser.linkWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getApplicationContext(), "Successfully connected!", Toast.LENGTH_SHORT).show();
                        clearHelperTexts();
                        startActivity(new Intent(getApplicationContext(), ListOfNotesActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),  e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}