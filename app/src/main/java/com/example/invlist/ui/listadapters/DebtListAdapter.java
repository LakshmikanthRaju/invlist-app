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
import com.example.invlist.components.MF;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;

public class DebtListAdapter extends ArrayAdapter<MF> {//implements View.OnClickListener {

    private ArrayList<MF> debtList;
    Context mContext;
    private Activity activity;

    public DebtListAdapter(Context context, Activity activity, ArrayList<MF> debtList) {
        super(context, R.layout.mf_row_item, debtList);
        this.mContext = context;
        this.debtList = debtList;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MF debitFund = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mf_row_item, parent, false);
        }

        TextView textViewName = (TextView) convertView.findViewById(R.id.name);
        textViewName.setText(debitFund.name);

        TextView textViewDate = (TextView) convertView.findViewById(R.id.date);
        textViewDate.setText(debitFund.date);

        TextView textViewPrice = (TextView) convertView.findViewById(R.id.price);
        textViewPrice.setText(debitFund.price);

        TextView textViewStatus = (TextView) convertView.findViewById(R.id.status);
        textViewStatus.setText(debitFund.message);

        ImageView up_arrow = (ImageView)convertView.findViewById(R.id.up_arrow_icon);
        ImageView down_arrow = (ImageView)convertView.findViewById(R.id.down_arrow_icon);

        if (debitFund.message.contains("Highest")) {

            textViewStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));

            up_arrow.setColorFilter(ContextCompat.getColor(mContext, R.color.colorGreen));
            up_arrow.setVisibility(View.VISIBLE);
            down_arrow.setVisibility(View.INVISIBLE);
        } else {

            textViewStatus.setTextColor(Color.RED);

            down_arrow.setColorFilter(Color.RED);
            down_arrow.setVisibility(View.VISIBLE);
            up_arrow.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public void addItem(MF mf) {
        activity.runOnUiThread(new Runnable() {
             public void run() {
                 debtList.add(mf);
                 notifyDataSetChanged();
             }
        });
    }
}