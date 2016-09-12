package com.android.pcc.phoneguard.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016/9/10.
 * 贝塞尔曲线
 *
 */
public class Sanbanquxian extends View {
    float subx;
    float suby;
    public Sanbanquxian(Context context) {
        super(context);
    }
    public Sanbanquxian(Context context, AttributeSet attrs){
        super(context,attrs,0);
    }
    public Sanbanquxian(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas){
        Paint mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        Path path = new Path();
        path.moveTo(200,200);
        path.quadTo(subx,suby,400,200);
        canvas.drawPath(path,mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                subx = event.getX();
                subx = event.getY();
                invalidate();
        }
        return true;
    }
}
