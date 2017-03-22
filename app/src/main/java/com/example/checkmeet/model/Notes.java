package com.example.checkmeet.model;

/**
 * Created by victo on 3/19/2017.
 */

public class Notes {

    public static final String TABLE_NAME = "notes";
    public static final String COL_ID = Meeting.COL_MEETINGID;
    public static final String COL_NOTE = "note";

    private int note_id;
    private String note;

    public int getNote_id() {
        return note_id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
    }

    public String getNotes() {
        return note;
    }

    public void setNotes(String note) {
        this.note = note;
    }
}
