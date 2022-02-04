package com.example.invlist.ui.listadapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.invlist.R;
import com.example.invlist.components.MF;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;

public class EquityListAdapter extends ArrayAdapter<MF> {//implements View.OnClickListener {

    private ArrayList<MF> equityList;
    Context mContext;
    private Activity activity;

    public EquityListAdapter(Context context, Activity activity, ArrayList<MF> equityList) {
        super(context, R.layout.mf_row_item, equityList);
        this.mContext = context;
        this.equityList = equityList;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MF equityFund = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mf_row_item, parent, false);
        }

        TextView textViewName = (TextView) convertView.findViewById(R.id.name);
        textViewName.setText(equityFund.name);

        TextView textViewDate = (TextView) convertView.findViewById(R.id.date);
        textViewDate.setText(equityFund.date);

        TextView textViewPrice = (TextView) convertView.findViewById(R.id.price);
        textViewPrice.setText(equityFund.price);

        TextView textViewHighest = (TextView) convertView.findViewById(R.id.highest);
        textViewHighest.setText(equityFund.highest);

        TextView textViewStatus = (TextView) convertView.findViewById(R.id.status);
        textViewStatus.setText(equityFund.message);

        ImageView up_arrow = (ImageView)convertView.findViewById(R.id.up_arrow_icon);
        ImageView down_arrow = (ImageView)convertView.findViewById(R.id.down_arrow_icon);

        if (equityFund.message.contains("Highest")) {

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
                 equityList.add(mf);
                 notifyDataSetChanged();
             }
        });
    }
}