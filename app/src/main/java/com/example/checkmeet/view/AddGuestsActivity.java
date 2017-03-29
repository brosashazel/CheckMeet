package com.example.checkmeet.view;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.example.checkmeet.R;
import com.example.checkmeet.adapter.AddGuestsPagerAdapter;
import com.example.checkmeet.model.Contact;
import com.example.checkmeet.model.Group;
import com.example.checkmeet.service.GroupService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddGuestsActivity extends AppCompatActivity {

    public static final String GUEST_LIST_TAG = "GUEST_LIST_TAG";

    private static final String TAG = "AddGuestsActivity";

    private List<String> participant_id_list;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private AddGuestsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guests);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Guests");

        participant_id_list = new ArrayList<>();

        // get participant_id_list from edit activity
        String participantList = getIntent().getStringExtra(
                EditMeetingActivity.EXTRA_PARTICIPANT_LIST);

        // null if from CreateMeetingActivity
        if(participantList != null) {
            parseParticipantListFromIntent(participantList);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter =
                new AddGuestsPagerAdapter(getSupportFragmentManager(), participantList);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cancel_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_save:
                save();
                break;
            case R.id.action_cancel:
            default: onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlert();
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
                        // must return RESULT_CANCELED to previous activity
                        setResult(RESULT_CANCELED);
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
        // must return RESULT_OK to previous activity
        setResult(RESULT_OK);
        getIntent().putExtra(GUEST_LIST_TAG, getStrParticipantList());

        Toast.makeText(getBaseContext(), "Saving Guests Added", Toast.LENGTH_SHORT).show();
    }

    private String getStrParticipantList()
    {
        //TODO: Gather all participant names with delimeter (,)
        //TODO: Go through all the paticipant id. get each name from db then append to  "strPartcipantList"

        String strPartcipantList = getContactName(participant_id_list.get(0));

        for(int i = 1; i < participant_id_list.size(); i++) {
                strPartcipantList += ", " + getContactName(participant_id_list.get(i));
        }

        //Check view group activity to see how contacts are retrieved.
        return strPartcipantList;
    }

    private String getContactName(String member_id) {

        ContentResolver cr = getBaseContext().getContentResolver();
        String name = null;

        Cursor cur = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts._ID + " = ?",
                new String[]{member_id},
                null);

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
            }

            cur.close();
        }

        return name;
    }

    public void addParticipantID(String participant_id) {

        // add string id
        participant_id_list.add(participant_id);

        // remove duplicates
        Set<String> hs = new HashSet<>();
        hs.addAll(participant_id_list);
        participant_id_list.clear();
        participant_id_list.addAll(hs);
    }

    public void removeParticipantID(String participant_id) {
        participant_id_list.remove(participant_id);
    }

    private void parseParticipantListFromIntent(String participantList) {
        if(!participantList.isEmpty()) {
            String[] tokens = participantList.split(",");

            for(int i =0; i < tokens.length; i ++) {
                participant_id_list.add(tokens[i]);
            }
        }
    }

}
