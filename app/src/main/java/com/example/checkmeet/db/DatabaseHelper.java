package com.example.checkmeet.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Date;
import com.example.checkmeet.model.Group;
import com.example.checkmeet.model.Meeting;
import com.example.checkmeet.model.Notes;
import com.example.checkmeet.model.Participant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victo on 3/18/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static DatabaseHelper instance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "checkmeet.db";

    ///// SQL CREATE TABLES /////

    // meeting
    private static final String SQL_CREATE_MEETING_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Meeting.TABLE_NAME + " ( " +
                    Meeting.COL_MEETINGID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Meeting.COL_TITLE + " TEXT NOT NULL, " +
                    Meeting.COL_DESCRIPTION + " TEXT, " +
                    Meeting.COL_DATE + " INTEGER NOT NULL, " +
                    Meeting.COL_TIMESTART + " INTEGER NOT NULL, " +
                    Meeting.COL_TIMEEND + " INTEGER NOT NULL, " +
                    Meeting.COL_HOST_NAME + " TEXT NOT NULL, " +
                    Meeting.COL_IS_HOST + " INTEGER NOT NULL, " +
                    Meeting.COL_COLOR + " INTEGER NOT NULL, " +
                    Meeting.COL_ADDRESS + " TEXT NOT NULL, " +
                    Meeting.COL_LATITUDE + " REAL NOT NULL, " +
                    Meeting.COL_LONGITUDE + " REAL NOT NULL, " +
                    Meeting.COL_PARTICIPANTS_STRING + " TEXT NOT NULL);";

    // notes
    private static final String SQL_CREATE_NOTES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Notes.TABLE_NAME + " ( " +
                    Notes.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Notes.COL_NOTE + " TEXT NOT NULL);";

    // participant
    private static final String SQL_CREATE_PARTICIPANT_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Participant.TABLE_NAME + " ( " +
                    Participant.COL_PARTICIPANTID + " INTEGER PRIMARY KEY);";

    // group
    private static final String SQL_CREATE_GROUP_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Group.TABLE_NAME + " ( " +
                    Group.COL_GROUPID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Group.COL_NAME + " TEXT NOT NULL);";

    // meeting_participant
    private static final String SQL_CREATE_MEETING_PARTICIPANT_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Meeting.TABLE_NAME_MEETING_PARTICIPANTS + " ( " +
                    Meeting.MEETING_PARTICIPANTS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Meeting.MEETING_PARTICIPANTS_COL_MEETINGID + " INTEGER NOT NULL, " +
                    Meeting.MEETING_PARTICIPANTS_COL_PARTICIPANTID + " INTEGER NOT NULL);";

    // group_participant
    private static final String SQL_CREATE_GROUP_PARTICIPANT_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Group.TABLE_NAME_GROUP_PARTICIPANT + " ( " +
                    Group.GROUP_PARTICIPANT_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Group.GROUP_PARTICIPANT_COL_GROUPID + " INTEGER NOT NULL, " +
                    Group.GROUP_PARTICIPANT_COL_PARTICIPANTID + " INTEGER NOT NULL);";

    ///// SQL DELETE TABLES /////
    private static final String SQL_DELETE_MEETING_TABLE =
            "DROP TABLE IF EXISTS " + Meeting.TABLE_NAME;

    private static final String SQL_DELETE_NOTES_TABLE =
            "DROP TABLE IF EXISTS " + Notes.TABLE_NAME;

    private static final String SQL_DELETE_PARTICIPANT_TABLE =
            "DROP TABLE IF EXISTS " + Participant.TABLE_NAME;

    private static final String SQL_DELETE_GROUP_TABLE =
            "DROP TABLE IF EXISTS " + Group.TABLE_NAME;

    private static final String SQL_DELETE_MEETING_PARTICIPANT_TABLE =
            "DROP TABLE IF EXISTS " + Meeting.TABLE_NAME_MEETING_PARTICIPANTS;

    private static final String SQL_DELETE_GROUP_PARTICIPANT_TABLE =
            "DROP TABLE IF EXISTS " + Group.TABLE_NAME_GROUP_PARTICIPANT;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseHelper(context);
        }

        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MEETING_TABLE);
        db.execSQL(SQL_CREATE_NOTES_TABLE);
        db.execSQL(SQL_CREATE_PARTICIPANT_TABLE);
        db.execSQL(SQL_CREATE_GROUP_TABLE);
        db.execSQL(SQL_CREATE_MEETING_PARTICIPANT_TABLE);
        db.execSQL(SQL_CREATE_GROUP_PARTICIPANT_TABLE);

        initData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_MEETING_TABLE);
        db.execSQL(SQL_DELETE_NOTES_TABLE);
        db.execSQL(SQL_DELETE_PARTICIPANT_TABLE);
        db.execSQL(SQL_DELETE_GROUP_TABLE);
        db.execSQL(SQL_DELETE_MEETING_PARTICIPANT_TABLE);
        db.execSQL(SQL_DELETE_GROUP_PARTICIPANT_TABLE);
        onCreate(db);
    }

    private void initData(SQLiteDatabase db) {
        List<Meeting> meetingList = new ArrayList<>();
        Meeting meeting;
        Date date;
        String participants = "Hazel Brosas, Nicolle Magpale, Mavic Reccion";

        // MEETING 1
        meeting = new Meeting();
        meeting.setIsHost(false);
        meeting.setTitle("Jollibee Delivery LOL");
        meeting.setDescription("Hi! Welcome to Jollibee! How may I take your order?");
        date = new Date(2, 30, 2017);
        meeting.setDate(date);
        meeting.setStartTime(1800);
        meeting.setEndTime(2000);
        meeting.setAddress("Gokongwei Hall, Taft Avenue, Manila, NCR");
        meeting.setLatitude(14.5662743);
        meeting.setLongitude(120.9907974);
        meeting.setColor(Color.parseColor("#ce93d8"));
        meeting.setHostName("Courtney Ngo");
        meeting.setStringParticipants(participants);

        meetingList.add(meeting);

        // MEETING 2
        meeting = new Meeting();
        meeting.setIsHost(false);
        meeting.setTitle("Fifth Harmony Live in Manila");
        meeting.setDescription("You don't gotta go to work, work, work, work, work, work, work\n" +
                "But you gotta put in work, work, work, work, work, work, work");
        date = new Date(3, 4, 2017);
        meeting.setDate(date);
        meeting.setStartTime(1000);
        meeting.setEndTime(1530);
        meeting.setAddress("Mall of Asia Arena, Mall of Asia Complex, Pasay, Metro Manila");
        meeting.setLatitude(14.5321663);
        meeting.setLongitude(120.9815684);
        meeting.setColor(Color.parseColor("#90caf9"));
        meeting.setHostName("Briane Samson");
        meeting.setStringParticipants(participants);

        meetingList.add(meeting);

        // MEETING 3
        meeting = new Meeting();
        meeting.setIsHost(false);
        meeting.setTitle("Food Trip, Baby!");
        meeting.setDescription("Are you hungry? Me too! Let's eat! :D");
        date = new Date(6, 15, 2017);
        meeting.setDate(date);
        meeting.setStartTime(800);
        meeting.setEndTime(1200);
        meeting.setAddress("Food Trip, 1872 Velasquez St, Tondo 95, Maynila, 1012 Kalakhang Maynila");
        meeting.setLatitude(14.6188292);
        meeting.setLongitude(120.9634773);
        meeting.setColor(Color.parseColor("#ffcc80"));
        meeting.setHostName("Roger Uy");
        meeting.setStringParticipants(participants);

        meetingList.add(meeting);

        // MEETING 4
        meeting = new Meeting();
        meeting.setIsHost(false);
        meeting.setTitle("Birthday ni Hazel");
        meeting.setDescription("Happy birthday to me~ Happy birthday to me~~ " +
                "Happy birthday happyy birthdaaaayy~ Happy birthday to meeeee~");
        date = new Date(1, 1, 2017);
        meeting.setDate(date);
        meeting.setStartTime(1800);
        meeting.setEndTime(2000);
        meeting.setAddress("Shangri-La at the Fort, Manila, 30th Street, Taguig, NCR");
        meeting.setLatitude(14.5524012);
        meeting.setLongitude(121.0450023);
        meeting.setColor(Color.parseColor("#a5d6a7"));
        meeting.setHostName("Tessie Limoanco");
        meeting.setStringParticipants(participants);

        meetingList.add(meeting);

        // MEETING 5
        meeting = new Meeting();
        meeting.setIsHost(false);
        meeting.setTitle("SPRINT meeting");
        meeting.setDescription("Officer's meeting only sabi ni madam");
        date = new Date(0, 1, 2017);
        meeting.setDate(date);
        meeting.setStartTime(1300);
        meeting.setEndTime(1800);
        meeting.setAddress("Gokongwei Hall, Taft Avenue, Manila, NCR");
        meeting.setLatitude(14.5662743);
        meeting.setLongitude(120.9907974);
        meeting.setColor(Color.parseColor("#ffd54f"));
        meeting.setHostName("Doc Mac");
        meeting.setStringParticipants(participants);

        meetingList.add(meeting);

        ContentValues contentValues;

        for(int i = 0; i < meetingList.size(); i ++) {
            meeting = meetingList.get(i);
            contentValues = new ContentValues();

            contentValues.put(Meeting.COL_TITLE, meeting.getTitle());
            contentValues.put(Meeting.COL_DATE, meeting.getDate().toMilliseconds());
            contentValues.put(Meeting.COL_TIMESTART, meeting.getStartTime());
            contentValues.put(Meeting.COL_TIMEEND, meeting.getEndTime());

            // location
            contentValues.put(Meeting.COL_ADDRESS, meeting.getAddress());
            contentValues.put(Meeting.COL_LATITUDE, meeting.getLatitude());
            contentValues.put(Meeting.COL_LONGITUDE, meeting.getLongitude());

            // color
            contentValues.put(Meeting.COL_COLOR, meeting.getColor());

            // description is optional
            if(meeting.getDescription() != null) {
                contentValues.put(Meeting.COL_DESCRIPTION, meeting.getDescription());
            }

            // host
            contentValues.put(Meeting.COL_HOST_NAME, meeting.getHostName());
            if(meeting.isHost()) {
                contentValues.put(Meeting.COL_IS_HOST, Meeting.FLAG_IS_HOST);
            } else {
                contentValues.put(Meeting.COL_IS_HOST, Meeting.FLAG_IS_NOT_HOST);
            }

            // list of members (for view meeting)
            contentValues.put(Meeting.COL_PARTICIPANTS_STRING, meeting.getStringParticipants());

            // store to DB
            db.insert(Meeting.TABLE_NAME, null, contentValues);

            Log.e("DATABASE", "inserted --- " + meeting.getTitle());
        }
    }
}
