package com.android.pcc.phoneguard.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.android.pcc.phoneguard.R;
import com.android.pcc.phoneguard.adapter.DragGridAdapter;

/**
 * Created by Administrator on 2016/11/2.
 */
public class DragGridView extends GridView implements AdapterView.OnItemLongClickListener{

    private static final String TAG="DragGridView";
    private static final int MY_PERMISSIONS_SYSTEM_ALERT_WINDOW = 1;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private static final int MODE_NOMAL=1;
    private static final int MODE_DRAG =2;
    private int mode = MODE_NOMAL;
    private WindowManager mWindowManager;
    //拖拽的位置
    private int position;
    private int tepPosition;


    private View view;
    //拖拽的view
    private View dragView;


    private float mWindowsX;
    private float mWindowsY;
   //view x差值
    private float mX;
    //view的Y差值
    private float mY;

    private WindowManager.LayoutParams mlayoutParams;

    public DragGridView(Context context){
        this(context,null);
        mContext = context;
    }
    public DragGridView(Context context,AttributeSet attrs){
        this(context,attrs,0);
        mContext = context;
    }
    public DragGridView(Context context,AttributeSet attrs,int defStyle){

        super(context,attrs,defStyle);
        mContext = context;
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setOnItemLongClickListener(this);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        Log.d(TAG,"onInterceptTouchEvent");
        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mWindowsX = ev.getRawX();
                mWindowsY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id){
        Log.d(TAG,"onItemLongCLick");
        if(mode == MODE_DRAG)
            return false;
        this.view = view;
        this.position = position;
        this.tepPosition = position;
        this.mX=mWindowsX -view.getLeft() - this.getLeft();
        this.mY=mWindowsY-view.getTop() - this.getTop();
        initDragView();
        return true;
    }
    private void initDragView(){
      if(dragView==null){
          dragView = mLayoutInflater.inflate(R.layout.drag_item,null);
          TextView mTextView = (TextView)dragView.findViewById(R.id.item_name);
          mTextView.setText(mTextView.getText());
      }
        if(mlayoutParams == null){
            mlayoutParams = new WindowManager.LayoutParams();
            mlayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            mlayoutParams.flags= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mlayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            mlayoutParams.height = view.getHeight();
            mlayoutParams.width  = view.getWidth();
            mlayoutParams.x = view.getLeft() + this.getLeft();
            mlayoutParams.y = view.getTop() + this.getTop();
            view.setVisibility(INVISIBLE);

        }
        if(mContext == null)
        {
            Log.d(TAG,"contenx     xxxxxxxxxxxxxxxxxxxxx");
        }

        mWindowManager.addView(dragView,mlayoutParams);


        mode = MODE_DRAG;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                 break;
            case MotionEvent.ACTION_MOVE:
                if(mode==MODE_DRAG) {
                    updateWindows(ev);
                }
                break;
            case MotionEvent.ACTION_UP:
                 if(mode == MODE_DRAG){
                     closeWindows(ev.getX(),ev.getY());
                 }
                 break;


        }

        return super.onTouchEvent(ev);
    }

    private void updateWindows(MotionEvent ev){
        if(mode == MODE_DRAG){
            float x = ev.getRawX() -mX;
            float y = ev.getRawY() - mY;
            if(mlayoutParams != null){
                mWindowManager.updateViewLayout(dragView,mlayoutParams);
            }

            float mx = ev.getX();
            float my = ev.getY();
            int dropPositon = pointToPosition((int)mx,(int)my);
            Log.d(TAG,"dropPositon "+dropPositon+"temposition"+tepPosition);
            if(dropPositon== tepPosition || dropPositon == INVALID_POSITION){
                return ;
            }
            itemMove(dropPositon);
        }
    }
    private void itemMove(int position){
        TranslateAnimation translateAnimation;
        if (position < tepPosition) {
            for (int i = position; i < tepPosition; i++) {
                View view = getChildAt(i);
                View nextView = getChildAt(i + 1);
                float xValue = (nextView.getLeft() - view.getLeft()) * 1f / view.getWidth();
                float yValue = (nextView.getTop() - view.getTop()) * 1f / view.getHeight();
                translateAnimation =
                        new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, xValue, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, yValue);
                translateAnimation.setInterpolator(new LinearInterpolator());
                translateAnimation.setFillAfter(true);
                translateAnimation.setDuration(300);
                if (i == tepPosition - 1) {
                    translateAnimation.setAnimationListener(animationListener);
                }
                view.startAnimation(translateAnimation);
            }
        } else {
            for (int i = tepPosition + 1; i <= position; i++) {
                View view = getChildAt(i);
                View prevView = getChildAt(i - 1);
                float xValue = (prevView.getLeft() - view.getLeft()) * 1f / view.getWidth();
                float yValue = (prevView.getTop() - view.getTop()) * 1f / view.getHeight();
                translateAnimation =
                        new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, xValue, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, yValue);
                translateAnimation.setInterpolator(new LinearInterpolator());
                translateAnimation.setFillAfter(true);
                translateAnimation.setDuration(300);
                if (i == position) {
                    translateAnimation.setAnimationListener(animationListener);
                }
                view.startAnimation(translateAnimation);
            }
        }
        tepPosition = position;
    }
    Animation.AnimationListener animationListener = new Animation.AnimationListener(){

        @Override
        public void onAnimationStart(Animation animation){

        }
        @Override
        public void onAnimationEnd(Animation animation)
        {
            ListAdapter adapter = getAdapter();
            if(adapter != null && adapter instanceof DragGridAdapter)
            {
                ((DragGridAdapter) adapter).exchangePosition(position, tepPosition, true);
            }
            position = tepPosition;
        }
        @Override
         public void onAnimationRepeat(Animation animation){

         }

    };
    private void closeWindows(float x,float y){
        if(dragView != null) {
            mWindowManager.removeView(dragView);
            dragView = null;
            mlayoutParams = null;
        }
        itemDrop();
        mode = MODE_NOMAL;
    }
    private void itemDrop(){
        if (tepPosition == position || tepPosition == GridView.INVALID_POSITION) {
            getChildAt(position).setVisibility(VISIBLE);
        } else {
            ListAdapter adapter = getAdapter();
            if (adapter != null && adapter instanceof DragGridAdapter) {
                ((DragGridAdapter) adapter).exchangePosition(position, tepPosition, false);
            }
        }
    }
}
