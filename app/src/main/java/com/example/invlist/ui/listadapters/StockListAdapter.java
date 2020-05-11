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

public class StockListAdapter extends ArrayAdapter<String> {//implements View.OnClickListener {

    private ArrayList<String> stockList;
    Context mContext;
    private Activity activity;

    // View lookup cache
    private static class ViewHolder {
        //TextView name;
        //TextView date;
        //TextView diff;
        TextView status;
    }

    public StockListAdapter(Context context, Activity activity, ArrayList<String> stockList) {
        super(context, R.layout.mf_row_item, stockList);
        this.mContext = context;
        this.stockList = stockList;
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

    public void addItem(String stock) {
        activity.runOnUiThread(new Runnable() {
             public void run() {
                 stockList.add(stock);
                 notifyDataSetChanged();
             }
        });
    }
}