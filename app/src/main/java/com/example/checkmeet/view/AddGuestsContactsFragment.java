package com.example.checkmeet.view;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.checkmeet.R;
import com.example.checkmeet.adapter.ContactItemClickCallback;
import com.example.checkmeet.adapter.GroupParticipantsAdapter;
import com.example.checkmeet.model.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by victo on 3/28/2017.
 */

public class AddGuestsContactsFragment extends Fragment implements ContactItemClickCallback{

    private final String TAG = this.getClass().getSimpleName();

    private RecyclerView recView;
    private GroupParticipantsAdapter adapter;

    private List<Contact> contactList;
    private List<Integer> colors;

    public AddGuestsContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // set up data
        contactList = new ArrayList<>();
        colors = new ArrayList<>();
        colors.add(Color.parseColor("#ce93d8"));
        colors.add(Color.parseColor("#90caf9"));
        colors.add(Color.parseColor("#ffcc80"));
        colors.add(Color.parseColor("#a5d6a7"));
        colors.add(Color.parseColor("#ffd54f"));

        final String participantList =
                getArguments().getString(EditMeetingActivity.EXTRA_PARTICIPANT_LIST);

        View rootView = inflater.inflate(R.layout.fragment_add_guests_contacts, container, false);

        // set up views
        recView = (RecyclerView) rootView.findViewById(R.id.rv_add_contacts);

        // wait for 2.5 seconds to retrieve contacts from default contact app of phone
        final ProgressBar progressBar =
                (ProgressBar) rootView.findViewById(R.id.progressbar_contacts);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();

                if(participantList != null) {
                    parseParticipantListArgument(participantList);
                }

                adapter = new GroupParticipantsAdapter(contactList, getContext());
                recView.setLayoutManager(new LinearLayoutManager(getContext()));
                recView.setAdapter(adapter);
                adapter.setContactItemClickCallback(AddGuestsContactsFragment.this);

                progressBar.setVisibility(View.GONE);
                recView.setVisibility(View.VISIBLE);
            }
        }, 2500);

        return rootView;
    }

    private void initData() {
        contactList.clear();

        Random rand = new Random();
        Contact c;

        ContentResolver cr = getContext().getContentResolver();
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

                        Log.e(TAG, "Name: " + name
                                + ", Phone No: " + phoneNo + "\t\tID: " + id);

                        c.setNumber(phoneNo);

                        pCur.close();
                    }
                }

                contactList.add(c);
            }

            cur.close();
        }
    }

    private void parseParticipantListArgument(String participantList) {
        if(!participantList.isEmpty()) {
            String[] tokens = participantList.split(",");

            for(int i = 0; i < tokens.length; i ++) {
                searchContactIfParticipant(tokens[0]);
            }
        }
    }

    private void searchContactIfParticipant(String id) {
        boolean found = false;
        int i = 0;

        while(i < contactList.size() && !found) {
            if(contactList.get(i).getContactID().equals(id))
                contactList.get(i).setSelected(true);
        }
    }

    @Override
    public void onItemClick(int p) {

        Contact c = contactList.get(p);

        // update data
        c.setSelected(!c.isSelected());

        if(c.isSelected()) {
            // add to participant_id_list
            ((AddGuestsActivity)getActivity())
                    .addParticipantID(contactList.get(p).getContactID());
        } else {
            // remove from participant_id_list
            ((AddGuestsActivity)getActivity())
                    .removeParticipantID(contactList.get(p).getContactID());
        }

        // pass new data to adapter and update
        adapter.setItems(contactList);
        adapter.notifyItemChanged(p);
    }
}