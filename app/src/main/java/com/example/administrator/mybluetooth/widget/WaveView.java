package com.example.administrator.mybluetooth.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dfth.sdk.model.ecg.ECGMeasureData;
import com.dfth.sdk.model.ecg.EcgDataTransmitted;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.example.administrator.mybluetooth.DfthSDKApplication;
import com.example.administrator.mybluetooth.R;
import com.example.administrator.mybluetooth.thread.LockLinkedList;
import com.example.administrator.mybluetooth.utils.DelayPerformMethod;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/10/18 0018.
 */
public class WaveView extends SurfaceView implements SurfaceHolder.Callback {
    private float perItemWidth;
    private float perLineWidth;
    private float leaderWidth;
    private int zoomX = 1;
    private int zoomY = 1;
    private float[] baseline;
    private PointF[] curPos;
    private boolean runner = false;
    private UpdateThread thread;
    private boolean[] leaders;
    private int ecgViewHeight;
    private float[] lineLeft;
    private float[] lineTop;
    private Rect leaderRect = new Rect();
    private String[] leaderNames = {"I", "II", "III", "avR", "avL", "avF",
            "V1", "V2", "V3", "V4", "V5", "V6"};
    private LockLinkedList<ECGDataView> blockDataList = new LockLinkedList<>(
            100, 500);
    private final int mLine;
    public WaveView(Context context,int line) {
        super(context);
        this.mLine = line;
        getHolder().addCallback(this);
        leaders = new boolean[mLine];
        Arrays.fill(leaders,true);
    }
    private void initalize(Context context) {
        lineLeft = null;
        lineTop = null;
        // 初始化格子的个数
        perItemWidth = this.getWidth() / 15f;
        // 计算导联显示区域的宽度
        leaderWidth = this.getWidth() / 15f;
        // 计算每一小格的宽度
        perLineWidth = perItemWidth / 5f;
        // 初始化当前波形的位置
        curPos = new PointF[mLine];
        // 心电波形的位置
        ecgViewHeight = (int) (this.getHeight() - 2 * perItemWidth);
        for (int i = 0; i < curPos.length; i++) {
            curPos[i] = new PointF(0, 0);
            curPos[i].x = leaderWidth;
            curPos[i].y = 0;
        }
        leaderRect.top = 0;
        leaderRect.left = 0;
        leaderRect.right = WaveView.this.getWidth();
        leaderRect.bottom = WaveView.this.getHeight();
    }

    public void addBlock(ECGDataView data) {
        blockDataList.addObject(data);
    }

    public synchronized ECGDataView getBlock() throws Exception {
        return blockDataList.getObject();
    }

    public void clear() {
        blockDataList.clear();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    private void drawBack(Canvas canvas) {
        Paint p = new Paint();
        LinearGradient gradient = new LinearGradient(getWidth() / 2, 0, getWidth() / 2, getHeight(), new int[]{
                DfthSDKApplication.getColorRes(R.color.bg_end),
                DfthSDKApplication.getColorRes(R.color.bg_start),
        }, new float[]{0, 1.0f}, Shader.TileMode.MIRROR);
        p.setShader(gradient);
        canvas.drawPaint(p);
    }

    private void drawLineBack(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(DfthSDKApplication.getColorRes(R.color.role_back));
        canvas.drawPaint(p);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (perItemWidth < 1)
            initalize(getContext());
        Canvas canvas = holder.lockCanvas(null);
        if (canvas != null) {
            drawBack(canvas);
            drawEcgGrid(canvas,
                    new Rect(0, 0, this.getWidth(), this
                            .getHeight()));
        }
        if (canvas != null) {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private synchronized void drawWave(Canvas canvas,
                                       ECGMeasureData data, float adunit,boolean empty) {
        drawBlockData(canvas, data, adunit,empty);
    }

    /**
     * 显示某一个数据块
     *
     * @param canvas
     * @param cur_block
     * @param adunit
     */
    private synchronized void drawBlockData(Canvas canvas,
                                            ECGMeasureData cur_block, float adunit,boolean empty) {
        int row = cur_block.chan();
        int col = cur_block.pts();
        if (baseline == null) {
            baseline = new float[row];
            int visible_count = 0;
            for (int i = 0; i < leaders.length; i++) {
                if (leaders[i]) {
                    visible_count++;
                }
            }
            float step_y = ecgViewHeight / (2.0f * visible_count);
            int visible_index = 0;
            for (int i = 0; i < row; i++) {
                short v = cur_block.getData(i,0);
                curPos[i] = new PointF(leaderWidth, v);
                if (leaders[i]) {
                    baseline[i] = 2 * perItemWidth + step_y
                            * (2 * visible_index + 1);
                    visible_index++;
                    curPos[i].y = baseline[i];
                }
            }
        }
        Paint paint = new Paint();
        paint.setStrokeWidth(2.5f);
        paint.setAntiAlias(true);
        Rect dirt = new Rect();
        dirt.left = (int) (curPos[0].x);
        dirt.right = (int) (curPos[0].x + perItemWidth * zoomX) + 4 + 4;
        dirt.top = 0;
        dirt.bottom = WaveView.this.getHeight();
        canvas.drawRect(dirt, paint);
        drawBack(canvas);
        drawEcgGrid(canvas, new Rect(0, 0, WaveView.this.getWidth(), WaveView.this.getHeight()));
        int color = empty ? (R.color.colorAccent) : (R.color.measure_line_color);
        paint.setColor(DfthSDKApplication.getColorRes(color));
        float step = perItemWidth / 50f;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                float x = curPos[i].x + step * zoomX;
                short val = cur_block.getData(i,j);
                float y = -((val) / adunit) * perLineWidth
                        * zoomY * 10;
                y += baseline[i];
                if (leaders[i]) {
                    canvas.drawLine(curPos[i].x, curPos[i].y, x, y, paint);
                }
                curPos[i].x = x;
                curPos[i].y = y;
                if (Math.abs(curPos[i].x) >= (lineLeft[lineLeft.length - 1] - 1)) {
                    curPos[i].x = leaderWidth;
                    break;
                }
            }
        }
    }

    /**
     * 画波形
     *
     * @param data
     */

    public void drawWave(EcgDataTransmitted data) {
        synchronized (data) {
            ECGDataView ecg = new ECGDataView();
            ecg.block = data.getEcgData();
            ecg.adunit = data.getAdunit();
            ecg.startTime = data.getStartTime();
            addBlock(ecg);
        }
    }

    // 绘制指定区域的心电网格
    public synchronized void drawEcgGrid(Canvas canvas, Rect rect) {
        try {
            Paint paint = new Paint();
            boolean status = false;// 判断是否是第一次绘制网格，为了网格能够重叠
            int number = (int) (rect.width() / perItemWidth * 5);// 计算需要绘制网格线的数量
            int leftNumber = 0;// 从第几根线开始绘制
            if (lineLeft == null) {
                lineLeft = new float[number + 1];
                status = true;
            } else {// 找出当前为第几根线
                float value = 7.2f;
                for (int i = 0; i < lineLeft.length; i++) {// 找出最小的number
                    float value1 = rect.left - lineLeft[i];
                    if (Math.abs(value1) < Math.abs(value)) {
                        value = value1;
                        leftNumber = i;
                    }
                }
            }
            float left = rect.left;
            int i;
            for (i = leftNumber; i < number + leftNumber; i++) {
                if (status)
                    lineLeft[i] = left;
                else
                    left = lineLeft[i];
                if (i % 5 == 0) {
                    paint.setColor(Color.rgb(163, 163, 188));
                    canvas.drawLine(left, rect.top, left, rect.bottom,
                            paint);
                } else {
                    paint.setColor(Color.rgb(66, 66, 92));
                    canvas.drawLine(left, rect.top, left, rect.bottom, paint);
                }
                left += perLineWidth;
            }
            if (status) {
                lineLeft[i] = left;
            }
            if (i % 5 == 0) {
                paint.setColor(Color.rgb(163, 163, 188));
                canvas.drawLine(left, rect.top, left, rect.bottom, paint);
            } else {
                paint.setColor(Color.rgb(66, 66, 92));
                canvas.drawLine(left, rect.top, left, rect.bottom, paint);
            }
            number = (int) (rect.height() / (float) perItemWidth * 5);
            if (lineTop == null) {
                lineTop = new float[number + 1];
                status = true;
            } else {
                float value = 7.2f;
                for (i = 0; i < lineTop.length; i++) {// 找出最小的number
                    float value1 = rect.top - lineTop[i];
                    if (Math.abs(value1) < Math.abs(value)) {
                        value = value1;
                        leftNumber = i;
                    }
                }
            }
            left = rect.top;
            for (i = leftNumber; i < number + leftNumber; i++) {
                if (status) {
                    lineTop[i] = left;
                } else {
                    left = lineTop[i];
                }
                if (i % 5 == 0) {
                    paint.setColor(Color.rgb(163, 163, 188));
                    canvas.drawLine(rect.left, left, rect.right, left, paint);
                } else {
                    paint.setColor(Color.rgb(66, 66, 92));
                    canvas.drawLine(rect.left, left, rect.right, left, paint);
                }
                left += perLineWidth;
            }
            if (status) {
                lineTop[i] = left;
            }
            if (i % 5 == 0) {
                paint.setColor(Color.rgb(163, 163, 188));
                canvas.drawLine(rect.left, left, rect.right, left, paint);
            } else {
                paint.setColor(Color.rgb(66, 66, 92));
                canvas.drawLine(rect.left, left, rect.right, left, paint);
            }
        } catch (Exception e) {
            Logger.e(e, null);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    private void drawLeader(Canvas canvas, Rect dirt) {
        if (canvas == null || baseline == null)
            return;
        Paint paint = new Paint();
        paint.setTextSize(perItemWidth / 2.2f);
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        if (dirt != null) {
            canvas.drawRect(dirt, paint);
        }
        paint.setColor(Color.WHITE);
        for (int i = 0; i < leaders.length; i++) {
            if (leaders[i]) {
                String text = leaderNames[i];
                if (baseline != null && baseline.length > i) {
                    canvas.drawText(text, dirt.width() / 2, baseline[i],
                            paint);
                }
            }
        }
    }

    // 心电波形绘制线程
    class UpdateThread extends Thread {
        boolean pause = false;

        public void pause() {
            pause = true;
        }
        public UpdateThread() {
            reset();
        }
        @Override
        public void run() {
            Rect dirt = new Rect();
            Rect dirt2 = new Rect();
            while (runner) {
                final SurfaceHolder holder = getHolder();
                synchronized (holder) {
                    Canvas canvas = null;
                    try {
                        ECGDataView data = getBlock();
                        if (data != null && !pause) {
                            dirt.left = (int) curPos[0].x;
                            dirt.right = (int) (curPos[0].x + perItemWidth
                                    * zoomX) + 8;
                            dirt.top = 0;
                            dirt.bottom = WaveView.this.getHeight();
                            canvas = holder.lockCanvas(dirt);
                            if (canvas != null) {// 绘制波形
                                drawWave(canvas, data.block, data.adunit,false);
                                holder.unlockCanvasAndPost(canvas);
                            }
                            canvas = holder.lockCanvas(leaderRect);
                            drawBack(canvas);
                            leaderRect.top = 0;
                            leaderRect.left = 0;
                            leaderRect.right = (int) leaderWidth;
                            leaderRect.bottom = WaveView.this.getHeight();
                            drawEcgGrid(canvas, leaderRect);
                            drawLeader(canvas, leaderRect);
                            holder.unlockCanvasAndPost(canvas);
                            dirt2.left = (int) curPos[0].x;
                            dirt2.right = (int) (curPos[0].x) + 8;
                            dirt2.top = 0;
                            dirt2.bottom = WaveView.this.getHeight();
                            canvas = holder.lockCanvas(dirt2);
                            drawLineBack(canvas);
                        }
                    } catch (Exception e) {
                    } finally {
                        if (canvas != null) {
                            holder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
            reset();
        }

        public void cancel() {
            clear();
            runner = false;
            interrupt();
        }
    }
    public void startDraw() {
        if (thread == null) {
            runner = true;
            thread = new UpdateThread();
            thread.start();
            clear();
        }
        reset();
    }
    public void endDraw() {
        if (thread != null) {
            thread.cancel();
            thread = null;
        }
    }
    // 界面重置
    public void reset() {
        final SurfaceHolder holder = getHolder();
        synchronized (holder) {
            Canvas canvas = holder.lockCanvas(null);
            if (canvas != null) {
                drawBack(canvas);
                drawEcgGrid(canvas,
                        new Rect(0, 0, this.getWidth(),
                                this.getHeight()));
            }
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
            if (curPos != null) {
                for (int i = 0; i < curPos.length; i++) {
                    curPos[i] = new PointF(0, 0);
                    curPos[i].x = (int) leaderWidth;
                    curPos[i].y = 0;
                }
                baseline = null;
            }
        }
    }

    // 波形数据的模型
    class ECGDataView {
        ECGMeasureData block;// 数据块（心电数据）
        float adunit;// 量化电平
        long startTime;
    }
}
