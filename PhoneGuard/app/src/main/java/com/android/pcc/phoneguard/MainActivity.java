package com.android.pcc.phoneguard;



import android.app.Activity;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.ViewGroup;

import com.android.pcc.phoneguard.Fragment.AppFragment;
import com.android.pcc.phoneguard.Fragment.GameFragment;
import com.android.pcc.phoneguard.Fragment.HomeFragment;
import com.android.pcc.phoneguard.Fragment.ManagerFragment;
import com.android.pcc.phoneguard.Fragment.SoftFragment;
import com.android.pcc.phoneguard.view.IconTextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private IconTextView itv_home;
    private IconTextView itv_game;
    private IconTextView itv_soft;
    private IconTextView itv_app;
    private IconTextView itv_manager;

    private AppFragment mAppFragment;
    private GameFragment mGameFragment;
    private HomeFragment mHomeFragment;
    private ManagerFragment mManagerFragment;
    private SoftFragment mSoftFragment;

    private ViewPager mViewPager;
    private  int currenttab = -1;
    private List<Fragment> mListFragment = new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
        changeView(0);
    }

    public void initView(){
        itv_home = (IconTextView)findViewById(R.id.buttom_home);
        itv_game = (IconTextView)findViewById(R.id.button_game);
        itv_soft = (IconTextView)findViewById(R.id.button_soft);
        itv_app  = (IconTextView)findViewById(R.id.button_app);
        itv_manager = (IconTextView)findViewById(R.id.button_manager);
        itv_home.setOnClickListener(this);
        itv_game.setOnClickListener(this);
        itv_soft.setOnClickListener(this);
        itv_app.setOnClickListener(this);
        itv_manager.setOnClickListener(this);

        mViewPager  = (ViewPager)findViewById(R.id.viewpager);
    }

    public void initFragment(){
        mAppFragment = new AppFragment();
        mGameFragment = new GameFragment();
        mHomeFragment = new HomeFragment();
        mManagerFragment = new ManagerFragment();
        mSoftFragment = new SoftFragment();
        mListFragment.add(mHomeFragment);
        mListFragment.add(mGameFragment);
        mListFragment.add(mAppFragment);
        mListFragment.add(mSoftFragment);
        mListFragment.add(mManagerFragment);
        mViewPager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager()));

    }
    @Override
    public void onClick(View v) {
         switch(v.getId()){
            case R.id.buttom_home:
                changeView(0);
                 break;
             case R.id.button_game:
                 changeView(1);
                 break;
             case R.id.button_soft:
                 changeView(2);
                 break;
             case R.id.button_app:
                 changeView(3);
                 break;
             case R.id.button_manager:
                 changeView(4);
                 break;
          default:
              break;
        }
    }


    class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter
    {

        public MyFrageStatePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mListFragment.get(position);
        }

        @Override
        public int getCount() {
            return mListFragment.size();
        }

        /**
         * 每次更新完成ViewPager的内容后，调用该接口，此处复写主要是为了让导航按钮上层的覆盖层能够动态的移动
         */
        @Override
        public void finishUpdate(ViewGroup container)
        {
            super.finishUpdate(container);//这句话要放在最前面，否则会报错
            //获取当前的视图是位于ViewGroup的第几个位置，用来更新对应的覆盖层所在的位置
            int currentItem=mViewPager.getCurrentItem();
            if (currentItem==currenttab)
            {
                return ;
            }

            currenttab=mViewPager.getCurrentItem();
        }

    }

    private void changeView(int desTab)
    {
        mViewPager.setCurrentItem(desTab, true);
    }
}
