package com.example.administrator.mybluetooth.activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Constant.DfthDeviceState;
import com.dfth.sdk.device.DfthECGDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.listener.DfthDeviceDataListener;
import com.dfth.sdk.listener.DfthDeviceStateListener;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.model.ecg.EcgDataTransmitted;
import com.dfth.sdk.permission.DfthPermissionException;
import com.dfth.sdk.permission.DfthPermissionManager;
import com.example.administrator.mybluetooth.R;
import com.example.administrator.mybluetooth.widget.WaveView;
/**
 * Created by leezhiqiang on 2017/3/10.
 */

public abstract class BaseECGActivity<T extends DfthECGDevice> extends AppCompatActivity implements View.OnClickListener,DfthDeviceStateListener,DfthDeviceDataListener<EcgDataTransmitted,ECGResult>{
    protected TextView mState;
    protected T mDevice;
    protected WaveView waveView;
    protected TextView measure_time;
    protected TextView currentDevice;
    protected TextView mHeartRate;
    protected boolean mIsEnd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        mState = (TextView) findViewById(R.id.state);
        measure_time = (TextView) findViewById(R.id.measure_time);
        currentDevice = (TextView) findViewById(R.id.deviceInfo);
        mHeartRate = (TextView) findViewById(R.id.heart_rate);
        findViewById(R.id.scan).setOnClickListener(this);
        findViewById(R.id.createAndConnect_ecg).setOnClickListener(this);
        findViewById(R.id.startMeasure_ecg).setOnClickListener(this);
        findViewById(R.id.stopMeasure_ecg).setOnClickListener(this);
        findViewById(R.id.disconnect_ecg).setOnClickListener(this);
        findViewById(R.id.timer_measure).setOnClickListener(this);
        findViewById(R.id.getVersion).setOnClickListener(this);
        findViewById(R.id.search_history).setOnClickListener(this);
        initializeWaveView((LinearLayout) findViewById(R.id.wave_view));

    }
    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View v) {
        if(!DfthSDKManager.getManager().isOauthSuccess()){
            toast("应用验证不成功");
        }
        switch (v.getId()) {
            //扫描设备
            case R.id.scan:
                try {
                    scanDevice();
                } catch (DfthPermissionException e) {
                    DfthPermissionManager.requestPermission(this,e.getPermission(),100);
                }
                break;
            //连接心电设备
            case R.id.createAndConnect_ecg:
                if(mDevice != null){
                    mDevice.connect().asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(final DfthResult<Boolean> response) {
                            Log.e("dfth_sdk","connect__status->" + response.getReturnData());
                            toast(response.getReturnData() ? "连接设备成功" : response.getErrorMessage());
                            if(response.getReturnData()) {
                                bindDataListener();
                            }
                        }
                    });
                }
                break;
            //开始测量心电
            case R.id.startMeasure_ecg:
                if(mDevice != null){
                    mIsEnd = true;
                    mDevice.startMeasure(0).asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            toast(response.getReturnData() ? "开始测量成功" : response.getErrorMessage());
                            if(response.getReturnData()){
                                waveView.startDraw();
                            }
                            Log.e("dfth_sdk","deviceStart->" + response.getReturnData());
                        }
                    });
                }
                break;
            //停止测量心电
            case R.id.stopMeasure_ecg:
                if(mDevice != null){
                    mIsEnd = true;
                    mDevice.stopMeasure().asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            toast(response.getReturnData() ? "结束测量成功" : response.getErrorMessage());
                            if(response.getReturnData()){
                                waveView.endDraw();
                            }
                            Log.e("dfth_sdk","deviceStop->" + response.getReturnData());
                        }
                    });
                }
                break;
            //心电设备断开连接
            case R.id.disconnect_ecg:
                if(mDevice != null){
                    mIsEnd = true;
                    mDevice.disconnect().asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            if(response.getReturnData()){
                                waveView.endDraw();
                            }
                            toast(response.getReturnData() ? "断开设备成功" : response.getErrorMessage());
                            Log.e("dfth_sdk","deviceDisconnect->" + response.getReturnData());
                        }
                    });
                }
                break;
            case R.id.getVersion: {
                if(mDevice != null){
                    mDevice.queryDeviceVersion().asyncExecute(new DfthCallBack<String>() {
                        @Override
                        public void onResponse(DfthResult<String> response) {
                            if (!TextUtils.isEmpty(response.getReturnData())) {
                                Log.e("dfth_sdk", "deviceVersion->" + response.getReturnData());
                                toast("设备版本号:" + response.getReturnData());
                            } else {
                                toast(response.getErrorMessage());
                            }
                        }
                    });
                }
            }
            break;
            case R.id.search_history:{
                searchHistory();
            }
            break;
            case R.id.timer_measure:{
                if(mDevice != null){
                    mIsEnd = false;
                    mDevice.startMeasure(1).asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            toast(response.getReturnData() ? "开始测量成功" : response.getErrorMessage());
                            if(response.getReturnData()){
                                waveView.startDraw();
                            }
                            Log.e("dfth_sdk","deviceStart->" + response.getReturnData());
                        }
                    });
                }
                break;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        switch (requestCode) {
            case 100:{
                DfthPermissionManager.verifyPermission(this,permissions[0],requestCode);
            }
            break;
            default:
                break;
        }
    }
    @Override
    public void onStateChange(int state) {
        switch (state){
            case DfthDeviceState.DISCONNECTED:{
                mState.setText("设备未连接");
                if(mIsEnd){
                    measure_time.setText(getTime(0));
                    mHeartRate.setText(String.format("%d bpm",0));
                    waveView.endDraw();
                }
            }
            break;
            case DfthDeviceState.MEASURING:{
                mState.setText("设备测量中");
            }
            break;
            case DfthDeviceState.CONNECTED:{
                mState.setText("设备已连接");
            }
            break;
        }
    }

    protected String getTime(long time){
        int second = (int) (time / 1000);
        int minute = second / 60;
        second %= 60;
        int hour = minute / 60;
        minute %= 60;
        return String.format("%02d:%02d:%02d",hour,minute,second);
    }

    protected void toast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    protected abstract void scanDevice() throws DfthPermissionException;

    protected abstract void searchHistory();

    protected abstract void bindDataListener();

    protected abstract void initializeWaveView(ViewGroup parent);
    @Override
    public void onDataChanged(EcgDataTransmitted data) {
        long time = System.currentTimeMillis() - data.getStartTime();
        mHeartRate.setText(String.format("%d bpm",data.getHeartRate()));
        measure_time.setText(getTime(time));
        waveView.drawWave(data);
    }

    @Override
    public void onBatteryChanged(float battery) {

    }

    @Override
    public void onResultData(ECGResult result) {
        Log.e("dfth_sdk",result.toString());
        measure_time.setText(getTime(0));
        mHeartRate.setText(String.format("%d bpm",0));
        waveView.endDraw();
    }
}
