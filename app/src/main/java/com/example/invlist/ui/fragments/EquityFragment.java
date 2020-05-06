package com.example.invlist.ui.fragments;

import android.app.Activity;
import android.content.Context;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.invlist.R;
import com.example.invlist.components.InvComponent;
import com.example.invlist.components.InvFactory;
import com.example.invlist.components.InvType;
import com.example.invlist.components.MF;

import java.util.ArrayList;


public class EquityFragment extends Fragment {

    private static Context context;
    private static ArrayAdapter<String> listAdapter;
    private ListView listView;
    private static ArrayList<String> mfName = new ArrayList<String>();
    private static Activity activity;


    public EquityFragment() {
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
        return inflater.inflate(R.layout.equity_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = this.getActivity();
        listView = (ListView) view.findViewById(R.id.equity_list);

        this.listAdapter = new ArrayAdapter<String>(context, R.layout.mf_row_item, R.id.name, mfName){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(R.id.name);

                if (textView.getText().toString().contains("Highest")) {
                    textView.setTextColor(Color.rgb(0,153,0));
                } else {
                    textView.setTextColor(Color.RED);
                }
                return view;
            }
        };

        listView.setAdapter(this.listAdapter);

        InvComponent invComponent = InvFactory.getInvComponent(InvType.EQUITY);
        String value = (invComponent != null) ? invComponent.values() : "";
    }

    public static void updateListView(MF mf) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    mfName.add(mf.message);
                    listAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}

