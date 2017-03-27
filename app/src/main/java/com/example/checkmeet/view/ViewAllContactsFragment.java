package com.example.checkmeet.view;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checkmeet.R;
import com.example.checkmeet.adapter.ContactListsAdapter;
import com.example.checkmeet.model.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.view.View.GONE;

/**
 * Created by Hazel on 19/03/2017.
 */

public class ViewAllContactsFragment extends ViewContactsBaseFragment {
    private static final String TAG = "ViewAllContactsFragment";

    List<Integer> colors;

    public ViewAllContactsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_all_contacts, container, false);

        // set up views
        recView = (RecyclerView) rootView.findViewById(R.id.rv_all_contacts);
        swipeRefreshLayout =
                (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout_all);
        tv_no_contacts_found = (TextView) rootView.findViewById(R.id.tv_no_contacts_found);


        // set up data
        contactList = new ArrayList<>();
        colors = new ArrayList<>();
        colors.add(Color.parseColor("#ce93d8"));
        colors.add(Color.parseColor("#90caf9"));
        colors.add(Color.parseColor("#ffcc80"));
        colors.add(Color.parseColor("#a5d6a7"));
        colors.add(Color.parseColor("#ffd54f"));


        // wait for 2.5 seconds to retrieve contacts from default contact app of phone
        final ProgressBar progressBar =
                (ProgressBar) rootView.findViewById(R.id.progressbar_contacts);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();

                adapter = new ContactListsAdapter(contactList, getContext());
                recView.setLayoutManager(new LinearLayoutManager(getContext()));
                recView.setAdapter(adapter);

                progressBar.setVisibility(GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        }, 2500);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }

            void refreshItems() {

                // load the whole set of data
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();

                        // Load complete
                        onItemsLoadComplete();
                    }
                }, 2500);
            }

            void onItemsLoadComplete() {
                // Update the adapter and notify data set changed
                adapter.setItems(contactList);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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

                c.setName(name);
                c.setColor(colors.get(rand.nextInt(5)));

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

                        Log.e("ViewAllContactsFragment", "Name: " + name
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


}
