package com.example.checkmeet.model;

import java.util.List;

/**
 * Created by victo on 2/22/2017.
 */

public class Meeting {

    // db attributes
    public static final String TABLE_NAME = "meeting";

    public static final String COL_MEETINGID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_DATE = "date";
    public static final String COL_TIMESTART = "starttime";
    public static final String COL_TIMEEND = "endtime";
    public static final String COL_HOST_NAME = "host_name";
    public static final String COL_IS_HOST = "isHost";
    public static final String COL_COLOR = "color";
    public static final String COL_ADDRESS = "address";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGITUDE = "longitude";
    public static final String COL_PARTICIPANTS_STRING = "string_participants";

    public static final String TABLE_NAME_MEETING_PARTICIPANTS = "meeting_participant";
    public static final String MEETING_PARTICIPANTS_COL_ID = "_id";
    public static final String MEETING_PARTICIPANTS_COL_MEETINGID = "meeting_id";
    public static final String MEETING_PARTICIPANTS_COL_PARTICIPANTID = "participant_id";


    public static final int FLAG_IS_HOST = 1;
    public static final int FLAG_IS_NOT_HOST = 0;


    /// final attributes
    private int meeting_id;
    private String title;
    private String description;
    private Date date;
    private int startTime;
    private int endTime;
    private int color;
    private boolean isHost;
    private String host_name;
    private List<Participant> participantList;
    private String address;
    private double latitude;
    private double longitude;
    private String stringParticipants;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(int meeting_id) {
        this.meeting_id = meeting_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public List<Participant> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(List<Participant> participantList) {
        this.participantList = participantList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStringParticipants() {
        return stringParticipants;
    }

    public void setStringParticipants(String stringParticipants) {
        this.stringParticipants = stringParticipants;
    }

    public String getHostName() {
        return host_name;
    }

    public void setHostName(String host_name) {
        this.host_name = host_name;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setIsHost(boolean host) {
        isHost = host;
    }
}
