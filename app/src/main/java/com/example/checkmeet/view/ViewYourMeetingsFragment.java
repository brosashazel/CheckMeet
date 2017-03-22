package com.example.checkmeet.view;


import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checkmeet.R;
import com.example.checkmeet.adapter.MeetingListsAdapter;
import com.example.checkmeet.model.Meeting;
import com.example.checkmeet.model.Month;
import com.example.checkmeet.service.MeetingService;

import java.util.ArrayList;
import java.util.List;


public class ViewYourMeetingsFragment extends ViewMeetingsBaseFragment {


    public ViewYourMeetingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_your_meetings, container, false);

        initData();

        // set up views
        recView = (RecyclerView) rootView.findViewById(R.id.rv_your_meetings);
//        swipeRefreshLayout =
//                (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout_yours);
        tv_no_meetings_found = (TextView) rootView.findViewById(R.id.tv_no_meetings_found);

        adapter = new MeetingListsAdapter(getContext(), cursor);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        recView.setAdapter(adapter);
        adapter.setMeetingItemClickCallback(this);

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshItems();
//            }
//
//            void refreshItems() {
//                // load the whole set of data
//                refreshData();
//
//                // Load complete
//                onItemsLoadComplete();
//            }
//
//            void onItemsLoadComplete() {
//                // Update the adapter and notify data set changed
//                adapter.setItems(meetingList);
//                adapter.notifyDataSetChanged();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // get cursor from db
        Cursor cursor = MeetingService.getFilteredMeetings(getContext(), Meeting.FLAG_IS_HOST);

        // will close the old cursor for you
        adapter.changeCursor(cursor);

        // check first if there are meetings
        // if yes, show list
        // if not, show error
        if(adapter.getItemCount() == 0) {
            recView.setVisibility(View.GONE);
            tv_no_meetings_found.setVisibility(View.VISIBLE);
        } else {
            recView.setVisibility(View.VISIBLE);
            tv_no_meetings_found.setVisibility(View.GONE);

            if(getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE) {
                recView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            } else {
                recView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        }
    }

    private void initData() {

        cursor = MeetingService.getFilteredMeetings(getContext(), Meeting.FLAG_IS_HOST);

//        this.meetingList = new ArrayList<>();
//
//        List<Integer> colors = new ArrayList<>();
//        colors.add(Color.parseColor("#FFBBFF"));
//        colors.add(Color.parseColor("#EEBBEE"));
//        colors.add(Color.parseColor("#DFB0FF"));
//        colors.add(Color.parseColor("#DBBFF7"));
//        colors.add(Color.parseColor("#CBC5F5"));
//        colors.add(Color.parseColor("#BAD0EF"));
//        colors.add(Color.parseColor("#A5DBEB"));
//        colors.add(Color.parseColor("#B5FFC8"));
//        colors.add(Color.parseColor("#B3FF99"));
//        colors.add(Color.parseColor("#DFFFCA"));
//        colors.add(Color.parseColor("#FFFFC8"));
//        colors.add(Color.parseColor("#F7F9D0"));
//
//        Meeting m;
//
//        for(int i = 0; i < 12; i ++) {
//            m = new Meeting();
//            m.setTitle("Meeting " + (i+1));
//            m.setMonth(Month.values()[i] + "");
//            m.setColor(colors.get(i));
//
//            meetingList.add(m);
//        }
    }

    public void refreshData() {
        // get data from server again

        // temporarily initialize data again
        initData();
    }

    @Override
    public void onItemClick(int meeting_id) {
        Intent intent = new Intent(getActivity(), ViewMeetingActivity.class);
        intent.putExtra(Meeting.COL_MEETINGID, meeting_id);
        startActivity(intent);
    }
}
