package com.exer.widget;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;

import com.exer.videocollection.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("Registered")
public class YoukuListView extends ListActivity {
    List<Map<String, Object>> list = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.listview_helper,
                new String[]{"title","info","img"},
                new int[]{R.id.title,R.id.info,R.id.img});
        setListAdapter(adapter);
    }

    private List<Map<String, Object>> getData() {

        Map<String, Object> map = new HashMap<>();
        map.put("title", "G1");
        map.put("info", "google 1");
        map.put("img", R.drawable.ic_menu_youku);
        list.add(map);

        map = new HashMap<>();
        map.put("title", "G2");
        map.put("info", "google 2");
        map.put("img", R.drawable.ic_menu_camera);
        list.add(map);

        map = new HashMap<>();
        map.put("title", "G3");
        map.put("info", "google 3");
        map.put("img", R.drawable.ic_menu_gallery);
        list.add(map);

        return list;
    }
}
