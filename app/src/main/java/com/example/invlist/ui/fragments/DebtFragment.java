package com.example.invlist.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.invlist.R;
import com.example.invlist.components.InvComponent;
import com.example.invlist.components.InvFactory;
import com.example.invlist.components.InvType;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class DebtFragment extends Fragment {

    private TextView textView;

    public DebtFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        textView = (TextView) view.findViewById(R.id.debt_label);

        InvComponent invComponent = InvFactory.getInvComponent(InvType.DEBT);
        invComponent.setTextView(textView, new Handler());
        String value = (invComponent != null) ? invComponent.values() : "";
        //System.out.println("DEBT: " + value);

        textView.setText(value);
    }
}


