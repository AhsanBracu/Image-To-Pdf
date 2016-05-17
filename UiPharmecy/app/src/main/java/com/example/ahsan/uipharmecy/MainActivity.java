package com.example.ahsan.uipharmecy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ahsan.uipharmecy.util.Constant;
import com.example.ahsan.uipharmecy.util.ExifUtil;
import com.example.ahsan.uipharmecy.util.BitmapUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager mFragmentManager;
    private MainFragment mMainFragMent;
    private Bitmap mBitmap, mOrientedBitmap;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        mContext = getApplicationContext();
        mFragmentManager = getFragmentManager();
        setView();
    }

    public void setView() {
        mMainFragMent = new MainFragment();
        android.app.FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.group, mMainFragMent, getResources().getString(R.string.main));
        fragmentTransaction.commit();
    }

    void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_photo) {
            startActivityForResult(BitmapUtils.getPickImageChooserIntent(MainActivity.this), Constant.IMAGE_PICKER_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.IMAGE_PICKER_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = BitmapUtils.getPickImageResultUri(data, mContext);
                String picturePath = BitmapUtils.getRealPathFromURI(imageUri, mContext);
                boolean requirePermissions = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        BitmapUtils.isUriRequiresPermissions(imageUri, mContext)) {
                    requirePermissions = true;
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
                if (!requirePermissions) {
                    mBitmap = BitmapUtils.makeBitmap(imageUri, mContext);
                    mOrientedBitmap = ExifUtil.rotateBitmap(picturePath, mBitmap);
                    mMainFragMent.setImageView(mOrientedBitmap);
                }
            }
        }
    }
}