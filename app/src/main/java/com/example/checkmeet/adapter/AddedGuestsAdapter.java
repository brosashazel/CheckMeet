package com.example.checkmeet.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checkmeet.R;
import com.example.checkmeet.view.CreateMeetingActivity;

import java.util.ArrayList;

/**
 * Created by ASUS-PC on 12/03/2017.
 */

public class AddedGuestsAdapter extends RecyclerView.Adapter<AddedGuestsAdapter.AddedGuestViewHolder> {

    private ArrayList<String> addedGuestList;
    public static String TAG = "Added Guests Adapter";

    public AddedGuestsAdapter(ArrayList<String> addedGuestList) {

        this.addedGuestList = addedGuestList;
        Log.d(TAG, "Added " + String.valueOf(addedGuestList.size()) + "guests");
    }

    @Override
    public AddedGuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.added_guest_item, parent, false);
        AddedGuestViewHolder viewHolder = new AddedGuestViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AddedGuestViewHolder holder, int position) {
        Log.d(TAG, "on Bind View Holder");
        String guestName = addedGuestList.get(position);
        holder.bindText(guestName);
    }

    @Override
    public int getItemCount() {
        return addedGuestList.size();
    }

    public class AddedGuestViewHolder extends RecyclerView.ViewHolder{

        public TextView tvGuestName;
        public ImageView btnRemove;


        public AddedGuestViewHolder(View view) {
            super(view);
            this.tvGuestName = (TextView) view.findViewById(R.id.tv_guest_name);
            this.btnRemove = (ImageButton) view.findViewById(R.id.btn_remove);
        }

        public void bindText(String name) {
            tvGuestName.setText(name);


        }
    }
}
