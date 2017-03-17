package com.example.administrator.mybluetooth.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mybluetooth.R;

import java.util.ArrayList;
import java.util.List;

import kr.co.namee.permissiongen.PermissionGen;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "DfthTestMain";

//    private DBRecordUser mUser;
    private long exitTime = 0;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ECG = (Button) findViewById(R.id.go_ecg);
        Button BP = (Button) findViewById(R.id.go_bp);
        findViewById(R.id.go_single_ecg).setOnClickListener(this);
        findViewById(R.id.go_printer).setOnClickListener(this);
        findViewById(R.id.go_service).setOnClickListener(this);
        ECG.setOnClickListener(this);
        BP.setOnClickListener(this);
        TextView version = (TextView) findViewById(R.id.sdk_version);
        String pkName = getPackageName();
        try {
            version.setText(getPackageManager().getPackageInfo(pkName,0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //进入心电页面
            case R.id.go_ecg:
                Intent ecg_Intent = new Intent();
                ecg_Intent.setClass(this, ShowECGActivity.class);
                startActivity(ecg_Intent);
                break;
            //进入血压页面
            case R.id.go_bp:
                Intent bp_Intent = new Intent();
                bp_Intent.setClass(this, ShowBPActivity.class);
                startActivity(bp_Intent);
                break;
            //进入打印页面
            case R.id.go_printer:
                Intent printer_Intent = new Intent();
                printer_Intent.setClass(this, ShowPrinterActivity.class);
                startActivity(printer_Intent);
                break;
            case R.id.go_service:
                Intent service = new Intent();
                service.setClass(this, TestNetworkService.class);
                startActivity(service);
                break;
            case R.id.go_single_ecg:{
                Intent single = new Intent();
                single.setClass(this, ShowSingleECGActivity.class);
                startActivity(single);
            }
        }
    }
}
