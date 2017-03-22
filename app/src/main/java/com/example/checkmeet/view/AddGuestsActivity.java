package com.example.checkmeet.view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.checkmeet.R;
import com.example.checkmeet.adapter.GuestAdapter;

import java.util.ArrayList;

public class AddGuestsActivity extends AppCompatActivity {

    public static String TAG = "Add Guests";
    public static String GUEST_LIST_TAG = "GUEST_LIST_TAG";

    private LinearLayout activityAddGuests;
    private RecyclerView rvAddGuest;
    private LinearLayoutManager llManager;
    private ArrayList<String> guestList;
    private GuestAdapter ga;
    //private Toolbar toolbar;
    private android.support.v7.app.ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guests);
        initList();
        initView();
    }

    private void initView() {
        activityAddGuests = (LinearLayout) findViewById(R.id.activity_add_guests);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        rvAddGuest = (RecyclerView) findViewById(R.id.rv_add_guest);
        llManager = new LinearLayoutManager(this);
        llManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAddGuest.setLayoutManager(llManager);
        ga = new GuestAdapter(guestList);
        rvAddGuest.setAdapter(ga);

        String name = "Add Guests"; // your string here
        SpannableString s = new SpannableString(name);
        s.setSpan(new TypefaceSpan("fonts/rancho_regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.getSupportActionBar().setTitle(s);

        Intent i= getIntent();
        int color = i.getIntExtra(CreateMeetingActivity.MEETING_COLOR, 0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
    }

    private void initList() {
        guestList = new ArrayList<String>();
        guestList.add("Lady Mavic Reccion");
        guestList.add("Nicolle Magpale");
        guestList.add("Hazel Brosas");
        guestList.add("Alex Diaz");
        guestList.add("Much Mobapde");
        guestList.add("Sample Name");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        String name = "DONE"; // your string here
        SpannableString s = new SpannableString(name);
        s.setSpan(new TypefaceSpan("fonts/roboto_regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        final MenuItem menuItem = menu.add(Menu.NONE, 1000, Menu.NONE, s);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Toast.makeText(getBaseContext(), "BACK PRESSED ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();

                intent.putStringArrayListExtra(GUEST_LIST_TAG, ga.getList());
                setResult(RESULT_OK, intent);
                finish();

                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
