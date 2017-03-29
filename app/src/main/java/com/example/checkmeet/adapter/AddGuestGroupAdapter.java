package com.example.checkmeet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Group;

import java.util.List;

/**
 * Created by victo on 3/28/2017.
 */

public class AddGuestGroupAdapter extends RecyclerView.Adapter<AddGuestGroupAdapter.GroupHolder> {

    private List<Group> groupList;

    private ContactItemClickCallback contactItemClickCallback;

    public void setContactItemClickCallback(
            final ContactItemClickCallback contactItemClickCallback) {
        this.contactItemClickCallback = contactItemClickCallback;
    }

    public AddGuestGroupAdapter(List<Group> groupList) {
        this.groupList = groupList;
    }

    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.select_group_item, parent, false);
        return new GroupHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupHolder holder, int position) {
        Group group = groupList.get(position);

        // name
        holder.tvName.setText(group.getName());

        // numParticipants
        holder.tvNumParticipants.setText(group.getNumMembers() + "");

        // selected
        if(group.isSelected()) {
            holder.check_container.setVisibility(View.VISIBLE);
        } else {
            holder.check_container.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void setItems(List<Group> groupList) {
        this.groupList = groupList;
    }

    class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvName;
        TextView tvNumParticipants;
        View container;
        View check_container;

        GroupHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvNumParticipants = (TextView) itemView.findViewById(R.id.tv_num_participants);
            container = itemView.findViewById(R.id.group_container);
            check_container = itemView.findViewById(R.id.fl_container);

            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == container.getId()) {
                contactItemClickCallback.onItemClick(getAdapterPosition());
            }
        }
    }

}
