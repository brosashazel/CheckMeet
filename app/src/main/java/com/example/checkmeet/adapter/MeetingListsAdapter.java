package com.example.checkmeet.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Date;
import com.example.checkmeet.model.Meeting;
import com.example.checkmeet.model.Month;
import com.example.checkmeet.utils.Utils;

/**
 * Created by victo on 2/22/2017.
 */

public class MeetingListsAdapter extends
        CursorRecyclerViewAdapter<MeetingListsAdapter.MeetingHolder>{

    private MeetingItemClickCallback meetingItemClickCallback;

    public MeetingListsAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(MeetingHolder viewHolder, Cursor cursor) {

        // id
        viewHolder.meeting_id = cursor.getInt(cursor.getColumnIndex(Meeting.COL_MEETINGID));

        // title
        String meeting_title = cursor.getString(cursor.getColumnIndex(Meeting.COL_TITLE));
        viewHolder.tv_title_meeting.setText(meeting_title);

        // date
        long date_milliseconds = cursor.getLong(cursor.getColumnIndex(Meeting.COL_DATE));
        Date date = new Date(date_milliseconds);
        viewHolder.tv_month.setText(Month.values()[date.getMonth()] + "");      // 0-11 months
        viewHolder.tv_day_of_month.setText(date.getDayOfMonth() + "");
        viewHolder.tv_year.setText(date.getYear() + "");

        // time
        int startTime_int = cursor.getInt(cursor.getColumnIndex(Meeting.COL_TIMESTART));
        String startTime_string = Utils.dateIntegerToString(startTime_int);
        int endTime_int = cursor.getInt(cursor.getColumnIndex(Meeting.COL_TIMEEND));
        String endTime_string = Utils.dateIntegerToString(endTime_int);
        viewHolder.tv_time_meeting.setText(startTime_string + " - " + endTime_string);

        // location
        String location = cursor.getString(cursor.getColumnIndex(Meeting.COL_ADDRESS));
        viewHolder.tv_location.setText(location);

        // host
        viewHolder.tv_host_name.setText(
                cursor.getString(cursor.getColumnIndex(Meeting.COL_HOST_NAME)));

        // color
        int color = cursor.getInt(cursor.getColumnIndex(Meeting.COL_COLOR));
        viewHolder.background_date.setBackgroundColor(color);

    }

    @Override
    public MeetingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.meeting_item, parent, false);
        return new MeetingHolder(view);
    }

    public void setMeetingItemClickCallback(
            final MeetingItemClickCallback meetingItemClickCallback) {
        this.meetingItemClickCallback = meetingItemClickCallback;
    }

    class MeetingHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{

        TextView tv_title_meeting;
        TextView tv_month;
        TextView tv_day_of_month;
        TextView tv_year;
        TextView tv_time_meeting;
        TextView tv_location;
        TextView tv_host_name;
        View background_date;
        View container;

        int meeting_id;

        MeetingHolder(View itemView) {
            super(itemView);

            tv_title_meeting = (TextView) itemView.findViewById(R.id.tv_title_meeting);
            tv_month = (TextView) itemView.findViewById(R.id.tv_month);
            tv_day_of_month = (TextView) itemView.findViewById(R.id.tv_day_of_month);
            tv_year = (TextView) itemView.findViewById(R.id.tv_year);
            tv_time_meeting = (TextView) itemView.findViewById(R.id.tv_time_meeting);
            tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            tv_host_name = (TextView) itemView.findViewById(R.id.tv_host_name);
            background_date = itemView.findViewById(R.id.background_date_meeting);
            container = itemView.findViewById(R.id.container_meeting_item);

            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == container.getId()) {
                meetingItemClickCallback.onItemClick(meeting_id);
            }
        }
    }
}
