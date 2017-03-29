package com.example.administrator.mybluetooth.activity;

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

import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.device.DfthBpDevice;
import com.dfth.sdk.listener.DfthBpDeviceDataListener;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.Others.Constant.DfthReturnCode;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;
import com.dfth.sdk.model.bp.BpStatus;
import com.dfth.sdk.permission.DfthPermissionException;
import com.dfth.sdk.permission.DfthPermissionManager;
import com.example.administrator.mybluetooth.DfthSDKApplication;
import com.example.administrator.mybluetooth.R;
import com.example.administrator.mybluetooth.utils.MeasureListHeightUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RuiYu on 2016/10/24.
 */
public class ShowBPActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener, DfthBpDeviceDataListener {

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
//        day_list.add("60");
//        day_list.add("无计划");
//        night_list.add("30");
        night_list.add("60");
//        night_list.add("无计划");

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
                        night_plan_time = 60;
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
                try {
                    DfthSDKManager.getManager().getDeviceFactory().getBpDevice("")
                            .asyncExecute(new DfthCallBack<DfthBpDevice>() {
                                @Override
                                public void onResponse(DfthResult<DfthBpDevice> response) {
                                    Logger.e("deviceMacAddress: " + (response.getReturnData() != null ? response.getReturnData().getMacAddress() : "null"));
                                    mBpDevice = response.getReturnData();
                                    if (mBpDevice != null) {
                                        Toast.makeText(ShowBPActivity.this, "连接到设备：" + mBpDevice.getMacAddress(), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(ShowBPActivity.this, "连接到设备：NULL", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ShowBPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
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
                                Toast.makeText(ShowBPActivity.this, "连接设备成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShowBPActivity.this, "连接设备失败！", Toast.LENGTH_SHORT).show();
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
                            if (response.getReturnData()) {
                                Toast.makeText(ShowBPActivity.this, "开始测量成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShowBPActivity.this, "开始测量失败！", Toast.LENGTH_SHORT).show();
                            }
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
                            if (response.getReturnData()) {
                                Toast.makeText(ShowBPActivity.this, "结束测量成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShowBPActivity.this, "结束测量失败！", Toast.LENGTH_SHORT).show();
                            }
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
                            if (response.getReturnData()) {
                                Toast.makeText(ShowBPActivity.this, "断开设备成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShowBPActivity.this, "断开设备失败！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.printDiscoveredDevice:
                break;

            case R.id.printCreateDevice:
                break;
            case R.id.getVersion:
                if (mBpDevice != null) {
                    mBpDevice.queryDeviceVersion().asyncExecute(new DfthCallBack<String>() {
                        @Override
                        public void onResponse(DfthResult<String> response) {
                            Logger.e("版本号 = " + response.getReturnData());
                            Toast.makeText(ShowBPActivity.this, "版本号 = " + response.getReturnData(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.setPlan:
                if (mBpDevice != null) {
                    Logger.e("白天计划时间: " + day_plan_time + "晚上计划时间: " + night_plan_time);
                    BpPlan plan = new BpPlan();
                    plan.setDayInterval(30 * 60);
                    plan.setNightInterval(60 * 60);
                    plan.setAlarmTime((short) 30);
                    plan.setStartTime((int) (System.currentTimeMillis() / 1000), false);
                    mBpDevice.createMeasurePlan(plan).asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            Logger.e("创建血压计划：" + response.getReturnData());
                            if (response.getReturnData()) {
                                Toast.makeText(ShowBPActivity.this, "创建血压计划成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShowBPActivity.this, "创建血压计划失败！", Toast.LENGTH_SHORT).show();
                            }
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
                            if (response.getReturnData()) {
                                Toast.makeText(ShowBPActivity.this, "清除血压计划成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShowBPActivity.this, "清除血压计划失败！", Toast.LENGTH_SHORT).show();
                            }
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
                            if (response.getReturnData()) {
                                Toast.makeText(ShowBPActivity.this, "打开语音成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShowBPActivity.this, "打开语音失败！", Toast.LENGTH_SHORT).show();
                            }
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
                            if (response.getReturnData()) {
                                Toast.makeText(ShowBPActivity.this, "关闭语音成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShowBPActivity.this, "关闭语音失败！", Toast.LENGTH_SHORT).show();
                            }
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
                            if (response.getReturnData() == 0) {
                                Toast.makeText(ShowBPActivity.this, "语音已打开", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ShowBPActivity.this, "语音已关闭", Toast.LENGTH_LONG).show();
                            }
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
                                Logger.e("设备计划：" + response.getReturnData().toString());
                                Toast.makeText(ShowBPActivity.this, "设备计划：" + response.getReturnData().toString(), Toast.LENGTH_LONG).show();
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
                                setListView(bpResults, showResult);
                            } else {
                                Logger.e("results = NULL");
                                Toast.makeText(ShowBPActivity.this, "手动结果为：NULL", Toast.LENGTH_LONG).show();
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
                                setListView(bpResults, showResult);
                            } else {
                                Logger.e("results = NULL");
                                Toast.makeText(ShowBPActivity.this, "自动结果为：NULL", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(ShowBPActivity.this, status.toString(),Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ShowBPActivity.this, "设备状态：NULL", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Logger.e("position = " + position + ", id = " + id);
        String str = DfthSDKApplication.getInstance().getBp_data_list().get(position);

        String str1[] = str.split(", ");

        mCurrentType = Integer.parseInt(str1[1]);
        mDeviceMac = str1[0];
        if (mCurrentType == DfthDevice.BpDevice) {
            currentDevice.setText("当前设备: " + mDeviceMac);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Logger.e("onNothingSelected");
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


    public void setListView(List list, ListView listView) {
        ArrayAdapter planAdapter = new ArrayAdapter(this, R.layout.bp_list_item, list);
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        showResult.setAdapter(planAdapter);
        MeasureListHeightUtils.setListViewHeightBasedOnChildren(listView, planAdapter);
        planAdapter.notifyDataSetChanged();
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
        if (result != null) {
            Logger.e(result.toString());
        }
    }

    @Override
    public void onMeasureException(String s) {

    }
}
