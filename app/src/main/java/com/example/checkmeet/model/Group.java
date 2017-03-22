package com.example.checkmeet.model;

import java.util.ArrayList;

/**
 * Created by Hazel on 19/03/2017.
 */

public class Group {

    // db attributes
    public static final String TABLE_NAME = "groups";

    public static final String COL_GROUPID = "_id";
    public static final String COL_NAME = "name";

    public static final String TABLE_NAME_GROUP_PARTICIPANT = "group_participant";
    public static final String GROUP_PARTICIPANT_COL_ID = "_id";
    public static final String GROUP_PARTICIPANT_COL_GROUPID = "group_id";
    public static final String GROUP_PARTICIPANT_COL_PARTICIPANTID = "participant_id";


    private int id;
    private String name;
    private ArrayList<Contact> participants;

    public Group() {
    }

    public Group(int id, String name, ArrayList<Contact> participants) {
        this.id = id;
        this.name = name;
        this.participants = participants;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Contact> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Contact> participants) {
        this.participants = participants;
    }
}
