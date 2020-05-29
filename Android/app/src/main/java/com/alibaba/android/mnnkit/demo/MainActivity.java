package com.alibaba.android.mnnkit.demo;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.mnnkit.demo.utils.PermissionUtils;
import com.alibaba.android.mnnkit.monitor.MNNMonitor;
import com.tsia.example.mnnkitdemo.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("MNNKit Demo");
        }

        MNNMonitor.setMonitorEnable(true);
        // Android M动态申请权限，摄像头+存储访问
        PermissionUtils.askPermission(this,new String[]{Manifest.permission.CAMERA, Manifest
                .permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},10, initViewRunnable);
    }

    private Runnable initViewRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    public void onFaceDetection(View v) {
        startActivity(new Intent(MainActivity.this, FaceDetectionActivity.class));
    }

    public void onHandGestureDetection(View v) {
        startActivity(new Intent(MainActivity.this, HandGestureDetectionActivity.class));
    }

    public void onPortraitSegmentation(View v) {
        startActivity(new Intent(MainActivity.this, PortraitSegmentationActivity.class));
    }

    public void onImageTest(View v){
        startActivity(new Intent(MainActivity.this, FaceDetectionImageTestActivity.class));
    }

}
