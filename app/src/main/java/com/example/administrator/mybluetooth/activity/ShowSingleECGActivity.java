package com.example.administrator.mybluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.BluetoothUtils;
import com.dfth.sdk.device.DfthSingleECGDevice;
import com.dfth.sdk.device.factory.DfthDeviceFactory;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.listener.DfthSingleDeviceDataListener;
import com.dfth.sdk.permission.DfthPermissionException;
import com.example.administrator.mybluetooth.DfthSDKApplication;
import com.example.administrator.mybluetooth.widget.WaveView;

/**
 * Created by leezhiqiang on 2017/2/27.
 */

public class ShowSingleECGActivity extends BaseECGActivity<DfthSingleECGDevice> implements DfthSingleDeviceDataListener {

    @Override
    public void onLeaderOut(boolean leadOff) {

    }

    @Override
    protected void scanDevice() throws DfthPermissionException {
        DfthDeviceFactory factory =  DfthSDKManager.getManager().getDeviceFactory();
        factory.getSingleEcgDevice("").asyncExecute(new DfthCallBack<DfthSingleECGDevice>() {
            @Override
            public void onResponse(DfthResult<DfthSingleECGDevice> response) {
                mDevice = response.getReturnData();
                if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
                    BluetoothUtils.startActivityBluetooth(ShowSingleECGActivity.this);
                }else if(mDevice == null){
                    toast(response.getErrorMessage());
                } else{
                    mDevice.bindUserId(TestNetworkService.mUserId);
                    mDevice.bindStateListener(ShowSingleECGActivity.this);
                    String deviceMessage = String.format("名称:%s,地址:%s",mDevice.getDeviceName(),mDevice.getMacAddress());
                    currentDevice.setText(deviceMessage);
                    toast("搜索到设备" + deviceMessage);
                }
            }
        });
    }

    @Override
    protected void searchHistory() {
        HistoryActivity.startHistory(this,1);
    }

    @Override
    protected void bindDataListener() {
        if(mDevice != null){
            mDevice.bindDataListener(this);
        }
    }

    @Override
    protected void initializeWaveView(ViewGroup parent) {
        waveView = new WaveView(this,1);
        LinearLayout.LayoutParams params = new
                LinearLayout.LayoutParams(0, 0);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) (DfthSDKApplication.getScreenHeight() * 0.3f);
        parent.addView(waveView, params);
    }

    @Override
    public void startProcessECGResult() {

    }
}
