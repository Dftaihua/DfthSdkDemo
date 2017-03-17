package com.example.administrator.mybluetooth.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.Others.Constant.DfthEvent;
import com.dfth.sdk.Others.Constant.DfthReturnCode;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.example.administrator.mybluetooth.DfthSDKApplication;
import com.example.administrator.mybluetooth.R;
import com.example.administrator.mybluetooth.utils.TimeUtils;

/**
 * Created by RuiYu on 2016/11/2.
 */
public class ShowPrinterActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
//
//    public static final String TAG = "ShowPrinterActivity";
//    TextView state;
//    TextView measure_time;
//    TextView currentDevice;
//
//
//    DFTH_MsgReceiver mMsgReceiver = null;
//    DfthPrinter mPrinterDevice = null;
//    Spinner mSpinner;
//    //    private List<String> data_list;
//    //    private ArrayList<String> ecg_List;
//    private ArrayAdapter<String> arr_adapter;
//    private int mCurrentType = DfthDevice.Unknown;
//    private String mDeviceMac;
//    private final int PERMISSION_REQUEST_CODE = 1;
//    private long time;
//    private Bitmap mBitmap ;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_printer);
//
//
////        ecg_List = new ArrayList<String>();
//
//
//        state = (TextView) findViewById(R.id.state);
//        measure_time = (TextView) findViewById(R.id.measure_time);
//        currentDevice = (TextView) findViewById(R.id.deviceInfo);
//
//
//        mSpinner = (Spinner) findViewById(R.id.spinner);
//
//        //适配器
//        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DfthSDKApplication.getInstance().getPrinter_data_list());
//
//        //设置样式
//        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        //加载适配器
//        if (DfthSDKApplication.getInstance().getPrinter_data_list().size() != 0) {
//            mSpinner.setAdapter(arr_adapter);
//        }
//
//
//        Button scan = (Button) findViewById(R.id.scan);
////        Button copyDB = (Button) findViewById(R.id.copyDB);
////        Button create_ecg = (Button) findViewById(R.id.create_ecg);
//        Button createAndConnect_ecg = (Button) findViewById(R.id.createAndConnect_ecg);
////        Button startMeasure_ecg = (Button) findViewById(R.id.startMeasure_ecg);
////        Button stopMeasure_ecg = (Button) findViewById(R.id.stopMeasure_ecg);
//        Button disconnect_ecg = (Button) findViewById(R.id.disconnect_ecg);
//        Button start_print = (Button) findViewById(R.id.start_print);
////        Button release_ecg = (Button) findViewById(R.id.release_ecg);
//        Button printDiscover = (Button) findViewById(R.id.printDiscoveredDevice);
//        Button printCreate = (Button) findViewById(R.id.printCreateDevice);
////        Button cancelSos = (Button) findViewById(R.id.cancelSos);
////        Button getVersion = (Button) findViewById(R.id.getVersion);
//
//
//        scan.setOnClickListener(this);
//        start_print.setOnClickListener(this);
////        copyDB.setOnClickListener(this);
////        create_ecg.setOnClickListener(this);
//        createAndConnect_ecg.setOnClickListener(this);
//        disconnect_ecg.setOnClickListener(this);
////        release_ecg.setOnClickListener(this);
////        startMeasure_ecg.setOnClickListener(this);
////        stopMeasure_ecg.setOnClickListener(this);
//        printDiscover.setOnClickListener(this);
//        printCreate.setOnClickListener(this);
////        cancelSos.setOnClickListener(this);
////        getVersion.setOnClickListener(this);
//
//        mSpinner.setOnItemSelectedListener(this);
//
//        mBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.singleecg);
//
//        //注册东方泰华的广播监听
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(DfthBroadcast.Action);
//        mMsgReceiver = new DFTH_MsgReceiver();
//        registerReceiver(mMsgReceiver, filter);
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (mMsgReceiver != null) {
//            unregisterReceiver(mMsgReceiver);
//        }
//
////        DfthSDKManager.getManager().onDestory();
//        super.onDestroy();
//    }
//
//    @Override
//    public void onClick(View v) {
//        int rlt = DfthReturnCode.Error;
//        switch (v.getId()) {
//            //扫描设备
//            case R.id.scan:
//                //判断是否有权限
//                if ((ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) && Build.VERSION.SDK_INT >= 23) {
//                    //请求权限
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                            PERMISSION_REQUEST_CODE);
//                } else {
//                    rlt = DfthSDKManager.getManager().discoverDevice("", DfthDevice.Printer, 20);
//                }
//                break;
//
//            //拷贝数据库
//            case R.id.copyDB:
////                rlt = DfthSDKManager.getManager().copyDbToSdcard();
//                break;
//
//            case R.id.start_print:
//                mPrinterDevice.printBitmap(mBitmap);
//                break;
//
//            //连接心电设备
//            case R.id.createAndConnect_ecg:
//                if (mPrinterDevice != null) {
//                    rlt = mPrinterDevice.connect(30);
//                }
////                }
//                break;
//
//            //心电设备断开连接
//            case R.id.disconnect_ecg:
//                if (mPrinterDevice != null) {
//                    rlt = mPrinterDevice.disconnect();
//                }
//                break;
//
//            case R.id.printDiscoveredDevice:
//                state.setText("扫描到的设备:\n" + DfthSDKManager.getManager().printDiscoveredList());
//                break;
//
//            case R.id.printCreateDevice:
//                state.setText("创建的设备:\n" + DfthSDKManager.getManager().printDeviceList());
//                break;
//
//        }
//
//        Toast.makeText(this, rlt == DfthReturnCode.Ok ? ((TextView) v).getText() + "ok" : ((TextView) v).getText() + "error", Toast.LENGTH_SHORT).show();
//    }
//
//
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Logger.e("position = " + position + ", id = " + id);
//        String str = DfthSDKApplication.getInstance().getPrinter_data_list().get(position);
//
//        String str1[] = str.split(", ");
//
//        mCurrentType = Integer.parseInt(str1[1]);
//        mDeviceMac = str1[0];
//        if (mCurrentType == DfthDevice.Printer) {
//            mPrinterDevice = (DfthPrinter) DfthSDKManager.getManager().getDevice(mDeviceMac, DfthSDKApplication.getInstance().getUserId(), this);
//            currentDevice.setText("当前设备: " + mDeviceMac);
//        }
//
//
//        if (mPrinterDevice != null) {
//            state.setText(mPrinterDevice.getPreState() + "->" + mPrinterDevice.getCurrentState());
//        }
////        if (mCurrentType == DfthDevice.EcgDevice) {
////            if (ecg_List.contains(mDeviceMac)) {
////                if (mEcgDevice.getCurrentState().equals(DfthDeviceState.CONNECTED)){
////                    mState.setText(mDeviceMac + " 心电设备已被连接");
////                }else if (mEcgDevice.getCurrentState().equals(DfthDeviceState.START_MEASURE)){
////                    mState.setText(mDeviceMac + " 心电设备正在测量");
////                }else {
////                    mState.setText(mDeviceMac + " 心电设备已被创建");
////                }
////            } else {
////                mState.setText(mDeviceMac);
////            }
////        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//        Logger.e("onNothingSelected");
//    }
//
//
//
//    public class DFTH_MsgReceiver extends BroadcastReceiver {
//        @Override
//        public synchronized void onReceive(Context context, Intent intent) {
//            String eventName = intent.getStringExtra(DfthBroadcast.EventName);
//            String mac = intent.getStringExtra(DfthBroadcast.DeviceMac);
//            String newState = intent.getStringExtra(DfthBroadcast.State);
//            String preState = intent.getStringExtra(DfthBroadcast.PreState);
//            int deviceType = intent.getIntExtra(DfthBroadcast.DeviceType,-1);
////            Log.e("test", "event name = " + eventName);
//            if (deviceType == DfthDevice.Printer) {
//                switch (eventName) {
//                    case DfthEvent.DeviceDiscovered:
//                        deviceDiscovered(intent);
////                    abortBroadcast();
//                        break;
//
//                    /*case DfthEvent.DisconnectedOk:
//                        Log.e(TAG, "DeviceDisconnected走了");
//                    case DfthEvent.EcgMeasureStopped:
//                        Log.e(TAG, "mac1=== " + mac + "+++++++preState1 == " + preState);
//                        if (preState.equals(DfthDeviceState.START_MEASURE)) {
//                            Log.e(TAG, "System.currentTimeMillis()=== " + System.currentTimeMillis() + "++++++time == " + time);
//                            time = System.currentTimeMillis() - SharePreferenceUtils.getPrefLong(ShowPrinterActivity.this, "time", 0);
//                            showMeasureTime(time);
//                        }
//                        mState.setText(eventName + " : " + preState + " -> " + newState);
////                    Log.e("test", mState.getText().toString());
//                        currentDevice.setText("当前设备: " + mac);
//                        break;*/
//                    default:
//                        state.setText(eventName + " : " + preState + " -> " + newState);
////                    Log.e("test", mState.getText().toString());
//                        currentDevice.setText("当前设备: " + mac);
//                        break;
//                }
//            }
//        }
//    }
//
//
//    private void showMeasureTime(long measureTime) {
////        runOnUiThread(new Runnable() {
////            @Override
////            public void run() {
//        measure_time.setText(TimeUtils.getHAMAS(measureTime));
////            }
////        });
//
//    }
//
//    private void deviceDiscovered(Intent intent) {
//        String mac = intent.getStringExtra(DfthBroadcast.DeviceMac);
//        int deviceType = intent.getIntExtra(DfthBroadcast.DeviceType, DfthDevice.Unknown);
//
//        if (deviceType == DfthDevice.Printer) {
//            DfthSDKApplication.getInstance().getPrinter_data_list().add(mac + ", " + deviceType);
//            if (DfthSDKApplication.getInstance().getPrinter_data_list().size() == 1) {
//                mSpinner.setAdapter(arr_adapter);
//            }
//            arr_adapter.notifyDataSetChanged();
//            Toast.makeText(this, "发现设备: " + mac, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
//            grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    DfthSDKManager.getManager().discoverDevice("", DfthDevice.
//                            Printer, 20);
//                } else {
//                    Log.e(TAG, "permission not get");
//                }
//                break;
//            default:
//                break;
//        }
//    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
