package com.example.checkmeet.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Meeting;
import com.example.checkmeet.model.Notes;
import com.example.checkmeet.service.NotesService;

public class OpenNotesActivity extends AppCompatActivity {

    private Notes notes;
    private EditText et_notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_notes);

        et_notes = (EditText) findViewById(R.id.et_notes);

        int meeting_id = getIntent().getIntExtra(Meeting.COL_MEETINGID, -1);
        String meeting_title = getIntent().getStringExtra(Meeting.COL_TITLE);

        notes = NotesService.getNote(getBaseContext(), meeting_id);

        if(notes == null) {
            // make new note even if it has empty body yet
            notes = new Notes();
            notes.setNote_id(meeting_id);
            notes.setNotes("");

            NotesService.createNote(getBaseContext(), notes);
        }

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setTitle(meeting_title);
            actionBar.setSubtitle("Notes");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        et_notes.setText(notes.getNotes());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.open_notes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_done) {
            // done editing

            notes.setNotes(et_notes.getText().toString());
            NotesService.updateNote(getBaseContext(), notes);

            onBackPressed();
        } else {
            // pressed back button in actionbar

            // show alert
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set title
            alertDialogBuilder.setTitle("Save your changes?");

            // set dialog message
            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity

                            notes.setNotes(et_notes.getText().toString());
                            NotesService.updateNote(getBaseContext(), notes);

                            Toast.makeText(OpenNotesActivity.this,
                                    "Saving changes...", Toast.LENGTH_SHORT).show();

                            onBackPressed();
                        }
                    })
                    .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            onBackPressed();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        }

        return super.onOptionsItemSelected(item);
    }
}
