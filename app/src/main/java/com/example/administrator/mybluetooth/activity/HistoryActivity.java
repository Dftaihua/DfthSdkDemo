package com.example.administrator.mybluetooth.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.ECGUtils;
import com.dfth.sdk.file.ECGFileFormat;
import com.dfth.sdk.listener.ECGFileUploadListener;
import com.dfth.sdk.model.ecg.ECGFormat;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.model.result.DfthDataResult;
import com.example.administrator.mybluetooth.R;
import com.example.administrator.mybluetooth.utils.TimeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by leezhiqiang on 2017/3/2.
 */

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    public static void startHistory(Context context, int type){
        Intent intent = new Intent();
        intent.setClass(context,HistoryActivity.class);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }


    private List<? extends DfthDataResult> results = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        TextView text = (TextView) findViewById(R.id.history_title);
        ListView listView = (ListView) findViewById(R.id.history);
        int type = getIntent().getIntExtra("type",0);
        initList(text,type);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
    }

    private void initList(TextView text,int type){
        if(type == 0){//12导
            text.setText("12导心电历史数据");
            results = DfthSDKManager.getManager().getDatabase().getTwelveECGResult("-1");
        }else if(type == 1){
            text.setText("单道心电历史数据");
            results = DfthSDKManager.getManager().getDatabase().getSingleResult(TestNetworkService.mUserId);
        }else{
            text.setText("血压历史数据");
        }
    }

    private String getString(DfthDataResult result){
        if(result instanceof ECGResult){
            String length = "0";
            int timeLength = 0;
            try {
                ECGFormat format = new ECGFormat(((ECGResult) result).getPath() + ECGFileFormat.ECG.toString());
                timeLength = format.timeLength();
                length = ECGUtils.getECGRecordTime(timeLength);
                format.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String upload = ((ECGResult) result).getPost() == ECGResult.CREATE_TASK_SUCCESS ? "已上传" : "未上传";
            return String.format(Locale.CHINESE,"测量时间%s-%s(时长%sms)\n文件测量时长%s(时长%sms)\n最大心率 = %d,最小心率 = %d\n平均心率 = %d\n室上性早搏次数= %d, 室性早搏次数= %d,心拍次数 = %d\n上传状态:%s",
                    TimeUtils.getTime(result.getMeasureStartTime(),TimeUtils.STANARD_TIME_FORMAT),TimeUtils.getTime(result.getMeasureEndTime(),TimeUtils.STANARD_TIME_FORMAT),result.getMeasureEndTime() - result.getMeasureStartTime(),length,timeLength,((ECGResult) result).getMaxHr(),((ECGResult) result).getMinHr(),((ECGResult) result).getAverHr(),((ECGResult) result).getSpCount(),((ECGResult) result).getPvcCount(),((ECGResult) result).getBeatCount(),upload);
        }else{
            return "";
        }
    }

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public Object getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textview;
            if(convertView == null){
                textview = new TextView(HistoryActivity.this);
            }else{
                textview = (TextView) convertView;
            }
            DfthDataResult result = (DfthDataResult) getItem(position);
            textview.setText(getString(result));
            return textview;
        }
    };
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DfthDataResult result = (DfthDataResult) mAdapter.getItem(position);
        if(result instanceof ECGResult){
            if(((ECGResult) result).getPost() != ECGResult.CREATE_TASK_SUCCESS){
                dialog = new ProgressDialog(this);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.show();
                DfthSDKManager.getManager().getDfthService().uploadECGData((ECGResult) result, new ECGFileUploadListener() {
                    @Override
                    public void onProgress(int progress) {
                        dialog.setProgress(progress);
                    }

                    @Override
                    public void onComplete(boolean success) {
                        String result = success ? "上传成功" : "上传失败";
                        Toast.makeText(HistoryActivity.this,result,Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).asyncExecute(null);
            }
        }
    }

    private ProgressDialog dialog;
}
