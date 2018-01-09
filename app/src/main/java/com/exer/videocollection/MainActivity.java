package com.exer.videocollection;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.exer.videoapi.NetVideo;
import com.exer.videoapi.NetVideoFrom;
import com.exer.videoapi.NetVideoHelper;
import com.exer.widgets.MyListAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private CoordinatorLayout coordinatorLayout;
    private View currentView;
    private List<NetVideo> NetVideoList = new ArrayList<>();
    private String SearchStr = "";
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
                            SearchStr = v.getText().toString();
                            new Thread(new Runnable() {
                                public void run() {
                                    Message msg = new Message();
                                    NetVideoList.clear();
                                    NetVideoList.addAll(NetVideoHelper.getNetVideoList(SearchStr, NetVideoFrom.Youku));
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        }
                        return false;
                    }
                });
                ListView videoListView = currentView.findViewById(R.id.listview_youku);
                //videoList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,nameList));
                getVideoList();
                MyListAdapter adapter = new MyListAdapter(MainActivity.this, NetVideoList);
                videoListView.setAdapter(adapter);
                videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Dialog dialog = new Dialog(MainActivity.this, R.style.ActionSheetDialogStyle);
                        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
                        LinearLayout ll = inflate.findViewById(R.id.dialog_layout);
                        TextView tv = new TextView(MainActivity.this);
                        tv.setText("哈哈哈");
                        tv.setTextColor(Color.BLACK);
                        tv.setGravity(Gravity.CENTER);
                        tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        tv.setTextSize(18);
                        tv.setHeight(110);
                        ImageView v = new ImageView(MainActivity.this);
                        v.setBackgroundColor(Color.GRAY);
                        v.setMaxHeight(3);
                        ll.addView(v);
                        ll.addView(tv);
                        dialog.setContentView(inflate);
                        Window dialogWindow = dialog.getWindow();
                        dialogWindow.setGravity( Gravity.BOTTOM);
                        //获得窗体的属性
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        lp.y = 20;//设置Dialog距离底部的距离
                         //       将属性设置给窗体
                        dialogWindow.setAttributes(lp);
                        dialog.show();//显示对话框
//                        Toast.makeText(getApplicationContext(), NetVideoList.get(i).getVideoUrl(),
//                                Toast.LENGTH_SHORT).show();
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

    private void getVideoList(){
        for (int i=0; i < 15; i++){
            NetVideoList.add(new NetVideo("Title" + i,"Info" + i,null,"Url" + i));
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ListView videoListView = currentView.findViewById(R.id.listview_youku);
            MyListAdapter la = (MyListAdapter) videoListView.getAdapter();
            la.notifyDataSetChanged();
        }
    };
}
