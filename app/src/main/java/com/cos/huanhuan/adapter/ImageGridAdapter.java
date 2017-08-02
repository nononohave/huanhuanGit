package com.cos.huanhuan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cos.huanhuan.MainActivity;
import com.cos.huanhuan.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/1.
 */

public class ImageGridAdapter extends BaseAdapter {
    private ArrayList<String> listUrls;
    private Context context;
    private int columnWidth = 300;
    public ImageGridAdapter(Context context, ArrayList<String> listUrls) {
        this.listUrls = listUrls;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listUrls.size();
    }

    @Override
    public String getItem(int position) {
        return listUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_image, null);
            imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(imageView);
            // 重置ImageView宽高
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(columnWidth, columnWidth);
            imageView.setLayoutParams(params);
        }else {
            imageView = (ImageView) convertView.getTag();
        }
        //Picasso.with(MainActivity.this).load(getItem(position)).placeholder(R.mipmap.default_error).into(imageView);
        Picasso.with(context).load(new File(getItem(position))).placeholder(R.mipmap.default_error).resize(300, 300).into(imageView);
            /*Glide.with(MainActivity.this)
                    .load(new File(getItem(position)))
                    .placeholder(R.mipmap.default_error)
                    .error(R.mipmap.default_error)
                    .centerCrop()
                    .crossFade()
                    .into(imageView);*/
        return convertView;
    }
}
