package com.example.administrator.mybluetooth.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.dfth.sdk.Others.Utils.LockLinkedList;
import com.dfth.sdk.model.ecg.ECGMeasureData;
import com.dfth.sdk.model.ecg.EcgDataTransmitted;

public class EcgWaveView extends RelativeLayout {
    static class Constants{
        static int ECG_GRID_COUNT = 15;
    }

    private ECGGridView mECGGirdView;
    private WaveView mWaveView;
    private LockLinkedList<ECGDataView> blockDataList = new LockLinkedList<ECGDataView>(100);
    private ECGMeasureData mLastDatas;
    public EcgWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mECGGirdView = new ECGGridView(context);
        mWaveView = new WaveView(context);
        addView(mECGGirdView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mWaveView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setWillNotDraw(false);
    }
    public EcgWaveView(Context context) {
        super(context);
        mECGGirdView = new ECGGridView(context);
        mWaveView = new WaveView(context);
        addView(mECGGirdView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mWaveView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setWillNotDraw(false);
    }

    private class ECGGridView extends View {
        private final Paint mLinePaint = new Paint();
        private float mEveryWidth;
        private float mEveryGridWidth;
        public ECGGridView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            mEveryWidth = getWidth() / Constants.ECG_GRID_COUNT;
            mEveryGridWidth = mEveryWidth / 5f;
            mLinePaint.setColor(Color.rgb(163, 163, 188));
            canvas.drawColor(Color.BLACK);
            for (int i = 0; i < Constants.ECG_GRID_COUNT * 5; i++) {
                if (i % 5 == 0) {
                    mLinePaint.setStrokeWidth(2f);
                    canvas.drawLine(i * mEveryGridWidth, 0, i * mEveryGridWidth, getHeight(), mLinePaint);
                } else {
                    mLinePaint.setStrokeWidth(1f);
                    //canvas.drawLine(i * mEveryGridWidth, 0, i * mEveryGridWidth, getHeight(), mLinePaint);
                }
            }
            int count = (int) (getHeight() / mEveryWidth + 1);
            for (int i = 0; i < count * 5; i++) {
                if (i % 5 == 0) {
                    mLinePaint.setStrokeWidth(2f);
                    canvas.drawLine(0, i * mEveryGridWidth, getWidth(), i * mEveryGridWidth, mLinePaint);
                } else {
                    mLinePaint.setStrokeWidth(1f);
                    //canvas.drawLine(0, i * mEveryGridWidth, getWidth(), i * mEveryGridWidth, mLinePaint);
                }
            }

        }
    }

    private class WaveView extends SurfaceView implements SurfaceHolder.Callback {
        private final SurfaceHolder mHolder;
        private Paint mBackPaint = new Paint();
        private float mBaseLine[];
        private final Paint mFreshPaint = new Paint();
        private final Paint mDrawLinePaint = new Paint();
        private UpdateThread mThread;
        private PointF mPoint[];
        private Bitmap mBitmap;
        private short[] mFirstPoints;
        public WaveView(Context context) {
            super(context);
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setFormat(PixelFormat.TRANSLUCENT);
            setZOrderOnTop(true);
            mBackPaint.setColor(Color.WHITE);
            mFreshPaint.setColor(Color.WHITE);
            mDrawLinePaint.setColor(Color.WHITE);
            mDrawLinePaint.setStrokeWidth(4);
            mDrawLinePaint.setAntiAlias(true);
            mDrawLinePaint.setStyle(Paint.Style.FILL);
        }

        private void reset() {
            synchronized (mHolder) {
                Canvas c = mHolder.lockCanvas(null);
                if (c != null) {
                    mBackPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    c.drawPaint(mBackPaint);
                    mHolder.unlockCanvasAndPost(c);
                }

            }
        }

        /**
         * 显示某一个数据块
         *
         * @param canvas
         */
        private synchronized void drawBlockData(Canvas canvas, Rect rect, ECGDataView data) {
            mBackPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            float perItemWidth = mECGGirdView.mEveryWidth;
            float perLineWidth = mECGGirdView.mEveryGridWidth;
            if (mBaseLine == null) {
                mBaseLine = new float[data.chan];
                mFirstPoints = new short[data.chan];
                float everyBlock = getHeight() / mBaseLine.length;
                for (int i = 1; i <= mBaseLine.length; i++) {
                    mBaseLine[i - 1] = i * everyBlock - everyBlock * 0.5f;
                    mFirstPoints[i - 1] = data.block.getData(i - 1,0);
                }
                mPoint = new PointF[4];
            }
            float step = perItemWidth / 50f;
            // 刷新条的宽度
            for (int i = 0; i < data.chan; i++) {
                if (mPoint[i] == null) {
                    mPoint[i] = new PointF(0, 0);
                }
                PointF p = new PointF(mPoint[i].x, mPoint[i].y);
                for (int k = 0; k < data.block.pts(); k++) {
                    float x = p.x + step * 1;
                    short val = data.block.getData(i, k);
                    val = (short) (val - mFirstPoints[i]);
                    float y = -(val);
                    y += mBaseLine[i];
                    canvas.drawLine(x, y, p.x, p.y, mDrawLinePaint);
                    p.x = x;
                    p.y = y;
                }
                mPoint[i].x = p.x;
                mPoint[i].y = p.y;
            }
            if (mPoint[0].x > getWidth()) {
                mPoint[0].x = 0;
            }
        }


        class UpdateThread extends Thread {
            static final long UPDATE_TIME = 90;
            boolean pause = false;
            long lastUpdateTime = 0;
            boolean runner = true;

            private void waitTime() throws Exception {
                if (lastUpdateTime == 0) {
                    lastUpdateTime = System.currentTimeMillis();
                } else {
                    long b = System.currentTimeMillis() - lastUpdateTime;
                    if (b < UPDATE_TIME) {
                        Thread.sleep(UPDATE_TIME - b);
                    }
                    lastUpdateTime = System.currentTimeMillis();
                }
            }

            @Override
            public void run() {
                while (runner) {
                    ECGDataView data = null;
                    try {
                        data = getBlock();
                        //waitTime();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        return;
                    }
                    Canvas canvas = null;
                    try {
                        synchronized (mHolder) {
                            if (mBitmap == null) {
                                mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                            }
                            if (data != null && !pause) {
                                Canvas c = new Canvas(mBitmap);
                                Rect rect = new Rect();
                                float perItemWidth = mECGGirdView.mEveryWidth;
//                                rect.left = (int) (mIndex * 30 * (perItemWidth / 200f));
                                rect.left = mPoint == null ? 0 : (int) mPoint[0].x;
                                if (rect.left == 0) {
                                    mBackPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                                    c.drawPaint(mBackPaint);
                                }

                                rect.right = (int) (rect.left + data.block.pts() * (mECGGirdView.mEveryWidth) / 200f + mECGGirdView.mEveryWidth / 2f);
                                rect.top = 0;
                                rect.bottom = getHeight();
                                rect.left = 0;
                                drawBlockData(c, rect, data);
                                canvas = mHolder.lockCanvas(rect);
                                if (canvas != null) {
                                    mBackPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                                    canvas.drawPaint(mBackPaint);
                                    canvas.drawBitmap(mBitmap, 0, 0, null);
//                                    drawBlockData(canvas,rect,data);
                                }
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    } finally {
                        if (canvas != null) {
                            mHolder.unlockCanvasAndPost(canvas);
                        }
                    }
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }


        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                                   int arg3) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder arg0) {
            reset();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder arg0) {
            // TODO Auto-generated method stub

        }

        void startDraw() {
            if (mThread == null) {
                mThread = new UpdateThread();
                mThread.start();
            }
        }
        void endDraw() {
            if(mThread != null){
                mThread.runner = false;
                mThread = null;
            }
            reset();
        }

    }

    public void addBlock(ECGDataView data) {
        try {
            if (blockDataList == null) {
                blockDataList = new LockLinkedList<ECGDataView>(100);
            }
            blockDataList.addObject(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ECGDataView getBlock() throws Exception {
        return blockDataList.getObject();
    }

    public void addData(EcgDataTransmitted data) {
        synchronized (data) {
            final ECGMeasureData cur_block = data.getEcgData();
            if (cur_block == null)
                throw new NullPointerException("不是波形数据");
            ECGDataView ecg = new ECGDataView();
            ecg.chan = cur_block.chan();
            ecg.block = cur_block;
            ecg.adunit = cur_block.adunit();
            ecg.processTime = System.currentTimeMillis();
            addBlock(ecg);






            /*int s = mLastDatas == null ? 0 : mLastDatas.col();
            int size = (cur_block.col() + s);
            if(size < 50){
                BinMatrix ss = new BinMatrix(cur_block.row(),cur_block.col() + s);
                if(s != 0){
                    System.arraycopy(mLastDatas.getData(),0,ss.getData(),0,mLastDatas.size());
                }
                System.arraycopy(cur_block.getData(),0,ss.getData(),s,cur_block.size());
                mLastDatas = ss;
            }else{
                int i;
                for (i = 0; i < cur_block.size();) {
                    ECGDataView ecg = new ECGDataView();
                    ecg.chan = data.getData().row();
                    BinMatrix block = new BinMatrix(cur_block.row(),50);
                    if(s != 0){
                        System.arraycopy(mLastDatas.getData(),0,block.getData(),0,mLastDatas.size());
                    }
                        if(cur_block.size() - i < (50 - s) * cur_block.row()){
                            break;
                        }
                        System.arraycopy(cur_block.getData(),i,block.getData(),s,(50 -s) * cur_block.row());
                    i += (50 - s) * cur_block.row();
                    s = 0;
                    ecg.block = Sampling2DrawData(block);
                    //ecg.hr = data.getNonZeroHr();
                    ecg.adunit = data.getData().get_adunit();
                    //ecg.startMeasureTime = data.getMeasureTime();
                    ecg.processTime = System.currentTimeMillis();
                    addBlock(ecg);
                }
                s = mLastDatas == null ? 0 : mLastDatas.col();
                mLastDatas = new BinMatrix(cur_block.row(),((size - s) * cur_block.row() - i) / cur_block.row());
                System.arraycopy(cur_block.getData(),i,mLastDatas.getData(),0,mLastDatas.size());
            }*/
        }
    }

    public void startDraw() {
        mWaveView.startDraw();
    }

    public void endDraw(){
        mWaveView.endDraw();
    }

    public static class ECGDataView {
        ECGMeasureData block;// 数据块（心电数据）
        int hr;// 心律
        int chan;
        float adunit;// 采样率
        long startMeasureTime;// 开始测量时间
        long processTime;// 记录时间
    }

}
