package com.example.invlist.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.invlist.R;
import com.example.invlist.components.Currency;
import com.example.invlist.components.Forex;
import com.example.invlist.components.InvComponent;
import com.example.invlist.components.InvFactory;
import com.example.invlist.components.InvType;
import com.example.invlist.ui.listadapters.ForexListAdapter;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ForexFragment extends Fragment {

    private static Context context;
    private static ForexListAdapter listAdapter = null;
    private ListView listView;
    private static ArrayList<Currency> forexes = new ArrayList<Currency>();
    private static Activity activity;

    public ForexFragment() {
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
        return inflater.inflate(R.layout.forex_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //forexes.clear();
        activity = this.getActivity();
        listAdapter = new ForexListAdapter(context, activity, forexes);

        listView = (ListView) view.findViewById(R.id.forex_list);
        listView.setAdapter(listAdapter);

        InvComponent invComponent = InvFactory.getInvComponent(InvType.FOREX);
        String value = (invComponent != null) ? invComponent.values() : "";
    }

    public static void updateListView(Currency currency) {
        listAdapter.addItem(currency);
    }
}


