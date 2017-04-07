package com.example.checkmeet.view;

import android.content.Intent;
import android.database.Cursor;
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
import com.example.checkmeet.adapter.GroupItemClickCallback;
import com.example.checkmeet.adapter.GroupListsAdapter;
import com.example.checkmeet.model.Group;
import com.example.checkmeet.service.GroupService;

/**
 * Created by Hazel on 19/03/2017.
 */

public class ViewAllGroupsFragment extends Fragment implements GroupItemClickCallback {
    private static final String TAG = "ViewAllGroupsFragment";

    private Cursor cursor;

    private RecyclerView recView;
    private GroupListsAdapter adapter;
    private TextView tv_no_groups_found;

    public ViewAllGroupsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_all_groups, container, false);

        initData();

        // set up views
        recView = (RecyclerView) rootView.findViewById(R.id.rv_all_groups);
        tv_no_groups_found = (TextView) rootView.findViewById(R.id.tv_no_groups_found);

        adapter = new GroupListsAdapter(getContext(), cursor);
        recView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recView.setAdapter(adapter);
        adapter.setGroupItemClickCallback(this);

        return rootView;
    }

    private void initData() {
        cursor = GroupService.getAllGroups(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        // get cursor from db
        Cursor cursor = GroupService.getAllGroups(getContext());

        // will close the old cursor for you
        adapter.changeCursor(cursor);

        Log.e(TAG, "item count = " + adapter.getItemCount());

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
    public void onItemClick(long group_id) {
        Intent intent = new Intent(getActivity(), ViewGroupActivity.class);
        intent.putExtra(Group.COL_GROUPID, group_id);
        startActivity(intent);
    }
}
