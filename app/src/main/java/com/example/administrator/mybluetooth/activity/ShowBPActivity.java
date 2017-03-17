package com.example.administrator.mybluetooth.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dfth.sdk.device.DfthBpDevice;
import com.dfth.sdk.listener.DfthBpDeviceDataListener;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.Others.Constant.DfthReturnCode;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;
import com.example.administrator.mybluetooth.DfthSDKApplication;
import com.example.administrator.mybluetooth.R;
import com.example.administrator.mybluetooth.utils.MeasureListHeightUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RuiYu on 2016/10/24.
 */
public class ShowBPActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener,DfthBpDeviceDataListener {

    TextView mState;
    TextView currentDevice;
    TextView bp_result;
    ListView showResult;
    DfthBpDevice mBpDevice = null;
    Spinner mSpinner;
    Spinner day_mSpinner;
    Spinner night_mSpinner;
    //    private ArrayList<String> bp_List;
    private ArrayList<String> day_list;
    private ArrayList<String> night_list;
    private ArrayAdapter<String> arr_adapter;
    private ArrayAdapter<String> day_arr_adapter;
    private ArrayAdapter<String> neight_arr_adapter;
    private int mCurrentType = DfthDevice.Unknown;
    private String mDeviceMac;
    private int day_plan_time = 30;
    private int night_plan_time = 30;
    private final int PERMISSION_REQUEST_CODE = 1;

    public static final int DAY_SPACE_TIME = 16;
    public static final int NIGHT_SPACE_TIME = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp);

        init();
//        //注册东方泰华的广播监听
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(DfthBroadcast.Action);
//        mMsgReceiver = new DFTH_MsgReceiver();
//        registerReceiver(mMsgReceiver, filter);
    }

    public void init() {

//        bp_List = new ArrayList<String>();
        day_list = new ArrayList<>();
        night_list = new ArrayList<>();
        day_list.add("30");
        day_list.add("60");
        day_list.add("无计划");
        night_list.add("30");
        night_list.add("60");
        night_list.add("无计划");

        mState = (TextView) findViewById(R.id.state);
        currentDevice = (TextView) findViewById(R.id.deviceInfo);
        bp_result = (TextView) findViewById(R.id.bp_result);
        showResult = (ListView) findViewById(R.id.bp_result_list);

        mSpinner = (Spinner) findViewById(R.id.spinner);
        day_mSpinner = (Spinner) findViewById(R.id.day_spinner);
        night_mSpinner = (Spinner) findViewById(R.id.neight_spinner);

        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DfthSDKApplication.getInstance().getBp_data_list());
        day_arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, day_list);
        neight_arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, night_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day_arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        neight_arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        if (DfthSDKApplication.getInstance().getBp_data_list().size() != 0) {
            mSpinner.setAdapter(arr_adapter);
        }
        day_mSpinner.setAdapter(day_arr_adapter);
        night_mSpinner.setAdapter(neight_arr_adapter);
        day_mSpinner.setSelection(0, true);
        night_mSpinner.setSelection(0, true);


        Button scan = (Button) findViewById(R.id.scan);
        Button copyDB = (Button) findViewById(R.id.copyDB);
//        Button create_bp = (Button) findViewById(R.id.create_bp);
        Button createAndConnect_bp = (Button) findViewById(R.id.createAndConnect_bp);
        Button startMeasure_bp = (Button) findViewById(R.id.startMeasure_bp);
        Button stopMeasure_bp = (Button) findViewById(R.id.stopMeasure_bp);
        Button disconnect_bp = (Button) findViewById(R.id.disconnect_bp);
//        Button release_bp = (Button) findViewById(R.id.release_bp);

        Button printDiscover = (Button) findViewById(R.id.printDiscoveredDevice);
        Button printCreate = (Button) findViewById(R.id.printCreateDevice);
        Button getVersion = (Button) findViewById(R.id.getVersion);

        Button setPlan = (Button) findViewById(R.id.setPlan);
        Button clearPlan = (Button) findViewById(R.id.clearPlan);
        Button checkPlan = (Button) findViewById(R.id.checkPlan);
        Button openV = (Button) findViewById(R.id.openV);
        Button closeV = (Button) findViewById(R.id.closeV);
        Button checkV = (Button) findViewById(R.id.queryV);
        Button requestMR = (Button) findViewById(R.id.requestMR);
        Button requsetAR = (Button) findViewById(R.id.requestAR);
        Button checkDevice = (Button) findViewById(R.id.checkDevice);
//        Button syncTime = (Button) findViewById(R.id.syncTime);


        scan.setOnClickListener(this);
        copyDB.setOnClickListener(this);
//        create_bp.setOnClickListener(this);
        createAndConnect_bp.setOnClickListener(this);
        disconnect_bp.setOnClickListener(this);
//        release_bp.setOnClickListener(this);
        startMeasure_bp.setOnClickListener(this);
        stopMeasure_bp.setOnClickListener(this);
        printDiscover.setOnClickListener(this);
        printCreate.setOnClickListener(this);
        getVersion.setOnClickListener(this);
        setPlan.setOnClickListener(this);
        clearPlan.setOnClickListener(this);
        checkPlan.setOnClickListener(this);
        openV.setOnClickListener(this);
        closeV.setOnClickListener(this);
        checkV.setOnClickListener(this);
        requestMR.setOnClickListener(this);
        requsetAR.setOnClickListener(this);
        checkDevice.setOnClickListener(this);
//        syncTime.setOnClickListener(this);

        mSpinner.setOnItemSelectedListener(this);
        day_mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        day_plan_time = 30;
                        break;
                    case 1:
                        day_plan_time = 60;
                        break;
                    case 2:
//                        day_plan_time = DBRecordBpPlan.DAY_SPACE_TIME * 60;
                        break;
                }
                Logger.e("position =====" + position);
                day_mSpinner.setSelection(position, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        night_mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        night_plan_time = 30;
                        break;
                    case 1:
                        night_plan_time = 60;
                        break;
                    case 2:
//                        night_plan_time = DBRecordBpPlan.NIGHT_SPACE_TIME * 60;
                        break;
                }
                Logger.e("position =====" + position);
                night_mSpinner.setSelection(position, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int rlt = DfthReturnCode.Error;
        switch (v.getId()) {
            //扫描设备
            case R.id.scan:
//                DfthSDKManager.getManager().getDeviceFactory().getBpDevice("")
//                        .asyncExecute(new DfthCallBack<DfthBpDevice>() {
//                            @Override
//                            public void onResponse(DfthResult<DfthBpDevice> response) {
//                                Logger.e("deviceMacAddress: " + (response.getReturnData() != null ? response.getReturnData().getMacAddress() : "null"));
//                                mBpDevice = response.getReturnData();
//                            }
//                        });
                break;
            //拷贝数据库
            case R.id.copyDB:
//                rlt = DfthSDKManager.getManager().copyDbToSdcard();
                break;
            //连接血压设备
            case R.id.createAndConnect_bp:
                if (mBpDevice != null) {
                    mBpDevice.connect().asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(final DfthResult<Boolean> response) {
                            Log.e("dfth_sdk", "connect__status->" + response.getReturnData());
                            if (response.getReturnData()) {
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
                        }
                    });
                }
                break;
//            case R.id.release_bp:
//                if (mBpDevice != null&& !(mBpDevice.getCurrentState().equals(DfthDeviceState.CONNECTED)
//                        || mBpDevice.getCurrentState().equals(DfthDeviceState.START_MEASURE))) {
//                    mBpDevice.release();
//                    mBpDevice = null;
//                    bp_List.remove(mDeviceMac);
//                    mState.setText(mDeviceMac + "血压设备释放");
//                }else{
//                    Toast.makeText(this,"请先断开连接",Toast.LENGTH_SHORT).show();
//                }
//                break;
            case R.id.printDiscoveredDevice:
//                mState.setText("扫描到的设备:\n" + DfthSDKManager.getManager().printDiscoveredList());
//                rlt = DfthReturnCode.Ok;
                break;

            case R.id.printCreateDevice:
//                mState.setText("创建的设备:\n" + DfthSDKManager.getManager().printDeviceList());
//                rlt = DfthReturnCode.Ok;
                break;
            case R.id.getVersion:
                switch (mCurrentType) {
                    case DfthDevice.BpDevice:
                        if (mBpDevice != null) {
                            mBpDevice.queryDeviceVersion().asyncExecute(new DfthCallBack<String>() {
                                @Override
                                public void onResponse(DfthResult<String> response) {
                                    Logger.e("版本号 = " + response.getReturnData());
                                }
                            });
                        }
                        break;
                }
                break;
            case R.id.setPlan:
                if (mBpDevice != null) {
                    Logger.e("白天计划时间: " + day_plan_time + "晚上计划时间: " + night_plan_time);
                    if (day_plan_time == DAY_SPACE_TIME * 60 && night_plan_time == NIGHT_SPACE_TIME * 60) {
                        Toast.makeText(this, "下发计划失败,不能白天和晚上都无计划", Toast.LENGTH_SHORT).show();
                    } else {
                        BpPlan plan = new BpPlan();
                        plan.setDayInterval(DAY_SPACE_TIME * 60 * 60);
                        plan.setNightInterval(NIGHT_SPACE_TIME * 60 * 60);
                        plan.setAlarmTime((short) 30);
                        plan.setStartTime((int) (System.currentTimeMillis() / 1000));

//                        rlt = mBpDevice.makeMeasurePlan(day_plan_time * 60, night_plan_time * 60, 120);
                        mBpDevice.createMeasurePlan(plan).asyncExecute(new DfthCallBack<Boolean>() {
                            @Override
                            public void onResponse(DfthResult<Boolean> response) {
                                Logger.e("创建血压计划：" + response.getReturnData());
                            }
                        });
                    }
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
                        }
                    });
                }
                break;
            case R.id.checkPlan:
                if (mBpDevice != null) {
                    mBpDevice.queryPlanStatus().asyncExecute(new DfthCallBack<BpPlan>() {
                        @Override
                        public void onResponse(DfthResult<BpPlan> response) {
                            if(response.getReturnData() != null){
                                Logger.e("设备状态：" + response.getReturnData().toString());
                            } else{
                                Logger.e("设备状态：NULL");
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
                            if(response.getReturnData() != null){
                                ArrayList<BpResult> bpResults = response.getReturnData();
                                for (int i = 0; i < bpResults.size(); i++){
                                    Logger.e("results = " + bpResults.get(i).toString());
                                }
                            } else{
                                Logger.e("results = NULL");
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
                            if(response.getReturnData() != null){
                                ArrayList<BpResult> bpResults = response.getReturnData();
                                for (int i = 0; i < bpResults.size(); i++){
                                    Logger.e("results = " + bpResults.get(i).toString());
                                }
                            } else{
                                Logger.e("results = NULL");
                            }
                            }
                    });
                }
                break;
            case R.id.checkDevice:
                if (mBpDevice != null) {
                    mBpDevice.queryDeviceStatus().asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            Logger.e("设备状态：" + response.getReturnData());
                        }
                    });
                }
                break;
//            case R.id.syncTime:
//                if (mBpDevice != null) {
//                    rlt = mBpDevice.syncTime(System.currentTimeMillis());
////                    Toast.makeText(this, rlt == DfthReturnCode.Ok? "同步时间成功" : "同步时间失败", Toast.LENGTH_SHORT).show();
//                }
//                break;
        }
        Toast.makeText(this, rlt == DfthReturnCode.Ok ? ((TextView) v).getText() + "ok" : ((TextView) v).getText() + "ok", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Logger.e("position = " + position + ", id = " + id);
        String str = DfthSDKApplication.getInstance().getBp_data_list().get(position);

        String str1[] = str.split(", ");

        mCurrentType = Integer.parseInt(str1[1]);
        mDeviceMac = str1[0];
        if (mCurrentType == DfthDevice.BpDevice) {
            //mBpDevice = (DfthBpDevice) DfthSDKManager.getManager().getDeviceFactory().getBpDevice(mDeviceMac);
            currentDevice.setText("当前设备: " + mDeviceMac);
        }

//        if (mBpDevice != null) {
//            mState.setText(mBpDevice.getPreState() + "->" + mBpDevice.getCurrentState());
//        }
//        if (mCurrentType == DfthDeviceA.BpDevice) {
//            if (bp_List.contains(mDeviceMac)) {
//                if (mBpDevice.getCurrentState().equals(DfthDeviceState.CONNECTED)){
//                    mState.setText(mDeviceMac + " 血压设备已被连接");
//                }else if (mBpDevice.getCurrentState().equals(DfthDeviceState.START_MEASURE)){
//                    mState.setText(mDeviceMac + " 血压设备正在测量");
//                }else {
//                    mState.setText(mDeviceMac + " 血压设备已被创建");
//                }
//            } else {
//                mState.setText(mDeviceMac);
//            }
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Logger.e("onNothingSelected");
    }

//    @Override
//    public void handleCurrentPressure(final int pressure) {
//        Logger.e("当前压力 ：" + pressure);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                bp_result.setText("当前压力 ：" + pressure);
//            }
//        });
//
//    }


    public void setListView(List list, ListView listView) {
        ArrayAdapter planAdapter = new ArrayAdapter(this, R.layout.bp_list_item, list);
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        showResult.setAdapter(planAdapter);
        MeasureListHeightUtils.setListViewHeightBasedOnChildren(listView, planAdapter);
        planAdapter.notifyDataSetChanged();
    }

//        private boolean isEqual(DBRecordBpPlan userPlan, DBRecordBpPlan devicePlan) {
//            if (userPlan == null && devicePlan == null) {
//                Toast.makeText(ShowBPActivity.this, "手机和设备无计划", Toast.LENGTH_SHORT).show();
//                return true;
//            } else if ((userPlan == null && devicePlan != null) || (userPlan != null && devicePlan == null)) {
//                return false;
//            } else {
//                if (userPlan.getStartTime() == devicePlan.getStartTime() &&
//                        userPlan.getDayInterval() == devicePlan.getDayInterval() &&
//                        userPlan.getNightInterval() == devicePlan.getNightInterval()) {
//                    Toast.makeText(ShowBPActivity.this, "手机和设备计划一致", Toast.LENGTH_SHORT).show();
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        }
//    }

    private void deviceDiscovered(Intent intent) {
//        String mac = intent.getStringExtra(DfthBroadcast.DeviceMac);
//        int deviceType = intent.getIntExtra(DfthBroadcast.DeviceType, DfthDevice.Unknown);
//
//        if (deviceType == DfthDevice.BpDevice) {
//            DfthSDKApplication.getInstance().getBp_data_list().add(mac + ", " + deviceType);
//            if (DfthSDKApplication.getInstance().getBp_data_list().size() == 1) {
//                mSpinner.setAdapter(arr_adapter);
//            }
//            arr_adapter.notifyDataSetChanged();
//            Toast.makeText(this, "发现设备: " + mac, Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //同意权限
//                    DfthSDKManager.getManager().discoverDevice("", DfthDevice.BpDevice, 20);
                } else {
                    //拒绝权限
                    Logger.e("permission not get");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDataChanged(Short data) {
        Logger.e("data = " + data);
    }

    @Override
    public void onBatteryChanged(float battery) {

    }

    @Override
    public void onResultData(BpResult result) {
        if(result != null){
            Logger.e(result.toString());
        }
    }
}
