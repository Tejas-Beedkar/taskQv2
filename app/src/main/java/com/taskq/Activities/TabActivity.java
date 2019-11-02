package com.taskq.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.taskq.CustomClasses.taskQGlobal;
import com.taskq.CustomClasses.taskQviewModel;
import com.taskq.DataBase.dBaseManager;
import com.taskq.Fragments.AllFragment;
import com.taskq.Fragments.HomeFragment;
import com.taskq.Fragments.WhatFragment;
import com.taskq.Fragments.WhenFragment;
import com.taskq.Fragments.WhoFragment;
import com.taskq.Settings.taskQSettings;
import com.taskq.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TabActivity extends AppCompatActivity {

    private Button tab_Button_NewItem;
    private TabLayout tab_TabLayout;
    private taskQSettings Settings;
    private Toolbar tab_Toolbar;
    private ViewPager tab_ViewPager;
    private int backButtonCount;
    private int[] tabIcons = {
            R.drawable.ic_tab_home_light,
            R.drawable.ic_tab_all_light,
            R.drawable.ic_tab_what_light,
            R.drawable.ic_tab_when_light,
            R.drawable.ic_tab_who_light
    };
    private taskQviewModel tagsDialogViewModel;
    private TextView tab_DashBoard;
    private dBaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        tab_Toolbar = findViewById(R.id.tab_toolbar);
        tab_TabLayout = findViewById(R.id.tab_TabLayout);
        tab_ViewPager = findViewById(R.id.tab_ViewPager);
        tab_DashBoard = findViewById(R.id.EditText_DashBoard);

        //==========================================================================================
        //          Feature - 003
        //          Load the tabs with images and text
        //==========================================================================================
        setSupportActionBar(tab_Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name_with_copyright);
        setupViewPager(tab_ViewPager);
        tab_TabLayout.setupWithViewPager(tab_ViewPager);
        setupTabIcons();

        dbManager = new dBaseManager(this);
        dbManager.open();

        tagsDialogViewModel =  ViewModelProviders.of(this).get(taskQviewModel.class);

    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//    }


    @Override
    protected void onResume() {
        super.onResume();

        Long lDateToday;
        Long lDate_pls1;
        Cursor cursor;
        int intActive;
        int intDue;
        int intCompleted;

        //Step 1 - Construct search queries
        //
        Calendar calenderToday = Calendar.getInstance();
        try {
            String strDateCheck =  new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

            calenderToday.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(strDateCheck));
            calenderToday.add(Calendar.DATE, 0);
            lDateToday = calenderToday.getTimeInMillis();
            calenderToday.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(strDateCheck));
            calenderToday.add(Calendar.DATE, 1);
            lDate_pls1 = calenderToday.getTimeInMillis();

        }catch(java.text.ParseException e) {
            e.printStackTrace();
            lDateToday = Calendar.getInstance().getTimeInMillis();
            lDate_pls1 = Calendar.getInstance().getTimeInMillis();
        }

        //Step 2 - make the counts
        cursor = dbManager.fetch();
        intActive = cursor.getCount();

        cursor = dbManager.fetchEntryByWhen_NoCompleted(lDateToday, lDate_pls1);
        intDue = cursor.getCount();

        cursor = dbManager.fetchEntryByWhen(lDateToday, lDate_pls1);
        intCompleted = cursor.getCount();
        intCompleted = intCompleted - intDue;

        String strDashboard = "      " + intDue + "  -  Due Today \n" + "      " + intCompleted + "  -  Completed Today \n" + "      " + intActive  + "  -  Active Tasks";
        tab_DashBoard.setText(strDashboard);

        //Feature - 002 distributed code
        setTheme(R.style.AppTheme_Main);

        //Feature - 004 distributed code
        backButtonCount = 0;

        //Feature - 005 distributed code
        Settings = new taskQSettings(getApplicationContext());

        //Feature - 006 distributed code
        tab_Button_NewItem = findViewById(R.id.tab_Button_NewItem);
        tab_Button_NewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if(true == tagsDialogViewModel.bSetUserEntryNew()){
                if(true == ((taskQGlobal) getApplication()).bSetUserEntryNew()){
                    Intent modifyIntent = new Intent(TabActivity.this, taskQEntryActivity.class);
                    startActivity(modifyIntent);
                }
                else{
                    Toast.makeText(TabActivity.this, "Cannot add new activity. App Error.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//    }


//    @Override
//    protected void onStop() {
//        super.onStop();
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }


    //Feature - 003 distributed code
    private void setupTabIcons() {
        tab_TabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tab_TabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tab_TabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tab_TabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tab_TabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    //Feature - 003 distributed code
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment(), "HOME");
        adapter.addFrag(new AllFragment(), "ALL");
        adapter.addFrag(new WhatFragment(), "WHAT");
        adapter.addFrag(new WhenFragment(), "WHEN");
        adapter.addFrag(new WhoFragment(), "WHO");
        viewPager.setAdapter(adapter);
    }

    //Feature - 003 distributed code
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //==========================================================================================
    //          Feature - 004
    //          Prevents the back button from exiting the app on the first press.
    //          Avoids that ungraceful exit.
    //==========================================================================================
    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(TabActivity.this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }


    //Feature - 005 distributed code
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        MenuItem sCompletedswitch = menu.findItem(R.id.tab_activity_menu_switch_completed);
        sCompletedswitch.setChecked(Settings.getSwitchShowCompleted());
        return true;
    }

    //Feature - 005 distributed code
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tab_activity_menu_switch_completed:
                if(item.isChecked())
                {
                    item.setChecked(false);
                    Settings.setSwitchShowCompleted(false);

                    //ToDo - Preserve the last view
                    //Force a activity reload as a refresh. Immidiatly refreshes the list views
                    Intent modifyIntent = new Intent(TabActivity.this, MainActivity.class);
                    startActivity(modifyIntent);
                }
                else
                {
                    item.setChecked(true);
                    Settings.setSwitchShowCompleted(true);

                    //ToDo - Preserve the last view
                    //Force a activity reload as a refresh. Immidiatly refreshes the list views
                    Intent modifyIntent = new Intent(TabActivity.this, MainActivity.class);
                    startActivity(modifyIntent);
                }
                break;
            case R.id.tab_activity_menu_show_all:
                Toast.makeText(this, getString(R.string.app_name_with_copyright) + " " + getString(R.string.app_version), Toast.LENGTH_LONG).show();
                break;
            case R.id.tab_activity_menu_about:
                Toast.makeText(this, getString(R.string.app_name_with_copyright) + " " + getString(R.string.app_version), Toast.LENGTH_LONG).show();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }
}
