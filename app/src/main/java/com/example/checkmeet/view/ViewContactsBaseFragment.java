package com.example.checkmeet.view;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.checkmeet.adapter.ContactListsAdapter;
import com.example.checkmeet.model.Contact;

import java.util.List;

/**
 * Created by Hazel on 19/03/2017.
 */

public abstract class ViewContactsBaseFragment extends Fragment{

    protected List<Contact> contactList;

    protected RecyclerView recView;
    protected ContactListsAdapter adapter;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected TextView tv_no_contacts_found;
}
