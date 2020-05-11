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

public class DebtListAdapter extends ArrayAdapter<String> {//implements View.OnClickListener {

    private ArrayList<String> debtList;
    Context mContext;
    private Activity activity;

    // View lookup cache
    private static class ViewHolder {
        //TextView name;
        //TextView date;
        //TextView diff;
        TextView status;
    }

    public DebtListAdapter(Context context, Activity activity, ArrayList<String> debtList) {
        super(context, R.layout.mf_row_item, debtList);
        this.mContext = context;
        this.debtList = debtList;
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
                 debtList.add(mf.message);
                 notifyDataSetChanged();
             }
        });
    }
}