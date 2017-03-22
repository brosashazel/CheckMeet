package com.example.checkmeet.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.checkmeet.db.DatabaseHelper;
import com.example.checkmeet.model.Date;
import com.example.checkmeet.model.Meeting;
import com.example.checkmeet.model.Notes;
import com.example.checkmeet.model.Participant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victo on 3/18/2017.
 */

public class MeetingService {

    /**
     * Create meeting
     * @param context
     * @param meeting details of the meeting must be complete
     * @param isHost true if HOST, false if NOT HOST
     * @return
     */
    public static long createMeeting(Context context, Meeting meeting, boolean isHost) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        ///// meeting table /////
        ContentValues contentValues = new ContentValues();
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
        if(isHost) {
            contentValues.put(Meeting.COL_IS_HOST, Meeting.FLAG_IS_HOST);
        } else {
            contentValues.put(Meeting.COL_IS_HOST, Meeting.FLAG_IS_NOT_HOST);
        }

        // list of members (for view meeting)
        contentValues.put(Meeting.COL_PARTICIPANTS_STRING, meeting.getStringParticipants());

        // store to DB, get ID
        long result = db.insert(Meeting.TABLE_NAME, null, contentValues);

        // only the host will have participants linked to the contacts app of his own phone
        if(isHost) {
            ///// meeting_participants table /////
            List<Participant> participantList = meeting.getParticipantList();
            for (int i = 0; i < participantList.size(); i ++) {
                contentValues = new ContentValues();
                contentValues.put(Meeting.MEETING_PARTICIPANTS_COL_MEETINGID, result);
                contentValues.put(Meeting.MEETING_PARTICIPANTS_COL_PARTICIPANTID,
                        participantList.get(i).getParticipant_id());

                db.insert(Meeting.TABLE_NAME_MEETING_PARTICIPANTS, null, contentValues);
            }
        }

        db.close();
        return result;
    }

    /**
     * This will only be called if host changes something about the details of the meeting
     * EXCLUDING the guest list
     */
    public static int updateMeeting(Context context, Meeting meeting) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        String selection = Meeting.COL_MEETINGID + " = ?";
        String[] selectionArgs = {meeting.getMeeting_id() + ""};

        ///// meeting table /////
        // update all attributes
        ContentValues contentValues = new ContentValues();
        contentValues.put(Meeting.COL_TITLE, meeting.getTitle());
        contentValues.put(Meeting.COL_DATE, meeting.getDate().toMilliseconds());
        contentValues.put(Meeting.COL_TIMESTART, meeting.getStartTime());
        contentValues.put(Meeting.COL_TIMEEND, meeting.getEndTime());
        contentValues.put(Meeting.COL_ADDRESS, meeting.getAddress());
        contentValues.put(Meeting.COL_LATITUDE, meeting.getLatitude());
        contentValues.put(Meeting.COL_LONGITUDE, meeting.getLongitude());
        contentValues.put(Meeting.COL_HOST_NAME, meeting.getHostName());
        contentValues.put(Meeting.COL_COLOR, meeting.getColor());

        if(meeting.getDescription() != null) {
            contentValues.put(Meeting.COL_DESCRIPTION, meeting.getDescription());
        }

        int result = db.update(Meeting.TABLE_NAME, contentValues, selection, selectionArgs);

        db.close();

        return result;
    }

    /**
     * This will be called only if user edits guests to the meeting
     * Participant String format: participant1, participant2, participant3, participant4
     */
    public static void updateMeetingParticipants(Context context,
                                                List<Participant> participantList,
                                                 String participantString,
                                                long meeting_id) {

        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        // remove all participants in meeting
        String selection = Meeting.MEETING_PARTICIPANTS_COL_MEETINGID + " = ?";
        String[] selectionArgs = {meeting_id + ""};

        db.delete(Meeting.TABLE_NAME_MEETING_PARTICIPANTS, selection, selectionArgs);

        // add new participants
        ContentValues contentValues;
        for(int i = 0; i < participantList.size(); i ++) {
            contentValues = new ContentValues();
            contentValues.put(Meeting.MEETING_PARTICIPANTS_COL_MEETINGID, meeting_id);
            contentValues.put(Meeting.MEETING_PARTICIPANTS_COL_PARTICIPANTID,
                    participantList.get(0).getParticipant_id());

            db.insert(Meeting.TABLE_NAME_MEETING_PARTICIPANTS, null, contentValues);
        }

        // update string participants in MEETING table
        selection = Meeting.COL_MEETINGID + " = ?";

        contentValues = new ContentValues();
        contentValues.put(Meeting.COL_PARTICIPANTS_STRING, participantString);
        db.update(Meeting.TABLE_NAME, contentValues, selection, selectionArgs);

        db.close();

    }

    /**
     * Must be called every time the host changes name
     */
    public static void updateHostName(Context context, String host_name) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        // only update meetings of host
        String selection = Meeting.COL_IS_HOST + " = ?";
        String[] selectionArgs = {Meeting.FLAG_IS_HOST + ""};

        ContentValues contentValues = new ContentValues();
        contentValues.put(Meeting.COL_HOST_NAME, host_name);

        db.update(Meeting.TABLE_NAME, contentValues, selection, selectionArgs);

        db.close();
    }

    public static int deleteMeeting(Context context, int id) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        String selection = Meeting.COL_MEETINGID + " = ?";
        String[] selectionArgs = {id + ""};

        ///// meeting table /////
        int result = db.delete(Meeting.TABLE_NAME, selection, selectionArgs);

        ///// meeting_participants table /////
        selection = Meeting.MEETING_PARTICIPANTS_COL_MEETINGID + " = ?";
        db.delete(Meeting.TABLE_NAME_MEETING_PARTICIPANTS, selection, selectionArgs);

        ///// notes table /////
        db.delete(Notes.TABLE_NAME, selection, selectionArgs);

        db.close();

        return result;
    }

    /**
     * for ViewAllMeetingsFragment
     */
    public static Cursor getAllMeetings(Context context) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();

        // sort by date and time
        String orderBy = Meeting.COL_DATE + ", " + Meeting.COL_TIMESTART;

        return db.query(Meeting.TABLE_NAME, null, null, null, null, null, orderBy);
    }

    /**
     * for ViewYourMeetingsFragment and ViewInvitedMeetingsFragment
     */
    public static Cursor getFilteredMeetings(Context context, int isHost) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();

        // 1 : host --- 0 : not host
        String selection = Meeting.COL_IS_HOST + " = ?";
        String[] selectionArgs = {isHost + ""};

        // sort by date and time
        String orderBy = Meeting.COL_DATE + ", " + Meeting.COL_TIMESTART;

        return db.query(Meeting.TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);

    }

    public static Meeting getMeeting(Context context, int id) {
        Meeting meeting = null;

        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();

        String selection = Meeting.TABLE_NAME + "." + Meeting.COL_MEETINGID + " = ?";
        String[] selectionArgs = {id + ""};


        ///// meeting table /////
        Cursor cursor = db.query(
                Meeting.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        if(cursor.moveToFirst()) {

            // meeting
            meeting = new Meeting();

            meeting.setMeeting_id(cursor.getInt(cursor.getColumnIndex(Meeting.COL_MEETINGID)));
            meeting.setTitle(cursor.getString(cursor.getColumnIndex(Meeting.COL_TITLE)));
            meeting.setDate(new Date(cursor.getLong(cursor.getColumnIndex(Meeting.COL_DATE))));
            meeting.setStartTime(cursor.getInt(cursor.getColumnIndex(Meeting.COL_TIMESTART)));
            meeting.setEndTime(cursor.getInt(cursor.getColumnIndex(Meeting.COL_TIMEEND)));

            // host
            meeting.setHostName(cursor.getString(cursor.getColumnIndex(Meeting.COL_HOST_NAME)));
            int isHost = cursor.getInt(cursor.getColumnIndex(Meeting.COL_IS_HOST));

            if(isHost == 1) {
                meeting.setIsHost(true);
            } else {
                meeting.setIsHost(false);
            }

            // location
            meeting.setAddress(cursor.getString(cursor.getColumnIndex(Meeting.COL_ADDRESS)));
            meeting.setLatitude(cursor.getDouble(cursor.getColumnIndex(Meeting.COL_LATITUDE)));
            meeting.setLongitude(cursor.getDouble(cursor.getColumnIndex(Meeting.COL_LONGITUDE)));

            // color
            meeting.setColor(cursor.getInt(cursor.getColumnIndex(Meeting.COL_COLOR)));

            // description is optional
            if(cursor.getString(cursor.getColumnIndex(Meeting.COL_DESCRIPTION)) != null) {
                meeting.setDescription(cursor.getString(
                        cursor.getColumnIndex(Meeting.COL_DESCRIPTION)));
            }

            meeting.setStringParticipants(
                    cursor.getString(cursor.getColumnIndex(Meeting.COL_PARTICIPANTS_STRING)));
        }

        cursor.close();

        // get participants if host
        if(meeting != null && meeting.isHost()) {

            if(meeting.getStringParticipants() == null) {
                String[] projection = {
                        Participant.COL_PARTICIPANTID
                };

                selection =
                        Meeting.TABLE_NAME + "." + Meeting.COL_MEETINGID + " = ? AND " +
                        Meeting.TABLE_NAME + "." + Meeting.COL_MEETINGID + " = " +
                        Meeting.TABLE_NAME_MEETING_PARTICIPANTS + "." + Meeting.COL_MEETINGID;

                selectionArgs = new String[]{id + ""};

                cursor = db.query(Meeting.TABLE_NAME_MEETING_PARTICIPANTS,
                        projection, selection, selectionArgs, null, null, null);

                List<Participant> participantList = new ArrayList<>();
                Participant p;
                while(cursor.moveToNext()) {
                    p = new Participant();
                    p.setParticipant_id(
                            cursor.getInt(cursor.getColumnIndex(Participant.COL_PARTICIPANTID)));

                    participantList.add(p);
                }

                meeting.setParticipantList(participantList);

                cursor.close();
            }

        }

        db.close();

        return meeting;
    }

}
