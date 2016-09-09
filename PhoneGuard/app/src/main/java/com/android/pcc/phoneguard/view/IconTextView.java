package com.android.pcc.phoneguard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.android.pcc.phoneguard.R;

/**
 * Created by Administrator on 2016/9/8.
 */
public class IconTextView extends View {
   //控件宽度
    private int  mWidth;
    //控件高度
    private int mHeight;

    //控件图片
    private Bitmap mIcon;

    //图片缩放模式
    private int mIconScale;
    private static final int ICON_SCALE_FITXY =0;
    private static final int ICON_SCALE_CENTER =1;

    //文字
   private String mText;
   private int mTextColor;
   private int mTextSize;
   private Paint mPaint;
   //文本的约束
   private Rect mTextBound;
   private Rect mRect;

    public IconTextView(Context context) {
        this(context,null);
    }

    public IconTextView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyle, 0);

        int n = a.getIndexCount();

        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);

            switch (attr)
            {
                case R.styleable.CustomImageView_image:
                    mIcon = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
                    break;
                case R.styleable.CustomImageView_imageScaleType:
                    mIconScale = a.getInt(attr, 0);
                    break;
                case R.styleable.CustomImageView_titleText:

                    mText = a.getString(attr);

                    break;
                case R.styleable.CustomImageView_customTextColor:
                    mTextColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomImageView_titleTextSize:
                    mTextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            16, getResources().getDisplayMetrics()));
                    break;

            }
        }
        a.recycle();
        mRect = new Rect();
        mPaint = new Paint();
        mTextBound = new Rect();
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mTextBound);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            Log.e("xxx", "EXACTLY");
            mWidth = specSize;
        } else
        {
            // 由图片决定的宽
            int desireByImg = getPaddingLeft() + getPaddingRight() + mIcon.getWidth();
            // 由字体决定的宽
            int desireByTitle = getPaddingLeft() + getPaddingRight() + mTextBound.width();

            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                int desire = Math.max(desireByImg, desireByTitle);
                mWidth = Math.min(desire, specSize);
                Log.e("xxx", "AT_MOST");
            }
        }

        /***
         * 设置高度
         */

        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mHeight = specSize;
        } else
        {
            int desire = getPaddingTop() + getPaddingBottom() + mIcon.getHeight() + mTextBound.height();
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                mHeight = Math.min(desire, specSize);
            }
        }
        setMeasuredDimension(mWidth, mHeight);

    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        // super.onDraw(canvas);
        /**
         * 边框
         */
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.CYAN);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        mRect.left = getPaddingLeft();
        mRect.right = mWidth - getPaddingRight();
        mRect.top = getPaddingTop();
        mRect.bottom = mHeight - getPaddingBottom();

        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        /**
         * 当前设置的宽度小于字体需要的宽度，将字体改为xxx...
         */
        if (mTextBound.width() > mWidth)
        {
            TextPaint paint = new TextPaint(mPaint);
            String msg = TextUtils.ellipsize(mText, paint, (float) mWidth - getPaddingLeft() - getPaddingRight(),
                    TextUtils.TruncateAt.END).toString();
            canvas.drawText(msg, getPaddingLeft(), mHeight - getPaddingBottom(), mPaint);

        } else
        {
            //正常情况，将字体居中
            canvas.drawText(mText, mWidth / 2 - mTextBound.width() * 1.0f / 2, mHeight - getPaddingBottom(), mPaint);
        }

        //取消使用掉的快
        mRect.bottom -= mTextBound.height();

        if (mIconScale == ICON_SCALE_FITXY)
        {
            canvas.drawBitmap(mIcon, null, mRect, mPaint);
        } else
        {
            //计算居中的矩形范围
            mRect.left = mWidth / 2 - mIcon.getWidth() / 2;
            mRect.right = mWidth / 2 + mIcon.getWidth() / 2;
            mRect.top = (mHeight - mTextBound.height()) / 2 - mIcon.getHeight() / 2;
            mRect.bottom = (mHeight - mTextBound.height()) / 2 + mIcon.getHeight() / 2;

            canvas.drawBitmap(mIcon, null, mRect, mPaint);
        }

    }
}
