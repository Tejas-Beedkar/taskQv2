package com.taskq.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.taskq.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//    }


    @Override
    protected void onResume() {
        super.onResume();

        setTheme(R.style.AppTheme_Main);
        Intent modifyIntent = new Intent(MainActivity.this, TabActivity.class);
        startActivity(modifyIntent);
    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//    }


//    @Override
//    protected void onStop() {
//        super.onStop();
//    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

}
