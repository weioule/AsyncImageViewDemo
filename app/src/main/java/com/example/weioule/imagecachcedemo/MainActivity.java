package com.example.weioule.imagecachcedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Create on 2018/6/13.
 * author weioule
 */

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.imageview_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecyAdapter(initData()));
    }

    private List initData() {
        List<String> urlList = new ArrayList<>();
        urlList.add("http://img.ivsky.com/img/tupian/pre/201801/30/pubu.jpg");
        urlList.add("http://img.ivsky.com/img/tupian/pre/201801/30/pubu-001.jpg");
        urlList.add("http://img.ivsky.com/img/tupian/pre/201801/30/pubu-016.jpg");
        urlList.add("http://img.ivsky.com/img/tupian/pre/201801/30/pubu-006.jpg");
        urlList.add("http://img.ivsky.com/img/tupian/pre/201801/30/pubu-007.jpg");
        urlList.add("http://img.ivsky.com/img/tupian/pre/201801/30/pubu-008.jpg");
        urlList.add("http://img.ivsky.com/img/tupian/pre/201801/30/pubu-009.jpg");
        urlList.add("http://img.ivsky.com/img/tupian/pre/201801/30/pubu-010.jpg");
        urlList.add("http://img.ivsky.com/img/tupian/pre/201801/30/pubu-011.jpg");
        urlList.add("http://img.ivsky.com/img/tupian/pre/201801/30/pubu-012.jpg");
        urlList.add("http://img.ivsky.com/img/tupian/pre/201801/30/pubu-013.jpg");
        urlList.add("http://img.ivsky.com/img/tupian/pre/201801/30/pubu-015.jpg");
        return urlList;
    }
}
