package com.example.checkmeet.view;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.checkmeet.adapter.MeetingItemClickCallback;
import com.example.checkmeet.adapter.MeetingListsAdapter;
import com.example.checkmeet.model.Meeting;

import java.util.List;

/**
 * Created by victo on 2/22/2017.
 */

public abstract class ViewMeetingsBaseFragment extends Fragment implements
        MeetingItemClickCallback {

    protected Cursor cursor;

    protected RecyclerView recView;
    protected MeetingListsAdapter adapter;
//    protected SwipeRefreshLayout swipeRefreshLayout;
    protected TextView tv_no_meetings_found;

}
