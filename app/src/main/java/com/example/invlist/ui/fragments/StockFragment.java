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
import com.example.invlist.components.Share;
import com.example.invlist.ui.listadapters.StockListAdapter;

import java.util.ArrayList;


public class StockFragment extends Fragment {

    private static Context context;
    private static StockListAdapter listAdapter;
    private ListView listView;
    private static ArrayList<Share> stocks = new ArrayList<Share>();
    private static Activity activity;

    public StockFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        stocks.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.stock_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //stocks.clear();
        activity = this.getActivity();
        listAdapter = new StockListAdapter(context, activity, stocks);

        listView = (ListView) view.findViewById(R.id.stock_list);
        listView.setAdapter(listAdapter);

        InvComponent invComponent = InvFactory.getInvComponent(InvType.STOCK);
        String value = (invComponent != null) ? invComponent.values() : "";
    }

    public static void updateListView(Share share) {
        listAdapter.addItem(share);
    }
}

