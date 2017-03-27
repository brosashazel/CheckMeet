package com.example.checkmeet.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Meeting;
import com.example.checkmeet.service.MeetingService;

public class OpenNotesActivity extends AppCompatActivity {

    private EditText et_notes;

    private long meeting_id;
    private String notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_notes);

        et_notes = (EditText) findViewById(R.id.et_notes);

        meeting_id = getIntent().getLongExtra(ViewMeetingActivity.EXTRA_MEETING_ID, -1);
        String meeting_title = getIntent().getStringExtra(Meeting.COL_TITLE);

        notes = getIntent().getStringExtra(Meeting.COL_NOTES);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setTitle(meeting_title);
            actionBar.setSubtitle("Notes");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        et_notes.setText(notes);

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

            // get string
            notes = et_notes.getText().toString();

            // update db
            MeetingService.updateNotes(getBaseContext(), meeting_id, notes);

            Toast.makeText(OpenNotesActivity.this,
                    "Saving changes...", Toast.LENGTH_SHORT).show();

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

                            // get string
                            notes = et_notes.getText().toString();

                            // update db
                            MeetingService.updateNotes(getBaseContext(), meeting_id, notes);

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
