package com.example.invlist.ui.listadapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.invlist.R;
import com.example.invlist.components.MF;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class EquityListAdapter extends ArrayAdapter<String> {//implements View.OnClickListener {

    private ArrayList<String> equityList;
    Context mContext;
    private Activity activity;

    // View lookup cache
    private static class ViewHolder {
        //TextView name;
        //TextView date;
        //TextView diff;
        TextView status;
    }

    public EquityListAdapter(Context context, Activity activity, ArrayList<String> equityList) {
        super(context, R.layout.mf_row_item, equityList);
        this.mContext = context;
        this.equityList = equityList;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String status = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mf_row_item, parent, false);
        }

        TextView textView= (TextView) convertView.findViewById(R.id.name);
        textView.setText(status);

        if (textView.getText().toString().contains("Highest")) {
            textView.setTextColor(Color.rgb(0,153,0));
        } else {
            textView.setTextColor(Color.RED);
        }
        return convertView;
    }

    public void addItem(MF mf) {
        activity.runOnUiThread(new Runnable() {
             public void run() {
                 equityList.add(mf.message);
                 notifyDataSetChanged();
             }
        });
    }
}