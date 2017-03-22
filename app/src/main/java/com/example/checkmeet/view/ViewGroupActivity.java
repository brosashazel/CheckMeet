package com.example.checkmeet.view;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.checkmeet.R;
import com.example.checkmeet.adapter.GroupParticipantsAdapter;
import com.example.checkmeet.model.Contact;
import com.example.checkmeet.model.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ViewGroupActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "groupid";

    RecyclerView rvParticipants;
    ImageView btnEdit, btnDelete, btnBack;
    TextView tvName, tvNumParticipants;
    GroupParticipantsAdapter groupParticipantsAdapter;
    ArrayList<Group> groupList;
    ArrayList<Contact> listParticipants;
    int groupId;

    public static final String EXTRA_MEETING_TITLE = "EXTRA_MEETING_TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);
        int groupId = getIntent().getIntExtra(ViewGroupActivity.EXTRA_GROUP_ID, -1);
        initData(); // for testing purposes only

        rvParticipants = (RecyclerView) findViewById(R.id.rv_participants);
        btnEdit = (ImageView) findViewById(R.id.btn_edit);
        btnDelete = (ImageView) findViewById(R.id.btn_delete);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        tvName = (TextView) findViewById(R.id.tv_group_name);
        tvNumParticipants = (TextView) findViewById(R.id.tv_num_participants);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Group g = findGroup(groupId);
        tvName.setText(g.getName());
        tvNumParticipants.setText(g.getParticipants().size() + " Participants");

        groupParticipantsAdapter = new GroupParticipantsAdapter(getBaseContext(), g.getParticipants());
        rvParticipants.setAdapter(groupParticipantsAdapter);
        rvParticipants.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), EditGroupActivity.class);
                i.putExtra(EXTRA_MEETING_TITLE, g.getName());
                startActivity(i);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initData() {
        this.groupList = new ArrayList<>();
        ArrayList<Contact> contactList = new ArrayList<>();

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#ce93d8"));
        colors.add(Color.parseColor("#90caf9"));
        colors.add(Color.parseColor("#ffcc80"));
        colors.add(Color.parseColor("#a5d6a7"));
        colors.add(Color.parseColor("#ffd54f"));

        Random rand = new Random();
        Contact c;

        c = new Contact("Hazel Anne Brosas", "09111111111", colors.get(rand.nextInt(5)));
        contactList.add(c);
        c = new Contact("Nicolle Magpale", "09999999999", colors.get(rand.nextInt(5)));
        contactList.add(c);
        c = new Contact("Maria Victoria Reccion", "09222222222", colors.get(rand.nextInt(5)));
        contactList.add(c);
        c = new Contact("Courtney Anne Ngo", "09777777777", colors.get(rand.nextInt(5)));
        contactList.add(c);

        Group g;

        g = new Group(1, "Group 1", contactList);
        groupList.add(g);
        g = new Group(2, "Group 2", contactList);
        groupList.add(g);
        g = new Group(3, "Group 3", contactList);
        groupList.add(g);
        g = new Group(4, "Group 4", contactList);
        groupList.add(g);
        g = new Group(5, "Group 5", contactList);
        groupList.add(g);
    }

    public Group findGroup(int groupId) {
        for(int i = 0; i < groupList.size(); i++) {
            if(groupList.get(i).getId() == groupId)
                return groupList.get(i);
        }

        return null;
    }

    public void refreshData() {
        // get data from server again

        // temporarily initialize data again
        initData();
    }
}
