package com.example.checkmeet.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Group;

/**
 * Created by Hazel on 19/03/2017.
 */

public class GroupListsAdapter extends CursorRecyclerViewAdapter<GroupListsAdapter.GroupHolder> {

    private GroupItemClickCallback groupItemClickCallback;

    public GroupListsAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public void setGroupItemClickCallback(
            final GroupItemClickCallback groupItemClickCallback) {
        this.groupItemClickCallback = groupItemClickCallback;
    }

    @Override
    public void onBindViewHolder(GroupHolder viewHolder, Cursor cursor) {
        String[] columnNames = cursor.getColumnNames();

        long id = cursor.getLong(cursor.getColumnIndex(columnNames[0]));
        String name = cursor.getString(cursor.getColumnIndex(columnNames[1]));
        int num = cursor.getInt(cursor.getColumnIndex(columnNames[2]));

        // id
        viewHolder.group_id = id;

        // name
        viewHolder.tvName.setText(name);

        // number of participants
        viewHolder.tvNumParticipants.setText(num + "");
    }

    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
            parent.getContext()).inflate(R.layout.list_group_item, parent, false);
        return new GroupHolder(view);
    }

    class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvName;
        TextView tvNumParticipants;
        View container;

        long group_id;

        GroupHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvNumParticipants = (TextView) itemView.findViewById(R.id.tv_num_participants);
            container = itemView.findViewById(R.id.group_container);

            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == container.getId()) {
                groupItemClickCallback.onItemClick(group_id);
            }
        }
    }
}
