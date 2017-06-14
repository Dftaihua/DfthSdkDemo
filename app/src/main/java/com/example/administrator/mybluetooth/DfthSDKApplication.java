package com.example.administrator.mybluetooth;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.dfth.sdk.DfthSDKConfig;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.DfthSdkCallBack;
import com.dfth.sdk.Others.Utils.Logger.LogLevel;
import com.dfth.sdk.Others.Utils.Logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RuiYu on 2016/10/25.
 */
public class DfthSDKApplication extends Application {
    private static DfthSDKApplication sAppcation;
    public static DfthSDKApplication getInstance() {
        return sAppcation;
    }
    private String userId;
    private List<String> ecg_data_list;
    private List<String> bp_data_list;
    private List<String> printer_data_list;
    @Override
    public void onCreate() {
        super.onCreate();
        sAppcation = this;
        DfthSDKConfig config = DfthSDKConfig.getConfig(getApplicationContext(),Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyBluetooth", "MyBluetooth", LogLevel.FULL, Logger.ERROR, "MyBluetooth", 1,"http://apitest.open.dfthlong.com/");
        config.setClientId("4e8b63624a4f490c8aa671b5c82f95f3");
        config.setClientSecret("b20f7cc80c6641e0b0e854c001c8f890");
        DfthSDKManager.initWithConfig(config);
        DfthSDKManager.getManager().onInit(this);
        DfthSDKManager.getManager().oauth(new DfthSdkCallBack() {
            @Override
            public void onInitResponse(boolean success, String accessToken) {
                Log.e("dfth_sdk", "oauth->" + success);
                //验证成功后，可以连接设备和查询数据
                //可以创建用户 详见 DfthSDKManager.getManager().getDfthService()
            }
        });
        Logger.e(DfthSDKManager.getManager().getSDKVersion());
        ecg_data_list = new ArrayList<>();
        bp_data_list = new ArrayList<>();
        printer_data_list = new ArrayList<>();
    }

    @Override
    public void onTerminate() {
        DfthSDKManager.getManager().onDestory();
        super.onTerminate();
    }

    public List<String> getPrinter_data_list() {
        return printer_data_list;
    }

    public List<String> getEcg_data_list() {
        return ecg_data_list;

    }
    public List<String> getBp_data_list() {
        return bp_data_list;
    }

    public String getUserId() {
        return userId;
    }

    public static String getStringRes(int id, Object... obj) {
        return sAppcation.getResources().getString(id, obj);
    }

    public static String getStringRes(int res){
        return sAppcation.getResources().getString(res);
    }

    public static int getColorRes(int res){
        return sAppcation.getResources().getColor(res);
    }

    /**
     * 获取屏幕高度
     * @return
     */
    public static int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) sAppcation.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    public static int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) sAppcation.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
