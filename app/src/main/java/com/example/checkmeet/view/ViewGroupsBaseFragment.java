package com.example.checkmeet.view;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.checkmeet.adapter.GroupItemClickCallback;
import com.example.checkmeet.adapter.GroupListsAdapter;
import com.example.checkmeet.model.Group;

import java.util.List;

/**
 * Created by Hazel on 19/03/2017.
 */

public abstract class ViewGroupsBaseFragment extends Fragment implements GroupItemClickCallback {
    protected List<Group> groupList;

    protected RecyclerView recView;
    protected GroupListsAdapter adapter;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected TextView tv_no_groups_found;
}
