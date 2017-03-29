package com.example.checkmeet.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.checkmeet.db.DatabaseHelper;
import com.example.checkmeet.model.Group;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by victo on 3/18/2017.
 */

public class GroupService {

    public static long createGroup(Context context, Group group) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        ///// group table /////
        ContentValues contentValues = new ContentValues();
        contentValues.put(Group.COL_NAME, group.getName());

        long result = db.insert(Group.TABLE_NAME, null, contentValues);

        ///// group_participant table /////
        List<String> memberList = group.getMemberList();
        for(int i = 0; i < memberList.size(); i ++) {
            contentValues = new ContentValues();
            contentValues.put(Group.GROUP_PARTICIPANT_COL_GROUPID, result);
            contentValues.put(Group.GROUP_PARTICIPANT_COL_PARTICIPANTID,
                    memberList.get(i));

            db.insert(Group.TABLE_NAME_GROUP_PARTICIPANT, null, contentValues);
        }

        db.close();

        return result;
    }

    public static int updateGroup(Context context, Group group) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        String selection = Group.COL_GROUPID + " = ?";
        String[] selectionArgs = {group.getId() + ""};

        ///// group table /////
        ContentValues contentValues = new ContentValues();
        contentValues.put(Group.COL_NAME, group.getName());

        int result = db.update(Group.TABLE_NAME, contentValues, selection, selectionArgs);

        ///// group_participant table /////
        updateGroupParticipants(db, group.getMemberList(), group.getId());

        db.close();

        return result;
    }

    private static void updateGroupParticipants(SQLiteDatabase db,
                                              List<String> participantList,
                                              int group_id) {

        // remove all participants in group
        String selection = Group.GROUP_PARTICIPANT_COL_GROUPID + " = ?";
        String[] selectionArgs = {group_id + ""};

        Log.e("updateGroupParticipants", "group_id = " + group_id);

        long result;

        db.delete(Group.TABLE_NAME_GROUP_PARTICIPANT, selection, selectionArgs);

        // add new participants
        ContentValues contentValues;
        for(int i = 0; i < participantList.size(); i ++) {
            contentValues = new ContentValues();
            contentValues.put(Group.GROUP_PARTICIPANT_COL_GROUPID, group_id);
            contentValues.put(
                    Group.GROUP_PARTICIPANT_COL_PARTICIPANTID, participantList.get(i));

            result = db.insert(Group.TABLE_NAME_GROUP_PARTICIPANT, null, contentValues);
            Log.e("inserting", result + "");
        }
    }

    public static int deleteGroup(Context context, int group_id) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        String selection = Group.COL_GROUPID + " = ?";
        String[] selectionArgs = {group_id + ""};

        ///// group table /////
        int result = db.delete(Group.TABLE_NAME, selection, selectionArgs);

        ///// group_participant table /////
        selection = Group.GROUP_PARTICIPANT_COL_GROUPID + " = ?";
        selectionArgs = new String[] {group_id + ""};

        db.delete(Group.TABLE_NAME_GROUP_PARTICIPANT, selection, selectionArgs);

        db.close();

        return result;
    }

    public static Cursor getAllGroups(Context context) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();

        // id, name, and number of participants
        String[] columns = {
                Group.TABLE_NAME + "." + Group.COL_GROUPID,
                Group.COL_NAME,
                "COUNT(" + Group.GROUP_PARTICIPANT_COL_PARTICIPANTID + ")"
        };

        // table
        String table = Group.TABLE_NAME + ", " + Group.TABLE_NAME_GROUP_PARTICIPANT;

        // selection
        String selection =
                Group.TABLE_NAME + "." + Group.COL_GROUPID + " = " +
                Group.TABLE_NAME_GROUP_PARTICIPANT + "." + Group.GROUP_PARTICIPANT_COL_GROUPID;

        // group by
        String groupBy = Group.TABLE_NAME + "." + Group.COL_GROUPID;

        // sort by name
        String orderBy = Group.COL_NAME;

        return db.query(table, columns, selection, null, groupBy, null, orderBy);
    }

    public static List<Group> getAllGroupsGuests(Context context) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();

        // id, name, and number of participants
        String[] columns = {
                Group.TABLE_NAME + "." + Group.COL_GROUPID,
                Group.COL_NAME,
                "COUNT(" + Group.GROUP_PARTICIPANT_COL_PARTICIPANTID + ")"
        };

        // table
        String table = Group.TABLE_NAME + ", " + Group.TABLE_NAME_GROUP_PARTICIPANT;

        // selection
        String selection =
                Group.TABLE_NAME + "." + Group.COL_GROUPID + " = " +
                Group.TABLE_NAME_GROUP_PARTICIPANT + "." + Group.GROUP_PARTICIPANT_COL_GROUPID;

        // group by
        String groupBy = Group.TABLE_NAME + "." + Group.COL_GROUPID;

        // sort by name
        String orderBy = Group.COL_NAME;

        List<Group> groupList = new ArrayList<>();
        Group group;

        Cursor cursor = db.query(table, columns, selection, null, groupBy, null, orderBy);

        while(cursor.moveToNext()) {
            group = new Group();
            group.setId(cursor.getInt(cursor.getColumnIndex(columns[0])));
            group.setName(cursor.getString(cursor.getColumnIndex(columns[1])));
            group.setNumMembers(cursor.getInt(cursor.getColumnIndex(columns[2])));
            group.setSelected(false);

            groupList.add(group);
        }

        cursor.close();
        db.close();

        return groupList;
    }

    public static Group getGroup(Context context, long group_id) {
        Group group = null;

        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();

        String selection = Group.COL_GROUPID + " = ?";
        String[] selectionArgs = {group_id + ""};

        Cursor cursor =
                db.query(Group.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        if(cursor.moveToFirst()) {
            // group
            group = new Group();
            group.setId(cursor.getInt(cursor.getColumnIndex(Group.COL_GROUPID)));
            group.setName(cursor.getString(cursor.getColumnIndex(Group.COL_NAME)));
        }

        cursor.close();

        if(group != null) {
            selection = Group.GROUP_PARTICIPANT_COL_GROUPID + " = ?";

            cursor = db.query(Group.TABLE_NAME_GROUP_PARTICIPANT, null,
                    selection, selectionArgs, null, null, null);

            List<String> participantList = new ArrayList<>();
            String p;
            while(cursor.moveToNext()) {
                p = cursor.getString(
                        cursor.getColumnIndex(Group.GROUP_PARTICIPANT_COL_PARTICIPANTID));

                participantList.add(p);
            }

            group.setMemberList(participantList);

            cursor.close();
        }

        db.close();

        return group;

    }

}
