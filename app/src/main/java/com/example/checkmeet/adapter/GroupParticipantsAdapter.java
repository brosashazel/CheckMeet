package com.example.checkmeet.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Contact;

import java.util.ArrayList;

/**
 * Created by Hazel on 19/03/2017.
 */

public class GroupParticipantsAdapter extends RecyclerView.Adapter<GroupParticipantsAdapter.GroupParticipantsHolder> {
    private ArrayList<Contact> listParticipants;

    public GroupParticipantsAdapter(Context context, ArrayList<Contact> listParticipants) {
        this.listParticipants = listParticipants;
    }

    @Override
    public GroupParticipantsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the view layout template for each item
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_contact_item, parent, false);
        return new GroupParticipantsHolder(v);
    }

    @Override
    public void onBindViewHolder(GroupParticipantsHolder holder, final int position) {
        holder.tvName.setText(listParticipants.get(position).getName());
        holder.tvNumber.setText(listParticipants.get(position).getNumber());
        holder.tvIcon.setText(listParticipants.get(position).getName().charAt(0) + "");
        holder.iconDrawable.setColor(listParticipants.get(position).getColor());
    }

    @Override
    public int getItemCount() {
        return listParticipants.size();
    }

    public class GroupParticipantsHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvNumber, tvIcon;
        GradientDrawable iconDrawable;
        View container;

        public GroupParticipantsHolder(View itemView) {
            super(itemView);
            // initialize the views
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            tvIcon = (TextView) itemView.findViewById(R.id.tv_icon);
            container = itemView.findViewById(R.id.contact_container);
            iconDrawable = (GradientDrawable) tvIcon.getBackground();
//            container.setOnClickListener(this);
        }
    }
}
