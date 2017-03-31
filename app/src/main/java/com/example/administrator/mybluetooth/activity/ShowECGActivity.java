package com.example.administrator.mybluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dfth.sdk.Others.Utils.BluetoothUtils;
import com.dfth.sdk.device.factory.DfthDeviceFactory;
import com.dfth.sdk.listener.DfthTwelveDeviceDataListener;
import com.dfth.sdk.device.DfthTwelveECGDevice;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.permission.DfthPermissionException;
import com.example.administrator.mybluetooth.DfthSDKApplication;
import com.example.administrator.mybluetooth.widget.WaveView;

/**
 * Created by RuiYu on 2016/10/24.
 */
public class ShowECGActivity extends BaseECGActivity<DfthTwelveECGDevice> implements DfthTwelveDeviceDataListener{
    @Override
    protected void initializeWaveView(ViewGroup parent) {
        waveView = new WaveView(this,12);
        LinearLayout.LayoutParams params = new
                LinearLayout.LayoutParams(0, 0);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) (DfthSDKApplication.getScreenHeight());
        parent.addView(waveView, params);
    }

    @Override
    public void onLeaderStatusChanged(boolean[] leaderOut) {

    }
    @Override
    public void onSosStatus(boolean status) {

    }
    @Override
    protected void scanDevice() throws DfthPermissionException {
        DfthDeviceFactory factory =  DfthSDKManager.getManager().getDeviceFactory();
        factory.getEcgDevice("").asyncExecute(new DfthCallBack<DfthTwelveECGDevice>() {
                    @Override
                    public void onResponse(DfthResult<DfthTwelveECGDevice> response) {
                        mDevice = response.getReturnData();
                        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
                            BluetoothUtils.startActivityBluetooth(ShowECGActivity.this);
                        }else if(mDevice == null){
                            toast(response.getErrorMessage());
                        } else{
                            mDevice.bindUserId(TestNetworkService.mUserId);
                            mDevice.bindStateListener(ShowECGActivity.this);
                            String deviceMessage = String.format("名称:%s,地址:%s",mDevice.getDeviceName(),mDevice.getMacAddress());
                            currentDevice.setText(deviceMessage);
                            toast("搜索到设备" + deviceMessage);
                        }
                    }
                });
    }

    @Override
    protected void searchHistory() {
        HistoryActivity.startHistory(this,0);
    }

    @Override
    protected void bindDataListener() {
        if(mDevice != null){
            mDevice.bindDataListener(this);
        }
    }

    @Override
    public void startProcessECGResult() {

    }
}
