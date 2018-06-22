package com.icarbox.demo.webviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.startWeb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                1、打开项目的asset目录，创建新的文件demo.html
//                2、补充html代码：添加供本地调用的js方法、调用本地方法的js代码
//                3、补充java代码：本地加载js代码、提供给js调用的方法
                startActivity(new Intent().setClass(MainActivity.this, WebActivity.class));
                finish();
            }
        });
    }
}
