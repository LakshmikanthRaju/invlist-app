package com.example.invlist.ui.fragments;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.invlist.R;
import com.example.invlist.components.InvComponent;
import com.example.invlist.components.InvFactory;
import com.example.invlist.components.InvType;
import com.example.invlist.components.MF;
import com.example.invlist.ui.listadapters.EquityListAdapter;

import java.util.ArrayList;


public class EquityFragment extends Fragment {

    private static Context context;
    private static EquityListAdapter listAdapter;
    private ListView listView;
    private static ArrayList<String> equityFunds = new ArrayList<String>();
    private static Activity activity;


    public EquityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        equityFunds.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.equity_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //equityFunds.clear();
        activity = this.getActivity();
        listAdapter = new EquityListAdapter(context, activity, equityFunds);

        listView = (ListView) view.findViewById(R.id.equity_list);
        listView.setAdapter(listAdapter);

        InvComponent invComponent = InvFactory.getInvComponent(InvType.EQUITY);
        String value = (invComponent != null) ? invComponent.values() : "";
    }

    public static void updateListView(MF mf) {
        listAdapter.addItem(mf);
    }
}

