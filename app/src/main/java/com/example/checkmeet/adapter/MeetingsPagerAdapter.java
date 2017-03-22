package com.example.checkmeet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.checkmeet.view.ViewAllMeetingsFragment;
import com.example.checkmeet.view.ViewInvitedMeetings;
import com.example.checkmeet.view.ViewMeetingsBaseFragment;
import com.example.checkmeet.view.ViewYourMeetingsFragment;

/**
 * Created by victo on 2/21/2017.
 */

public class MeetingsPagerAdapter extends FragmentPagerAdapter {

    private String[] tab_titles = {"All Meetings", "Your Meetings", "Invited Meetings"};

    public MeetingsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public ViewMeetingsBaseFragment getItem(int position) {

        switch (position) {
            case 0:
                return new ViewAllMeetingsFragment();
            case 1:
                return new ViewYourMeetingsFragment();
            default:
                return new ViewInvitedMeetings();
        }
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
