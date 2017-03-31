package com.example.administrator.mybluetooth.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Constant.DfthDeviceState;
import com.dfth.sdk.Others.Utils.BluetoothUtils;
import com.dfth.sdk.device.DfthBpDevice;
import com.dfth.sdk.listener.DfthBpDeviceDataListener;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.listener.DfthDeviceStateListener;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;
import com.dfth.sdk.model.bp.BpStatus;
import com.dfth.sdk.permission.DfthPermissionException;
import com.dfth.sdk.permission.DfthPermissionManager;
import com.example.administrator.mybluetooth.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by RuiYu on 2016/10/24.
 */
public class ShowBPActivity extends AppCompatActivity implements View.OnClickListener, DfthBpDeviceDataListener,DfthDeviceStateListener {

    TextView mState;
    TextView currentDevice;
    TextView bp_result;
    DfthBpDevice mBpDevice = null;
    Spinner day_mSpinner;
    Spinner night_mSpinner;
    public static final int DAY_SPACE_TIME = 16;
    public static final int NIGHT_SPACE_TIME = 8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp);
        init();
    }

    public void init() {
        mState = (TextView) findViewById(R.id.state);
        currentDevice = (TextView) findViewById(R.id.deviceInfo);
        bp_result = (TextView) findViewById(R.id.bp_result);
        day_mSpinner = (Spinner) findViewById(R.id.day_spinner);
        night_mSpinner = (Spinner) findViewById(R.id.neight_spinner);
        initializePlanIntervalTime(day_mSpinner);
        initializePlanIntervalTime(night_mSpinner);
        findViewById(R.id.scan).setOnClickListener(this);
        findViewById(R.id.createAndConnect_bp).setOnClickListener(this);
        findViewById(R.id.startMeasure_bp).setOnClickListener(this);
        findViewById(R.id.stopMeasure_bp).setOnClickListener(this);
        findViewById(R.id.disconnect_bp).setOnClickListener(this);
        findViewById(R.id.getVersion).setOnClickListener(this);
        findViewById(R.id.setPlan).setOnClickListener(this);
        findViewById(R.id.clearPlan).setOnClickListener(this);
        findViewById(R.id.checkPlan).setOnClickListener(this);
        findViewById(R.id.queryV).setOnClickListener(this);
        findViewById(R.id.openV).setOnClickListener(this);
        findViewById(R.id.closeV).setOnClickListener(this);
        findViewById(R.id.requestMR).setOnClickListener(this);
        findViewById(R.id.requestAR).setOnClickListener(this);
        findViewById(R.id.checkDevice).setOnClickListener(this);
    }

    private void initializePlanIntervalTime(final Spinner spinner){
        final List<Integer> intervalTimes = Arrays.asList(30,60,120);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,intervalTimes);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setSelection(0,true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner.setSelection(position,true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //扫描设备
            case R.id.scan:
                try {
                    scanDevice();
                } catch (DfthPermissionException e) {
                    DfthPermissionManager.requestPermission(this,e.getPermission(),100);
                }
                break;
            //连接血压设备
            case R.id.createAndConnect_bp:
                if(mBpDevice != null){
                    mBpDevice.connect().asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(final DfthResult<Boolean> response) {
                            Log.e("dfth_sdk","connect__status->" + response.getReturnData());
                            toast(response.getReturnData() ? "连接设备成功" : response.getErrorMessage());
                            if(response.getReturnData()) {
                                mBpDevice.bindDataListener(ShowBPActivity.this);
                            }
                        }
                    });
                }
                break;
            //开始测量血压
            case R.id.startMeasure_bp:
                if (mBpDevice != null) {
                    mBpDevice.startMeasure().asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            Logger.e("deviceStart->：" + response.getReturnData());
                            toast(response.getReturnData() ? "开始测量成功" : response.getErrorMessage());
                        }
                    });
                }
                break;
            //停止测量血压
            case R.id.stopMeasure_bp:
                if (mBpDevice != null) {
                    mBpDevice.stopMeasure().asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            Logger.e("deviceStop->：" + response.getReturnData());
                            toast(response.getReturnData() ? "结束测量成功" : response.getErrorMessage());
                        }
                    });
                }
                break;
            case R.id.disconnect_bp:
                if (mBpDevice != null) {
                    mBpDevice.disconnect().asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            Logger.e("deviceDisconnect->：" + response.getReturnData());
                            toast(response.getReturnData() ? "断开设备成功" : response.getErrorMessage());
                        }
                    });
                }
                break;
            case R.id.getVersion:
                if (mBpDevice != null) {
                    mBpDevice.queryDeviceVersion().asyncExecute(new DfthCallBack<String>() {
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
                break;
            case R.id.setPlan:
                if (mBpDevice != null) {
                    int deyInterval = (int) day_mSpinner.getSelectedItem();
                    int nightInterval = (int) night_mSpinner.getSelectedItem();
                    BpPlan plan = new BpPlan();
                    plan.setDayInterval(deyInterval * 60);
                    plan.setNightInterval(nightInterval * 60);
                    plan.setAlarmTime((short) 30);
                    plan.setStartTime((int) (System.currentTimeMillis() / 1000), false);
                    mBpDevice.createMeasurePlan(plan).asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            Logger.e("创建血压计划：" + response.getReturnData());
                            toast(response.getReturnData() ? "创建血压计划成功！" : "创建血压计划失败！");
                        }
                    });
//                    }
                }
                break;
            case R.id.clearPlan:
                if (mBpDevice != null) {
                    BpPlan plan = new BpPlan();
                    plan.setDayInterval(DAY_SPACE_TIME * 60 * 60);
                    plan.setNightInterval(NIGHT_SPACE_TIME * 60 * 60);
                    mBpDevice.createMeasurePlan(plan).asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            Logger.e("清除血压计划：" + response.getReturnData());
                            toast(response.getReturnData() ? "清除血压计划成功！" : "清除血压计划失败！");
                        }
                    });
                }
                break;
            case R.id.openV:
                if (mBpDevice != null) {
                    mBpDevice.openVoice().asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            Logger.e("打开语音：" + response.getReturnData());
                            toast(response.getReturnData() ? "打开语音成功！" : "打开语音失败！");
                        }
                    });
                }
                break;
            case R.id.closeV:
                if (mBpDevice != null) {
                    mBpDevice.closeVoice().asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            Logger.e("关闭语音：" + response.getReturnData());
                            toast(response.getReturnData() ? "关闭语音成功！" : "关闭语音失败！");
                        }
                    });
                }
                break;
            case R.id.queryV:
                if (mBpDevice != null) {
                    mBpDevice.deviceVoiceStatus().asyncExecute(new DfthCallBack<Integer>() {
                        @Override
                        public void onResponse(DfthResult<Integer> response) {
                            Logger.e("语音状态：" + response.getReturnData());
                            toast(response.getReturnData() == 0 ? "语音已打开！" : "语音已关闭！");
                        }
                    });
                }
                break;
            case R.id.checkPlan:
                if (mBpDevice != null) {
                    mBpDevice.queryPlanStatus().asyncExecute(new DfthCallBack<BpPlan>() {
                        @Override
                        public void onResponse(DfthResult<BpPlan> response) {
                            if (response.getReturnData() != null) {
                                dialog("测量计划",response.getReturnData().toString());
                            } else {
                                Logger.e("设备计划：NULL");
                                Toast.makeText(ShowBPActivity.this, "设备计划：NULL", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                break;
            case R.id.requestAR:
                if (mBpDevice != null) {
                    mBpDevice.getPlanResult().asyncExecute(new DfthCallBack<ArrayList<BpResult>>() {
                        @Override
                        public void onResponse(DfthResult<ArrayList<BpResult>> response) {
                            if (response.getReturnData() != null) {
                                ArrayList<BpResult> bpResults = response.getReturnData();
                                listDialog("自动结果",bpResults);
                            } else {
                                Logger.e("results = NULL");
                                Toast.makeText(ShowBPActivity.this, "自动结果为：NULL", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                break;
            case R.id.requestMR:
                if (mBpDevice != null) {
                    mBpDevice.getManualResult().asyncExecute(new DfthCallBack<ArrayList<BpResult>>() {
                        @Override
                        public void onResponse(DfthResult<ArrayList<BpResult>> response) {
                            if (response.getReturnData() != null) {
                                ArrayList<BpResult> bpResults = response.getReturnData();
                                listDialog("手动结果",bpResults);
                            } else {
                                Logger.e("results = NULL");
                                Toast.makeText(ShowBPActivity.this, "手动结果为：NULL", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                break;
            case R.id.checkDevice:
                if (mBpDevice != null) {
                    mBpDevice.queryDeviceStatus().asyncExecute(new DfthCallBack<BpStatus>() {
                        @Override
                        public void onResponse(DfthResult<BpStatus> response) {
                            BpStatus status = response.getReturnData();
                            if (status != null) {
                                dialog("设备状态",status.toString());
                            } else {
                                toast("查询设备状态失败");
                            }
                        }
                    });
                }
                break;
        }
    }

    private void scanDevice() throws DfthPermissionException{
        DfthSDKManager.getManager().getDeviceFactory().getBpDevice("")
                .asyncExecute(new DfthCallBack<DfthBpDevice>() {
                    @Override
                    public void onResponse(DfthResult<DfthBpDevice> response) {
                        mBpDevice = response.getReturnData();
                        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
                            BluetoothUtils.startActivityBluetooth(ShowBPActivity.this);
                        }else if(mBpDevice == null){
                            toast(response.getErrorMessage());
                        } else{
                            mBpDevice.bindUserId(TestNetworkService.mUserId);
                            mBpDevice.bindStateListener(ShowBPActivity.this);
                            String deviceMessage = String.format("名称:%s,地址:%s",mBpDevice.getDeviceName(),mBpDevice.getMacAddress());
                            currentDevice.setText(deviceMessage);
                            toast("搜索到设备" + deviceMessage);
                        }
                    }
                });
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
    public void onDataChanged(Short data) {
        mState.setText("设备测量中" + "(当前值:" + data + ")");
    }

    @Override
    public void onBatteryChanged(float battery) {

    }

    @Override
    public void onResultData(BpResult result) {
        if (result != null) {
            bp_result.setText("测量结果:" + result.toString());
        }
    }

    @Override
    public void onMeasureException(String s) {

    }

    @Override
    public void onStateChange(int state) {
        switch (state){
            case DfthDeviceState.DISCONNECTED:{
                mState.setText("设备未连接");
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

    protected void toast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    protected void dialog(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message).create().show();
    }

    protected void listDialog(String title,List<BpResult> results){
        ArrayList<String> contents = new ArrayList<>();
        for(BpResult result: results){
            contents.add(result.toString());
        }
        String[] strings = new String[results.size()];
        strings = contents.toArray(strings);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setItems(strings,null).create().show();
    }


}
