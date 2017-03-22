package com.example.checkmeet.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.checkmeet.db.DatabaseHelper;
import com.example.checkmeet.model.Notes;

/**
 * Created by victo on 3/19/2017.
 */

public class NotesService {

    public static long createNote(Context context, Notes notes) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Notes.COL_ID, notes.getNote_id());
        contentValues.put(Notes.COL_NOTE, notes.getNotes());

        long result = db.insert(Notes.TABLE_NAME, null, contentValues);

        db.close();

        return result;
    }

    public static int updateNote(Context context, Notes notes) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        String selection = Notes.COL_ID + " = ?";
        String[] selectionArgs = {notes.getNote_id() + ""};

        ContentValues contentValues = new ContentValues();
        contentValues.put(Notes.COL_NOTE, notes.getNotes());

        int result = db.update(Notes.TABLE_NAME, contentValues, selection, selectionArgs);

        db.close();

        return result;
    }

    /*
     * DELETE NOTES will be taken care of when MEETING is deleted.
     * Code is in MeetingService.deleteMeeting
     */

    public static Notes getNote(Context context, int id) {
        Notes notes = null;

        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();

        String selection = Notes.COL_ID + " = ?";
        String[] selectionArgs = {id + ""};

        Cursor cursor = db.query(Notes.TABLE_NAME, null,
                selection, selectionArgs, null, null, null);

        if(cursor.moveToFirst()) {
            notes = new Notes();
            notes.setNote_id(cursor.getInt(cursor.getColumnIndex(Notes.COL_ID)));
            notes.setNotes(cursor.getString(cursor.getColumnIndex(Notes.COL_NOTE)));
        }

        cursor.close();
        db.close();

        return notes;
    }

}
