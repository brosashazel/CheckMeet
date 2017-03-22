package com.example.checkmeet.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.checkmeet.R;

public class AddGuestActivity extends AppCompatActivity {

    RecyclerView rvContacts;
    ImageView btnCancel, btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guest);

        rvContacts = (RecyclerView) findViewById(R.id.rv_contacts);
        btnCancel = (ImageView) findViewById(R.id.btn_cancel);
        btnAdd = (ImageView) findViewById(R.id.btn_create);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), AddGroupActivity.class);
                startActivity(i);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), AddGroupActivity.class);
                startActivity(i);
            }
        });
    }
}
