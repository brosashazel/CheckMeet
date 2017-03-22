//package com.example.checkmeet.service;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.example.checkmeet.db.DatabaseHelper;
//import com.example.checkmeet.model.Group;
//import com.example.checkmeet.model.Participant;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by victo on 3/18/2017.
// */
//
//public class GroupService {
//
//    public static long createGroup(Context context, Group group) {
//        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
//
//        ///// group table /////
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(Group.COL_NAME, group.getName());
//
//        long result = db.insert(Group.TABLE_NAME, null, contentValues);
//
//        ///// group_participant table /////
//        List<Participant> memberList = group.getMemberList();
//        for(int i = 0; i < memberList.size(); i ++) {
//            contentValues = new ContentValues();
//            contentValues.put(Group.GROUP_PARTICIPANT_COL_GROUPID, result);
//            contentValues.put(Group.GROUP_PARTICIPANT_COL_PARTICIPANTID,
//                    memberList.get(0).getParticipant_id());
//
//            db.insert(Group.TABLE_NAME_GROUP_PARTICIPANT, null, contentValues);
//        }
//
//        db.close();
//
//        return result;
//    }
//
//    public static int updateGroup(Context context, Group group) {
//        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
//
//        String selection = Group.COL_GROUPID + " = ?";
//        String[] selectionArgs = {group.getGroup_id() + ""};
//
//        ///// group table /////
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(Group.COL_NAME, group.getName());
//
//        int result = db.update(Group.TABLE_NAME, contentValues, selection, selectionArgs);
//
//        ///// group_participant table /////
//        updateGroupParticipants(db, group.getMemberList(), group.getGroup_id());
//
//        db.close();
//
//        return result;
//    }
//
//    private static void updateGroupParticipants(SQLiteDatabase db,
//                                              List<Participant> participantList,
//                                              int group_id) {
//
//        // remove all participants in group
//        String selection = Group.GROUP_PARTICIPANT_COL_GROUPID + " = ?";
//        String[] selectionArgs = {group_id + ""};
//
//        db.delete(Group.TABLE_NAME_GROUP_PARTICIPANT, selection, selectionArgs);
//
//        // add new participants
//        ContentValues contentValues;
//        for(int i = 0; i < participantList.size(); i ++) {
//            contentValues = new ContentValues();
//            contentValues.put(Group.COL_GROUPID, group_id);
//            contentValues.put(
//                    Participant.COL_PARTICIPANTID, participantList.get(0).getParticipant_id());
//
//            db.insert(Group.TABLE_NAME_GROUP_PARTICIPANT, null, contentValues);
//        }
//    }
//
//    public static int deleteGroup(Context context, int group_id) {
//        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
//
//        String selection = Group.COL_GROUPID + " = ?";
//        String[] selectionArgs = {group_id + ""};
//
//        ///// group table /////
//        int result = db.delete(Group.TABLE_NAME, selection, selectionArgs);
//
//        ///// group_participant table /////
//        selection = Group.GROUP_PARTICIPANT_COL_GROUPID + " = ?";
//        selectionArgs = new String[] {group_id + ""};
//
//        db.delete(Group.TABLE_NAME_GROUP_PARTICIPANT, selection, selectionArgs);
//
//        db.close();
//
//        return result;
//    }
//
//    public static Cursor getAllGroups(Context context) {
//        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();
//
//        // sort by name
//        String orderBy = Group.COL_NAME;
//
//        return db.query(Group.TABLE_NAME, null, null, null, null, null, orderBy);
//    }
//
//    public static Group getGroup(Context context, int group_id) {
//        Group group = null;
//
//        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();
//
//        String selection = Group.COL_GROUPID + " = ?";
//        String[] selectionArgs = {group_id + ""};
//
//        Cursor cursor =
//                db.query(Group.TABLE_NAME, null, selection, selectionArgs, null, null, null);
//
//        if(cursor.moveToFirst()) {
//            // group
//            group = new Group();
//            group.setGroup_id(cursor.getInt(cursor.getColumnIndex(Group.COL_GROUPID)));
//            group.setName(cursor.getString(cursor.getColumnIndex(Group.COL_NAME)));
//        }
//
//        cursor.close();
//
//        if(group != null) {
//            selection = Group.GROUP_PARTICIPANT_COL_GROUPID + " = ?";
//
//            cursor = db.query(Group.TABLE_NAME_GROUP_PARTICIPANT, null,
//                    selection, selectionArgs, null, null, null);
//
//            List<Participant> participantList = new ArrayList<>();
//            Participant p;
//            while(cursor.moveToNext()) {
//                p = new Participant();
//                p.setParticipant_id(
//                        cursor.getInt(cursor.getColumnIndex(Participant.COL_PARTICIPANTID)));
//
//                participantList.add(p);
//            }
//
//            group.setMemberList(participantList);
//
//            cursor.close();
//        }
//
//        db.close();
//
//        return group;
//
//    }
//
//}
