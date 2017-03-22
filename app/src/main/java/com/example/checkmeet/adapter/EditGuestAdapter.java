package com.example.checkmeet.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.checkmeet.R;
import com.example.checkmeet.view.CreateMeetingActivity;

import java.util.ArrayList;

/**
 * Created by victo on 3/6/2017.
 */

public class EditGuestAdapter extends RecyclerView.Adapter<EditGuestAdapter.GuestViewHolder> {

    private ArrayList<String> guestListName;
    public static String TAG = "Edit Guest Adapter";
    GuestViewHolder viewHolder;
    private ArrayList<String> checked;

    public EditGuestAdapter(ArrayList<String> guestList, ArrayList<String> checkedGuestList)
    {
        Log.d(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<ENTERED ADAPTER>>>>>>>>>>>>>>>>>>>>>>>>>>");
        guestListName = new ArrayList<>();
        this.guestListName = guestList;
        checked = new ArrayList<>();
        this.checked = checkedGuestList;
        Log.d(TAG, String.valueOf(guestList.size()));
        for(int i = 0; i< guestList.size(); i++)
            Log.d(TAG, guestList.get(i));
    }

    public EditGuestAdapter(ArrayList<String> guestList)
    {
        Log.d(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<ENTERED ADAPTER>>>>>>>>>>>>>>>>>>>>>>>>>>");
        guestListName = new ArrayList<>();
        this.guestListName = guestList;
        checked = new ArrayList<>();
        Log.d(TAG, String.valueOf(guestList.size()));
        for(int i = 0; i< guestList.size(); i++)
            Log.d(TAG, guestList.get(i));
    }

    @Override
    public GuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.guest_item, parent, false);
        viewHolder = new GuestViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GuestViewHolder holder, int position) {
        Log.d(TAG, "on Bind View Holder");
        String guestName = guestListName.get(position);
        holder.bindText(guestName);
    }

    public GuestViewHolder getViewHolder()
    {
        return viewHolder;
    }

    public ArrayList<String> getList()
    {
        return checked;
    }

    @Override
    public int getItemCount() {
        return guestListName.size();
    }

    class GuestViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cbGuest;
        public TextView tvGuestName;

        public GuestViewHolder(View view) {
            super(view);
            this.cbGuest = (CheckBox) view.findViewById(R.id.cb_guest);
            this.tvGuestName = (TextView) view.findViewById(R.id.tv_guest_name);
        }

        public void bindText(String name)
        {
            tvGuestName.setText(name);
            tvGuestName.setTypeface(CreateMeetingActivity.tf_roboto);
            if(checked.contains(name))
            {
                cbGuest.setChecked(true);
            }
            cbGuest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(cbGuest.isChecked())
                    {
                        Log.d(TAG, "CHECKED");
                        checked.add(tvGuestName.getText().toString());
                    }else
                    {

                        Log.d(TAG, "REMOVED CHECKED");
                        checked.remove(tvGuestName.getText().toString());
                    }
                }
            });
        }
    }
}
