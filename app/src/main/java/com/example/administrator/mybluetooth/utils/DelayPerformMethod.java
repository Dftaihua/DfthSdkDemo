package com.example.administrator.mybluetooth.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.reflect.Method;

public class DelayPerformMethod {
    private static DelayPerformMethod manager;

    public synchronized static DelayPerformMethod getMethod() {
        if (manager == null) {
            manager = new DelayPerformMethod();
        }
        return manager;
    }

    public void performMedthDelayStaticTime(long time, Class<?> cla, String methodName, Class[] claes, Object[] obj) {
        Object[] objs = new Object[4];
        objs[0] = cla;
        objs[1] = methodName;
        objs[2] = obj;
        objs[3] = claes;
        Message message = new Message();
        message.what = 1;
        message.obj = objs;
        handler.sendMessageDelayed(message, time);
    }

    public void performMethodDelayTime(long time, Object object, String methodName, Object... obj) {
        Object[] objs = new Object[3];
        objs[0] = object;
        objs[1] = methodName;
        objs[2] = obj;
        Message message = new Message();
        message.what = 0;
        message.obj = objs;
        handler.sendMessageDelayed(message, time);
    }

    private void invokeStatic(Class<?> cla, String methodName, Object[] obj, Class[] classes) {
        Method method;
        while (!(cla.equals(Object.class))) {
            try {
                if (classes == null) {
                    method = cla.getDeclaredMethod(methodName);
                } else {
                    method = cla.getDeclaredMethod(methodName, classes);
                }
                method.setAccessible(true);
                method.invoke(null, obj);
                break;
            } catch (Exception e) {
                cla = cla.getSuperclass();
            }
        }
    }


    @SuppressWarnings("rawtypes")
    private void invoke(Object object, String methodName, Object[] obj) {
        Class[] classs = null;
        if (obj != null && obj.length > 0) {
            classs = new Class[obj.length];
            for (int i = 0; i < obj.length; i++) {
                classs[i] = obj[i].getClass();
            }
        }
        Method method;

        Class c = object.getClass();
        while (!(c.equals(Object.class))) {
            try {
                if (classs == null) {
                    method = c.getDeclaredMethod(methodName);
                } else {
                    method = c.getDeclaredMethod(methodName, classs);
                }
                method.setAccessible(true);
                method.invoke(object, obj);
                break;
            } catch (Exception e) {
                c = c.getSuperclass();
            }
        }
    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Object obj = msg.obj;
                Object[] objs = (Object[]) obj;
                if (((Object[]) objs[2]).length > 0) {
                    invoke(objs[0], (String) objs[1], (Object[]) objs[2]);
                } else {
                    invoke(objs[0], (String) objs[1], null);
                }
            } else {
                Object obj = msg.obj;
                Object[] objs = (Object[]) obj;
                if (((Object[]) objs[2]).length > 0) {
                    invokeStatic((Class) objs[0], (String) objs[1], (Object[]) objs[2], (Class[]) objs[3]);
                } else {
                    invokeStatic((Class) objs[0], (String) objs[1], null, null);
                }
            }

        }
    };
}
