package com.tistory.lky1001.androidonvif;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tistory.lky1001.androidonvif.lib.OnvifManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OnvifManager onvifManager = new OnvifManager();
    }
}
