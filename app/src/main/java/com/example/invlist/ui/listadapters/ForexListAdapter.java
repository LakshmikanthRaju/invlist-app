package com.example.invlist.ui.listadapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.invlist.R;
import com.example.invlist.components.MF;

import java.util.ArrayList;

public class ForexListAdapter extends ArrayAdapter<String> {//implements View.OnClickListener {

    private ArrayList<String> forexList;
    Context mContext;
    private Activity activity;

    // View lookup cache
    private static class ViewHolder {
        //TextView name;
        //TextView date;
        //TextView diff;
        TextView status;
    }

    public ForexListAdapter(Context context, Activity activity, ArrayList<String> mfList) {
        super(context, R.layout.mf_row_item, mfList);
        this.mContext = context;
        this.forexList = mfList;
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
        } else if (textView.getText().toString().contains("Lowest")){
            textView.setTextColor(Color.RED);
        } else {
            textView.setTextColor(Color.BLUE);
        }
        return convertView;
    }

    public void addItem(String status) {
        activity.runOnUiThread(new Runnable() {
             public void run() {
                 forexList.add(status);
                 notifyDataSetChanged();
             }
        });
    }
}