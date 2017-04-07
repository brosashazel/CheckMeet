package com.example.checkmeet.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
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

import com.example.checkmeet.model.Date;
import com.example.checkmeet.model.Meeting;
import com.example.checkmeet.service.MeetingService;
import com.example.checkmeet.utils.Utils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.thebluealliance.spectrum.SpectrumPalette;
import com.example.checkmeet.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class EditMeetingActivity extends AppCompatActivity implements
        SpectrumPalette.OnColorSelectedListener,
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    public static String TAG = "EditMeetingActivity";
    public static final String MEETING_COLOR = "MEETING_COLOR";
    public static final String EXTRA_PARTICIPANT_LIST = "EXTRA_PARTICIPANT_LIST";
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
    private ImageButton btnOpenCalendar;
    private ImageButton btnOpenFromTime;
    private ImageButton btnOpenToTime;
    private ImageButton btnPickLocation;
    private ImageButton btnAddGuests;

    private int timeFlag;
    private int fromHour;
    private int fromMinute;
    private int toHour;
    private int toMinute;
    private int month;
    private int day;
    private int year;
    private int meetingColor;
    private String strParticipantIdList;
    private String address;
    private double latitude;
    private double longitude;
    private String guestNames;

    private int isToday;

    private Meeting meeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meeting);

        int meeting_id =
                Integer.parseInt(getIntent().getStringExtra(Meeting.COL_MEETINGID));
        meeting = MeetingService.getMeeting(getBaseContext(), meeting_id);

        initView();
    }

    private void initView() {

        timeFlag = 0;
        etMeetingName = (EditText) findViewById(R.id.et_meeting_name);
        etMeetingDescription = (EditText) findViewById(R.id.et_meeting_description);
        palette = (SpectrumPalette) findViewById(R.id.palette);
        btnOpenCalendar = (ImageButton) findViewById(R.id.btn_open_calendar);
        btnOpenFromTime = (ImageButton) findViewById(R.id.btn_open_from_time);
        btnOpenToTime = (ImageButton) findViewById(R.id.btn_open_to_time);
        btnPickLocation = (ImageButton) findViewById(R.id.btn_pick_location);
        btnAddGuests = (ImageButton) findViewById(R.id.btn_add_guests);

        tv_selected_location = (TextView) findViewById(R.id.tv_selected_location);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTimefrom = (TextView) findViewById(R.id.tv_timefrom);
        tvTimeto = (TextView) findViewById(R.id.tv_timeto);
        tvGuestList = (TextView) findViewById(R.id.tv_add_guests);

        etMeetingName.setText(meeting.getTitle());
        meetingColor = meeting.getColor();
        tvDate.setText(meeting.getDate().toString());
        tvTimefrom.setText(Utils.dateIntegerToString(meeting.getStartTime()));
        tvTimeto.setText(Utils.dateIntegerToString(meeting.getEndTime()));
        fromHour = Utils.getHourFromTimeInteger(meeting.getStartTime());
        fromMinute = Utils.getMinuteFromTimeInteger(meeting.getStartTime());
        tvGuestList.setText(meeting.getStringParticipants());
        generateParticipantIdString();

        address = meeting.getAddress();
        latitude = meeting.getLatitude();
        longitude = meeting.getLongitude();

        toHour = Utils.getHourFromTimeInteger(meeting.getEndTime());
        toMinute = Utils.getMinuteFromTimeInteger(meeting.getEndTime());

        Date date = meeting.getDate();
        month = date.getMonth();
        day = date.getDayOfMonth();
        year = date.getYear();
        if(meeting.getDescription() != null) {
            etMeetingDescription.setText(meeting.getDescription());
        }

        tv_selected_location.setText(meeting.getAddress());
        tv_selected_location.setVisibility(View.VISIBLE);

        setListeners();

        String name = "Edit Meeting";
        SpannableString s = new SpannableString(name);
        s.setSpan(new TypefaceSpan("fonts/rancho_regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.getSupportActionBar().setTitle(s);

        palette.setSelectedColor(meeting.getColor());

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(meeting.getColor()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Utils.getDarkColor(meeting.getColor()));
        }
    }

    private void setListeners()
    {
        palette.setOnColorSelectedListener(this);
        btnOpenCalendar.setOnClickListener(this);
        btnOpenFromTime.setOnClickListener(this);
        btnOpenToTime.setOnClickListener(this);
        btnAddGuests.setOnClickListener(this);
        btnPickLocation.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_GUESTS) {
            switch(resultCode) {
                case RESULT_OK:
                    Log.d(TAG, "From Add Guests");

                    guestNames = data.getStringExtra(AddGuestsActivity.GUEST_NAMES_TAG);
                    strParticipantIdList = data.getStringExtra(AddGuestsActivity.GUEST_LIST_TAG);
                    tvGuestList.setText(guestNames);
                    //TODO: UPDATE PARTICIPANTS

                    meeting.setStringParticipants(guestNames);
                    meeting.setParticipantList(getParticipantListID());

                    MeetingService.updateMeetingParticipants(getBaseContext(),
                            getParticipantListID(),
                            guestNames,
                            meeting.getMeeting_id());

                    break;
                case RESULT_CANCELED:
                    break;
            }
        } else if(requestCode == PLACE_PICKER_REQUEST) {
            switch(resultCode) {
                case RESULT_OK:

                    Place place = PlacePicker.getPlace(getBaseContext(), data);

                    String finalAddress;

                    // check if coordinates
                    if(place.getName().toString().contains("°")) {
                        finalAddress = place.getAddress().toString();
                    } else {
                        finalAddress = place.getName().toString() + ", " +
                                place.getAddress().toString();
                    }

                    address = (String) place.getAddress();
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;

                    tv_selected_location.setText(finalAddress);
                    tv_selected_location.setVisibility(View.VISIBLE);

                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
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
                Toast.makeText(getBaseContext(), "Editing Meeting Canceled",
                        Toast.LENGTH_LONG).show();
                super.onBackPressed();
                break;
            case R.id.action_save:
                if(checkAllInput())
                    showAlertEdit();
                else
                    showAlertDialog("Please fill up all necessary details.", 0);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorSelected(@ColorInt int color) {

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Utils.getDarkColor(color));
        }

        meetingColor = color;
    }

    public void showAlertDialog(String msg, final int resumeDialog)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(resumeDialog == 1)
                    showTimePicker(1);
                else if(resumeDialog == 2)
                    showTimePicker(0);
                else if(resumeDialog == 3)
                    showDatePicker();
            }
        });
        builder.setMessage(msg);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "CLICKED");
        if (view.getId() == btnOpenCalendar.getId()) {
            showDatePicker();
        } else if (view.getId() == btnOpenToTime.getId()) {
            timeFlag = 1;
            showTimePicker(1);
        } else if (view.getId() == btnOpenFromTime.getId()) {
            timeFlag = 0;
            showTimePicker(0);
        } else if (view.getId() == btnAddGuests.getId()) {
            Intent i = new Intent(this, AddGuestsActivity.class);
            i.putExtra(MEETING_COLOR, meetingColor);
            Log.e(TAG, strParticipantIdList);
            i.putExtra(EXTRA_PARTICIPANT_LIST, strParticipantIdList);
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

                Toast.makeText(this,
                        "Your Google Play services is not updated.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //////////////////////////////////////// SETTING MEETING TIME /////////////////////////////////

    public void showTimePicker(int tofromtime) {
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
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (timeFlag == 0) {
            if(isToday == 1 && checkTimeOfDay(hourOfDay, minute)){
                tvTimeto.setText(Utils.dateIntegerToString((hourOfDay + 1) * 100  + minute ));
                tvTimefrom.setText(Utils.dateIntegerToString(hourOfDay * 100 + minute));
                fromHour = hourOfDay;
                fromMinute = minute;
                toHour = hourOfDay + 1;
                toMinute = minute;
                timeFlag = 1;
                showTimePicker(1);
            }else if(isToday == 0){
                tvTimeto.setText(Utils.dateIntegerToString((hourOfDay + 1) * 100  + minute ));
                tvTimefrom.setText(Utils.dateIntegerToString(hourOfDay * 100 + minute));
                fromHour = hourOfDay;
                fromMinute = minute;
                toHour = hourOfDay + 1;
                toMinute = minute;
                timeFlag = 1;
                showTimePicker(1);
            }else
            {
                showAlertDialog("Sorry, the time you entered has already passed. " +
                        "Please choose a later time.", 2);
            }
        }else{
            if (isTimeValid(fromHour, hourOfDay, fromMinute, minute)) {
                tvTimeto.setText(Utils.dateIntegerToString(hourOfDay * 100 + minute));
                toHour = hourOfDay;
                toMinute = minute;
            } else {
                showAlertDialog(
                        "Sorry, CheckMeet only accepts meetings that are within the day.", 1);

            }
        }
    }

    public boolean isTimeValid(int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute) {
        if (fromHourOfDay > toHourOfDay)
            return false;
        else if (fromHourOfDay < toHourOfDay) {
            return true;
        } else if (fromHourOfDay == toHourOfDay) {
            return fromMinute <= toMinute;
        }
        return false;
    }

    public boolean checkTimeOfDay(int hour, int min) {
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.HOUR_OF_DAY) > hour)
            return false;
        else if (now.get(Calendar.HOUR_OF_DAY) < hour) {
            return true;
        } else if (now.get(Calendar.HOUR_OF_DAY) == hour) {
            return now.get(Calendar.MINUTE) <= min;
        }
        return false;
    }

    //////////////////////////////////////// SETTING MEETING DATE /////////////////////////////////

    public void showDatePicker()
    {
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                year,
                month,
                day
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.setAccentColor(meetingColor);
        dpd.setCancelColor(Color.DKGRAY);
        dpd.setOkColor(Color.DKGRAY);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int pyear, int pmonthOfYear, int pdayOfMonth) {
        Calendar now = Calendar.getInstance();
        Calendar datePick = Calendar.getInstance();
        datePick.set(pyear, pmonthOfYear, pdayOfMonth);
        if(now.after(datePick)) {
            showAlertDialog("Oops! You can't set a meeting any time before today.", 3);
        }else if(now.equals(datePick))
        {
            isToday = 1;
            tvDate.setText((pmonthOfYear + 1) + "/" + pdayOfMonth + "/" + year);
            year = pyear;
            month = pmonthOfYear;
            day = pdayOfMonth;
        }else {
            tvDate.setText((pmonthOfYear + 1) + "/" + pdayOfMonth + "/" + year);
            year = pyear;
            month = pmonthOfYear;
            day = pdayOfMonth;
        }
    }

    //////////////////////////////////////// EDITING MEETING /////////////////////////////////

    private void showAlertEdit() {
        String message = "You are about to send " + getParticipantListID().size()
                + " SMS. Do you want to proceed editing a meeting?";

        String title = "Edit meeting?";

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

                        // update db
                        updateMeeting();

                        // send sms to participants
                        String msg = generateSMS();
                        Log.e(TAG, "SMS = \n" + msg);
                        Utils.sendSMS(getBaseContext(), msg, getParticipantListID());

                        // tell user
                        Toast.makeText(getBaseContext(),
                                "Meeting has been edited!", Toast.LENGTH_LONG).show();

                        // view created meeting
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

    public void updateMeeting() {
        String[] dateParts = tvDate.getText().toString().split("/");
        meeting.setTitle(etMeetingName.getText().toString());
        meeting.setDescription(etMeetingDescription.getText().toString());
        meeting.setDate(new Date(Integer.parseInt(dateParts[0]) - 1,
                Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[2])));
        meeting.setStartTime(Utils.dateStringToInteger(tvTimefrom.getText().toString()));
        meeting.setEndTime(Utils.dateStringToInteger(tvTimeto.getText().toString()));
        meeting.setHostName(CreateMeetingActivity.HOSTNAME); //TODO: CHANGE TO PREFERENCE
        meeting.setIsHost(true);
        meeting.setColor(meetingColor);
        meeting.setAddress(address);
        meeting.setLongitude(latitude);
        meeting.setLatitude(longitude);
        meeting.setStringParticipants(guestNames);
        meeting.setParticipantList(getParticipantListID());

        Log.e(TAG, "start time in text field = " + tvTimefrom.getText().toString());
        Log.e(TAG, "end time in text field = " + tvTimeto.getText().toString());

        Log.e(TAG, "title = " + meeting.getTitle());
        Log.e(TAG, "description = " + meeting.getDescription());
        Log.e(TAG, "date = " + meeting.getDate().toString());
        Log.e(TAG, "start time = " + Utils.dateIntegerToString(meeting.getStartTime()));
        Log.e(TAG, "end time = " + Utils.dateIntegerToString(meeting.getEndTime()));
        Log.e(TAG, "address = " + meeting.getAddress());
        Log.e(TAG, "latitude = " + meeting.getLatitude());
        Log.e(TAG, "longitude = " + meeting.getLongitude());
        Log.e(TAG, "participants = " + meeting.getStringParticipants());

        Log.e(TAG, MeetingService.updateMeeting(getBaseContext(), meeting, true)  + "");
    }

    private void generateParticipantIdString()
    {
        List<String> partcipantListID = meeting.getParticipantList();
        strParticipantIdList = partcipantListID.get(0);
        for(int i = 1 ; i < partcipantListID.size(); i ++) {
            strParticipantIdList += ", " + partcipantListID.get(i);
        }
    }

    private List<String> getParticipantListID()
    {
        List<String> partcipantListID = new ArrayList<>();

        String [] idArray = strParticipantIdList.split(", ");
        Collections.addAll(partcipantListID, idArray);
        return partcipantListID;
    }

    public boolean checkAllInput()
    {
        return !(etMeetingName.getText().toString().isEmpty()) &&
                !(tvDate.getText().toString().isEmpty()) &&
                !(tvTimefrom.getText().toString().isEmpty()) &&
                !(tvTimeto.getText().toString().isEmpty()) &&
                !(tv_selected_location.getText().toString().isEmpty()) &&
                !(tvGuestList.getText().toString().isEmpty());
    }

    //////////////////////////////////////// SMS GENERATION  /////////////////////////////////

    public String generateSMS()
    {

        Meeting newMeeting = MeetingService.getMeeting(getBaseContext(),
                (int) meeting.getMeeting_id());

        String sms = "CKMT [EDT] \n\n" +
                newMeeting.getTitle() + "\n\n" +
                "Hi! My scheduled meeting with you has been changed. The meeting is now on " +
                Utils.dateToString(newMeeting.getDate()) + " " +
                Utils.dateIntegerToString(newMeeting.getStartTime()) + " - " +
                Utils.dateIntegerToString(newMeeting.getEndTime()) + " at " +
                newMeeting.getAddress() + "\n\n" +
                "See you there! \n\n " +
                "__________" + //10 underscores
                newMeeting.getDevice_id() + "$&" +
                newMeeting.getTitle() + "$&" +
                newMeeting.getDate().toString() + "$&" +
                newMeeting.getStartTime() + "$&" +
                newMeeting.getEndTime()  + "$&" +
                newMeeting.getAddress() + "$&" +
                newMeeting.getLatitude() + "$&" +
                newMeeting.getLongitude() + "$&" +
                newMeeting.getStringParticipants() + "$&" +
                CreateMeetingActivity.HOSTNAME + "$&" +  /*Change to shared preference shiz*/
                newMeeting.getColor();


        Log.e(TAG, "title = " + meeting.getTitle());
        Log.e(TAG, "description = " + meeting.getDescription());
        Log.e(TAG, "date = " + meeting.getDate().toString());
        Log.e(TAG, "start time = " + meeting.getStartTime());
        Log.e(TAG, "end time = " + meeting.getEndTime());

        if(!(etMeetingDescription.getText().toString().isEmpty()))
            sms += "$&" + etMeetingDescription.getText().toString();

        sms += "&&&";
        return sms;
    }



}
