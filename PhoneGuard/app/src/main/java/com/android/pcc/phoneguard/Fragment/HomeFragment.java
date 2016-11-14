package com.android.pcc.phoneguard.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pcc.phoneguard.R;
import com.android.pcc.phoneguard.adapter.DragGridAdapter;
import com.android.pcc.phoneguard.adapter.GridAdapter;
import com.android.pcc.phoneguard.bean.Gridviewbean;
import com.android.pcc.phoneguard.contants.HomeBean;
import com.android.pcc.phoneguard.view.DragGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/9.
 */
public class HomeFragment extends Fragment {
    private GridView mGridView;
    private List<Gridviewbean> mlist ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=null;
        view = inflater.inflate(R.layout.home_layout,container,false);
        mGridView = (GridView) view.findViewById(R.id.hGridView);
        initList();
        return view;
    }
    class myAdapter extends GridAdapter<Gridviewbean>{
        public myAdapter(List list){
            super(list);
        }
        @Override
        public View getItemView(int position,View convertView,ViewGroup parent){

            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.drag_item,null);
            TextView tv_text = (TextView)convertView.findViewById(R.id.item_name);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.item_image);
            tv_text.setText(getList().get(position).getName());
            imageView.setImageResource(getList().get(position).getId());
            return convertView;
        }
    }

    private void initList(){
        mlist = HomeBean.getList();
        myAdapter adapter = new myAdapter(mlist);
        mGridView.setAdapter(adapter);

    }
/*
   class MAdapter extends DragGridAdapter<String> {
       public MAdapter(List list){
           super(list);
       }
       @Override
       public View getItemView(int position, View convertView,ViewGroup parent){
           String text = getList().get(position);
           convertView = LayoutInflater.from(getActivity()).inflate(R.layout.drag_item,null);
           TextView tv_text = (TextView)convertView.findViewById(R.id.tv_text);
           tv_text.setText(text);
           return convertView;
       }
   }
   */
}
