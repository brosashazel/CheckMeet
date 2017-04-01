package com.example.checkmeet.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Date;
import com.example.checkmeet.model.Meeting;
import com.example.checkmeet.service.MeetingService;
import com.example.checkmeet.utils.Utils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.thebluealliance.spectrum.SpectrumPalette;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CreateMeetingActivity extends AppCompatActivity implements SpectrumPalette.OnColorSelectedListener,
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {


    public static final String TAG = "CreateMeeting";
    public static final String MEETING_COLOR = "MEETING_COLOR";
    public static final int REQUEST_ADD_GUESTS = 1;
    private static final int PLACE_PICKER_REQUEST = 2;

    private EditText etMeetingName;
    private EditText etMeetingDescription;
    private SpectrumPalette palette;
    private TextView tvDate;
    private TextView tvTimefrom;
    private TextView tvTimeto;
    private TextView tv_selected_location;
    private TextView tvGuestList;
    private ImageButton btnAddGuests;
    private ImageButton btnPickLocation;
    private ImageButton btnOpenFromTime;
    private ImageButton btnOpenCalendar;
    private ImageButton btnOpenToTime;


    private int timeFlag;
    private int timeFirstSet;
    private int fromHour;
    private int fromMinute;
    private int toHour;
    private int toMinute;
    private int meetingColor;
    private String strParticipantIdList;
    private String guestNames;
    private Place place;

    public static Typeface tf_roboto;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

        initView();
    }

    private void initView() {

        timeFlag = 0;
        timeFirstSet = 0;
        meetingColor = Color.rgb(255, 152, 0);
        etMeetingName = (EditText) findViewById(R.id.et_meeting_name);
        etMeetingDescription = (EditText) findViewById(R.id.et_meeting_description);


        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTimefrom = (TextView) findViewById(R.id.tv_timefrom);
        tvTimeto = (TextView) findViewById(R.id.tv_timeto);
        tv_selected_location = (TextView) findViewById(R.id.tv_selected_location);
        tvGuestList = (TextView) findViewById(R.id.tv_guest_list);

        btnOpenFromTime = (ImageButton) findViewById(R.id.btn_open_from_time);
        btnOpenCalendar = (ImageButton) findViewById(R.id.btn_open_calendar);
        btnOpenToTime = (ImageButton) findViewById(R.id.btn_open_to_time);
        btnAddGuests = (ImageButton) findViewById(R.id.btn_add_guests);
        btnPickLocation = (ImageButton) findViewById(R.id.btn_pick_location_create);

        palette = (SpectrumPalette) findViewById(R.id.palette);

        actionBar = getSupportActionBar();

        Calendar dateToday = Calendar.getInstance();
        tvDate.setText(dateToday.get(Calendar.MONTH) + 1 + "/" +
                dateToday.get(Calendar.DAY_OF_MONTH) + "/" +
                dateToday.get(Calendar.YEAR));
        tvTimefrom.setText(convertTimeToString(dateToday.get(Calendar.HOUR_OF_DAY), dateToday.get(Calendar.MINUTE)));
        tvTimeto.setText(convertTimeToString(dateToday.get(Calendar.HOUR_OF_DAY) + 1, dateToday.get(Calendar.MINUTE)));
        fromHour = dateToday.get(Calendar.HOUR_OF_DAY);
        toHour = dateToday.get(Calendar.HOUR_OF_DAY) + 1;
        fromMinute = dateToday.get(Calendar.MINUTE);
        toMinute = dateToday.get(Calendar.MINUTE) + 1;
        strParticipantIdList = "";

        palette.setOnColorSelectedListener(this);
        btnOpenCalendar.setOnClickListener(this);
        btnOpenFromTime.setOnClickListener(this);
        btnOpenToTime.setOnClickListener(this);
        btnAddGuests.setOnClickListener(this);
        btnPickLocation.setOnClickListener(this);

        String name = "Create Meeting"; // your string here
        SpannableString s = new SpannableString(name);
        s.setSpan(new TypefaceSpan("fonts/rancho_regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.getSupportActionBar().setTitle(s);
    }

    public String convertTimeToString(int hourOfDay, int minute) {
        String partOfDay;
        String strTime;
        if (hourOfDay - 12 <= 0)
            partOfDay = "AM";
        else
            partOfDay = "PM";

        if (hourOfDay % 12 == 0)
            hourOfDay = 12;
        else
            hourOfDay = hourOfDay % 12;

        String hr = String.valueOf(hourOfDay);
        String min = String.valueOf(minute);

        if (hourOfDay < 10)
            hr = "0" + hr;
        if (minute < 10)
            min = "0" + min;

        strTime = hr + ":" + min + "  " + partOfDay;
        return strTime;
    }

    @Override
    public void onColorSelected(@ColorInt int color) {

        Log.d(TAG, "Meeting Color changed to " + String.format("#%06X", (0xFFFFFF & color)));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Utils.getDarkColor(color));
        }

        meetingColor = color;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "CLICKED");
        if (view.getId() == btnOpenCalendar.getId()) {
            Log.d(TAG, "DATE PICKER BUTTON CLICKED");
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.setAccentColor(meetingColor);
            dpd.setVersion(DatePickerDialog.Version.VERSION_2);
            dpd.setCancelColor(Color.DKGRAY);
            dpd.setOkColor(Color.DKGRAY);
            dpd.show(getFragmentManager(), "Datepickerdialog");
        } else if (view.getId() == btnOpenToTime.getId()) {
            timeFlag = 1;
            showTimePicker(1);
        } else if (view.getId() == btnOpenFromTime.getId()) {
            timeFlag = 0;
            showTimePicker(0);
        } else if (view.getId() == btnAddGuests.getId()) {
            Intent i = new Intent(this, AddGuestsActivity.class);
            i.putExtra(MEETING_COLOR, meetingColor);
            i.putExtra(EditMeetingActivity.EXTRA_PARTICIPANT_LIST, strParticipantIdList);
            //Error IDK What
            Log.e(TAG, "Going to ADD GUEST");
            startActivityForResult(i, REQUEST_ADD_GUESTS);
        } else if (view.getId() == btnPickLocation.getId()) {

            Toast.makeText(this, "Opening map...", Toast.LENGTH_SHORT).show();

            // open place picker
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException |
                    GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        tvDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        if (timeFlag == 0) {
            tvTimefrom.setText(Utils.dateIntegerToString(hourOfDay * 100 + minute));
            if (timeFirstSet == 0)
                tvTimeto.setText(Utils.dateIntegerToString(hourOfDay * 100 + minute + 1));
            Log.d(TAG, "TIME: " + hourOfDay + " : " + minute);
            fromHour = hourOfDay;
            fromMinute = minute;
        } else {
            if (isTimeValid(fromHour, hourOfDay, fromMinute, minute)) {
                tvTimeto.setText(Utils.dateIntegerToString(hourOfDay * 100 + minute));
                toHour = hourOfDay;
                toMinute = minute;
            } else {
                Toast.makeText(getBaseContext(), "Invalid Time", Toast.LENGTH_SHORT).show();
                showTimePicker(1);
            }
        }
    }

    public boolean isTimeValid(int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute) {
        if (fromHourOfDay > toHourOfDay)
            return false;
        else if (fromHourOfDay < toHourOfDay) {
            return true;
        } else if (fromHourOfDay == toHourOfDay) {
            if (fromMinute > toMinute)
                return false;
            else
                return true;
        }
        return false;
    }

    public void showTimePicker(int tofromtime) {
        Calendar now = Calendar.getInstance();
        int hour;
        int min;

        if (tofromtime == 0) {
            hour = fromHour;
            min = fromMinute;
        } else {
            hour = toHour;
            min = toMinute;
        }

        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                hour,
                min,
                false);
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
        tpd.setAccentColor(meetingColor);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cancel_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_cancel:
                Toast.makeText(getBaseContext(), "Creating Meeting Canceled",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_save:
                Toast.makeText(getBaseContext(), "Meeting Saved",
                        Toast.LENGTH_SHORT).show();
                createMeeting();
        }

        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void createMeeting() {
        Log.e(TAG, "Creating Meeting");
        Meeting meeting = new Meeting();
        String[] dateParts = tvDate.getText().toString().split("/");
        meeting.setTitle(etMeetingName.getText().toString());
        meeting.setDescription(etMeetingDescription.getText().toString());
        meeting.setDate(new Date(Integer.parseInt(dateParts[0]),
                Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[2])));
        meeting.setStartTime(Utils.dateStringToInteger(tvTimefrom.getText().toString()));
        meeting.setEndTime(Utils.dateStringToInteger(tvTimeto.getText().toString()));
        meeting.setHostName("Nicolle Magpale");
        meeting.setIsHost(true);
        meeting.setColor(meetingColor);
        meeting.setAddress(place.getAddress() + "");
        meeting.setLongitude(place.getLatLng().longitude);
        meeting.setLatitude(place.getLatLng().latitude);
        meeting.setStringParticipants(guestNames);
        meeting.setParticipantList(getParticipantListID());

        MeetingService.createMeeting(getBaseContext(), meeting, true);
    }

    private List<String> getParticipantListID()
    {
        List<String> partcipantListID = null;

        String [] idArray = strParticipantIdList.split(", ");
        Log.e(TAG, strParticipantIdList);
        for(int i = 0; i < idArray.length ; i ++)
        {
            Log.e(TAG, strParticipantIdList);
            partcipantListID.add(idArray[i]);
        }

        return partcipantListID;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_GUESTS) {

            guestNames = data.getStringExtra(AddGuestsActivity.GUEST_NAMES_TAG);
            strParticipantIdList = data.getStringExtra(AddGuestsActivity.GUEST_LIST_TAG);
            Log.e(TAG, strParticipantIdList);
            tvGuestList.setText(guestNames);

        } else if (requestCode == PLACE_PICKER_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:

                    place = PlacePicker.getPlace(getBaseContext(), data);

                    String finalAddress;

                    // check if coordinates
                    if (place.getName().toString().contains("°")) {
                        finalAddress = place.getAddress().toString();
                    } else {
                        finalAddress = place.getName().toString() + ", " +
                                place.getAddress().toString();
                    }

                    String toastMsg = String.format("Place: %s", finalAddress);
                    Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                    tv_selected_location.setText(finalAddress);
                    tv_selected_location.setVisibility(View.VISIBLE);

                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
    }
}
