package com.example.checkmeet.view;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.checkmeet.R;
import com.example.checkmeet.adapter.ContactListsAdapter;
import com.example.checkmeet.model.Contact;
import com.example.checkmeet.model.Group;
import com.example.checkmeet.service.GroupService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ViewGroupActivity extends AppCompatActivity {

    private static final String TAG = ViewGroupActivity.class.getSimpleName();

    private TextView tv_group_name;
    private RecyclerView rv_members;
    private ContactListsAdapter adapter;

    private Group group;
    private List<Contact> contactList;
    private List<Integer> colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        // get id from intent
        long group_id = getIntent().getLongExtra(Group.COL_GROUPID, -1);

        // get group from db
        if(group_id != -1) {
            group = GroupService.getGroup(getBaseContext(), (int) group_id);
        }

        // get contacts
        initData();

        // set up views
        rv_members = (RecyclerView) findViewById(R.id.rv_members);
        tv_group_name = (TextView) findViewById(R.id.tv_group_name);

        rv_members.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactListsAdapter(contactList, getBaseContext());
        rv_members.setAdapter(adapter);

        tv_group_name.setText(group.getName());

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_delete_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {
            case R.id.action_edit:
                break;
            case R.id.action_delete:
                break;
            default:
                super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initData() {

        contactList = new ArrayList<>();
        colors = new ArrayList<>();
        colors.add(Color.parseColor("#ce93d8"));
        colors.add(Color.parseColor("#90caf9"));
        colors.add(Color.parseColor("#ffcc80"));
        colors.add(Color.parseColor("#a5d6a7"));
        colors.add(Color.parseColor("#ffd54f"));

        // get details of participants from default contacts app
        Random rand = new Random();

        for(int i = 0; i < group.getMemberList().size(); i ++) {
            findContact(group.getMemberList().get(i), colors.get(rand.nextInt(5)));
        }

    }

    private void findContact(String member_id, int color) {

        ContentResolver cr = getBaseContext().getContentResolver();
        Contact c;

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
