package com.example.checkmeet.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.checkmeet.view.AddGuestsContactsFragment;
import com.example.checkmeet.view.AddGuestsGroupsFragment;
import com.example.checkmeet.view.EditMeetingActivity;
import com.example.checkmeet.view.ViewAllContactsFragment;
import com.example.checkmeet.view.ViewAllGroupsFragment;

/**
 * Created by victo on 3/28/2017.
 */

public class AddGuestsPagerAdapter extends FragmentPagerAdapter {

    private String[] tab_titles = {"Contacts", "Groups"};
    private String participantList;

    public AddGuestsPagerAdapter(FragmentManager fm, String participantList) {
        super(fm);
        this.participantList = participantList;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putString(EditMeetingActivity.EXTRA_PARTICIPANT_LIST, participantList);

        switch (position) {
            case 0:
                fragment = new AddGuestsContactsFragment();
                fragment.setArguments(bundle);
            default:
                fragment =  new AddGuestsGroupsFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tab_titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tab_titles[position];
    }
}
