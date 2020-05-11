package com.example.invlist.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.invlist.R;
import com.example.invlist.components.InvComponent;
import com.example.invlist.components.InvFactory;
import com.example.invlist.components.InvType;
import com.example.invlist.components.MF;
import com.example.invlist.ui.listadapters.DebtListAdapter;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class DebtFragment extends Fragment {

    private static Context context;
    private static DebtListAdapter listAdapter;
    private ListView listView;
    private static ArrayList<String> debtFunds = new ArrayList<String>();
    private static Activity activity;

    public DebtFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.debt_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //debtFunds.clear();
        activity = this.getActivity();
        listAdapter = new DebtListAdapter(context, activity, debtFunds);

        listView = (ListView) view.findViewById(R.id.debt_list);
        listView.setAdapter(listAdapter);

        InvComponent invComponent = InvFactory.getInvComponent(InvType.DEBT);
        String value = (invComponent != null) ? invComponent.values() : "";
    }

    public static void updateListView(MF mf) {
        listAdapter.addItem(mf);
    }
}


