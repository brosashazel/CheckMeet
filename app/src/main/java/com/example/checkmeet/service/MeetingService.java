package com.example.checkmeet.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.checkmeet.db.DatabaseHelper;
import com.example.checkmeet.model.Date;
import com.example.checkmeet.model.Group;
import com.example.checkmeet.model.Meeting;
import com.example.checkmeet.model.Status;
import com.example.checkmeet.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victo on 3/18/2017.
 */

public class MeetingService {

    private static final String TAG = "MeetingService";

    /**
     * Create meeting
     *
     * @param context
     * @param meeting details of the meeting must be complete
     * @param isHost  true if HOST, false if NOT HOST
     * @return
     */
    public static long createMeeting(Context context, Meeting meeting, boolean isHost) {

        // DEVICE_ID IS IMEI ID FIRST
        // IT WILL BE CONCATENATED ONCE THE MEETING ID IS GENERATED

        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        ///// meeting table /////
        ContentValues contentValues = new ContentValues();

        // title
        contentValues.put(Meeting.COL_TITLE, meeting.getTitle());

        // date and time
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
        if (meeting.getDescription() != null) {
            contentValues.put(Meeting.COL_DESCRIPTION, meeting.getDescription());
        }

        // host
        contentValues.put(Meeting.COL_HOST_NAME, meeting.getHostName());
        if (isHost) {
            contentValues.put(Meeting.COL_IS_HOST, Meeting.FLAG_IS_HOST);
        } else {
            contentValues.put(Meeting.COL_IS_HOST, Meeting.FLAG_IS_NOT_HOST);
        }

        // list of members (for view meeting)
        contentValues.put(Meeting.COL_PARTICIPANTS_STRING, meeting.getStringParticipants());

        // notes
        contentValues.put(Meeting.COL_NOTES, "");

        // status
        contentValues.put(Meeting.COL_STATUS, Status.PENDING.name());

        // store to DB, get ID
        long result = db.insert(Meeting.TABLE_NAME, null, contentValues);


        String selection = Meeting.COL_MEETINGID + " = ?";

        String[] selectionArgs = {result + ""};
        contentValues = new ContentValues();

        // device_id
        if (isHost) {
            contentValues.put(Meeting.COL_DEVICE_ID, meeting.getDevice_id() + "-" + result);
            Log.e("createMeeting", "device id == " + meeting.getDevice_id() + "-" + result);
        } else {
            contentValues.put(Meeting.COL_DEVICE_ID, meeting.getDevice_id());
        }

        db.update(Meeting.TABLE_NAME, contentValues, selection, selectionArgs);


        // only the host will have participants linked to the contacts app of his own phone
        if (isHost) {
            ///// meeting_participants table /////
            List<String> participantList = meeting.getParticipantList();
            for (int i = 0; i < participantList.size(); i++) {
                contentValues = new ContentValues();
                contentValues.put(Meeting.MEETING_PARTICIPANTS_COL_MEETINGID, result);
                contentValues.put(Meeting.MEETING_PARTICIPANTS_COL_PARTICIPANTID,
                        participantList.get(i));

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
    public static int updateMeeting(Context context, Meeting meeting, boolean isHost) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        Log.e("DB", "EDITING THE MEETING NOW");
        String selection;
        String[] selectionArgs = new String[1];

        if(isHost) {
            selection = Meeting.COL_MEETINGID + " = ?";
            selectionArgs[0] = meeting.getMeeting_id() + "";
        } else {
            selection = Meeting.COL_DEVICE_ID + " = ?";
            selectionArgs[0] = meeting.getDevice_id() + "";
        }

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

        if (meeting.getDescription() != null) {
            contentValues.put(Meeting.COL_DESCRIPTION, meeting.getDescription());
        }

        Log.e(TAG, "id = " + meeting.getMeeting_id());
        Log.e(TAG, "title = " + meeting.getTitle());
        Log.e(TAG, "description = " + meeting.getDescription());
        Log.e(TAG, "date = " + meeting.getDate().toString());
        Log.e(TAG, "start time = " + Utils.dateIntegerToString(meeting.getStartTime()));
        Log.e(TAG, "end time = " + Utils.dateIntegerToString(meeting.getEndTime()));
        Log.e(TAG, "address = " + meeting.getAddress());
        Log.e(TAG, "latitude = " + meeting.getLatitude());
        Log.e(TAG, "longitude = " + meeting.getLongitude());
        Log.e(TAG, "participants = " + meeting.getStringParticipants());
        int result = db.update(Meeting.TABLE_NAME, contentValues, selection, selectionArgs);
        Log.e(TAG, "result = " + result);
        db.close();

        return result;
    }

    /**
     * This will be called only if user edits guests to the meeting
     * Participant String format: participant1, participant2, participant3, participant4
     */
    public static void updateMeetingParticipants(Context context,
                                                 List<String> participantList,
                                                 String participantString,
                                                 long meeting_id) {

        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        // remove all participants in meeting
        String selection = Meeting.MEETING_PARTICIPANTS_COL_MEETINGID + " = ?";
        String[] selectionArgs = {meeting_id + ""};

        db.delete(Meeting.TABLE_NAME_MEETING_PARTICIPANTS, selection, selectionArgs);

        // add new participants
        ContentValues contentValues;
        for (int i = 0; i < participantList.size(); i++) {
            contentValues = new ContentValues();
            contentValues.put(Meeting.MEETING_PARTICIPANTS_COL_MEETINGID, meeting_id);
            contentValues.put(Meeting.MEETING_PARTICIPANTS_COL_PARTICIPANTID,
                    participantList.get(i));

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

    /**
     * Update notes of a certain meeting
     */
    public static void updateNotes(Context context, long meeting_id, String notes) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        String selection = Meeting.COL_MEETINGID + " = ?";
        String[] selectionArgs = {meeting_id + ""};

        ContentValues contentValues = new ContentValues();
        contentValues.put(Meeting.COL_NOTES, notes);

        int result = db.update(Meeting.TABLE_NAME, contentValues, selection, selectionArgs);

        Log.e("MeetingService", "result = " + result);

        db.close();
    }

    /**
     * cancels the meeting
     * if HOST: find meetingID
     * if PARTICIPANT: find imei id of host
     */
    public static void cancelMeeting(Context context, String id, boolean isHost) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        String selection;
        String[] selectionArgs = {id};
        ContentValues contentValues = new ContentValues();

        if (isHost) {
            selection = Meeting.COL_MEETINGID + " = ?";
            selectionArgs[0] = Long.parseLong(id) + "";
        } else {
            selection = Meeting.COL_DEVICE_ID + " = ?";
        }

        contentValues.put(Meeting.COL_STATUS, Status.CANCELLED.name());

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

        if (cursor.moveToFirst()) {

            // meeting
            meeting = new Meeting();

            // id
            meeting.setMeeting_id(cursor.getInt(cursor.getColumnIndex(Meeting.COL_MEETINGID)));
            meeting.setDevice_id(cursor.getString(cursor.getColumnIndex(Meeting.COL_DEVICE_ID)));

            // title
            meeting.setTitle(cursor.getString(cursor.getColumnIndex(Meeting.COL_TITLE)));

            // date and time
            meeting.setDate(new Date(cursor.getLong(cursor.getColumnIndex(Meeting.COL_DATE))));
            meeting.setStartTime(cursor.getInt(cursor.getColumnIndex(Meeting.COL_TIMESTART)));
            meeting.setEndTime(cursor.getInt(cursor.getColumnIndex(Meeting.COL_TIMEEND)));

            // host
            meeting.setHostName(cursor.getString(cursor.getColumnIndex(Meeting.COL_HOST_NAME)));
            int isHost = cursor.getInt(cursor.getColumnIndex(Meeting.COL_IS_HOST));

            if (isHost == 1) {
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
            if (cursor.getString(cursor.getColumnIndex(Meeting.COL_DESCRIPTION)) != null) {
                meeting.setDescription(cursor.getString(
                        cursor.getColumnIndex(Meeting.COL_DESCRIPTION)));
            }

            // participants string
            meeting.setStringParticipants(
                    cursor.getString(cursor.getColumnIndex(Meeting.COL_PARTICIPANTS_STRING)));

            // notes
            meeting.setNotes(cursor.getString(cursor.getColumnIndex(Meeting.COL_NOTES)));

            // status
            meeting.setStatus(
                    Status.getValue(cursor.getString(cursor.getColumnIndex(Meeting.COL_STATUS))));

        }

        cursor.close();

        // get participants if host
        if (meeting != null && meeting.isHost()) {

            String tables = Meeting.TABLE_NAME_MEETING_PARTICIPANTS;

            selection =
                    Meeting.MEETING_PARTICIPANTS_COL_MEETINGID + " = ?";

            selectionArgs = new String[]{id + ""};

            cursor = db.query(tables, null, selection, selectionArgs, null, null, null)
            ;

            List<String> participantList = new ArrayList<>();
            String p;
            while (cursor.moveToNext()) {
                p = cursor.getString(
                        cursor.getColumnIndex(Meeting.MEETING_PARTICIPANTS_COL_PARTICIPANTID));

                Log.e("getMeeting", "adding participant " + p);
                participantList.add(p);
            }

            meeting.setParticipantList(participantList);

            cursor.close();

        }

        db.close();

        return meeting;
    }
}