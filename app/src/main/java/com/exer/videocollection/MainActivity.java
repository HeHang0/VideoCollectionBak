package com.exer.videocollection;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.Toast;


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
            return true;
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_youku:
                ((Toolbar)findViewById(R.id.toolbar)).setTitle("优酷");
                coordinatorLayout.removeView(currentView);
                LayoutInflater mInflater = LayoutInflater.from(this);
                mInflater.inflate(R.layout.youku,coordinatorLayout);
                currentView = findViewById(R.id.youku_layout);
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
}
