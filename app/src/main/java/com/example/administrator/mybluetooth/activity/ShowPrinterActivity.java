package com.example.administrator.mybluetooth.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.example.administrator.mybluetooth.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by RuiYu on 2016/11/2.
 */
public class ShowPrinterActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        findViewById(R.id.scan).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.scan: {
                test();

            }
            break;
        }
    }


    private void test(){
        Log.e("dfth_sdk","start-->" + Thread.currentThread().getId());
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                Log.e("dfth_sdk","create-->" + Thread.currentThread().getId());
                e.onNext(1);
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io()).flatMap(new Function<Object,ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Object o) throws Exception {
                Log.e("dfth_sdk","apply-->" + Thread.currentThread().getId());
                return new ObservableSource<Object>() {
                    @Override
                    public void subscribe(Observer<? super Object> observer) {
                        observer.onNext(1);
                    }
                };
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.e("dfth_sdk","subscribe-->" + Thread.currentThread().getId());

            }
        });
    }

}
