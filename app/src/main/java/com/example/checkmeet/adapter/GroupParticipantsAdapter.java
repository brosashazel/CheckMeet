package com.example.checkmeet.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.checkmeet.R;
import com.example.checkmeet.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hazel on 19/03/2017.
 */

public class GroupParticipantsAdapter extends
        RecyclerView.Adapter<GroupParticipantsAdapter.GroupParticipantsHolder> {

    private List<Contact> listParticipants;
    private LayoutInflater inflater;

    public void setContactItemClickCallback(ContactItemClickCallback contactItemClickCallback) {
        this.contactItemClickCallback = contactItemClickCallback;
    }

    private ContactItemClickCallback contactItemClickCallback;

    public GroupParticipantsAdapter(List<Contact> listParticipants, Context context) {
        this.listParticipants = listParticipants;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public GroupParticipantsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the view layout template for each item
        View v = inflater.inflate(R.layout.select_contact_item, parent, false);
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

    class GroupParticipantsHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        TextView tvName, tvNumber, tvIcon;
        GradientDrawable iconDrawable;
        ImageView iv_selected_contact;
        View container;

        GroupParticipantsHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            tvIcon = (TextView) itemView.findViewById(R.id.tv_icon);
            container = itemView.findViewById(R.id.contact_container);
            iconDrawable = (GradientDrawable) tvIcon.getBackground();

            container.setOnClickListener(this);
            iv_selected_contact.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
