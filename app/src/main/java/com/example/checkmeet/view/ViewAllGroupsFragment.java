package com.example.checkmeet.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.checkmeet.R;
import com.example.checkmeet.adapter.GroupListsAdapter;
import com.example.checkmeet.model.Contact;
import com.example.checkmeet.model.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Hazel on 19/03/2017.
 */

public class ViewAllGroupsFragment extends ViewGroupsBaseFragment {
    private static final String TAG = "ViewAllGroupsFragment";

    public ViewAllGroupsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_all_groups, container, false);

        initData();

        // set up views
        recView = (RecyclerView) rootView.findViewById(R.id.rv_all_groups);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout_all);
        tv_no_groups_found = (TextView) rootView.findViewById(R.id.tv_no_groups_found);

        adapter = new GroupListsAdapter(groupList, getContext());
        recView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recView.setAdapter(adapter);
        adapter.setGroupItemClickCallback(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }

            void refreshItems() {

                // load the whole set of data
                refreshData();

                // Load complete
                onItemsLoadComplete();
            }

            void onItemsLoadComplete() {
                // Update the adapter and notify data set changed
                adapter.setItems(groupList);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    private void initData() {
        this.groupList = new ArrayList<>();
        ArrayList<Contact> contactList = new ArrayList<>();

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#ce93d8"));
        colors.add(Color.parseColor("#90caf9"));
        colors.add(Color.parseColor("#ffcc80"));
        colors.add(Color.parseColor("#a5d6a7"));
        colors.add(Color.parseColor("#ffd54f"));

        Random rand = new Random();
        Contact c;

        c = new Contact("Hazel Anne Brosas", "09111111111", colors.get(rand.nextInt(5)));
        contactList.add(c);
        c = new Contact("Nicolle Magpale", "09999999999", colors.get(rand.nextInt(5)));
        contactList.add(c);
        c = new Contact("Maria Victoria Reccion", "09222222222", colors.get(rand.nextInt(5)));
        contactList.add(c);
        c = new Contact("Courtney Anne Ngo", "09777777777", colors.get(rand.nextInt(5)));
        contactList.add(c);

        Group g;

        g = new Group(1, "Group 1", contactList);
        groupList.add(g);
        g = new Group(2, "Group 2", contactList);
        groupList.add(g);
        g = new Group(3, "Group 3", contactList);
        groupList.add(g);
        g = new Group(4, "Group 4", contactList);
        groupList.add(g);
        g = new Group(5, "Group 5", contactList);
        groupList.add(g);
    }

    public void refreshData() {
        // get data from server again

        // temporarily initialize data again
        initData();
    }

    @Override
    public void onItemClick(int p) {
        Log.d(TAG, "title = " + groupList.get(p).getName());
        Intent intent = new Intent(getActivity(), ViewGroupActivity.class);
        intent.putExtra(ViewGroupActivity.EXTRA_GROUP_ID, groupList.get(p).getId());
        startActivity(intent);
    }
}
