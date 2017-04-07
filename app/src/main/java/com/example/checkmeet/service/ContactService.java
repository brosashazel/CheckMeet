package com.example.checkmeet.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.checkmeet.model.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by victo on 4/7/2017.
 */

public class ContactService {

    private static final String TAG = "ContactService";

    public static List<Contact> getAllContacts(Context context, List<Integer> colors) {

        List<Contact> contactList = new ArrayList<>();

        Random rand = new Random();
        Contact c;

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.DISPLAY_NAME);

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {

                c = new Contact();

                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                c.setContactID(id);
                c.setName(name);
                c.setColor(colors.get(rand.nextInt(5)));
                c.setSelected(false);

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    if (pCur != null && pCur.moveToFirst()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        Log.e(TAG, "Name: " + name + ", Phone No: " + phoneNo + "\t\tID: " + id);

                        c.setNumber(phoneNo);

                        pCur.close();
                    }
                }

                contactList.add(c);
            }

            cur.close();
        }

        return contactList;
    }

    public static Contact searchContact(Context context, String member_id, int color) {
        ContentResolver cr = context.getContentResolver();
        Contact c = null;

        Cursor cur = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts._ID + " = ?",
                new String[]{member_id},
                null);

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {

                c = new Contact();

                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                c.setContactID(id);
                c.setName(name);
                c.setColor(color);

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    if (pCur != null && pCur.moveToFirst()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        Log.e(TAG, "Name: " + name
                                + ", Phone No: " + phoneNo + "\t\tID: " + id);

                        c.setNumber(phoneNo);

                        pCur.close();
                    }
                }
            }

            cur.close();

        }

        return c;
    }
}
