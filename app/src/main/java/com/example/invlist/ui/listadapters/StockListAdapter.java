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
import com.example.invlist.components.Share;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;

public class StockListAdapter extends ArrayAdapter<Share> {//implements View.OnClickListener {

    private ArrayList<Share> stockList;
    Context mContext;
    private Activity activity;

    public StockListAdapter(Context context, Activity activity, ArrayList<Share> stockList) {
        super(context, R.layout.mf_row_item, stockList);
        this.mContext = context;
        this.stockList = stockList;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Share share = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.status_row_item, parent, false);
        }

        TextView textViewName = (TextView) convertView.findViewById(R.id.name);
        textViewName.setText(share.name);

        TextView textViewDate = (TextView) convertView.findViewById(R.id.date);
        textViewDate.setText(share.date);

        TextView textViewPrice = (TextView) convertView.findViewById(R.id.price);
        textViewPrice.setText(share.price);

        TextView textViewStatus = (TextView) convertView.findViewById(R.id.status);
        textViewStatus.setText(share.message);

        ImageView up_arrow = (ImageView)convertView.findViewById(R.id.up_arrow_icon);
        ImageView down_arrow = (ImageView)convertView.findViewById(R.id.down_arrow_icon);

        if (share.message.contains("Highest")) {
            textViewStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));

            up_arrow.setColorFilter(ContextCompat.getColor(mContext, R.color.colorGreen));
            up_arrow.setVisibility(View.VISIBLE);
            down_arrow.setVisibility(View.INVISIBLE);
        } else if (share.message.contains("Lowest")){
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

    public void addItem(Share share) {
        activity.runOnUiThread(new Runnable() {
             public void run() {
                 stockList.add(share);
                 notifyDataSetChanged();
             }
        });
    }
}