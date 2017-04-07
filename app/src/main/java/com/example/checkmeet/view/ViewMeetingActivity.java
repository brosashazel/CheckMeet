package com.example.checkmeet.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Date;
import com.example.checkmeet.model.Meeting;
import com.example.checkmeet.model.Status;
import com.example.checkmeet.service.MeetingService;
import com.example.checkmeet.utils.Utils;

import java.util.Calendar;

public class ViewMeetingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ViewMeetingActivity";

    private ImageView iv_open_view_map;

    private Meeting meeting;
    private int meeting_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meeting);

        meeting_id = getIntent().getIntExtra(Meeting.COL_MEETINGID, -1);

        Log.e(TAG, "meeting_id = " + meeting_id);

        iv_open_view_map = (ImageView) findViewById(R.id.iv_open_view_map);
        iv_open_view_map.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        Log.e(TAG, "isHost?? -- " + meeting.isHost());
        Log.e(TAG, "isMeetingDone?? -- " + isMeetingDone());
        Log.e(TAG, "isMeetingCancelled?? -- " + meeting.getStatus());

        // if meeting is DONE or CANCELLED
        if(isMeetingDone() || meeting.getStatus() == Status.CANCELLED) {
            getMenuInflater().inflate(R.menu.delete_open_notes_menu, menu);
        }

        // if HOST and meeting is NOT DONE and NOT CANCELLED
        else if(meeting.isHost()) {
            getMenuInflater().inflate(R.menu.edit_cancel_open_notes_menu, menu);
        }

        // if NOT HOST and meeting is NOT DONE and NOT CANCELLED
        else if(!meeting.isHost()) {
            getMenuInflater().inflate(R.menu.open_notes_participants_menu, menu);
        }

        return true;
    }

    public boolean isMeetingDone()
    {
        // meeting
        Calendar meeting_date = Calendar.getInstance();
        meeting_date.set(
                meeting.getDate().getYear(),
                meeting.getDate().getMonth(),
                meeting.getDate().getDayOfMonth(),
                meeting.getEndTime() / 100,
                meeting.getEndTime() % 100
        );

        // current
        Calendar current = Calendar.getInstance();

        // check if meeting is after current date and time
        return meeting_date.before(current);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {
            case R.id.popup_edit:
                Intent intent = new Intent(this, EditMeetingActivity.class);
                intent.putExtra(Meeting.COL_MEETINGID, meeting.getMeeting_id() + "");
                startActivity(intent);
                break;
            case R.id.popup_delete:
                showAlertDelete();
                break;
            case R.id.popup_open_notes:
                Intent intent1 = new Intent(this, OpenNotesActivity.class);
                intent1.putExtra(Meeting.COL_MEETINGID, meeting.getMeeting_id());
                intent1.putExtra(Meeting.COL_NOTES, meeting.getNotes());
                intent1.putExtra(Meeting.COL_TITLE, meeting.getTitle());
                intent1.putExtra(EditMeetingActivity.MEETING_COLOR, meeting.getColor());
                startActivity(intent1);
                break;
            case R.id.popup_cancel:
                showAlertCancel();
                break;
            default:
                super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == iv_open_view_map.getId()) {
            Toast.makeText(ViewMeetingActivity.this,
                    "Opening map...", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(ViewMeetingActivity.this,
                    ViewLocationActivity.class);

            intent.putExtra(Meeting.COL_TITLE, meeting.getTitle());
            intent.putExtra(Meeting.COL_LATITUDE, meeting.getLatitude());
            intent.putExtra(Meeting.COL_LONGITUDE, meeting.getLongitude());
            intent.putExtra(Meeting.COL_ADDRESS, meeting.getAddress());

            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        meeting = MeetingService.getMeeting(getBaseContext(), meeting_id);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(meeting.getTitle());
            actionBar.setBackgroundDrawable(new ColorDrawable(meeting.getColor()));
        }

        // date
        ((TextView) findViewById(R.id.tv_date)).setText(Utils.dateToString(meeting.getDate()));

        // time
        ((TextView) findViewById(R.id.tv_time)).setText(
                Utils.dateIntegerToString(meeting.getStartTime()) + " - " +
                        Utils.dateIntegerToString(meeting.getEndTime())
        );

        // location
        ((TextView) findViewById(R.id.tv_location)).setText(meeting.getAddress());

        // description
        if(meeting.getDescription() == null || meeting.getDescription().isEmpty()) {
            findViewById(R.id.container_description).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.tv_description)).setText(meeting.getDescription());
        }

        // host
        ((TextView) findViewById(R.id.tv_host_name)).setText(meeting.getHostName());

        // participants
        ((TextView) findViewById(R.id.tv_member_list)).setText(meeting.getStringParticipants());

        // color status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Utils.getDarkColor(meeting.getColor()));
        }

    }

    private void showAlertDelete() {
        String message = "All this meeting's data will be deleted permanently." +
                " This includes the details of the meeting and your notes.";

        String title = "Delete meeting?";

        // show alert
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // delete meeting on the db
                        MeetingService.deleteMeeting(getBaseContext(), meeting_id);

                        // tell user
                        Toast.makeText(getBaseContext(),
                                "Meeting has been deleted!", Toast.LENGTH_LONG).show();

                        // finish activity
                        onBackPressed();

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
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

    private void showAlertCancel() {
        String message = "You are about to send " + meeting.getParticipantList().size()
                + " SMS. Do you want to proceed cancelling the meeting?";

        String title = "Cancel meeting?";

        // show alert
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // updateDB
                        MeetingService.cancelMeeting(getBaseContext(),
                                meeting.getMeeting_id() + "", true);

                        // tell user
                        Toast.makeText(getBaseContext(),
                                "Meeting has been cancelled!", Toast.LENGTH_LONG).show();

                        // send sms
                        String message = generateSMS();
                        Log.e(TAG, "SMS = \n" + message);
                        Utils.sendSMS(getBaseContext(), message, meeting.getParticipantList());

                        // finish activity
                        onBackPressed();

                    }
                })
                .setNegativeButton("GO BACK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
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

    //////////////////////////////////////// SMS GENERATION  /////////////////////////////////

    public String generateSMS()
    {
        String sms = "CKMT [CNL] \n\n" +
                meeting.getTitle() + "\n\n" +
                "Hi! My scheduled meeting with you on " +
                meeting.getDate().toString() + " " +
                meeting.getStartTime() + " - " +
                meeting.getEndTime() + " at " +
                meeting.getAddress()+ " is cancelled. \n\n" +
                "Sorry for the inconvenience. \n\n " +
                "__________" + //10 underscores
                meeting.getDevice_id();

        sms += "&&&";
        return sms;
    }
}
