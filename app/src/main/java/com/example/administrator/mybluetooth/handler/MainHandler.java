package com.example.administrator.mybluetooth.handler;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Administrator on 2016/10/25 0025.
 */
public class MainHandler extends Handler {
	private static volatile MainHandler instance;

	public static MainHandler getInstance() {
		if (null == instance) {
			synchronized (MainHandler.class) {
				if (null == instance) {
					instance = new MainHandler();
				}
			}
		}
		return instance;
	}
	private MainHandler() {
		super(Looper.getMainLooper());
	}
}