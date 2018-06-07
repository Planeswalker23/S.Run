package com.nanbei.srun;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter{

    private int[]res;
    private Context context;
    public ImageAdapter(int []res,Context context) {
        this.res=res;
        this.context=context;
    }
    //返回数据源的数量
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {
        return res[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("Main", "position="+position+"res的角标="+position%res.length);
        ImageView image=new ImageView(context);
        image.setBackgroundResource(res[position%res.length]);
        image.setLayoutParams(new Gallery.LayoutParams(200, 150));
        image.setScaleType(ScaleType.FIT_XY);
        return image;
    }
}