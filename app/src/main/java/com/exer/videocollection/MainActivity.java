package com.exer.videocollection;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.exer.videoapi.NetVideo;
import com.exer.videoapi.NetVideoFrom;
import com.exer.videoapi.NetVideoHelper;
import com.exer.widgets.MyDialogAdapter;
import com.exer.widgets.MyListAdapter;
import com.exer.widgets.Tools;
import com.exer.widgets.VideoUrlItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private CoordinatorLayout coordinatorLayout;
    private View currentView;
    private List<NetVideo> NetVideoList = new ArrayList<>();
    private List<VideoUrlItem> videoUrlList = new ArrayList<>();
    private String SearchStr = "";
    private NetVideo netVideo;
    private boolean isPlayVideoInBrowser = false;
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

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        switch (id){
            case R.id.action_settings:
                if (item.isChecked()){
                    isPlayVideoInBrowser = true;
                    item.setTitle("浏览器打开视频");
                    item.setChecked(false);
                }else{
                    isPlayVideoInBrowser = false;
                    item.setTitle("软件内打开视频");
                    item.setChecked(true);
                }
                break;
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
                OpenPlatePage(NetVideoFrom.Youku);
                break;
            case R.id.nav_qqlive:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("腾讯视频");
                OpenPlatePage(NetVideoFrom.QQLive);
                break;
            case R.id.nav_iqiyi:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("爱奇艺");
                OpenPlatePage(NetVideoFrom.IQiYi);
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
    private NetVideoFrom pageType;
    private void OpenPlatePage(NetVideoFrom type){
        pageType = type;
        coordinatorLayout.removeView(currentView);
        LayoutInflater mInflater = LayoutInflater.from(this);
        mInflater.inflate(R.layout.youku,coordinatorLayout);
        currentView = findViewById(R.id.youku_layout);
        TextView searchYouku = currentView.findViewById(R.id.search_text_youku);
        searchYouku.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    SearchStr = v.getText().toString();
                    Tools.HideKeyboard(v);
                    v.clearFocus();
                    v.setFocusable(false);
                    v.setFocusableInTouchMode(false);
                    NetVideoList.clear();
                    ListView videoListView = currentView.findViewById(R.id.listview_youku);
                    MyListAdapter la = (MyListAdapter) videoListView.getAdapter();
                    la.notifyDataSetChanged();
                    new Thread(new Runnable() {
                        public void run() {
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putInt("MessageType",MessageType.YouKuSearch.ordinal());
                            msg.setData(data);
                            NetVideoList.addAll(NetVideoHelper.getNetVideoList(SearchStr, pageType));
                            handler.sendMessage(msg);
                        }
                    }).start();
                    GifImageView loading = currentView.findViewById(R.id.loading_youku);
                    loading.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        ListView videoListView = currentView.findViewById(R.id.listview_youku);
        NetVideoList.clear();
        MyListAdapter adapter = new MyListAdapter(MainActivity.this, NetVideoList);
        videoListView.setAdapter(adapter);
        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GifImageView loading = currentView.findViewById(R.id.loading_youku);
                loading.setVisibility(View.VISIBLE);
                netVideo = NetVideoList.get(i);
                new Thread(new Runnable() {
                    public void run() {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putInt("MessageType",MessageType.YouKuUrl.ordinal());
                        data.putString("VideoTitle",netVideo.getTitle());
                        msg.setData(data);
                        videoUrlList.clear();
                        videoUrlList.addAll(NetVideoHelper.getNetVideoUrlList(netVideo, pageType));
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
        new Thread(new Runnable() {
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt("MessageType",MessageType.YouKuSearch.ordinal());
                msg.setData(data);
                NetVideoList.addAll(NetVideoHelper.getNetVideoListByHome(pageType));
                handler.sendMessage(msg);
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (MessageType.values()[(int)(msg.getData().get("MessageType"))]){
                case YouKuSearch:
                    ListView videoListView = currentView.findViewById(R.id.listview_youku);
                    MyListAdapter la = (MyListAdapter) videoListView.getAdapter();
                    la.notifyDataSetChanged();
                    GifImageView loading = currentView.findViewById(R.id.loading_youku);
                    loading.setVisibility(View.INVISIBLE);

                    TextView search_text_youku = currentView.findViewById(R.id.search_text_youku);
                    search_text_youku.setFocusable(true);
                    search_text_youku.setFocusableInTouchMode(true);
                    new Thread(new Runnable() {
                        public void run() {
                            for(int i = 0; i < NetVideoList.size(); i++){
                                NetVideo nv = NetVideoList.get(i);
                                if (nv.getImg() == null && !(nv.getImgUrl() == null || nv.getImgUrl().isEmpty())){
                                    try {
                                        URL url = new URL(nv.getImgUrl());
                                        nv.setImg(BitmapFactory.decodeStream(url.openStream()));
                                        if(i % 11 == 0 || i == NetVideoList.size() - 1){
                                            Message msg = new Message();
                                            Bundle data = new Bundle();
                                            data.putInt("MessageType",MessageType.YouKuImg.ordinal());
                                            msg.setData(data);
                                            handler.sendMessage(msg);
                                        }
                                    } catch (java.io.IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();
                    break;
                case YouKuUrl:
                    GifImageView loading1 = currentView.findViewById(R.id.loading_youku);
                    loading1.setVisibility(View.INVISIBLE);
                    if (videoUrlList.size() > 0){
                        final MyDialogAdapter mdAdapter = new MyDialogAdapter(MainActivity.this, videoUrlList, (String)(msg.getData().get("VideoTitle")));
                        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                        ad.setTitle("清晰度");
                        ad.setAdapter(mdAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                if (!isPlayVideoInBrowser){
                                    Intent intent = new Intent(MainActivity.this, VideoPlayActivity.class);
                                    intent.putExtra("VideoUrl", videoUrlList.get(i).getUrl());
                                    intent.putExtra("VideoTitle", mdAdapter.getVideoTitle());
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    Uri content_url = Uri.parse(videoUrlList.get(i).getUrl());
                                    intent.setData(content_url);
                                    intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                                                                    startActivity(intent);
                                }
                            }
                        });
                        ad.show();
                    }else{
                        Toast.makeText(MainActivity.this, "操作频繁，10s后再试！",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case YouKuImg:
                    ListView videoListView1 = currentView.findViewById(R.id.listview_youku);
                    MyListAdapter la1 = (MyListAdapter) videoListView1.getAdapter();
                    la1.notifyDataSetChanged();
                    break;
                case ExitApp:
                    isExit = false;
                    break;
            }
        }
    };

    private static boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("MessageType",MessageType.ExitApp.ordinal());
            msg.setData(data);
            handler.sendMessageDelayed(msg, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

}
