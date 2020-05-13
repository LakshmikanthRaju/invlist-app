package com.example.invlist.ui.listadapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.invlist.R;
import com.example.invlist.components.Currency;
import com.example.invlist.components.MF;

import java.util.ArrayList;

public class ForexListAdapter extends ArrayAdapter<Currency> {//implements View.OnClickListener {

    private ArrayList<Currency> forexList;
    Context mContext;
    private Activity activity;

    // View lookup cache
    private static class ViewHolder {
        //TextView name;
        //TextView date;
        //TextView diff;
        TextView status;
    }

    public ForexListAdapter(Context context, Activity activity, ArrayList<Currency> mfList) {
        super(context, R.layout.mf_row_item, mfList);
        this.mContext = context;
        this.forexList = mfList;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Currency currency = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.status_row_item, parent, false);
        }

        TextView textViewName = (TextView) convertView.findViewById(R.id.name);
        textViewName.setText(currency.name);

        TextView textViewDate = (TextView) convertView.findViewById(R.id.date);
        textViewDate.setText(currency.date);

        TextView textViewPrice = (TextView) convertView.findViewById(R.id.price);
        textViewPrice.setText(currency.price);

        TextView textViewStatus = (TextView) convertView.findViewById(R.id.status);
        textViewStatus.setText(currency.message);

        ImageView up_arrow = (ImageView)convertView.findViewById(R.id.up_arrow_icon);
        ImageView down_arrow = (ImageView)convertView.findViewById(R.id.down_arrow_icon);

        if (currency.message.contains("Highest")) {
            //textViewName.setTextColor(Color.rgb(27, 168, 46));
            textViewStatus.setTextColor(Color.rgb(27, 168, 46));

            up_arrow.setColorFilter(Color.rgb(27, 168, 46));
            up_arrow.setVisibility(View.VISIBLE);
            down_arrow.setVisibility(View.INVISIBLE);
        } else if (currency.message.contains("Lowest")){
            //textViewName.setTextColor(Color.RED);
            textViewStatus.setTextColor(Color.RED);

            down_arrow.setColorFilter(Color.RED);
            down_arrow.setVisibility(View.VISIBLE);
            up_arrow.setVisibility(View.INVISIBLE);
        } else {
            textViewStatus.setTextColor(Color.BLUE);
            up_arrow.setVisibility(View.INVISIBLE);
            down_arrow.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void addItem(Currency currency) {
        activity.runOnUiThread(new Runnable() {
             public void run() {
                 forexList.add(currency);
                 notifyDataSetChanged();
             }
        });
    }
}