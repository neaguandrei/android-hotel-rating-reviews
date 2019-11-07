package ratingapp.ddey.com.testratingapp.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.utils.adapters.NoteAdapter;
import ratingapp.ddey.com.testratingapp.models.Note;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseHelper;
import ratingapp.ddey.com.testratingapp.utils.others.Session;

public class NotesActivity extends AppCompatActivity {
    private Session mSession;
    private DatabaseHelper mDb;

    private NoteAdapter mAdapter;
    private ListView lvNotes;
    private List<Note> notesList;

    private ProgressBar progressBar;
    private Activity currentActivity;
    private FirebaseHelper mFirebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        initializeComponents();
    }

    private void initializeComponents() {
        mFirebaseHelper = FirebaseHelper.getInstance();
        progressBar = findViewById(R.id.progressbar_notes);
        this.setTitle("Notes");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        currentActivity = this;
        lvNotes = findViewById(R.id.lv_notes);
        notesList = new ArrayList<>();

        FloatingActionButton buttonAdd = findViewById(R.id.add_button_notes);
        mSession = new Session(this);
        mDb = new DatabaseHelper(this);

        loadNotes();
        buttonAdd.setOnClickListener(addEvent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }

    public void loadNotes() {
        if (verifyConnection()) {
            syncNotesToFirebase();
            loadNotesFromFirebase();
        } else {
            Toast.makeText(getApplicationContext(), "Showing offline data", Toast.LENGTH_SHORT).show();
            loadNotesFromDb();
        }
    }

    public void loadNotesFromDb() {
        notesList = mDb.getNotesList(mDb.getUserToken(mSession));
        mAdapter = new NoteAdapter(getApplicationContext(), R.layout.listview_notes, notesList, this);
        lvNotes.setAdapter(mAdapter);
    }

    public void loadNotesFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        mFirebaseHelper.openConnection();
        mFirebaseHelper.getUserNotesReference().orderByChild("idNote").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Note note = data.getValue(Note.class);
                    if (note != null && note.getUserToken().equals(mDb.getUserToken(mSession))) {
                        notesList.add(note);
                    }
                }
                mAdapter = new NoteAdapter(getApplicationContext(), R.layout.listview_notes, notesList, currentActivity);
                lvNotes.setAdapter(mAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void syncNotesToFirebase() {
        if (verifyConnection()) {
            mFirebaseHelper.openConnection();
            for (Note n : notesList) {
                if (n.getNoteToken() == null || n.getNoteToken().trim().isEmpty()) {
                    n.setNoteToken(mFirebaseHelper.getUserNotesReference().push().getKey());
                    mFirebaseHelper.getUserNotesReference().child(n.getNoteToken()).setValue(n);
                    mDb.insertNote(n, mDb.getUserToken(mSession));
                    mDb.updateNoteToken(n, mDb.getUserToken(mSession));
                }
            }
        }
    }

    public View.OnClickListener addEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText noteEditText = new EditText(NotesActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(NotesActivity.this)
                        .setTitle("Add new note")
                        .setMessage("What do you need to remember for your travel?")
                        .setView(noteEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String noteString = String.valueOf(noteEditText.getText().toString());
                                Note newNote = new Note(noteString);
                                mDb.insertNote(newNote, mDb.getUserToken(mSession));
                                mAdapter.notifyDataSetChanged();
                                loadNotesFromDb();
                                syncNotesToFirebase();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        };
    }

    public boolean verifyConnection() {
        ConnectionStatus connection = ConnectionStatus.getInstance(getApplicationContext());
        return connection.isOnline();
    }

}
