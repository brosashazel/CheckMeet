package com.example.checkmeet.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Group;

import java.util.List;

/**
 * Created by Hazel on 19/03/2017.
 */

public class GroupListsAdapter extends RecyclerView.Adapter<GroupListsAdapter.GroupHolder> {
    private List<Group> groupList;
    private LayoutInflater inflater;

    private GroupItemClickCallback groupItemClickCallback;

    public GroupListsAdapter(List<Group> groupList, Context context) {
        this.groupList = groupList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("GROUPS", "onCreate");
        View v = inflater.inflate(R.layout.list_group_item, parent, false);
        return new GroupHolder(v);
    }

    @Override
    public void onBindViewHolder(GroupHolder holder, int position) {
        holder.tvName.setText(groupList.get(position).getName());
        holder.tvParticipants.setText(groupList.get(position).getParticipants().size() + " Participants");
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void setGroupItemClickCallback(
            final GroupItemClickCallback groupItemClickCallback) {
        this.groupItemClickCallback = groupItemClickCallback;
    }

    public void setItems(List<Group> groupList) {
        this.groupList = groupList;
    }

    class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvName, tvParticipants;
        View container;

        GroupHolder(View itemView) {
            super(itemView);

            Log.d("GROUPS", "GroupHolder");
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvParticipants = (TextView) itemView.findViewById(R.id.tv_participants);
            container = itemView.findViewById(R.id.group_container);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == container.getId()) {
                Log.e("adapter", "clicked item");
                groupItemClickCallback.onItemClick(getAdapterPosition());
            }
        }
    }
}
