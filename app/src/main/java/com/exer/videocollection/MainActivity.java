package com.exer.videocollection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exer.videoapi.NetVideo;
import com.exer.videoapi.NetVideoFrom;
import com.exer.videoapi.NetVideoHelper;
import com.exer.videoapi.anlysis.YouTubeAnalyze;
import com.exer.widgets.ClearEditText;
import com.exer.widgets.MyDialogAdapter;
import com.exer.widgets.MyListAdapter;
import com.exer.widgets.MyMediaController;
import com.exer.widgets.PlayerView;
import com.exer.widgets.Tools;
import com.exer.widgets.VideoUrlItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static com.exer.videoapi.NetVideoHelper.videoUrlApi;


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
        currentView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //LogUtils.i(LogUtils.LOG_TAG, "onTouchEvent");
                if ((new GestureDetector(MainActivity.this, new GestureListener())).onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        });
        //coordinatorLayout.addView(contentView);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navheaderView = navigationView.getHeaderView(0);
        navheaderView.setOnClickListener(this);
        TextView subbtn = findViewById(R.id.test_url);
        subbtn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String ss = v.getText().toString();
                    if (!ss.contains("http")) ss = Environment.getExternalStorageDirectory().getPath() + "/" + ss;
                    Intent intent = new Intent(MainActivity.this, VideoPlayActivity.class);
                    intent.putExtra("VideoUrl", ss);
                    intent.putExtra("VideoTitle", "测试");
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        subbtn.setOnClickListener(this);
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
//            case R.id.submit_test_url:
//                String url = ((ClearEditText)findViewById(R.id.test_url)).getText().toString();
//                Intent intent = new Intent(MainActivity.this, VideoPlayActivity.class);
//                intent.putExtra("VideoUrl", url);
//                intent.putExtra("VideoTitle", "测试");
//                startActivity(intent);
//                break;
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (webView != null){
            setCookies();
            webView.destroy();
            webView = null;
        }
        switch (id){
            case R.id.nav_live:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("直播");
                OpenPlatePage(NetVideoFrom.Live);
                break;
            case R.id.nav_youku:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("优酷");
                //OpenPlatePage(NetVideoFrom.Youku);
                OpenWebViewPage(NetVideoFrom.Youku);
                break;
            case R.id.nav_iqiyi:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("爱奇艺");
//                OpenPlatePage(NetVideoFrom.IQiYi);
                OpenWebViewPage(NetVideoFrom.IQiYi);
                break;
            case R.id.nav_cloundmusic:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("云音乐");
                OpenPlatePage(NetVideoFrom.CloudMusic);
                break;
            case R.id.nav_qqlive:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("腾讯视频");
//                OpenPlatePage(NetVideoFrom.QQLive);
                OpenWebViewPage(NetVideoFrom.QQLive);
                break;
            case R.id.nav_bilibili:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("哔哩哔哩");
//                OpenPlatePage(NetVideoFrom.Bilibili);
                OpenWebViewPage(NetVideoFrom.Bilibili);
                break;
            case R.id.nav_youtube:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("YouTube");
                OpenWebViewPage(NetVideoFrom.YouTube);
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
        return true;
    }
    private NetVideoFrom pageType;
    private WebView webView;
    private String youtubeVid;
    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void OpenWebViewPage(NetVideoFrom type){
        pageType = type;
        coordinatorLayout.removeView(currentView);
        LayoutInflater mInflater = LayoutInflater.from(this);
        mInflater.inflate(R.layout.webview_page_layout,coordinatorLayout);
        currentView = findViewById(R.id.findVideo_layout);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (webView != null){
            setCookies();
            webView.destroy();
            webView = null;
        }
        webView = currentView.findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //设置 缓存模式
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        webView.loadUrl(NetVideoHelper.WebViewUrlAPI.get(type));

        final GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureListener());
        webView.setClickable(true);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event))
                    return true;
                return false;
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (errorCode == ERROR_TIMEOUT || errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT  || errorCode == ERROR_FAILED_SSL_HANDSHAKE){
                    view.loadUrl("file:///android_asset/404.html");
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
//                if (pageType == NetVideoFrom.YouTube && NetVideoHelper.checkWebViewUrl(url, pageType)){
//                    youtubeVid = Tools.getStrWithRegular("/watch\\?[\\s\\S]+v=([\\s\\S]+)",url);;
//                    ProgressBar loading2 = currentView.findViewById(R.id.loading_youku);
//                    loading2.setVisibility(View.VISIBLE);
//                    webView.loadUrl("https://www.findyoutube.net/");
//                }else {
//                    super.onLoadResource(view, url);
//                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return super.shouldInterceptRequest(view,url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (NetVideoHelper.checkWebViewUrl(url, pageType)) {
                    Toast.makeText(MainActivity.this, "点击视频链接" + url,
                            Toast.LENGTH_SHORT).show();
                    ProgressBar loading2 = currentView.findViewById(R.id.loading_youku);
                    loading2.setVisibility(View.VISIBLE);
                    new MyThread(url) {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("VideoTitle",NetVideoHelper.getVideoTitleByUrl(param, pageType));
                            String url =  NetVideoHelper.getVideoUrl(param, pageType);
                            if (url =="") return;
                            data.putString("VideoUrl",NetVideoHelper.getVideoUrl(param, pageType));
                            data.putInt("MessageType",MessageType.OpenVideo.ordinal());
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    }.start();
                    return true;
                }else if(NetVideoHelper.getIsShield(url, pageType)){
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!(pageType == NetVideoFrom.YouTube && url.contains("findyoutube.net"))){
                    ProgressBar loading2 = currentView.findViewById(R.id.loading_youku);
                    loading2.setVisibility(View.INVISIBLE);
                }
                Toast.makeText(MainActivity.this, "页面开始加载！" + url,
                        Toast.LENGTH_SHORT).show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Toast.makeText(MainActivity.this, "页面加载完成！" + url,
                        Toast.LENGTH_SHORT).show();
                webView.loadUrl("javascript:" + NetVideoHelper.getPageLoadedJs(pageType));
                super.onPageFinished(view, url);
//                if (pageType == NetVideoFrom.YouTube && url.contains("findyoutube.net")){
//                    webView.loadUrl("javascript:function getYoutubeUrlHtml(){var str='';$.ajax({type:'post',url:'https://www.findyoutube.net/result',data:{url:'https://www.youtube.com/watch?v=" + youtubeVid + "'},async:false,success:function(data){str=data;}});return str;}");
//                    webView.evaluateJavascript("javascript:getYoutubeUrlHtml()", new ValueCallback<String>() {
//                        @Override
//                        public void onReceiveValue(String value) {
//                            value = value.replace("&amp;","&").replace("\\u003C","<");
//                            String[] info = YouTubeAnalyze.getVideoInfoByHtml(value);
//                            if (info.length == 2){
//                                new MyThread(info[0] + "%%%%%" + info[1]) {
//                                    @Override
//                                    public void run() {
//                                        String[] info = param.split("%%%%%");
//                                        if (info.length == 2){
//                                            Message msg = new Message();
//                                            Bundle data = new Bundle();
//                                            data.putString("VideoTitle",info[0]);
//                                            data.putString("VideoUrl",info[1]);
//                                            data.putInt("MessageType",MessageType.OpenVideo.ordinal());
//                                            msg.setData(data);
//                                            handler.sendMessage(msg);
//                                        }
//                                    }
//                                }.start();
//                            }
//                            System.out.println(value);
//                            webView.goBack();
//                            webView.goBack();
//                            ProgressBar loading2 = currentView.findViewById(R.id.loading_youku);
//                            loading2.setVisibility(View.INVISIBLE);
//                        }
//                    });
//                }
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view,url);
            }
        });
    }
    public void setCookies(){
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        CookieSyncManager.getInstance().sync();
    }
    private void OpenPlatePage(final NetVideoFrom type){
        pageType = type;
        coordinatorLayout.removeView(currentView);
        LayoutInflater mInflater = LayoutInflater.from(this);
        mInflater.inflate(R.layout.youku,coordinatorLayout);
        currentView = findViewById(R.id.youku_layout);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (type != NetVideoFrom.Live){
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
                                data.putInt("MessageType",MessageType.Search.ordinal());
                                msg.setData(data);
                                NetVideoList.addAll(NetVideoHelper.getNetVideoList(SearchStr, pageType));
                                handler.sendMessage(msg);
                            }
                        }).start();
                        ProgressBar loading = currentView.findViewById(R.id.loading_youku);
                        loading.setVisibility(View.VISIBLE);
                    }
                    return false;
                }
            });
        }else{
            TextView searchYouku = currentView.findViewById(R.id.search_text_youku);
            searchYouku.setVisibility(View.GONE);
        }
        ListView videoListView = currentView.findViewById(R.id.listview_youku);
        NetVideoList.clear();
        MyListAdapter adapter = new MyListAdapter(MainActivity.this, NetVideoList);
        videoListView.setAdapter(adapter);
        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProgressBar loading = currentView.findViewById(R.id.loading_youku);
                loading.setVisibility(View.VISIBLE);
                netVideo = NetVideoList.get(i);
                new Thread(new Runnable() {
                    public void run() {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("VideoTitle",netVideo.getTitle());
                        switch (pageType){
//                                data.putInt("MessageType",MessageType.OpenVideo.ordinal());
//                                data.putString("VideoUrl",NetVideoHelper.getVideoUrlFrom163ren(netVideo.getVideoUrl()));
//                                break;
                            case Live:
                                data.putInt("MessageType",MessageType.OpenVideo.ordinal());
                                data.putString("VideoUrl",netVideo.getVideoUrl());
                                break;
                            case Bilibili:
                                data.putInt("MessageType",MessageType.OpenVideo.ordinal());
                                data.putString("VideoUrl",NetVideoHelper.getVideoUrlFromBiliBili(netVideo.getVideoUrl()));
                                break;
                            case CloudMusic:
                                data.putInt("MessageType",MessageType.Url.ordinal());
                                videoUrlList.clear();
                                videoUrlList.addAll(NetVideoHelper.getNetVideoUrlList(netVideo, pageType));
                                break;
                            case Youku:
                            case IQiYi:
                            default:
                                data.putInt("MessageType",MessageType.OpenVideo.ordinal());
                                data.putString("VideoUrl",NetVideoHelper.getVideoUrlFromThird(netVideo.getVideoUrl()));
                                break;
                        }
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
        new Thread(new Runnable() {
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt("MessageType",MessageType.Search.ordinal());
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
                case Search:
                    ListView videoListView = currentView.findViewById(R.id.listview_youku);
                    MyListAdapter la = (MyListAdapter) videoListView.getAdapter();
                    la.notifyDataSetChanged();
                    ProgressBar loading = currentView.findViewById(R.id.loading_youku);
                    loading.setVisibility(View.INVISIBLE);

                    TextView search_text_youku = currentView.findViewById(R.id.search_text_youku);
                    search_text_youku.setFocusable(true);
                    search_text_youku.setFocusableInTouchMode(true);
                    new Thread(new Runnable() {
                        public void run() {
                            for(int i = 0; i < NetVideoList.size(); i++){
                                NetVideo nv = NetVideoList.get(i);
                                if (nv.getImg() == null && !(nv.getImgUrl() == null || nv.getImgUrl().isEmpty())){
                                    nv.setImg(Tools.getImageThumbnail(nv.getImgUrl(), 320, 180));
                                    if ((i+1) % 6 == 0 || (i+1) == NetVideoList.size()){
                                        Message msg = new Message();
                                        Bundle data = new Bundle();
                                        data.putInt("MessageType",MessageType.Img.ordinal());
                                        msg.setData(data);
                                        handler.sendMessage(msg);
                                    }
                                }
                            }
                        }
                    }).start();
                    break;
                case Url:
                    ProgressBar loading1 = currentView.findViewById(R.id.loading_youku);
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
                                    Intent intent = new Intent(MainActivity.this, IJKVideoPlayActivity.class);
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
                case Img:
                    ListView videoListView1 = currentView.findViewById(R.id.listview_youku);
                    if (videoListView1 != null){
                        MyListAdapter la1 = (MyListAdapter) videoListView1.getAdapter();
                        la1.notifyDataSetChanged();
                    }
                    break;
                case OpenVideo:
                    ProgressBar loading2 = currentView.findViewById(R.id.loading_youku);
                    loading2.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(MainActivity.this, IJKVideoPlayActivity.class);
                    intent.putExtra("VideoUrl", (String)(msg.getData().get("VideoUrl")));
                    intent.putExtra("VideoTitle", (String)(msg.getData().get("VideoTitle")));
                    startActivity(intent);
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

    private abstract class MyThread extends Thread{
        public String param;
        public MyThread(String param)
        {
            this.param = param;
        }
        public abstract void run();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            float mOldX = e1.getX(), mOldY = e1.getY();
//            int y = (int) e2.getRawY();
//            int x = (int) e2.getRawX();
//            Display disp = (MainActivity.this).getWindowManager().getDefaultDisplay();
//            int windowWidth = disp.getWidth();
//            int windowHeight = disp.getHeight();
//            DrawerLayout drawer = findViewById(R.id.drawer_layout);
//                if (x > mOldX){
//                    if (!drawer.isDrawerOpen(GravityCompat.START)) {
//                        drawer.openDrawer(GravityCompat.START);
//                    }
//                    return true;
//                }
//            return false;
//        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            int screenWidth = Tools.getScreenSize(MainActivity.this)[0];
            int screenHeight = Tools.getScreenSize(MainActivity.this)[1];
            if (Math.abs(-deltaX / screenWidth) > 0.03 && Math.abs(-deltaY / screenHeight) < 0.015){
                if (-deltaX / screenWidth < 0){
                    if (webView != null){
                        webView.goForward();
                    }
                    //Toast.makeText(MainActivity.this, "向右", Toast.LENGTH_SHORT).show();
                }else{
                    if (webView != null){
                        webView.goBack();
                    }
                    //Toast.makeText(MainActivity.this, "向左", Toast.LENGTH_SHORT).show();
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (webView != null){
                webView.loadUrl("javascript:" + NetVideoHelper.getPageLoadedJs(pageType));
            }
            return super.onDown(e);
        }
    }
}
