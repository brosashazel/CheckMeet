package com.example.checkmeet.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class CreateMeetingActivity extends AppCompatActivity implements
        SpectrumPalette.OnColorSelectedListener,
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {


    public static final String TAG = "CreateMeeting";
    public static final String MEETING_COLOR = "MEETING_COLOR";
    public static final int REQUEST_ADD_GUESTS = 1;
    private static final int PLACE_PICKER_REQUEST = 2;
    public static String HOSTNAME = "Nicolle Magpale";

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
    private int fromHour;
    private int fromMinute;
    private int toHour;
    private int toMinute;
    private int month;
    private int day;
    private int year;
    private int meetingColor;
    private int isToday;
    private String strParticipantIdList;
    private String guestNames;
    private String imeiID;
    private Place place;

    public static Typeface tf_roboto;
    private Meeting createdMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);
        initView();
    }

    private void initView() {

        timeFlag = 0;
        meetingColor = Color.rgb(255, 152, 0);
        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imeiID = mngr.getDeviceId();
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

        Calendar dateToday = Calendar.getInstance();
        tvDate.setText(dateToday.get(Calendar.MONTH) + 1 + "/" +
                dateToday.get(Calendar.DAY_OF_MONTH) + "/" +
                dateToday.get(Calendar.YEAR));
        tvTimefrom.setText(convertTimeToString(
                dateToday.get(Calendar.HOUR_OF_DAY), dateToday.get(Calendar.MINUTE)));
        tvTimeto.setText(convertTimeToString(
                dateToday.get(Calendar.HOUR_OF_DAY) + 1 , dateToday.get(Calendar.MINUTE)));
        fromHour = dateToday.get(Calendar.HOUR_OF_DAY);
        toHour = dateToday.get(Calendar.HOUR_OF_DAY) + 1;
        fromMinute = dateToday.get(Calendar.MINUTE);
        toMinute = dateToday.get(Calendar.MINUTE);
        month = dateToday.get(Calendar.MONTH);
        day = dateToday.get(Calendar.DAY_OF_MONTH);
        year = dateToday.get(Calendar.YEAR);
        strParticipantIdList = "";

        setListeners();

        String name = "Create Meeting"; // your string here
        SpannableString s = new SpannableString(name);
        s.setSpan(new TypefaceSpan("fonts/rancho_regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.getSupportActionBar().setTitle(s);
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
    public void onColorSelected(@ColorInt int color) {
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
            i.putExtra(EditMeetingActivity.EXTRA_PARTICIPANT_LIST, strParticipantIdList);
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
                        Toast.LENGTH_LONG).show();
                super.onBackPressed();
                break;
            case R.id.action_save:
                if(checkAllInput()) {
                    showAlertCreate();
                } else
                    showAlertDialog("Please fill up all necessary details.", 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_GUESTS) {

            switch (resultCode) {
                case RESULT_OK:
                    guestNames = data.getStringExtra(AddGuestsActivity.GUEST_NAMES_TAG);
                    strParticipantIdList = data.getStringExtra(AddGuestsActivity.GUEST_LIST_TAG);
                    tvGuestList.setText(guestNames);

                    break;
                case RESULT_CANCELED:
                    break;

            }

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

                    tv_selected_location.setText(finalAddress);
                    tv_selected_location.setVisibility(View.VISIBLE);

                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
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
        dpd.setCancelColor(Color.DKGRAY);
        dpd.setOkColor(Color.DKGRAY);
        dpd.setAccentColor(meetingColor);
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

        if(tofromtime == 0)
            tpd.setTitle("Start Time");
        else
            tpd.setTitle("End Time");
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

        strTime = hr + ":" + min + " " + partOfDay;
        return strTime;
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

    public boolean isTimeValid(int fromHourOfDay, int toHourOfDay, int fromMinute, int toMinute) {
        Log.e(TAG, "Checking " + fromHourOfDay + ":" +
                fromMinute + " " + toHourOfDay + ":" + toMinute );

        if (fromHourOfDay > toHourOfDay)
            return false;
        else if (fromHourOfDay < toHourOfDay) {
            return true;
        } else if (fromHourOfDay == toHourOfDay) {
            return fromMinute <= toMinute;
        }
        return false;
    }

//////////////////////////////////////// SMS GENERATION  /////////////////////////////////

    public String generateSMS()
    {
        String sms = "CKMT [CRT] \n\n" +
                    createdMeeting.getTitle() + "\n\n" +
                    "Hi! I have scheduled a meeting with you on " +
                    Utils.dateToString(createdMeeting.getDate()) + " " +
                    Utils.dateIntegerToString(createdMeeting.getStartTime()) + " - " +
                    Utils.dateIntegerToString(createdMeeting.getEndTime()) + " at " +
                    createdMeeting.getAddress() + "\n\n" +
                    "See you there! \n\n " +
                    "__________" + //10 underscores
                    createdMeeting.getDevice_id() + "$&" +
                    createdMeeting.getTitle() + "$&" +
                    createdMeeting.getDate().toString() + "$&" +
                    createdMeeting.getStartTime() + "$&" +
                    createdMeeting.getEndTime() + "$&" +
                    createdMeeting.getAddress() + "$&" +
                    createdMeeting.getLatitude() + "$&" +
                    createdMeeting.getLongitude() + "$&" +
                    createdMeeting.getStringParticipants() + "$&" +
                    HOSTNAME + "$&" +  /*Change to shared preference shiz*/
                    createdMeeting.getColor();

                if(!(etMeetingDescription.getText().toString().isEmpty()))
                    sms = sms + "$&" + createdMeeting.getDescription();

        sms += "&&&";

        Log.e(TAG, "CKMT CODE = " + Utils.getCKMTcode(sms));

        return sms;
    }


//////////////////////////////////////// CREATING MEETING /////////////////////////////////

    private void showAlertCreate() {
        String message = "You are about to send " + getParticipantListID().size()
                + " SMS. Do you want to proceed creating a meeting?";

        String title = "Create meeting?";

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
                        createMeeting();

                        // send sms to participants
                        String msg = generateSMS();
                        Log.e(TAG, "SMS = \n" + msg);
                        Utils.sendSMS(getBaseContext(), msg, createdMeeting.getParticipantList());

                        // tell user
                        Toast.makeText(getBaseContext(),
                                "Meeting has been created!", Toast.LENGTH_LONG).show();

                        Log.e(TAG, "meeting_id == " + createdMeeting.getMeeting_id());

                        // view created meeting
                        Intent intent = new Intent(CreateMeetingActivity.this,
                                ViewMeetingActivity.class);
                        intent.putExtra(Meeting.COL_MEETINGID,
                                (int) createdMeeting.getMeeting_id());
                        startActivity(intent);
                        finish();

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

    public void createMeeting() {
        Log.e(TAG, "Creating Meeting");
        Meeting meeting = new Meeting();
        String[] dateParts = tvDate.getText().toString().split("/");
        meeting.setDevice_id(imeiID);
        meeting.setTitle(etMeetingName.getText().toString());
        meeting.setDescription(etMeetingDescription.getText().toString());
        meeting.setDate(new Date(Integer.parseInt(dateParts[0]) - 1,
                Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[2])));
        meeting.setStartTime(Utils.dateStringToInteger(tvTimefrom.getText().toString()));
        meeting.setEndTime(Utils.dateStringToInteger(tvTimeto.getText().toString()));
        meeting.setHostName(HOSTNAME); //TODO: CHANGE TO PREFERENCE
        meeting.setIsHost(true);
        meeting.setColor(meetingColor);
        meeting.setAddress(place.getAddress() + "");
        meeting.setLongitude(place.getLatLng().longitude);
        meeting.setLatitude(place.getLatLng().latitude);
        meeting.setStringParticipants(guestNames);
        meeting.setParticipantList(getParticipantListID());

        long meeting_id = MeetingService.createMeeting(getBaseContext(), meeting, true);
        createdMeeting = MeetingService.getMeeting(getBaseContext(), (int) meeting_id);
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

    private List<String> getParticipantListID()
    {
        List<String> partcipantListID = new ArrayList<>();

        String [] idArray = strParticipantIdList.split(", ");
        Collections.addAll(partcipantListID, idArray);
        return partcipantListID;
    }

}
