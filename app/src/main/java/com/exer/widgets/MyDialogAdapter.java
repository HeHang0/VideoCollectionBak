package com.exer.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.exer.videocollection.R;

import java.util.List;


public class MyDialogAdapter implements ListAdapter {
    private Activity context;
    private String videoTitle;
    private List<VideoUrlItem> list;

    public MyDialogAdapter(Activity context, List<VideoUrlItem> list, String videoTitle) {
        this.context = context;
        this.list = list;
        this.videoTitle = videoTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View itemView = inflater.inflate(R.layout.dialog_item_layout, null);
        VideoUrlItem info = list.get(position);
        TextView titleView = itemView.findViewById(R.id.description);
        titleView.setTag(info);
        titleView.setText(info.getDescription());
        return itemView;
    }

    @Override
    public int getItemViewType(int i) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    public String getVideoTitle() {
        return videoTitle;
    }
}

