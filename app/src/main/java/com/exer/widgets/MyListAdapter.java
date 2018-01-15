package com.exer.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.exer.videoapi.NetVideo;
import com.exer.videocollection.R;

import java.util.List;


public class MyListAdapter extends BaseAdapter {
    private Activity context;
    private List<NetVideo> list;

    public MyListAdapter(Activity context, List<NetVideo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"}) View itemView = inflater.inflate(R.layout.listview_helper, null);
        NetVideo info = list.get(position);
        TextView titleView = itemView.findViewById(R.id.title);
        TextView infoView = itemView.findViewById(R.id.info);
        TextView imgText = itemView.findViewById(R.id.imgText);
        ImageView imageView = itemView.findViewById(R.id.img);
        titleView.setText(info.getTitle());
        infoView.setText(info.getInfo());
        if(info.getImg() != null){
            imageView.setImageBitmap(info.getImg());
            imgText.setVisibility(View.INVISIBLE);
        }else if(info.getNumber() != null && !info.getNumber().isEmpty()){
            imgText.setText(info.getNumber());
        }
        return itemView;
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
}
