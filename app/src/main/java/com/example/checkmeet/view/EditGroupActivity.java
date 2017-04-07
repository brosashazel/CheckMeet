package com.example.checkmeet.view;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.checkmeet.R;
import com.example.checkmeet.adapter.ContactItemClickCallback;
import com.example.checkmeet.adapter.ContactListsAdapter;
import com.example.checkmeet.adapter.GroupParticipantsAdapter;
import com.example.checkmeet.model.Contact;
import com.example.checkmeet.model.Group;
import com.example.checkmeet.service.ContactService;
import com.example.checkmeet.service.GroupService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EditGroupActivity extends AppCompatActivity implements ContactItemClickCallback {

    private static final String TAG = EditGroupActivity.class.getSimpleName();

    private EditText etName;
    private RecyclerView recView;
    private GroupParticipantsAdapter adapter;

    private List<Contact> contactList;
    private List<Integer> colors;

    private List<String> participant_id_list;

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        getSupportActionBar().setTitle("Edit Group");

        // get group_id from intent
        int group_id = getIntent().getIntExtra(Group.COL_GROUPID, -1);
        group = GroupService.getGroup(getBaseContext(), group_id);

        // set up data
        contactList = new ArrayList<>();
        participant_id_list = new ArrayList<>();
        colors = new ArrayList<>();
        colors.add(Color.parseColor("#ce93d8"));
        colors.add(Color.parseColor("#90caf9"));
        colors.add(Color.parseColor("#ffcc80"));
        colors.add(Color.parseColor("#a5d6a7"));
        colors.add(Color.parseColor("#ffd54f"));

        initData();

        // set up views
        recView = (RecyclerView) findViewById(R.id.rv_members);
        etName = (EditText) findViewById(R.id.et_group_name);

        adapter = new GroupParticipantsAdapter(contactList, this);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(adapter);
        adapter.setContactItemClickCallback(this);

        etName.setText(group.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cancel_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_cancel:
                showAlert();
                break;
            case R.id.action_save:
                save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {

        // get all contacts
        contactList = ContactService.getAllContacts(getBaseContext(), colors);

        // filter contacts
        for(int i = 0; i < contactList.size(); i ++) {
            if(isParticipant(contactList.get(i).getContactID())) {
                contactList.get(i).setSelected(true);
                participant_id_list.add(contactList.get(i).getContactID());
            }
        }
    }

    private boolean isParticipant(String contactID) {
        List<String> memberList = group.getMemberList();

        for(int i = 0; i < memberList.size(); i ++) {
            if(memberList.get(i).equals(contactID))
                return true;
        }

        return false;
    }

    private void showAlert() {
        // show alert
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Save your changes?");

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        save();
                    }
                })
                .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        finish();
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

    private void save() {

        String group_name = etName.getText().toString();

        if(group_name.isEmpty()) {
            etName.setError("Please input group name!");
        } else if(participant_id_list.size() <= 1) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Sorry! Your group must have more than one member.");
            alert.setPositiveButton("OK", null);
            alert.show();
        } else {

            // set new name
            group.setName(group_name);

            // set new list
            group.setMemberList(participant_id_list);

            // update db
            int result = GroupService.updateGroup(getBaseContext(), group);

            if(result != -1) {
                Toast.makeText(EditGroupActivity.this,
                        "Saving changes...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditGroupActivity.this,
                        "Oops! Something went wrong!", Toast.LENGTH_SHORT).show();
            }

            finish();
        }

    }

    @Override
    public void onItemClick(int p) {
        Contact c = contactList.get(p);

        // update data
        c.setSelected(!c.isSelected());

        if(c.isSelected()) {
            // add to participant_id_list
            participant_id_list.add(c.getContactID());
        } else {
            // remove from participant_id_list
            participant_id_list.remove(c.getContactID());
        }

        // pass new data to adapter and update
        adapter.setItems(contactList);
        adapter.notifyItemChanged(p);
    }
}
