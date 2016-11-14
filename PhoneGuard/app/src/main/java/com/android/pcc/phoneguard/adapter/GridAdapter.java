package com.android.pcc.phoneguard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.pcc.phoneguard.R;

import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2016/11/11.
 */
public abstract class GridAdapter<T> extends BaseAdapter {
    private List<T> mList;
    private Context mContext;
    private LayoutInflater layoutInflater;
    public GridAdapter(List list){
        mList = list;
    }
    public List<T> getList(){
        return mList;
    }
    public GridAdapter(Context context,List list){
        mContext = context;
        mList = list;
        layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount(){
        return mList.size();
    }
    @Override
    public Object getItem(int position){
        return mList.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    public View getView(int position,View convertView,ViewGroup parent){
       View view=null;
        if(view == null){
            view = getItemView(position,convertView,parent);
        }

        return view;
  }

    protected  abstract  View getItemView(int position,View convertView,ViewGroup parent);
}
