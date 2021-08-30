package com.example.notesonfire.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesonfire.R;
import com.example.notesonfire.adapters.NotesAdapter;
import com.example.notesonfire.models.Note;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ListOfNotesActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        NotesAdapter.OnNoteListener,
        NotesAdapter.OnImageDeleteListener {

    public static final String NOTE_FROM_INTENT = "note_from_intent";
    public static final String NOTE_ID = "note_id";

    //ui
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerViewNotes;
    private FloatingActionButton floatingActionButtonAddNote;
    private View headerView;
    private TextView textViewMailHeader;

    //vars
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference usersReference;
    private CollectionReference notesReference;
    private NotesAdapter notesAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private StorageReference uploadsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_notes);
        initActionBar();
        initUI();
        setToggle();
        initFirebase();
        setListeners();
        setRecyclerView();
        setDrawerEmail();
    }

    private void setDrawerEmail() {
        if (firebaseUser.isAnonymous()) {
            textViewMailHeader.setText("Temporary Account");
        } else {
            textViewMailHeader.setText(firebaseUser.getEmail());
        }
    }

    private void initFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        usersReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());
        notesReference = usersReference.collection("notes");
        uploadsReference = FirebaseStorage.getInstance().getReference("uploads");
    }

    private void setRecyclerView() {
        Query query = notesReference.orderBy("noteDate", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        notesAdapter = new NotesAdapter(this, options);
        recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerViewNotes.setAdapter(notesAdapter);
    }

    private void setListeners() {
        navigationView.setNavigationItemSelectedListener(this);
        floatingActionButtonAddNote.setOnClickListener(v -> goToNoteActivity());
    }

    private void setToggle() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    private void initUI() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        floatingActionButtonAddNote = findViewById(R.id.floatingActionButtonAddNote);
        headerView = navigationView.getHeaderView(0);
        textViewMailHeader = headerView.findViewById(R.id.textViewMailHeader);
    }

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbarListOfNotes);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawers();
        switch (item.getItemId()) {
            case R.id.itemCreateNewAccount:
                signUp();
                return true;
            case R.id.itemAddNote:
                goToNoteActivity();
                return true;
            case R.id.itemSyncNotes:
                syncNotes();
                return true;
            case R.id.itemLogout:
                logout();
            default:
                return false;
        }
    }


    private void signUp() {
        if (firebaseUser.isAnonymous()) {
            firebaseUser.delete().addOnSuccessListener(unused -> {
                startActivity(new Intent(this, LoginActivity.class));
            });
        } else {
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }

    }

    private void logout() {
        if (firebaseUser.isAnonymous()) {
            displayLogoutDialog();
        } else {
            firebaseAuth.signOut();
            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
        }
    }

    private void displayLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("You are logged in with temporary account. Logging out will delete all the notes");
        builder.setPositiveButton("Sync notes", (dialog, which) -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        builder.setNegativeButton("Logout", (dialog, which) -> {
            firebaseUser.delete().addOnSuccessListener(unused ->
                    startActivity(new Intent(getApplicationContext(), SplashActivity.class)));
        });
        builder.show();
    }

    private void syncNotes() {
        if (firebaseUser.isAnonymous()) {
            displaySyncNotesDialog();
        } else {
            Toast.makeText(this, "You are already logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displaySyncNotesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("You are logged in with temporary account. Logging out will delete all the notes");
        builder.setPositiveButton("Sync notes", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });

        builder.setNegativeButton("Login", (dialog, which) -> {
            firebaseUser.delete().addOnSuccessListener(unused ->
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
        });
        builder.show();
    }

    private void goToNoteActivity() {
        Intent intent = new Intent(this, NoteActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_list_of_notes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setOnNoteClickListener(DocumentSnapshot documentSnapshot, int position) {
        Intent intent = new Intent(this, NoteActivity.class);
        Note noteFromIntent = documentSnapshot.toObject(Note.class);
        intent.putExtra(NOTE_FROM_INTENT, noteFromIntent);
        intent.putExtra(NOTE_ID, documentSnapshot.getId());
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawerLayout.closeDrawers();
        notesAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        notesAdapter.startListening();
    }

    @Override
    public void deleteImageListener(String noteImagePath) {
        StorageReference storageReference = uploadsReference.getStorage().getReferenceFromUrl(noteImagePath);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Note successfully deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}