package com.example.checkmeet.view;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.checkmeet.R;
import com.example.checkmeet.adapter.ContactItemClickCallback;
import com.example.checkmeet.adapter.ContactListsAdapter;
import com.example.checkmeet.adapter.GroupParticipantsAdapter;
import com.example.checkmeet.model.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AddGroupActivity extends AppCompatActivity implements ContactItemClickCallback {

    EditText etName;
    ImageView ivAddMember;

    protected List<Contact> contactList;

    protected RecyclerView recView;
    protected GroupParticipantsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        etName = (EditText) findViewById(R.id.et_group_name);
        ivAddMember = (ImageView) findViewById(R.id.iv_add);

        getSupportActionBar().setTitle("Create Group");

        initData();

        recView = (RecyclerView) findViewById(R.id.rv_members);

        adapter = new GroupParticipantsAdapter(contactList, this);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(adapter);
        adapter.setContactItemClickCallback(this);
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
                break;
            case R.id.action_save:

        }

        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        this.contactList = new ArrayList<>();

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
    }

    @Override
    public void onItemClick(int p) {

    }
}
