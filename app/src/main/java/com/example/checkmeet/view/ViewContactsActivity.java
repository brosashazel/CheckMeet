package com.example.checkmeet.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.example.checkmeet.R;
import com.example.checkmeet.adapter.ContactsPagerAdapter;

import static com.example.checkmeet.R.*;

public class ViewContactsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ContactsPagerAdapter adapter;
    private ViewPager viewPager;

    public static final int REQUEST_CODE_CREATE_GROUP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_view_contacts);

        Toolbar toolbar = (Toolbar) findViewById(id.toolbar);
        toolbar.setTitle("Contacts");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, string.navigation_drawer_open, string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), AddGroupActivity.class);
                startActivityForResult(i, REQUEST_CODE_CREATE_GROUP);
            }
        });

        adapter = new ContactsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(id.pager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0) {
                    fab.setVisibility(View.GONE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if(tab.getPosition() == 0) {
                    fab.setVisibility(View.GONE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_meetings) {
            Intent i = new Intent(getBaseContext(), ViewMeetingsActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_contacts) {
            Intent i = new Intent(getBaseContext(), ViewContactsActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_signout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
