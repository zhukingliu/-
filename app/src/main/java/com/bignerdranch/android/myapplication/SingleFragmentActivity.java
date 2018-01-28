package com.bignerdranch.android.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by T540p on 2017/6/7.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity{
    protected abstract Fragment createFragment();
    @Override
    public  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm=getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.fragment_container);

        if(fragment==null){
            fragment=createFragment();
            fm.beginTransaction().add(R.id.fragment_container,fragment).commit();
        }
    }
}
