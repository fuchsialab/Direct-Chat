package com.fuchsia.directchat;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.fuchsia.directchat.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainActivity extends AppCompatActivity implements UpdateHelper.OnUpdateCheckListener {

    ActivityMainBinding binding;
    CallLogViewPagerAdapter adapter;

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    private long backPressTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initComponents();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navdrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));

        UpdateHelper.with(this)
                .onUpdateCheck(this)
                .check();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menuHome:

                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;

                    case R.id.menuprivacy:
                        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.lkmkm)));
                        startActivity(browse);
                        drawerLayout.closeDrawer(GravityCompat.START);

                        return true;

                    case R.id.menurate:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        final String appPackageName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }

                        return true;

                    case R.id.menuwhatsapp:

                        drawerLayout.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.fuchsia.saver")));


                        return true;

                    case R.id.menumoreapp:
                        Intent browses = new Intent(Intent.ACTION_VIEW, Uri.parse(("https://play.google.com/store/apps/collection/cluster?clp=igM4ChkKEzUzNjIwODY3OTExNjgyNTA2MTkQCBgDEhkKEzUzNjIwODY3OTExNjgyNTA2MTkQCBgDGAA%3D:S:ANO1ljJMw2s&gsr=CjuKAzgKGQoTNTM2MjA4Njc5MTE2ODI1MDYxORAIGAMSGQoTNTM2MjA4Njc5MTE2ODI1MDYxORAIGAMYAA%3D%3D:S:ANO1ljI3U6g")));
                        startActivity(browses);
                        drawerLayout.closeDrawer(GravityCompat.START);

                        return true;

                    case R.id.menushare:

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "Download Direct Chat App.  https://play.google.com/store/apps/details?id=com.fuchsia.directchat";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Direct Chat App");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        drawerLayout.closeDrawer(GravityCompat.START);

                        return true;

                    case R.id.menuexit:

                        finishAffinity();

                        return true;
                }
                return false;


            }
        });


    }


    private void initComponents(){
        setSupportActionBar(binding.toolbar);

        setUpViewPager();
    }

    private void setUpViewPager(){
        binding.tabs.setupWithViewPager(binding.contentView.viewpager);
        adapter = new CallLogViewPagerAdapter(getSupportFragmentManager());
        AllCallLogFragment fragment1 = new AllCallLogFragment();
        ChatFragment oFragment = new ChatFragment();

        adapter.addFragment("Send Message", oFragment);
        adapter.addFragment("Chat History",fragment1);


        binding.contentView.viewpager.setAdapter(adapter);
    }

    static class CallLogViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public CallLogViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(String title, Fragment fragment){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



    @Override
    public void onBackPressed() {


        if (backPressTime+2000>System.currentTimeMillis()){

            finishAffinity();


        }else {
            Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT).show();
        }

        backPressTime= System.currentTimeMillis();


    }


    @Override
    public void onUpdateCheckListener(String urlApp) {
        final String appPackageName = getPackageName();

        AlertDialog alertDialog=new AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage(" Please update for better experience")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

                    }
                }).setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        alertDialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 123){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpViewPager();
            }else{
                finish();
            }
        }
    }
}