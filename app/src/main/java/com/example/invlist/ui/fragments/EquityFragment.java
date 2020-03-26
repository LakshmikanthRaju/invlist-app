package com.example.invlist.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.invlist.R;
import com.example.invlist.components.InvComponent;
import com.example.invlist.components.InvFactory;
import com.example.invlist.components.InvType;


public class EquityFragment extends Fragment {

    private TextView textView;

    public EquityFragment() {
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
        return inflater.inflate(R.layout.equity_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = (TextView) view.findViewById(R.id.mf_label);

        InvComponent invComponent = InvFactory.getInvComponent(InvType.EQUITY);
        invComponent.setTextView(textView, new Handler());
        String value = (invComponent != null) ? invComponent.values() : "";
        //System.out.println("EQUITY: " + value);

        textView.setText(value);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }
}

