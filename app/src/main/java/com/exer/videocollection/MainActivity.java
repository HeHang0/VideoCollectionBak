package com.exer.videocollection;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private CoordinatorLayout coordinatorLayout;
    private View currentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        coordinatorLayout = drawer.findViewById(R.id.screen_main);
        LayoutInflater mInflater = LayoutInflater.from(this);
        mInflater.inflate(R.layout.content_main,coordinatorLayout);
        currentView = findViewById(R.id.content_main);

        //coordinatorLayout.addView(contentView);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navheaderView = navigationView.getHeaderView(0);
        navheaderView.setOnClickListener(this);




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "设置",
                    Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.nav_header:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("VideoCollection");
                coordinatorLayout.removeView(currentView);
                LayoutInflater mInflater = LayoutInflater.from(this);
                mInflater.inflate(R.layout.content_main,coordinatorLayout);
                currentView = findViewById(R.id.content_main);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_youku:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("优酷");
                coordinatorLayout.removeView(currentView);
                LayoutInflater mInflater = LayoutInflater.from(this);
                mInflater.inflate(R.layout.youku,coordinatorLayout);
                currentView = findViewById(R.id.youku_layout);
                TextView searchYouku = currentView.findViewById(R.id.search_text_youku);
                searchYouku.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                        if (actionId == EditorInfo.IME_ACTION_SEND) {
                            Toast.makeText(getApplicationContext(), v.getText(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                ListView videoList = currentView.findViewById(R.id.listview_youku);
                //videoList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,nameList));
                SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.listview_helper,
                        new String[]{"title","info","img"},
                        new int[]{R.id.title,R.id.info,R.id.img});
                videoList.setAdapter(adapter);
                videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView title = view.findViewById(R.id.title);
                        Toast.makeText(getApplicationContext(), title.getText(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.nav_camera:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("camera");
                break;
            case R.id.nav_gallery:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("gallery");
                break;
            case R.id.nav_slideshow:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("slideshow");
                break;
            case R.id.nav_manage:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("nav_manage");
                break;
            case R.id.nav_share:
                Toast.makeText(getApplicationContext(), "Share",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(getApplicationContext(), "Send",
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
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
