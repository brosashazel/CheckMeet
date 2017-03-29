package com.example.checkmeet.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.checkmeet.R;
import com.example.checkmeet.adapter.AddGuestGroupAdapter;
import com.example.checkmeet.adapter.ContactItemClickCallback;
import com.example.checkmeet.model.Group;
import com.example.checkmeet.service.GroupService;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddGuestsGroupsFragment extends Fragment implements ContactItemClickCallback{

    private static final String TAG = "AddGuestsGroupsFragment";

    private List<Group> groupList;

    private RecyclerView recView;
    private AddGuestGroupAdapter adapter;
    private TextView tv_no_groups_found;

    public AddGuestsGroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_guests_groups, container, false);

        initData();

        // set up views
        recView = (RecyclerView) rootView.findViewById(R.id.rv_add_groups);
        tv_no_groups_found = (TextView) rootView.findViewById(R.id.tv_no_groups_found);

        adapter = new AddGuestGroupAdapter(groupList);
        recView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recView.setAdapter(adapter);
        adapter.setContactItemClickCallback(this);

        return rootView;
    }

    private void initData() {
        groupList = GroupService.getAllGroupsGuests(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        // get data from db
        groupList = GroupService.getAllGroupsGuests(getContext());

        // set items
        adapter.setItems(groupList);
        adapter.notifyDataSetChanged();

        // check first if there are groups
        // if yes, show list
        // if not, show error
        if(adapter.getItemCount() == 0) {
            recView.setVisibility(View.GONE);
            tv_no_groups_found.setVisibility(View.VISIBLE);
        } else {
            recView.setVisibility(View.VISIBLE);
            tv_no_groups_found.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(int p) {
        Group group = groupList.get(p);

        boolean isSelected = !group.isSelected();

        // update data
        group.setSelected(isSelected);

        // pass new data to adapter and update
        adapter.setItems(groupList);
        adapter.notifyItemChanged(p);

        // get members of group from db
        group = GroupService.getGroup(getContext(), group.getId());
        List<String> memberList = group.getMemberList();

        if(isSelected) {
            for(int i = 0; i < memberList.size(); i ++) {
                // add to participant_id_list
                ((AddGuestsActivity)getActivity()).addParticipantID(memberList.get(i));
            }
        } else {
            for(int i = 0; i < memberList.size(); i ++) {
                // remove from participant_id_list
                ((AddGuestsActivity)getActivity()).removeParticipantID(memberList.get(i));
            }
        }
    }
}
