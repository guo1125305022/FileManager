package com.gxl.filemanager;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gxl.filemanager.AppConfigData.FileConfigUlits;
import com.gxl.filemanager.adapter.FileFragmentAdapter;
import com.gxl.filemanager.dialog.TaskPageDialog;
import com.gxl.filemanager.fragment.FileFragment;
import com.gxl.filemanager.thread.FileLoad;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener, FileConfigUlits.OnLoadConfigListener {
    private static final String TAG = "MainActivity";
    private ViewPager viewPager;
    private FileFragmentAdapter fragmentAdapter;
    private PagerTitleStrip titleStrip;
    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initComponents() {
        Log.i(TAG, "initComponents: " + this.getFilesDir().getAbsolutePath());
        initTooBar();
        init();
    }

    private void initTooBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void init() {
        Log.i(TAG, "init: 开始初始化");
        viewPager = (ViewPager) findViewById(R.id.vp_main_fragments);
        titleStrip= (PagerTitleStrip) findViewById(R.id.pagerTitle);
        fragmentAdapter = new FileFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOnPageChangeListener(this);
        FileConfigUlits.loadList(this,this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        FileFragment fragment=fragmentAdapter.getFragments().get(viewPager.getCurrentItem());
        fragment.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
       switch (id){
           case R.id.menu_add_tab_root_file:
               addTable("/");
               break;
           case R.id.menu_add_tab_sd_file:
               addTable(Environment.getExternalStorageDirectory().getAbsolutePath());
               break;
           case  R.id.menu_exit_app:
               System.exit(0);
               break;
           case  R.id.menu_query_task_page:
               TaskPageDialog taskPageDialog=new TaskPageDialog(this,fragmentAdapter.getFragments());
               taskPageDialog.show();
               break;
       }

       return true;
    }

    private void addTable(String path){
        FileLoad fileLoad=new FileLoad();
        File file=new File(path);
        fileLoad.setCurrParentFile(file);
        FileFragment fileFragment=new FileFragment(file.getName());
        if (path.equals("/")){
            fileFragment.setTitle("/");
        }
        fileFragment.setLoad(fileLoad);
        fragmentAdapter.addFragments(fileFragment);
        viewPager.setCurrentItem(fragmentAdapter.getFragmentPosition(fileFragment));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSuccess(final ArrayList<FileLoad> list) {

        if (list == null || list.size() == 0) {
            FileFragment fileFragment = new FileFragment(Environment.getExternalStorageDirectory().getName());
            FileLoad fileLoad = new FileLoad();
            fileLoad.setCurrParentFile(Environment.getExternalStorageDirectory());
            fileFragment.setLoad(fileLoad);
            fragmentAdapter.addFragments(fileFragment);
            return;
        }

        ArrayList<FileFragment> fileFragments=new ArrayList<>();
        for (FileLoad fileLoad : list) {
            FileFragment fileFragment = new FileFragment(fileLoad.getCurrParentFile().getName());
            fileFragments.add(fileFragment);
        }
        fragmentAdapter.setFragments(fileFragments);
        fragmentAdapter.notifyDataSetChanged();

    }


}
