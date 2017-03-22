package com.example.checkmeet.model;

/**
 * Created by victo on 3/18/2017.
 */

public class Participant {

    // db attributes
    public static final String TABLE_NAME = "participant";
    public static final String COL_PARTICIPANTID = "_id";

    private int participant_id;

    public Participant() {}

    public Participant(int participant_id) {
        this.participant_id = participant_id;
    }

    public int getParticipant_id() {
        return participant_id;
    }

    public void setParticipant_id(int participant_id) {
        this.participant_id = participant_id;
    }
}
