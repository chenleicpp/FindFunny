package com.sanshisoft.findfunny.adapter;

import java.util.List;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanshisoft.findfunny.App;
import com.sanshisoft.findfunny.R;
import com.sanshisoft.findfunny.model.Category;

public class DrawerAdapter extends BaseAdapter {
    private List<Category> mList;
    private int curPos;

    public DrawerAdapter(List<Category> list) {
        mList = list;
    }

    public void setPos(int pos){
        curPos = pos;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Category getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(App.getContext()).inflate(R.layout.drawer_list_item, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.text);
        textView.setText(getItem(position).getLable());
        ImageView imageView = (ImageView)convertView.findViewById(R.id.image);
        imageView.setImageResource(getItem(position).getImage());
        if (position == curPos){
            textView.setTextColor(App.getContext().getResources().getColorStateList(R.color.main_bg));
            textView.getPaint().setFakeBoldText(true);
        }else{
            textView.setTextColor(Color.BLACK);
            textView.getPaint().setFakeBoldText(false);
        }
        return convertView;
    }
}
