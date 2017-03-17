package com.example.administrator.mybluetooth.thread;

import android.util.Log;

import com.dfth.sdk.Others.Utils.Logger.Logger;

import java.util.LinkedList;

public class LockLinkedList<T> {
    private LinkedList<T> quene = new LinkedList<T>();
    private int bufferSize = 100;
    private Object lock = new Object();
    private long waitTime;
    private static final String TAG = "LockLinkedList";

    public LockLinkedList(int bufferSize) {
        this(bufferSize, Integer.MAX_VALUE);
    }

    public LockLinkedList(int bufferSize, long waitTime) {
        this.bufferSize = bufferSize;
        this.waitTime = waitTime;
    }

    public void addObject(T t) {
        synchronized (lock) {
            if (quene != null) {
                if (quene.size() >= bufferSize) {
                    try {
                        lock.wait(waitTime);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        Logger.e(e, null);
                    }
                }
            } else {
                quene = new LinkedList<T>();
            }
            quene.addLast(t);
            lock.notifyAll();
        }
    }

    public T getObject() throws Exception {
        synchronized (lock) {
            if (quene != null) {
                if (quene.size() <= 0) {
                    try {
                        lock.wait(waitTime);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        Logger.e(e, null);
                    }
                }
                lock.notifyAll();
                if (quene != null) {
                    return quene.removeFirst();
                }
            }
            return null;
        }
    }

    public void clear() {
        synchronized (lock) {
            if (quene != null) {
                quene.clear();
                quene = null;
                lock.notifyAll();
            }
        }
    }

    public boolean contains(T t) {
        synchronized (lock) {
            if (quene != null) {
                return quene.contains(t);
            } else {
                return false;
            }
        }
    }

    public LinkedList<T> getQuene() {
        return quene;
    }
}
