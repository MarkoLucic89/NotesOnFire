package com.example.notesonfire.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesonfire.R;
import com.example.notesonfire.dialogs.AddWebUrlDialog;
import com.example.notesonfire.models.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener, AddWebUrlDialog.WebUrlListener {

    private static final int REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION = 1;
    private static final String TAG = "NoteActivity";
    //ui
    private View viewIndicator;
    private TextView textViewDateTime;
    private EditText editTextNoteTitle;
    private EditText editTextNoteText;
    private TextView textViewWebUrl;
    private ImageView imageViewDeleteWebUrl;
    private ImageView imageViewNoteImage;
    private ImageView imageViewDeleteNoteImage;

    //bottom sheet ui
    private LinearLayout layoutBottomSheetBehavior;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private ImageView imageViewMenu;
    private ImageView imageViewColor1;
    private ImageView imageViewColor2;
    private ImageView imageViewColor3;
    private ImageView imageViewColor4;
    private ImageView imageViewColor5;
    private LinearLayout layoutAddImage;
    private LinearLayout layoutAddWebUrl;
    private LinearLayout linearLayoutWebUrl;

    //note vars
    private String noteDocumentId;
    private String noteTitle;
    private String noteText;
    private String noteDateTime;
    private String noteWebUrl;
    private String noteImagePath;
    private Uri imageUri;

    //firebase
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DocumentReference usersReference;
    private StorageReference uploadsReference;
    private CollectionReference notesReference;
    private FirebaseUser firebaseUser;

    //colors
    private int noteColor1;
    private int noteColor2;
    private int noteColor3;
    private int noteColor4;
    private int noteColor5;
    private int selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        initUi();
        setToolbar();
        initFirestore();
        initColors();
        setDateAndTime();
        bottomSheetBehavior();
        setListeners();
        setNoteFromIntent();
    }

    private void initFirestore() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        usersReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());
        uploadsReference = FirebaseStorage.getInstance().getReference("uploads");
        notesReference = usersReference.collection("notes");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_note_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.itemSaveNote:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarNoteActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    private void setDateAndTime() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", Locale.getDefault());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
        noteDateTime = simpleDateFormat.format(new Date());
        textViewDateTime.setText(noteDateTime);
    }

    private void initColors() {
        noteColor1 = getResources().getColor(R.color.colorDefaultNoteColor);
        noteColor2 = getResources().getColor(R.color.colorNoteColor2);
        noteColor3 = getResources().getColor(R.color.colorNoteColor3);
        noteColor4 = getResources().getColor(R.color.colorNoteColor4);
        noteColor5 = getResources().getColor(R.color.colorNoteColor5);
        selectedColor = noteColor1;
    }

    private void setNoteFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(ListOfNotesActivity.NOTE_FROM_INTENT)
                && intent.hasExtra(ListOfNotesActivity.NOTE_ID)) {
            Note noteFromIntent = (Note) intent.getSerializableExtra(ListOfNotesActivity.NOTE_FROM_INTENT);
            noteDocumentId = intent.getStringExtra(ListOfNotesActivity.NOTE_ID);
            editTextNoteTitle.setText(noteFromIntent.getNoteTitle());
            editTextNoteText.setText(noteFromIntent.getNoteText());
            viewIndicator.setBackgroundColor(noteFromIntent.getNoteColor());
            selectedColor = noteFromIntent.getNoteColor();
            if (noteFromIntent.getNoteWebUrl().isEmpty()) {
                linearLayoutWebUrl.setVisibility(View.GONE);
            } else {
                textViewWebUrl.setText(noteFromIntent.getNoteWebUrl());
                linearLayoutWebUrl.setVisibility(View.VISIBLE);
            }
            noteImagePath = noteFromIntent.getNoteImagePath();
            if (noteImagePath == null) {
                imageViewNoteImage.setVisibility(View.GONE);
                imageViewDeleteNoteImage.setVisibility(View.GONE);
            } else {
                Picasso.get().load(noteImagePath).into(imageViewNoteImage);
                imageViewNoteImage.setVisibility(View.VISIBLE);
                imageViewDeleteNoteImage.setVisibility(View.VISIBLE);
            }
        }
    }


    private void setListeners() {
        imageViewMenu.setOnClickListener(this);
        imageViewColor1.setOnClickListener(this);
        imageViewColor2.setOnClickListener(this);
        imageViewColor3.setOnClickListener(this);
        imageViewColor4.setOnClickListener(this);
        imageViewColor5.setOnClickListener(this);
        layoutAddImage.setOnClickListener(this);
        layoutAddWebUrl.setOnClickListener(this);
        imageViewDeleteWebUrl.setOnClickListener(this);
        imageViewDeleteNoteImage.setOnClickListener(this);
    }

    private void initUi() {
        layoutBottomSheetBehavior = findViewById(R.id.layoutBottomSheetBehavior);
        editTextNoteTitle = findViewById(R.id.editTextNoteTitle);
        editTextNoteText = findViewById(R.id.editTextNoteText);
        viewIndicator = findViewById(R.id.viewIndicator);
        textViewDateTime = findViewById(R.id.textViewDateTime);
        textViewWebUrl = findViewById(R.id.textViewWebUrl);
        imageViewDeleteWebUrl = findViewById(R.id.imageViewDeleteWebUrl);
        imageViewNoteImage = findViewById(R.id.imageViewNoteImage);
        imageViewDeleteNoteImage = findViewById(R.id.imageViewDeleteNoteImage);
        linearLayoutWebUrl = findViewById(R.id.linearLayoutWebUrl);
        imageViewMenu = layoutBottomSheetBehavior.findViewById(R.id.imageViewMenu);
        imageViewColor1 = layoutBottomSheetBehavior.findViewById(R.id.imageViewColor1);
        imageViewColor2 = layoutBottomSheetBehavior.findViewById(R.id.imageViewColor2);
        imageViewColor3 = layoutBottomSheetBehavior.findViewById(R.id.imageViewColor3);
        imageViewColor4 = layoutBottomSheetBehavior.findViewById(R.id.imageViewColor4);
        imageViewColor5 = layoutBottomSheetBehavior.findViewById(R.id.imageViewColor5);
        layoutAddImage = layoutBottomSheetBehavior.findViewById(R.id.layoutAddImage);
        layoutAddWebUrl = layoutBottomSheetBehavior.findViewById(R.id.layoutAddWebUrl);
    }

    private void bottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheetBehavior);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewMenu:
                handleBottomSheet();
                break;
            case R.id.imageViewColor1:
                setNoteColor(noteColor1);
                break;
            case R.id.imageViewColor2:
                setNoteColor(noteColor2);
                break;
            case R.id.imageViewColor3:
                setNoteColor(noteColor3);
                break;
            case R.id.imageViewColor4:
                setNoteColor(noteColor4);
                break;
            case R.id.imageViewColor5:
                setNoteColor(noteColor5);
                break;
            case R.id.layoutAddImage:
                addImage();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.layoutAddWebUrl:
                addWebUrl();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.imageViewDeleteWebUrl:
                deleteWebUrl();
                break;
            case R.id.imageViewDeleteNoteImage:
                deleteNoteImage();
                break;
        }
    }

    private void handleBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void deleteNoteImage() {
        imageViewNoteImage.setVisibility(View.GONE);
        imageViewDeleteNoteImage.setVisibility(View.GONE);
        StorageReference storageReference = uploadsReference.getStorage().getReferenceFromUrl(noteImagePath);
        storageReference.delete();
        noteImagePath = null;
        imageViewNoteImage.setImageURI(null);
    }


    private void deleteWebUrl() {
        textViewWebUrl.setText("");
        linearLayoutWebUrl.setVisibility(View.GONE);
    }

    private void addWebUrl() {
        AddWebUrlDialog addWebUrlDialog = new AddWebUrlDialog();
        addWebUrlDialog.show(getSupportFragmentManager(), null);
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void addImage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION
            );
        } else {
            chooseImageLauncher.launch("image/*");
        }
    }

    private ActivityResultLauncher<String> chooseImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    Picasso.get().load(result).into(imageViewNoteImage);
                    imageViewNoteImage.setVisibility(View.VISIBLE);
                    imageViewDeleteNoteImage.setVisibility(View.VISIBLE);
                    imageUri = result;

                }
            }
    );

    private void setNoteColor(int noteColor) {
        selectedColor = noteColor;
        imageViewColor1.setImageResource(0);
        imageViewColor2.setImageResource(0);
        imageViewColor3.setImageResource(0);
        imageViewColor4.setImageResource(0);
        imageViewColor5.setImageResource(0);
        viewIndicator.setBackgroundColor(selectedColor);

        if (noteColor == noteColor1) {
            imageViewColor1.setImageResource(R.drawable.ic_check_2);
        } else if (noteColor == noteColor2) {
            imageViewColor2.setImageResource(R.drawable.ic_check_2);
        } else if (noteColor == noteColor3) {
            imageViewColor3.setImageResource(R.drawable.ic_check_2);
        } else if (noteColor == noteColor4) {
            imageViewColor4.setImageResource(R.drawable.ic_check_2);
        } else if (noteColor == noteColor5) {
            imageViewColor5.setImageResource(R.drawable.ic_check_2);
        } else {
            imageViewColor1.setImageResource(R.drawable.ic_check_2);
        }
    }

    private void saveNote() {
        noteTitle = editTextNoteTitle.getText().toString().trim();
        noteDateTime = textViewDateTime.getText().toString().trim();
        noteText = editTextNoteText.getText().toString().trim();
        noteWebUrl = textViewWebUrl.getText().toString().trim();

        if (noteTitle.isEmpty() || noteText.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImageToStorage();
        } else {
            uploadNote();
        }

        finish();
    }

    private void uploadNote() {
        Note note = new Note(noteTitle, noteDateTime, noteText, selectedColor, noteWebUrl, noteImagePath);
        if (noteDocumentId != null) {
            notesReference.document(noteDocumentId).set(note);
        } else {
            notesReference.add(note);
        }
    }

    private void uploadImageToStorage() {

        StorageReference storageReference = uploadsReference.child(noteTitle+" "+noteDateTime);
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    noteImagePath = uri.toString();
                                    uploadNote();
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }


    @Override
    public void onWebUrlListener(String url) {
        Pattern pattern = Patterns.WEB_URL;
        Matcher matcher = pattern.matcher(url);
        boolean isValidUrl = matcher.matches();

        if (url.isEmpty()) {
            Toast.makeText(this, "Please, enter URL", Toast.LENGTH_SHORT).show();
        } else if (!isValidUrl) {
            Toast.makeText(this, "Please, enter valid URL", Toast.LENGTH_SHORT).show();
        } else {
            textViewWebUrl.setText(url);
            linearLayoutWebUrl.setVisibility(View.VISIBLE);
        }

    }
}