package com.example.administrator.mybluetooth.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.listener.ECGFileDownloadListener;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.network.response.DfthDeviceInfoResponse;
import com.dfth.sdk.network.response.UserInfoResponse;
import com.dfth.sdk.network.response.DfthServiceResult;
import com.dfth.sdk.network.response.GetAccessTokenResponse;
import com.dfth.sdk.network.response.LoginResponse;
import com.dfth.sdk.network.response.OauthResponse;
import com.dfth.sdk.network.response.UserResponse;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.user.DfthUser;
import com.example.administrator.mybluetooth.R;

import java.util.List;

/**
 * Created by leezhiqiang on 2017/2/21.
 */

public class TestNetworkService extends Activity implements View.OnClickListener{
    private String mCode;
    private String mAccessToken;
    private EditText mVeriCodeEditText;
    private TextView mInfo;
    private EditText mRegisterPhone,mRegisterPassword;
    public static String mUserId = "-1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        mInfo = (TextView) findViewById(R.id.info);
        findViewById(R.id.oauth).setOnClickListener(this);
        findViewById(R.id.accessToken).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.smsCode).setOnClickListener(this);
        findViewById(R.id.registerUser).setOnClickListener(this);
        findViewById(R.id.resetSmsCode).setOnClickListener(this);
        findViewById(R.id.resetPassword).setOnClickListener(this);
        findViewById(R.id.readInfo).setOnClickListener(this);
        findViewById(R.id.updateUserInfo).setOnClickListener(this);
        findViewById(R.id.readUserInfo).setOnClickListener(this);
        mRegisterPhone = (EditText) findViewById(R.id.registerPhone);
        mRegisterPassword = (EditText) findViewById(R.id.registerPassword);
        mVeriCodeEditText = (EditText) findViewById(R.id.registerCode);
    }
    private ProgressDialog mDialog;
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.oauth:{
                DfthSDKManager.getManager().getDfthService().oauth().asyncExecute(new DfthServiceCallBack<OauthResponse>() {
                    @Override
                    public void onResponse(DfthServiceResult<OauthResponse> response) {
                        if(response.mResult == 0 && response.mData != null){
                            mCode = response.mData.getCode();
                        }
                        Log.e("dfth_sdk","code->" + mCode);
                    }
                });
            }
            break;
            case R.id.accessToken:{
                DfthSDKManager.getManager().getDfthService().getAccessToken(mCode).asyncExecute(new DfthServiceCallBack<GetAccessTokenResponse>() {
                    @Override
                    public void onResponse(DfthServiceResult<GetAccessTokenResponse> response) {
                        if(response.mResult == 0 && response.mData != null){
                            mAccessToken = response.mData.getAccessToken();
                        }
                        Log.e("dfth_sdk","accessToken->" + mAccessToken);
                    }
                });
            }
            break;
            case R.id.login:{
                String password = mRegisterPassword.getText().toString();
                String account = mRegisterPhone.getText().toString();
                DfthSDKManager.getManager().getDfthService().login(account,password).asyncExecute(new DfthServiceCallBack<LoginResponse>() {
                    @Override
                    public void onResponse(DfthServiceResult<LoginResponse> response) {
                        String content = response.mResult == 0 ? "用户登录成功" : "用户登录失败";
                        Toast.makeText(TestNetworkService.this,content,Toast.LENGTH_SHORT).show();
                        if(response.mData != null){
                            mUserId = response.mData.userId;
                        }
                    }
                });
            }
            break;
            case R.id.smsCode:{
                DfthSDKManager.getManager().getDfthService().registerSmsCode("18610486326").asyncExecute(new DfthServiceCallBack<Void>() {
                    @Override
                    public void onResponse(DfthServiceResult<Void> response) {
                        if(response.mResult == 0 && response.mData != null){

                        }
                        Log.e("dfth_sdk","smsCode_code->" + response.mResult);
                    }
                });
            }
            break;
            case R.id.registerUser:{
                String vericode = mVeriCodeEditText.getText().toString();
                DfthSDKManager.getManager().getDfthService().registerUser("18610486326","123456",vericode).asyncExecute(new DfthServiceCallBack<UserResponse>() {
                    @Override
                    public void onResponse(DfthServiceResult<UserResponse> response) {
                        Log.e("dfth_sdk","register->" + response.mResult);
                    }
                });
            }
            break;
            case R.id.resetPassword:{
                String vericode = mVeriCodeEditText.getText().toString();
                DfthSDKManager.getManager().getDfthService().resetPassword("18610486326","123456",vericode).asyncExecute(new DfthServiceCallBack<Void>() {
                    @Override
                    public void onResponse(DfthServiceResult<Void> response) {
                        Log.e("dfth_sdk","reset_password->" + response.mResult);
                        Log.e("dfth_sdk","reset_password_message->" + response.mMessage);

                    }
                });
            }
            break;
            case R.id.resetSmsCode:{
                DfthSDKManager.getManager().getDfthService().resetPasswordSmsCode("18610486326").asyncExecute(new DfthServiceCallBack<Void>() {
                    @Override
                    public void onResponse(DfthServiceResult<Void> response) {
                        Log.e("dfth_sdk","smsCode_code->" + response.mResult);
                    }
                });
            }
            break;
            case R.id.readUserInfo:{

                DfthSDKManager.getManager().getDfthService().getECGGroupData(mUserId,0,System.currentTimeMillis(),0,1000).asyncExecute(new DfthServiceCallBack<List<ECGResult>>() {
                    @Override
                    public void onResponse(DfthServiceResult<List<ECGResult>> response) {
                        mDialog = new ProgressDialog(TestNetworkService.this);
                        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mDialog.show();
                        DfthSDKManager.getManager().getDfthService().downloadECGFile(response.mData.get(0), new ECGFileDownloadListener() {
                            @Override
                            public void onProgress(int progress) {
                                mDialog.setProgress(progress);
                            }

                            @Override
                            public void onComplete(boolean success) {
                                String result = success ? "下载成功" : "下载失败";
                                Toast.makeText(TestNetworkService.this,result,Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }
                        });
                    }
                });
//                DfthSDKManager.getManager().getDfthService().getDeviceInfo("a0:e6:f8:4d:c7:eb").asyncExecute(new DfthServiceCallBack<DfthDeviceInfoResponse>() {
//                    @Override
//                    public void onResponse(DfthServiceResult<DfthDeviceInfoResponse> response) {
//
//                    }
//                });

//                DfthSDKManager.getManager().getDfthService().deviceBindUser(mUserId,"a0:e6:f8:4d:c7:eb",System.currentTimeMillis()).asyncExecute(new DfthServiceCallBack<Void>() {
//                    @Override
//                    public void onResponse(DfthServiceResult<Void> response) {
//
//                    }
//                });

            }
            break;

            case R.id.updateUserInfo:{
                DfthUser user = new DfthUser();
                user.setUserId(mUserId);
                user.setName("1234");
                user.setGender(1);
                user.setBirthday(System.currentTimeMillis());
                user.setTelNum("18610486326");
                DfthSDKManager.getManager().getDfthService().updateMember(user).asyncExecute(new DfthServiceCallBack<Void>() {
                    @Override
                    public void onResponse(DfthServiceResult<Void> response) {
                        System.out.print("");
                    }
                });
            }
            break;

        }
    }
}
