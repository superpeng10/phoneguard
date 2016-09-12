package com.android.pcc.phoneguard.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.android.pcc.phoneguard.R;

/**
 * Created by Administrator on 2016/9/10.
 */
public class Memory360cleanView extends View {
    private int mWidth;
    private int mHeight;

    //线的Y坐标
    private int mLineY=100;
    private boolean isDrawBack=false;

    private int mBitmapX;
    private int mBitmapY;

    private int mWindowHeight;
    //飞行百分比
    private int mFlyRercent=100;
    private int startPointX=20;

    //结束点
    private int endX;
    private int endY;
    //结束点,/辅助点坐标
    private Point endPoint;
    private Point subPoint;
    private  BitmapFactory.Options opt;
    private Paint mPaint;
    private Path mPath;
    private Bitmap backGround;
    public Memory360cleanView(Context context){
        this(context,null);
    }
    public Memory360cleanView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public Memory360cleanView(Context context,AttributeSet attrs,int defStyle){
        super(context, attrs,defStyle);
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        opt = new BitmapFactory.Options();
        mPaint = new Paint();
        mPath = new Path();
        mWindowHeight = wm.getDefaultDisplay().getHeight();
        backGround = BitmapFactory.decodeResource(getResources(),R.drawable.t,opt);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(widthMode == MeasureSpec.EXACTLY){
            mWidth  = widthSize;
        }else{
            mWidth = getPaddingLeft()+getPaddingRight()+backGround.getWidth();
        }

        if(heightMode == MeasureSpec.EXACTLY){
            mHeight = heightSize;
        }else{
            mHeight = getPaddingBottom()+getPaddingTop()+backGround.getHeight();
        }


        setMeasuredDimension(mWidth,mHeight);
        endPoint = new Point(mWidth-getPaddingRight()-20,mHeight-startPointX-40);
        subPoint = new Point(mWidth/2,mHeight-startPointX-40);
    }

    @Override
    protected void onDraw(Canvas canvas){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mb);
        // opt.inSampleSize =2;
        // opt.inJustDecodeBounds =false;

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(20);
        canvas.drawBitmap(backGround,0,0,mPaint);

        if(isDrawBack){

            mPaint.setColor(Color.YELLOW);
            mPath.reset();
            mPath.moveTo(20,mHeight-startPointX-40);
            int tempY= mHeight-startPointX-40;
            mPath.quadTo(subPoint.x,tempY+(subPoint.y-tempY)*mFlyRercent/100,endPoint.x,endPoint.y);
            canvas.drawPath(mPath,mPaint);

            if(mFlyRercent > 0){
                canvas.drawBitmap(bitmap,mBitmapX,mBitmapY*mFlyRercent/100,mPaint);
                mFlyRercent -=5;
                postInvalidateDelayed(10);
            }else{
                mFlyRercent = 100;
                isDrawBack = false;
            }
        }else{
            mPaint.setColor(Color.YELLOW);
            mPath.reset();
            mPath.moveTo(20,mHeight-startPointX-40);
            mPath.quadTo(subPoint.x,subPoint.y,endPoint.x,endPoint.y);
            canvas.drawPath(mPath,mPaint);

            mBitmapX = subPoint.x - bitmap.getWidth()/2;
            mBitmapY = (subPoint.y-mLineY)/2 + mLineY-bitmap.getHeight();
            canvas.drawBitmap(bitmap,mBitmapX,mBitmapY,mPaint);
        }
        mPaint.setColor(Color.GRAY);
        canvas.drawCircle(20,endPoint.y,10,mPaint);
        canvas.drawCircle(mWidth-getPaddingRight()-20,endPoint.y,10,mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getY()> (mHeight-startPointX-40)) {

                    subPoint.y = (int) event.getY();
                }
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                endX = (int) event.getX();
                endY = (int) event.getY();

                isDrawBack = true;
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }
}
