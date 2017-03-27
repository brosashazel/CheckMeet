package com.example.checkmeet.view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Meeting;
import com.example.checkmeet.service.MeetingService;
import com.example.checkmeet.utils.Utils;

public class ViewMeetingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ViewMeetingActivity";

    private ImageView iv_open_view_map;

    public static final String EXTRA_LATITUDE = "EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "EXTRA_LONGITUDE";
    public static final String EXTRA_ADDRESS = "EXTRA_ADDRESS";

    public static final String EXTRA_MEETING_ID = "EXTRA_MEETING_ID";
    public static final String EXTRA_MEETING_NOTES = "EXTRA_MEETING_NOTES";

    private Meeting meeting;
    private int meeting_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meeting);

        meeting_id = getIntent().getIntExtra(Meeting.COL_MEETINGID, -1);

        iv_open_view_map = (ImageView) findViewById(R.id.iv_open_view_map);
        iv_open_view_map.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.popupmenu_temp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {
            case R.id.popup_edit:
                Intent intent = new Intent(this, EditMeetingActivity.class);
                intent.putExtra(EXTRA_MEETING_ID, meeting.getMeeting_id() + "");
                startActivity(intent);
                break;
            case R.id.popup_delete:
                Toast.makeText(this, "DELETE", Toast.LENGTH_SHORT).show();
                break;
            case R.id.popup_open_notes:
                Intent intent1 = new Intent(this, OpenNotesActivity.class);
                intent1.putExtra(EXTRA_MEETING_ID, meeting.getMeeting_id());
                intent1.putExtra(Meeting.COL_NOTES, meeting.getNotes());
                intent1.putExtra(Meeting.COL_TITLE, meeting.getTitle());
                startActivity(intent1);
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

            intent.putExtra(EXTRA_LATITUDE, meeting.getLatitude());
            intent.putExtra(EXTRA_LONGITUDE, meeting.getLongitude());
            intent.putExtra(EXTRA_ADDRESS, meeting.getAddress());

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
}
