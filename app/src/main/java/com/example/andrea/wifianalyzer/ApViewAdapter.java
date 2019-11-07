package com.example.andrea.wifianalyzer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ApViewAdapter extends RecyclerView.Adapter<ApViewAdapter.ViewHolder>{

    private List<AccessPoint> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    ApViewAdapter(Context context, List<AccessPoint> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.ap_row_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AccessPoint ap = mData.get(position);
        holder.ssid.setText("NAME: " + ap.SSID);
        holder.distance.setText("DIST:" + String.valueOf(ap.getDistance()));
        holder.signal.setText("SIGN: " + String.valueOf(ap.getSignalStrenght()));
        holder.time.setText("TIME: " + String.valueOf(ap.timestamp));
        holder.scanNumber.setText("SCAN NUMBER: " + String.valueOf(ap.misurationNumber));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ssid;
        TextView distance;
        TextView signal;
        TextView time;
        TextView scanNumber;

        ViewHolder(View itemView) {
            super(itemView);
            ssid = itemView.findViewById(R.id.ssid);
            distance = itemView.findViewById(R.id.distance);
            signal = itemView.findViewById(R.id.signal);
            time = itemView.findViewById(R.id.time);
            scanNumber = itemView.findViewById(R.id.scanNumber);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    AccessPoint getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

