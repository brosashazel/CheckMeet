package com.example.checkmeet.view;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.checkmeet.R;
import com.example.checkmeet.adapter.ContactListsAdapter;
import com.example.checkmeet.model.Contact;
import com.example.checkmeet.model.Group;
import com.example.checkmeet.service.ContactService;
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
    private List<String> participant_id_list;
    private List<Integer> colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        // get id from intent
        long group_id = getIntent().getLongExtra(Group.COL_GROUPID, -1);

        // get group from db
        if(group_id != -1) {
            group = GroupService.getGroup(getBaseContext(), group_id);
        }

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
    protected void onResume() {
        super.onResume();

        // get group from db
        group = GroupService.getGroup(getBaseContext(), group.getId());

        initData();
        adapter.setItems(contactList);
        adapter.notifyDataSetChanged();

        tv_group_name.setText(group.getName());
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
                Intent intent_edit = new Intent(this, EditGroupActivity.class);
                intent_edit.putExtra(Group.COL_GROUPID, group.getId());
                startActivity(intent_edit);
                break;
            case R.id.action_delete:
                showAlert();
                break;
            default:
                super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initData() {

        contactList = new ArrayList<>();
        participant_id_list = new ArrayList<>();
        colors = new ArrayList<>();
        colors.add(Color.parseColor("#ce93d8"));
        colors.add(Color.parseColor("#90caf9"));
        colors.add(Color.parseColor("#ffcc80"));
        colors.add(Color.parseColor("#a5d6a7"));
        colors.add(Color.parseColor("#ffd54f"));

        // get details of participants from default contacts app
        Random rand = new Random();
        int counter = 0;

        Contact c;

        for(int i = 0; i < group.getMemberList().size(); i++) {
            c = ContactService.searchContact(getBaseContext(), group.getMemberList().get(i),
                    colors.get(rand.nextInt(5)));

            if(c == null) {
                counter++;
            } else {
                contactList.add(c);
                participant_id_list.add(c.getContactID());
            }
        }

        if(counter > 0) {
            showErrorContacts();

            // must update db
            group.setMemberList(participant_id_list);
            GroupService.updateGroup(getBaseContext(), group);
        }

    }

    private void showAlert() {
        // show alert
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Are you sure you want to delete " + group.getName() + "?");

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // delete
                        int result = GroupService.deleteGroup(getBaseContext(), group.getId());

                        if(result != -1) {
                            Toast.makeText(ViewGroupActivity.this,
                                    "Deleting group...", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ViewGroupActivity.this,
                                    "Oops! Something went wrong!", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void showErrorContacts() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Some of the contacts have been deleted.");
        alert.setPositiveButton("OK", null);
        alert.show();
    }
}
