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
import com.example.checkmeet.model.Contact;

import java.util.List;

/**
 * Created by Hazel on 19/03/2017.
 */

public class ContactListsAdapter extends RecyclerView.Adapter<ContactListsAdapter.ContactHolder> {

    private List<Contact> contactList;
    private LayoutInflater inflater;

    private ContactItemClickCallback contactItemClickCallback;

    public ContactListsAdapter(List<Contact> contactList, Context context) {
        this.contactList = contactList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.list_contact_item, parent, false);
        return new ContactHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactHolder holder, int position) {
        holder.tvName.setText(contactList.get(position).getName());
        holder.tvNumber.setText(contactList.get(position).getNumber());
        holder.tvIcon.setText(contactList.get(position).getName().charAt(0) + "");
        holder.iconDrawable.setColor(contactList.get(position).getColor());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void setContactItemClickCallback(
            final ContactItemClickCallback contactItemClickCallback) {
        this.contactItemClickCallback = contactItemClickCallback;
    }

    public void setItems(List<Contact> contactList) {
        this.contactList = contactList;
    }

    class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvName, tvNumber, tvIcon;
        GradientDrawable iconDrawable;
        View container;

        ContactHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            tvIcon = (TextView) itemView.findViewById(R.id.tv_icon);
            container = itemView.findViewById(R.id.contact_container);
            iconDrawable = (GradientDrawable) tvIcon.getBackground();
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == container.getId()) {
                Log.e("adapter", "clicked item");
                contactItemClickCallback.onItemClick(getAdapterPosition());
            }
        }
    }
}
